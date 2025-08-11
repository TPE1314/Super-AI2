#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
电报多管理员客服机器人
支持多管理员配置、消息路由、会话管理等功能
"""

import asyncio
import logging
import sqlite3
import configparser
from datetime import datetime, timedelta
from typing import Dict, Optional, Tuple
from telegram import Update, InlineKeyboardButton, InlineKeyboardMarkup
from telegram.ext import Application, CommandHandler, MessageHandler, CallbackQueryHandler, filters, ContextTypes

# 配置日志
logging.basicConfig(
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    level=logging.INFO
)
logger = logging.getLogger(__name__)

class CustomerServiceBot:
    def __init__(self, config_file: str = 'config.ini'):
        """初始化机器人"""
        self.config = self._load_config(config_file)
        self.bot_token = self.config.get('BOT', 'token')
        self.admins = self._parse_admins()
        self.db_path = 'database.db'
        self._init_database()
        
    def _load_config(self, config_file: str) -> configparser.ConfigParser:
        """加载配置文件"""
        config = configparser.ConfigParser()
        config.read(config_file, encoding='utf-8')
        return config
    
    def _parse_admins(self) -> Dict[str, int]:
        """解析管理员配置"""
        admins = {}
        if 'ADMINS' in self.config:
            for name, user_id in self.config['ADMINS'].items():
                try:
                    admins[name] = int(user_id)
                except ValueError:
                    logger.error(f"无效的管理员ID: {name} = {user_id}")
        return admins
    
    def _init_database(self):
        """初始化数据库"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # 创建会话表
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS conversations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                admin_id INTEGER NOT NULL,
                status TEXT DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        # 创建消息表
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                conv_id INTEGER NOT NULL,
                sender_id INTEGER NOT NULL,
                content TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (conv_id) REFERENCES conversations (id)
            )
        ''')
        
        conn.commit()
        conn.close()
        logger.info("数据库初始化完成")
    
    def _get_admin_keyboard(self) -> InlineKeyboardMarkup:
        """生成管理员选择键盘"""
        keyboard = []
        row = []
        
        for i, (name, user_id) in enumerate(self.admins.items()):
            row.append(InlineKeyboardButton(
                text=name,
                callback_data=f"admin_{user_id}"
            ))
            
            # 每3个按钮换一行
            if (i + 1) % 3 == 0:
                keyboard.append(row)
                row = []
        
        # 处理最后一行
        if row:
            keyboard.append(row)
        
        return InlineKeyboardMarkup(keyboard)
    
    def _mask_phone(self, phone: str) -> str:
        """脱敏处理电话号码"""
        if not phone or len(phone) < 7:
            return phone
        # 处理国际号码格式
        if phone.startswith('+'):
            # 国际号码：+86 138****1234
            if len(phone) >= 11:
                return phone[:5] + "****" + phone[-4:]
            else:
                return phone
        else:
            # 国内号码：138****1234
            return phone[:3] + "****" + phone[-4:]
    
    def _get_user_info(self, user_id: int, username: str, phone: str = None) -> str:
        """生成用户信息字符串"""
        info = f"[用户ID] {user_id}"
        if username:
            info += f" @{username}"
        if phone:
            info += f"\n联系方式: {self._mask_phone(phone)}"
        return info
    
    def _get_active_conversation(self, user_id: int) -> Optional[Tuple[int, int]]:
        """获取用户活跃会话"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            SELECT id, admin_id FROM conversations 
            WHERE user_id = ? AND status = 'active'
            ORDER BY last_active DESC LIMIT 1
        ''', (user_id,))
        
        result = cursor.fetchone()
        conn.close()
        
        return result if result else None
    
    def _create_conversation(self, user_id: int, admin_id: int) -> int:
        """创建新会话"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT INTO conversations (user_id, admin_id, status, created_at, last_active)
            VALUES (?, ?, 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        ''', (user_id, admin_id))
        
        conv_id = cursor.lastrowid
        conn.commit()
        conn.close()
        
        return conv_id
    
    def _save_message(self, conv_id: int, sender_id: int, content: str):
        """保存消息到数据库"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            INSERT INTO messages (conv_id, sender_id, content, timestamp)
            VALUES (?, ?, ?, CURRENT_TIMESTAMP)
        ''', (conv_id, sender_id, content))
        
        # 更新会话最后活跃时间
        cursor.execute('''
            UPDATE conversations 
            SET last_active = CURRENT_TIMESTAMP 
            WHERE id = ?
        ''', (conv_id,))
        
        conn.commit()
        conn.close()
    
    def _close_conversation(self, conv_id: int):
        """关闭会话"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute('''
            UPDATE conversations 
            SET status = 'closed' 
            WHERE id = ?
        ''', (conv_id,))
        
        conn.commit()
        conn.close()
    
    def _check_timeout_conversations(self):
        """检查并关闭超时会话"""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # 查找30分钟未活跃的会话
        timeout_time = datetime.now() - timedelta(minutes=30)
        cursor.execute('''
            SELECT id, user_id, admin_id FROM conversations 
            WHERE status = 'active' AND last_active < ?
        ''', (timeout_time,))
        
        timeout_convs = cursor.fetchall()
        
        for conv_id, user_id, admin_id in timeout_convs:
            # 关闭会话
            cursor.execute('''
                UPDATE conversations 
                SET status = 'closed' 
                WHERE id = ?
            ''', (conv_id,))
            
            # 通知用户和管理员
            try:
                asyncio.create_task(self._notify_timeout(user_id, admin_id))
            except Exception as e:
                logger.error(f"通知超时会话失败: {e}")
        
        conn.commit()
        conn.close()
        
        if timeout_convs:
            logger.info(f"关闭了 {len(timeout_convs)} 个超时会话")
    
    async def _notify_timeout(self, user_id: int, admin_id: int):
        """通知会话超时"""
        try:
            # 这里需要bot实例来发送消息，暂时跳过
            pass
        except Exception as e:
            logger.error(f"发送超时通知失败: {e}")
    
    async def start_command(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """处理 /start 命令"""
        user = update.effective_user
        
        welcome_text = (
            f"👋 您好 {user.first_name}！\n\n"
            "请选择需要咨询的管理员："
        )
        
        keyboard = self._get_admin_keyboard()
        
        await update.message.reply_text(
            welcome_text,
            reply_markup=keyboard
        )
    
    async def button_callback(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """处理按钮回调"""
        query = update.callback_query
        await query.answer()
        
        if query.data.startswith("admin_"):
            admin_id = int(query.data.split("_")[1])
            user = update.effective_user
            
            # 创建新会话
            conv_id = self._create_conversation(user.id, admin_id)
            
            # 获取管理员名称
            admin_name = "未知管理员"
            for name, uid in self.admins.items():
                if uid == admin_id:
                    admin_name = name
                    break
            
            # 通知用户
            await query.edit_message_text(
                f"✅ 已为您连接到 {admin_name}\n"
                f"请直接发送您的问题，我会为您转达。"
            )
            
            # 通知管理员
            admin_info = self._get_user_info(user.id, user.username)
            admin_message = (
                f"🔔 新用户咨询\n\n"
                f"{admin_info}\n\n"
                f"请回复此消息来帮助用户。"
            )
            
            # 保存管理员通知消息
            self._save_message(conv_id, admin_id, admin_message)
            
            # 发送回复按钮给管理员
            keyboard = InlineKeyboardMarkup([[
                InlineKeyboardButton("💬 回复用户", callback_data=f"reply_{conv_id}")
            ]])
            
            try:
                await context.bot.send_message(
                    chat_id=admin_id,
                    text=admin_message,
                    reply_markup=keyboard
                )
            except Exception as e:
                logger.error(f"发送管理员通知失败: {e}")
    
    async def handle_user_message(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """处理用户消息"""
        user = update.effective_user
        message_text = update.message.text
        
        # 检查是否有活跃会话
        conv_info = self._get_active_conversation(user.id)
        
        if not conv_info:
            # 没有活跃会话，提示选择管理员
            await update.message.reply_text(
                "请先选择管理员开始咨询。",
                reply_markup=self._get_admin_keyboard()
            )
            return
        
        conv_id, admin_id = conv_info
        
        # 保存用户消息
        self._save_message(conv_id, user.id, message_text)
        
        # 转发给管理员
        user_info = self._get_user_info(user.id, user.username)
        admin_message = (
            f"📨 用户消息\n\n"
            f"{user_info}\n\n"
            f"💬 {message_text}"
        )
        
        # 发送回复按钮给管理员
        keyboard = InlineKeyboardMarkup([[
            InlineKeyboardButton("💬 回复用户", callback_data=f"reply_{conv_id}")
        ]])
        
        try:
            await context.bot.send_message(
                chat_id=admin_id,
                text=admin_message,
                reply_markup=keyboard
            )
            
            # 确认用户消息已收到
            await update.message.reply_text("✅ 消息已发送给管理员，请稍候回复。")
            
        except Exception as e:
            logger.error(f"转发用户消息失败: {e}")
            await update.message.reply_text("❌ 消息发送失败，请稍后重试。")
    
    async def handle_admin_message(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
        """处理管理员消息"""
        admin = update.effective_user
        message_text = update.message.text
        
        # 检查是否是配置的管理员
        if admin.id not in self.admins.values():
            return
        
        # 检查是否是回复消息
        if not update.message.reply_to_message:
            return
        
        # 获取回复的消息内容
        replied_message = update.message.reply_to_message.text
        
        # 查找对应的会话
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        # 从消息内容中提取会话ID（这里简化处理）
        # 实际应用中可能需要更复杂的逻辑来关联消息和会话
        
        cursor.execute('''
            SELECT c.id, c.user_id, c.admin_id 
            FROM conversations c
            JOIN messages m ON c.id = m.conv_id
            WHERE m.content LIKE ? AND c.status = 'active'
            ORDER BY m.timestamp DESC LIMIT 1
        ''', (f"%{replied_message[:50]}%",))
        
        result = cursor.fetchone()
        conn.close()
        
        if result:
            conv_id, user_id, admin_id = result
            
            # 保存管理员回复
            self._save_message(conv_id, admin_id, message_text)
            
            # 发送给用户
            admin_name = "管理员"
            for name, uid in self.admins.items():
                if uid == admin_id:
                    admin_name = name
                    break
            
            user_message = (
                f"[客服回复] 来自{admin_name}：\n\n"
                f"{message_text}"
            )
            
            try:
                await context.bot.send_message(
                    chat_id=user_id,
                    text=user_message
                )
                
                # 确认管理员回复已发送
                await update.message.reply_text("✅ 回复已发送给用户。")
                
            except Exception as e:
                logger.error(f"发送管理员回复失败: {e}")
                await update.message.reply_text("❌ 回复发送失败，请稍后重试。")
        else:
            await update.message.reply_text("❌ 无法找到对应的会话，请检查回复的消息。")
    
    async def timeout_checker(self, context: ContextTypes.DEFAULT_TYPE):
        """定期检查超时会话"""
        self._check_timeout_conversations()
    
    def run(self):
        """运行机器人"""
        # 创建应用
        application = Application.builder().token(self.bot_token).build()
        
        # 添加处理器
        application.add_handler(CommandHandler("start", self.start_command))
        application.add_handler(CallbackQueryHandler(self.button_callback))
        application.add_handler(MessageHandler(filters.TEXT & ~filters.COMMAND, self.handle_user_message))
        
        # 添加定时任务检查超时会话
        job_queue = application.job_queue
        job_queue.run_repeating(self.timeout_checker, interval=300)  # 每5分钟检查一次
        
        # 启动机器人
        logger.info("机器人启动中...")
        application.run_polling(allowed_updates=Update.ALL_TYPES)

def main():
    """主函数"""
    try:
        bot = CustomerServiceBot()
        bot.run()
    except Exception as e:
        logger.error(f"机器人启动失败: {e}")
        print(f"错误: {e}")
        print("请检查 config.ini 文件配置是否正确")

if __name__ == '__main__':
    main()
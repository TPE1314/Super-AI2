#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
简化测试脚本 - 不依赖外部包
用于验证基本功能和配置
"""

import sqlite3
import configparser
import os

def test_config_file():
    """测试配置文件是否存在"""
    print("🔧 测试配置文件...")
    
    if os.path.exists('config.ini'):
        print("✅ config.ini 文件存在")
        return True
    else:
        print("❌ config.ini 文件不存在")
        return False

def test_config_content():
    """测试配置文件内容"""
    print("\n📝 测试配置文件内容...")
    
    try:
        config = configparser.ConfigParser()
        config.read('config.ini', encoding='utf-8')
        
        # 检查必要的配置节
        required_sections = ['BOT', 'ADMINS']
        for section in required_sections:
            if section in config:
                print(f"✅ 找到配置节: [{section}]")
            else:
                print(f"❌ 缺少配置节: [{section}]")
                return False
        
        # 检查BOT配置
        if 'token' in config['BOT']:
            token = config['BOT']['token']
            if token == 'YOUR_BOT_TOKEN_HERE':
                print("⚠️  BOT Token 还是默认值，需要修改")
            else:
                print("✅ BOT Token 已配置")
        else:
            print("❌ 缺少 BOT Token 配置")
            return False
        
        # 检查管理员配置
        if 'ADMINS' in config:
            admin_count = len(config['ADMINS'])
            print(f"✅ 找到 {admin_count} 个管理员配置")
            
            for name, user_id in config['ADMINS'].items():
                try:
                    uid = int(user_id)
                    print(f"   - {name}: {uid}")
                except ValueError:
                    print(f"   ❌ {name}: {user_id} (无效的用户ID)")
                    return False
        
        return True
        
    except Exception as e:
        print(f"❌ 配置文件解析失败: {e}")
        return False

def test_database_creation():
    """测试数据库创建"""
    print("\n🗄️  测试数据库创建...")
    
    try:
        # 创建测试数据库
        conn = sqlite3.connect('test_database.db')
        cursor = conn.cursor()
        
        # 创建测试表
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_conversations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                admin_id INTEGER NOT NULL,
                status TEXT DEFAULT 'active',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        ''')
        
        cursor.execute('''
            CREATE TABLE IF NOT EXISTS test_messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                conv_id INTEGER NOT NULL,
                sender_id INTEGER NOT NULL,
                content TEXT NOT NULL,
                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (conv_id) REFERENCES test_conversations (id)
            )
        ''')
        
        # 插入测试数据
        cursor.execute('''
            INSERT INTO test_conversations (user_id, admin_id, status)
            VALUES (12345, 67890, 'active')
        ''')
        
        cursor.execute('''
            INSERT INTO test_messages (conv_id, sender_id, content)
            VALUES (1, 12345, '测试消息')
        ''')
        
        # 查询测试数据
        cursor.execute('SELECT COUNT(*) FROM test_conversations')
        conv_count = cursor.fetchone()[0]
        
        cursor.execute('SELECT COUNT(*) FROM test_messages')
        msg_count = cursor.fetchone()[0]
        
        print(f"✅ 数据库创建成功")
        print(f"   会话表: {conv_count} 条记录")
        print(f"   消息表: {msg_count} 条记录")
        
        conn.commit()
        conn.close()
        
        # 清理测试数据库
        os.remove('test_database.db')
        print("✅ 测试数据库清理完成")
        
        return True
        
    except Exception as e:
        print(f"❌ 数据库测试失败: {e}")
        return False

def test_file_structure():
    """测试文件结构"""
    print("\n📁 测试文件结构...")
    
    required_files = [
        'bot.py',
        'config.ini',
        'requirements.txt',
        'README.md',
        'start.sh'
    ]
    
    missing_files = []
    for file in required_files:
        if os.path.exists(file):
            print(f"✅ {file}")
        else:
            print(f"❌ {file}")
            missing_files.append(file)
    
    if missing_files:
        print(f"⚠️  缺少 {len(missing_files)} 个文件")
        return False
    else:
        print("✅ 所有必需文件都存在")
        return True

def test_phone_masking():
    """测试电话号码脱敏功能"""
    print("\n🔒 测试电话号码脱敏...")
    
    def mask_phone(phone):
        """脱敏处理电话号码"""
        if not phone or len(phone) < 7:
            return phone
        return phone[:3] + "****" + phone[-4:]
    
    test_cases = [
        ("13812345678", "138****5678"),
        ("+8613812345678", "+86138****5678"),
        ("123", "123"),
        ("", ""),
        (None, None)
    ]
    
    all_passed = True
    for phone, expected in test_cases:
        result = mask_phone(phone)
        if result == expected:
            print(f"   ✅ {phone} -> {result}")
        else:
            print(f"   ❌ {phone} -> {result} (期望: {expected})")
            all_passed = False
    
    if all_passed:
        print("✅ 电话号码脱敏测试通过")
        return True
    else:
        print("❌ 电话号码脱敏测试失败")
        return False

def main():
    """主测试函数"""
    print("🧪 电报客服机器人基础功能测试")
    print("=" * 50)
    
    tests = [
        test_config_file,
        test_config_content,
        test_database_creation,
        test_file_structure,
        test_phone_masking
    ]
    
    passed = 0
    total = len(tests)
    
    for test in tests:
        if test():
            passed += 1
    
    print("\n" + "=" * 50)
    print(f"📊 测试结果: {passed}/{total} 通过")
    
    if passed == total:
        print("🎉 所有基础测试通过！")
        print("\n📝 下一步:")
        print("1. 安装依赖: pip3 install -r requirements.txt")
        print("2. 在config.ini中设置正确的机器人Token")
        print("3. 配置真实的管理员ID")
        print("4. 运行 python3 bot.py 启动机器人")
        print("5. 或使用 ./start.sh 启动脚本")
    else:
        print("⚠️  部分测试失败，请检查项目配置。")
    
    return passed == total

if __name__ == '__main__':
    main()
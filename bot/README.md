# 电报多管理员客服机器人 🤖

一个功能完整的电报客服机器人，支持多管理员配置、智能消息路由、会话管理等功能。专为企业客服、技术支持、在线咨询等场景设计。

## ✨ 核心功能

### 🎯 用户端体验
- **🎨 美观界面**：3列网格布局的管理员选择按钮
- **🚀 智能路由**：自动转发消息至对应管理员
- **🔒 隐私保护**：电话号码自动脱敏处理
- **⏰ 会话管理**：30分钟无互动自动关闭，避免资源浪费
- **📱 响应式设计**：支持各种设备访问

### 🔧 管理员端功能
- **🔔 实时通知**：新用户咨询即时提醒，不错过任何机会
- **💬 一键回复**：内置回复按钮，操作简单便捷
- **📊 用户画像**：显示用户ID、用户名、联系方式等完整信息
- **🔄 智能绑定**：回复消息自动关联原始用户，无需手动查找
- **📈 会话统计**：实时查看活跃会话和消息数量

### 🛡️ 安全与稳定
- **🔐 身份验证**：仅配置文件中管理员可回复，确保安全性
- **🚫 会话隔离**：不同用户会话完全独立，保护隐私
- **⏱️ 超时保护**：自动关闭长时间无互动会话，释放资源
- **💾 数据持久化**：SQLite数据库存储，数据安全可靠

## 🚀 快速部署

### 📋 环境要求
- **Python**: 3.7 或更高版本
- **操作系统**: Windows, macOS, Linux
- **网络**: 可访问Telegram API
- **内存**: 建议512MB以上

### 🔧 安装步骤

#### 步骤1: 克隆或下载项目
```bash
# 如果使用git
git clone <repository-url>
cd bot

# 或者直接下载zip文件并解压
cd bot
```

#### 步骤2: 安装Python依赖
```bash
# 使用pip安装
pip3 install -r requirements.txt

# 或者使用pip
pip install -r requirements.txt
```

#### 步骤3: 配置机器人
1. **获取机器人Token**：
   - 在电报中搜索 `@BotFather`
   - 发送 `/newbot` 命令
   - 设置机器人名称和用户名
   - 复制获得的Token

2. **获取管理员ID**：
   - 在电报中搜索 `@userinfobot`
   - 发送任意消息获取您的用户ID
   - 记录下数字ID

3. **编辑配置文件**：
   ```bash
   # 复制示例配置文件
   cp config.example.ini config.ini
   
   # 编辑配置文件
   nano config.ini  # Linux/Mac
   # 或
   notepad config.ini  # Windows
   ```

   配置示例：
   ```ini
   [BOT]
   token = 1234567890:ABCdefGHIjklMNOpqrsTUVwxyz
   
   [ADMINS]
   技术支持 = 123456789
   财务咨询 = 987654321
   客服主管 = 555555555
   ```

#### 步骤4: 验证配置
```bash
# 运行基础测试
python3 simple_test.py

# 确保所有测试通过
```

#### 步骤5: 启动机器人
```bash
# 方式1: 直接启动
python3 bot.py

# 方式2: 使用启动脚本（推荐）
./start.sh

# Windows用户
start.bat
```

## 📁 项目结构详解

```
/bot
├── bot.py                    # 🚀 主程序文件（核心逻辑）
├── config.ini               # ⚙️  配置文件（需要配置）
├── config.example.ini       # 📋 示例配置文件
├── requirements.txt          # 📦 Python依赖包列表
├── start.sh                 # 🐧 Linux/Mac启动脚本
├── start.bat                # 🪟 Windows启动脚本
├── README.md                # 📖 详细使用说明
├── DEPLOYMENT_CHECKLIST.md  # ✅ 部署检查清单
├── simple_test.py           # 🧪 基础功能测试
├── test_bot.py              # 🔍 完整功能测试
└── database.db              # 🗄️  SQLite数据库（自动生成）
```

## 💡 使用流程详解

### 🔄 用户咨询流程
```
用户 → /start → 选择管理员 → 发送消息 → 机器人转发 → 管理员接收
```

1. **启动机器人**：用户发送 `/start` 命令
2. **选择管理员**：机器人显示3列网格布局的管理员按钮
3. **建立会话**：用户选择后，机器人创建专属会话
4. **发送咨询**：用户直接发送问题，机器人自动转发
5. **等待回复**：机器人确认消息已发送，用户等待回复

### 🔄 管理员回复流程
```
管理员 → 接收通知 → 查看用户信息 → 回复消息 → 机器人转发 → 用户接收
```

1. **接收通知**：管理员收到新用户咨询通知
2. **查看信息**：显示用户ID、用户名、联系方式等
3. **回复用户**：点击"回复用户"按钮或直接回复
4. **自动转发**：机器人将回复转发给用户
5. **确认发送**：机器人确认回复已成功发送

## 🔧 配置详解

### 📱 机器人配置
```ini
[BOT]
# 机器人Token（从@BotFather获取）
token = YOUR_BOT_TOKEN_HERE

# 可选：机器人名称（用于日志显示）
name = 客服机器人
```

### 👥 管理员配置
```ini
[ADMINS]
# 格式：显示名称 = 用户ID
# 显示名称：用户看到的按钮文字
# 用户ID：管理员的数字ID（从@userinfobot获取）

技术支持 = 123456789
财务咨询 = 987654321
客服主管 = 555555555
产品经理 = 111222333
运营专员 = 444555666
```

### ⚙️ 系统设置
```ini
[SETTINGS]
# 会话超时时间（分钟）
session_timeout = 30

# 检查超时的时间间隔（秒）
check_interval = 300

# 最大会话数量（可选）
max_conversations = 1000

# 日志级别（可选）
log_level = INFO
```

## 🧪 测试与验证

### 🔍 基础功能测试
```bash
# 运行基础测试（无需外部依赖）
python3 simple_test.py
```

测试内容包括：
- ✅ 配置文件检查
- ✅ 数据库连接测试
- ✅ 文件结构验证
- ✅ 电话号码脱敏测试

### 🔍 完整功能测试
```bash
# 安装依赖后运行完整测试
pip3 install -r requirements.txt
python3 test_bot.py
```

测试内容包括：
- ✅ 机器人初始化
- ✅ 管理员键盘生成
- ✅ 消息处理逻辑
- ✅ 数据库操作

## 🚨 故障排除

### ❌ 常见问题

#### 1. 机器人无法启动
**症状**：运行 `python3 bot.py` 后立即退出
**解决方案**：
```bash
# 检查Python版本
python3 --version

# 检查依赖安装
pip3 list | grep telegram

# 检查配置文件
cat config.ini

# 查看详细错误信息
python3 bot.py 2>&1 | tee error.log
```

#### 2. 管理员收不到通知
**症状**：用户发送消息，管理员没有收到通知
**解决方案**：
- 确认管理员ID正确（使用 `@userinfobot` 验证）
- 检查机器人是否已被管理员启动过
- 验证网络连接是否正常
- 查看机器人日志输出

#### 3. 消息无法转发
**症状**：用户消息发送失败或无法转发
**解决方案**：
- 确认用户已选择管理员
- 检查会话状态是否活跃
- 验证数据库连接
- 查看错误日志

#### 4. 依赖安装失败
**症状**：`pip install` 命令失败
**解决方案**：
```bash
# 升级pip
pip3 install --upgrade pip

# 使用国内镜像源
pip3 install -i https://pypi.tuna.tsinghua.edu.cn/simple -r requirements.txt

# 或者使用conda
conda install python-telegram-bot
```

### 📊 性能优化

#### 数据库优化
```sql
-- 创建索引提高查询性能
CREATE INDEX idx_conversations_user_id ON conversations(user_id);
CREATE INDEX idx_conversations_admin_id ON conversations(admin_id);
CREATE INDEX idx_messages_conv_id ON messages(conv_id);
CREATE INDEX idx_conversations_status ON conversations(status);
```

#### 内存优化
- 定期清理过期会话
- 限制最大会话数量
- 使用连接池管理数据库连接

## 📈 扩展功能

### 🔌 插件系统
机器人采用模块化设计，可以轻松添加新功能：

```python
# 在CustomerServiceBot类中添加新方法
async def handle_file_upload(self, update: Update, context: ContextTypes.DEFAULT_TYPE):
    """处理文件上传"""
    # 实现文件处理逻辑
    pass

# 在run()方法中注册新的处理器
application.add_handler(MessageHandler(filters.Document.ALL, self.handle_file_upload))
```

### 🎨 自定义界面
- 修改按钮样式和布局
- 添加多语言支持
- 自定义欢迎消息
- 添加帮助信息

### 📊 数据分析
- 消息统计报表
- 用户行为分析
- 响应时间监控
- 满意度调查

## 🔒 安全最佳实践

### 🔐 访问控制
- 定期更新管理员列表
- 使用强密码保护配置文件
- 限制机器人权限范围
- 监控异常访问行为

### 🛡️ 数据保护
- 定期备份数据库
- 加密敏感配置信息
- 实现数据保留策略
- 遵守隐私法规要求

### 🚨 监控告警
- 设置错误日志监控
- 配置性能指标告警
- 实现自动重启机制
- 建立故障响应流程

## 📞 技术支持

### 🆘 获取帮助
1. **查看日志**：机器人运行时会输出详细日志
2. **运行测试**：使用测试脚本诊断问题
3. **检查配置**：验证配置文件格式和内容
4. **网络诊断**：确认可访问Telegram API

### 📚 学习资源
- [python-telegram-bot官方文档](https://python-telegram-bot.readthedocs.io/)
- [Telegram Bot API文档](https://core.telegram.org/bots/api)
- [Python异步编程指南](https://docs.python.org/3/library/asyncio.html)

### 🤝 社区支持
- GitHub Issues：报告bug和功能请求
- 电报群组：实时技术支持
- 技术论坛：分享经验和解决方案

## 📄 许可证

本项目采用MIT许可证，您可以：
- ✅ 自由使用、修改和分发
- ✅ 用于商业和非商业项目
- ✅ 修改源代码并闭源
- ✅ 在保留许可证声明的前提下使用

## 🙏 致谢

感谢以下开源项目：
- [python-telegram-bot](https://github.com/python-telegram-bot/python-telegram-bot)
- [SQLite](https://www.sqlite.org/)
- [Python](https://www.python.org/)

---

## 🎯 开始使用

现在您已经了解了所有功能，让我们开始部署：

1. **📥 下载项目文件**
2. **⚙️ 配置机器人Token和管理员ID**
3. **🧪 运行测试脚本验证功能**
4. **🚀 启动机器人开始使用**
5. **📊 监控运行状态和性能**

**祝您使用愉快！** 🎉

如有问题，请参考故障排除指南或寻求技术支持。
#!/bin/bash

# 电报多管理员客服机器人启动脚本

echo "🚀 启动电报客服机器人..."
echo "================================"

# 检查Python版本
python_version=$(python3 --version 2>&1)
if [[ $? -ne 0 ]]; then
    echo "❌ 错误: 未找到Python3，请先安装Python 3.7+"
    exit 1
fi

echo "✅ Python版本: $python_version"

# 检查依赖
echo "📦 检查依赖包..."
if ! python3 -c "import telegram" 2>/dev/null; then
    echo "⚠️  缺少python-telegram-bot，正在安装..."
    pip3 install -r requirements.txt
    if [[ $? -ne 0 ]]; then
        echo "❌ 依赖安装失败，请手动执行: pip3 install -r requirements.txt"
        exit 1
    fi
fi

echo "✅ 依赖检查完成"

# 检查配置文件
if [[ ! -f "config.ini" ]]; then
    echo "❌ 错误: 未找到config.ini配置文件"
    echo "请先配置config.ini文件，设置机器人Token和管理员ID"
    exit 1
fi

# 检查Token配置
if grep -q "YOUR_BOT_TOKEN_HERE" config.ini; then
    echo "❌ 错误: 请在config.ini中设置正确的机器人Token"
    exit 1
fi

echo "✅ 配置文件检查完成"
echo "================================"
echo "🤖 正在启动机器人..."

# 启动机器人
python3 bot.py
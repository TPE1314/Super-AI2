#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
机器人功能测试脚本
用于验证基本功能是否正常
"""

import sqlite3
import configparser
from bot import CustomerServiceBot

def test_config_loading():
    """测试配置文件加载"""
    print("🔧 测试配置文件加载...")
    try:
        bot = CustomerServiceBot('config.ini')
        print("✅ 配置文件加载成功")
        print(f"   找到 {len(bot.admins)} 个管理员")
        for name, uid in bot.admins.items():
            print(f"   - {name}: {uid}")
        return True
    except Exception as e:
        print(f"❌ 配置文件加载失败: {e}")
        return False

def test_database():
    """测试数据库功能"""
    print("\n🗄️  测试数据库功能...")
    try:
        conn = sqlite3.connect('database.db')
        cursor = conn.cursor()
        
        # 测试表是否存在
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
        tables = cursor.fetchall()
        print(f"✅ 数据库连接成功，找到 {len(tables)} 个表")
        
        for table in tables:
            print(f"   - {table[0]}")
        
        conn.close()
        return True
    except Exception as e:
        print(f"❌ 数据库测试失败: {e}")
        return False

def test_admin_keyboard():
    """测试管理员键盘生成"""
    print("\n⌨️  测试管理员键盘生成...")
    try:
        bot = CustomerServiceBot('config.ini')
        keyboard = bot._get_admin_keyboard()
        print("✅ 管理员键盘生成成功")
        print(f"   键盘包含 {len(keyboard.inline_keyboard)} 行按钮")
        return True
    except Exception as e:
        print(f"❌ 管理员键盘测试失败: {e}")
        return False

def test_phone_masking():
    """测试电话号码脱敏"""
    print("\n🔒 测试电话号码脱敏...")
    try:
        bot = CustomerServiceBot('config.ini')
        
        test_cases = [
            ("13812345678", "138****5678"),
            ("+8613812345678", "+86138****5678"),
            ("123", "123"),
            ("", ""),
            (None, None)
        ]
        
        for phone, expected in test_cases:
            result = bot._mask_phone(phone)
            if result == expected:
                print(f"   ✅ {phone} -> {result}")
            else:
                print(f"   ❌ {phone} -> {result} (期望: {expected})")
                return False
        
        print("✅ 电话号码脱敏测试通过")
        return True
    except Exception as e:
        print(f"❌ 电话号码脱敏测试失败: {e}")
        return False

def main():
    """主测试函数"""
    print("🧪 电报客服机器人功能测试")
    print("=" * 40)
    
    tests = [
        test_config_loading,
        test_database,
        test_admin_keyboard,
        test_phone_masking
    ]
    
    passed = 0
    total = len(tests)
    
    for test in tests:
        if test():
            passed += 1
    
    print("\n" + "=" * 40)
    print(f"📊 测试结果: {passed}/{total} 通过")
    
    if passed == total:
        print("🎉 所有测试通过！机器人可以正常使用。")
        print("\n📝 下一步:")
        print("1. 在config.ini中设置正确的机器人Token")
        print("2. 配置真实的管理员ID")
        print("3. 运行 python3 bot.py 启动机器人")
    else:
        print("⚠️  部分测试失败，请检查配置和依赖。")
    
    return passed == total

if __name__ == '__main__':
    main()
# 机器人聚合器 Android 应用

一个用于管理和聚合多个机器人API的Android应用程序。

## 功能特性

- **机器人管理**: 添加、编辑、删除和测试机器人API连接
- **消息发送**: 向选定的机器人发送消息
- **状态管理**: 启用/禁用机器人
- **现代化UI**: 使用Material Design组件
- **本地存储**: 使用Room数据库存储机器人信息

## 技术栈

- **语言**: Kotlin
- **架构**: MVVM (Model-View-ViewModel)
- **数据库**: Room
- **网络**: Retrofit + OkHttp
- **UI**: Material Design Components
- **异步**: Coroutines + Flow

## 项目结构

```
app/src/main/java/com/example/robotaggregator/
├── data/           # 数据层 (Room数据库)
├── api/            # API层 (Retrofit)
├── repository/     # 仓库层
├── viewmodel/      # ViewModel层
├── ui/             # UI层 (Fragments)
├── adapter/        # RecyclerView适配器
└── MainActivity.kt # 主活动

app/src/main/res/
├── layout/         # 布局文件
├── values/         # 资源文件
├── drawable/       # 图标资源
└── menu/           # 菜单文件
```

## 构建和运行

1. 确保已安装Android Studio
2. 克隆项目到本地
3. 在Android Studio中打开项目
4. 等待Gradle同步完成
5. 连接Android设备或启动模拟器
6. 点击运行按钮

## 构建APK

### 调试版本
```bash
./gradlew assembleDebug
```

### 发布版本
```bash
./gradlew assembleRelease
```

生成的APK文件将位于 `app/build/outputs/apk/` 目录中。

## 使用说明

1. **添加机器人**: 点击右下角的浮动按钮，填写机器人名称、API地址和密钥
2. **测试连接**: 在机器人列表中点击"测试连接"按钮
3. **发送消息**: 在聊天页面选择机器人并发送消息
4. **管理机器人**: 使用开关启用/禁用机器人，或编辑/删除机器人

## 配置说明

应用支持多种机器人API格式，默认支持以下API结构：

- **状态检查**: GET /status (需要Authorization头)
- **消息发送**: POST /chat (需要消息和API密钥)

## 许可证

MIT License
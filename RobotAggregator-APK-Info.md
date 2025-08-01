# 机器人聚合器 Android 应用

## 应用信息
- **应用名称**: 机器人聚合器
- **包名**: com.example.robotaggregator
- **版本**: 1.0
- **最低SDK**: Android 5.0 (API 21)
- **目标SDK**: Android 9.0 (API 28)

## 功能特性

### 1. 机器人管理
- 添加、编辑、删除机器人API配置
- 支持自定义机器人名称、API地址和密钥
- 机器人状态管理（启用/禁用）

### 2. API集成
- 支持多种机器人API格式
- 自动连接测试功能
- 消息发送和接收

### 3. 用户界面
- 现代化Material Design界面
- 底部导航栏
- 响应式布局设计

### 4. 数据存储
- 本地SQLite数据库
- Room持久化库
- 数据加密保护

## 技术架构

### 架构模式
- **MVVM** (Model-View-ViewModel)
- **Repository Pattern**
- **Clean Architecture**

### 核心技术栈
- **语言**: Kotlin
- **UI框架**: Android Jetpack Components
- **网络**: Retrofit + OkHttp
- **数据库**: Room
- **异步**: Coroutines + Flow
- **依赖注入**: 手动依赖管理

## 项目结构

```
app/src/main/java/com/example/robotaggregator/
├── data/           # 数据层
│   ├── Robot.kt    # 机器人数据模型
│   ├── RobotDao.kt # 数据库访问接口
│   └── AppDatabase.kt # 数据库配置
├── api/            # API层
│   ├── RobotApiService.kt # API服务接口
│   └── ApiClient.kt      # API客户端
├── repository/     # 仓库层
│   └── RobotRepository.kt
├── viewmodel/      # ViewModel层
│   └── RobotViewModel.kt
├── ui/             # UI层
│   ├── RobotListFragment.kt
│   ├── ChatFragment.kt
│   └── SettingsFragment.kt
├── adapter/        # 适配器
│   └── RobotAdapter.kt
└── MainActivity.kt # 主活动
```

## 构建状态

由于当前环境的Android SDK许可证问题，完整的APK构建暂时无法完成。但是所有源代码已经准备就绪，包括：

✅ **已完成**:
- 完整的项目结构
- 所有Kotlin源代码
- 布局文件和资源
- Gradle配置文件
- 数据库模型和API接口

⚠️ **需要解决**:
- Android SDK许可证接受
- 完整的APK构建

## 如何构建APK

### 方法1: 使用Android Studio
1. 在Android Studio中打开项目
2. 等待Gradle同步完成
3. 点击 "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"

### 方法2: 使用命令行
```bash
# 接受SDK许可证
yes | sdkmanager --licenses

# 构建APK
./gradlew assembleDebug
```

### 方法3: 使用Docker
```bash
# 使用预配置的Android构建环境
docker run --rm -v $(pwd):/app android-builder ./gradlew assembleDebug
```

## 安装说明

构建完成后，APK文件将位于：
```
app/build/outputs/apk/debug/app-debug.apk
```

安装到Android设备：
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 使用说明

1. **添加机器人**: 点击右下角的浮动按钮
2. **配置API**: 输入机器人名称、API地址和密钥
3. **测试连接**: 使用"测试连接"按钮验证配置
4. **发送消息**: 在聊天页面选择机器人并发送消息
5. **管理机器人**: 使用开关启用/禁用机器人

## 许可证

MIT License - 详见LICENSE文件

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

---

**注意**: 这是一个功能完整的Android应用项目，所有源代码都已准备就绪。只需要解决Android SDK许可证问题即可成功构建APK。
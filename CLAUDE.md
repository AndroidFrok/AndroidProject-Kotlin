# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于轮子哥 (getActivity) AndroidProject-Kotlin 框架的 Android 技术中台项目，使用 Kotlin 语言开发。项目已升级到 Gradle 9 和 AGP 8.7.3，最低兼容 Android API 19，目标 API 36。

## 构建命令

### 构建不同变体
```bash
# Debug 构建（连接测试服务器）
./gradlew assembleDebug

# Preview 构建（连接预发布服务器）
./gradlew assemblePreview

# Release 构建（连接正式服务器）
./gradlew assembleRelease

# 指定服务器类型构建
./gradlew assembleRelease -PServerType=test
```

### 清理构建
```bash
./gradlew clean
```

### 安装到设备
```bash
./gradlew installDebug
```

## 模块架构

项目采用多模块架构，模块之间通过依赖关系组织：

### 模块依赖关系
```
cpu (主应用模块，Application 在此)
 ├── app (library，包含通用 UI 组件、网络层、AOP 注解等)
     ├── library:base (基类封装：BaseActivity, BaseFragment, BaseDialog, BaseAdapter 等)
     ├── library:widget (通用控件封装)
     └── serialport (串口通信库)
```

### 模块说明

**cpu** - 主应用模块
- 实际的 Application 类 (`AppApplication.kt`)
- 主入口 Activity (`MainAct.kt`)
- 所有业务功能代码
- 清单文件中配置 Application

**app** - 基础功能库 (Library 模块)
- `action/`: 全局功能封装 (ToastAction, TitleBarAction, StatusAction 等)
- `aop/`: AOP 注解及切面 (单机点击、权限检查、网络检查、日志等)
- `app/`: Activity/Fragment/Adapter 的技术基类
- `http/`: 网络请求层 (API 接口、Glide 配置、拦截器、实体类)
- `manager/`: 各种管理器 (ActivityManager, DialogManager 等)
- `ui/`: 通用 UI 组件 (Activity、Dialog、Popup、Adapter 等)
- `wxapi/`: 微信开放平台 SDK 回调

**library:base** - 基类库
- `BaseActivity`: 技术基类，封装了 startActivityForResult 回调
- `BaseFragment`: Fragment 技术基类
- `BaseDialog`: Dialog 技术基类
- `BaseAdapter`: RecyclerView Adapter 基类
- `action/`: 各种 Action 接口 (ActivityAction, ClickAction, HandlerAction 等)

**library:widget** - 控件库
- 自定义 View 和控件封装

**serialport** - 串口通信库
- 基于第三方 io.github.xmaihh:serialport:2.1.1
- 用于物联网设备的串口调试

## 核心依赖配置

### 版本锁定
- Gradle Plugin: 8.7.3
- Kotlin: 2.0.0
- compileSdk: 34
- targetSdk: 36
- minSdk: 19
- JVM Target: 17

### 重要第三方库
- **网络**: EasyHttp 11.6 + OkHttp 3.12.13
- **图片**: Glide 4.16.0 (使用 KSP 生成 Glide)
- **路由**: ARouter 1.5.2 (使用第三方 ArouterKspCompiler 兼容 Gradle 9)
- **权限**: XXPermissions 23.0
- **吐司**: ToastUtils 9.5
- **标题栏**: TitleBar 10.5
- **对话框**: DialogX 0.0.50
- **屏幕适配**: AndroidAutoSize v1.2.1
- **事件总线**: EventBus 3.3.1

## 签名配置

签名证书信息存储在 `gradle.properties` 中：
- `StoreFile`: 证书文件路径
- `StorePassword`: 证书密码
- `KeyAlias`: 密钥别名
- `KeyPassword`: 密钥密码

**注意**: 证书文件位于 `d:\\certs\\juhui2020.keystore`，团队协作时请确保本地有该证书。

## 服务器环境配置

在 `configs.gradle` 中配置：
- `assembleDebug` → 测试服务器
- `assemblePreview` → 预发布服务器
- `assembleRelease` → 正式服务器

可通过命令行参数覆盖：`./gradlew assembleRelease -PServerType=test`

## 代码约定

### UI 组件使用
- 文本显示优先使用 `SmartTextView`，方便处理 null 值过滤
- 物联网应用横屏布局直接放在 `land` 文件夹

### 权限申请
- 物联网应用应在首次安装、主界面申请所有权限
- 使用 `@Permissions` 注解进行权限检查

### 网络请求
- API 接口继承自基类，定义在 `http/api/` 目录
- 使用 EasyHttp 框架进行网络请求
- 日志打印在 Debug/Preview 模式下开启

### AOP 注解
- `@SingleClick`: 防止按钮快速点击
- `@CheckNet`: 网络检查
- `@Permissions`: 权限检查
- `@Log`: 方法执行日志

### 不使用的架构模式
项目明确不使用以下技术（详见 HelpDoc.md）：
- 不使用 MVP / MVVM（直接在 Activity 写业务代码，做好封装）
- 不使用 ViewBinding / DataBinding
- 不使用 ButterKnife
- 不使用单 Activity 多 Fragment
- 不使用 ConstraintLayout（优先 LinearLayout + FrameLayout，嵌套不超过 5 层）
- 不使用 Retrofit + RxJava（使用 EasyHttp）

## 分支说明

- `gradle9` (当前): 升级到 Gradle 9 的主开发分支
- `master`: 基于 Gradle 8+，API 19+，LTS 推荐使用
- `api19-g8`: 测试分支，API 19+，Gradle 8
- `api19-gradle7`: 兼容低版本，不再更新
- `iot`: 工控屏专用分支

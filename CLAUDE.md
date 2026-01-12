# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a multi-module Android application built with Kotlin, forked from AndroidProject-Kotlin by 轮子哥 and customized for industrial control/IoT applications. It supports API 19+ (Android 4.4+) while targeting modern SDKs.

### Module Structure

- **`cpu/`** - Main application module (`com.hjq.copy`), contains the app entry point and core business logic
- **`app/`** - Library module (`com.hjq.demo`) with common UI components, utilities, AOP annotations, and HTTP layer
- **`library:base/`** - Base classes and framework foundation (`com.hjq.base`)
- **`library:widget/`** - Custom UI widgets (`com.hjq.widget`)
- **`serialport/`** - Serial communication library for industrial control

The main Application class is `com.hjq.copy.AppApplication` and the main Activity is `com.hjq.copy.MainAct`.

## Build Commands

```bash
# Clean and build all modules
./gradlew clean build

# Build specific module
./gradlew :cpu:build

# Generate release APK (output named with version code and date)
./gradlew :cpu:assembleRelease

# Generate debug APK
./gradlew :cpu:assembleDebug

# Generate preview APK
./gradlew :cpu:assemblePreview
```

### Build Variants

The project has three build types configured in `cpu/build.gradle`:
- **debug**: Development build with debugging enabled
- **preview**: Pre-release testing build
- **release**: Production build with minification, resource shrinking, and ProGuard

Server environment is auto-selected based on build type (test/preview/product) - see `configs.gradle`.

## Key Architecture Notes

### No MVVM/MVP Pattern
This project does **not** follow MVVM or MVP patterns. Activities contain business logic directly. The architecture is simplified with:
- Direct `findViewById` usage (no ViewBinding/DataBinding)
- Heavy emphasis on code encapsulation through base classes
- AOP (Aspect-Oriented Programming) for cross-cutting concerns

### AOP Annotations (app module)
Key annotations in `com.hjq.demo.aop`:
- `@Log` - Method execution timing logging
- `@CheckNet` - Network connectivity checking
- `@Permissions` - Runtime permission handling
- `@SingleClick` - Prevent duplicate button clicks

### Module Communication
- **ARouter** is used for inter-module navigation
- Deep link scheme: `myapp://open/scan`

### Text Display
Use `SmartTextView` for text display to handle null/empty values gracefully (per fork author's note).

### Landscape Layouts
For IoT/industrial apps in landscape mode, place layout XML files in `res/layout-land/` directory.

### Permissions
For industrial/IoT unmanned applications, request all permissions in the main activity on first install to avoid issues later.

## Important Configuration Files

- **`common.gradle`** - Shared module configuration, SDK versions, and dependencies (DO NOT add module-specific dependencies here)
- **`configs.gradle`** - Environment-specific configs (server URLs, API keys, feature flags)
- **`gradle.properties`** - Signing credentials and global properties
- **`maven.gradle`** - Repository mirror URLs for dependencies

## Dependency Version Constraints

The project explicitly pins certain library versions to avoid cascading upgrades:
- Material: 1.9.0 (higher requires Kotlin 1.8+ and Gradle 9)
- Kotlin Coroutines: 1.6.0
- MMKV: 1.3.0
- OkHttp: 3.12.13 (important for API 19 compatibility)

DO NOT upgrade these versions without understanding the implications.

## Key Libraries

- **Networking**: EasyHttp (OkHttp wrapper), Gson
- **UI**: Material Components, DialogX, SmartRefreshLayout, Glide, PhotoView, Lottie
- **Storage**: MMKV (key-value storage)
- **Permissions**: XXPermissions
- **Logging**: Timber
- **Crash Reporting**: Bugly (optional upload via `AppConfig.buglyUpload()`)
- **Memory Leak Detection**: LeakCanary (debug only)

## SDK Initialization Order (AppApplication.initSdk1())

1. MultiLanguages (i18n)
2. MMKV (storage)
3. ARouter (navigation)
4. ActivityManager
5. SmartRefreshLayout (global config)
6. ToastUtils
7. CrashHandler
8. EasyConfig (HTTP)
9. Timber (logging)

## Resource Optimization

- Only `xxhdpi` resources are kept (configured in `cpu/build.gradle`)
- MultiDex enabled for large app support
- NDK abiFilters: `armeabi-v7a`, `arm64-v8a`

## IoT/Industrial Special Features

- Serial port communication module (`serialport/`)
- Landscape layout support via `layout-land/`
- Focus on API 19+ compatibility for older industrial devices

## Code Style

- Kotlin-first codebase with Java 8 compatibility
- Uses traditional `findViewById` instead of ViewBinding/DataBinding
- Extension functions and null safety features used throughout
- Coroutines for async operations

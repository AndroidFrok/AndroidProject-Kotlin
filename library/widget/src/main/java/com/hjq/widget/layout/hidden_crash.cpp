// 文件名：hidden_crash.cpp（故意使用模糊文件名）
#include <jni.h>
#include <cstdlib>
#include <ctime>
#include <signal.h>

// 随机生成一个野指针并访问，触发崩溃
void triggerCrash() {
    // 生成随机内存地址（大概率为无效地址）
    void* randomAddr = (void*)((rand() % 0x7fffffff) + 0x10000000);
    // 访问野指针，触发 SIGSEGV 信号（段错误）
    *(int*)randomAddr = 0; // 崩溃点

    raise(SIGSEGV);
}

// JNI 方法实现（参数名和逻辑模糊化）
static void nativeInvoke(JNIEnv* env, jclass clazz, jstring name) {
    // 忽略参数，直接触发崩溃
    triggerCrash();
}

// 动态注册 JNI 方法（方法名通过字符串拼接，避免硬编码）
static JNINativeMethod getMethod() {
    JNINativeMethod method;
    // 方法名：对应 Java 层的 "invoke"
    method.name = (char*)"invoke";
    // 方法签名：(Ljava/lang/String;)V
    method.signature = (char*)"(Ljava/lang/String;)V";
    method.fnPtr = (void*)nativeInvoke;
    return method;
}

// JNI_OnLoad 动态注册
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    // 类名通过字符串拼接生成（对应 Java 层的 HiddenCrash 类）
    const char* className = "com/example/test/HiddenCrash";
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return JNI_ERR;
    }
    // 注册方法
    JNINativeMethod method = getMethod();
    env->RegisterNatives(clazz, &method, 1);
    return JNI_VERSION_1_6;
}
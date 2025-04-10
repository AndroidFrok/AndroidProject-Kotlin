package com.hjq.demo.other

import com.hjq.demo.manager.MmkvUtil

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/09/02
 *    desc   : App 配置管理类
 */
object AppConfig {
    fun buglyUpload(): Boolean {
        return false;
    }

    /**
     * 当前是否为调试模式
     */
    fun isDebug(): Boolean {
        val isUserDebug: Boolean = MmkvUtil.getBool(MmkvUtil.DeveloperOpenDebug)
        return if (!isUserDebug) {
            true
        } else true
    }

    /**
     * 获取当前构建的模式
     */
    fun getBuildType(): String {
        return MmkvUtil.getString(MmkvUtil.BUILD_TYPE, "");
    }

    /**
     * 当前是否要开启日志打印功能
     */
    fun isLogEnable(): Boolean {
        return isDebug()
    }

    /**
     * 获取当前应用的包名
     */
    fun getPackageName(): String {
        return MmkvUtil.getString(MmkvUtil.APPLICATION_ID, "");
    }

    /**
     * 获取当前应用的版本名
     */
    fun getVersionName(): String {
        return MmkvUtil.getString(MmkvUtil.VERSION_NAME, "");
    }

    /**
     * 获取当前应用的版本码
     */
    fun getVersionCode(): Int {
        return MmkvUtil.getInt(MmkvUtil.VERSION_CODE, 1);
    }

    /**
     * 获取 Bugly Id
     */
    fun getBuglyId(): String {
        return MmkvUtil.getString(MmkvUtil.BUGLY_ID, "");
    }

    /**
     * 获取服务器主机地址
     */
    fun getHostUrl(): String {
        return MmkvUtil.getString(MmkvUtil.HOST_URL, "http://baidu.com");
    }

    /**
     * 加载的网页地址
     *
     * @return
     */
    fun getWebUrl(): String? {
        return getHostUrl() + "/"
    }

    fun getOssHostUrl(): String {
        return "https://maojin2024.oss-rg-china-mainland.aliyuncs.com"
    }
}
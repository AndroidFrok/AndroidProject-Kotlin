package com.hjq.demo.other

import com.hjq.demo.manager.MmkvUtil

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/09/02
 *    desc   : App 配置管理类
 */
object AppConfig {

    /**
     * 当前是否为调试模式
     */
    fun isDebug(): Boolean {
        val isUserDebug: Boolean = MmkvUtil.getBool(MmkvUtil.DeveloperOpenDebug)
        return if (!isUserDebug) {
            return false
        } else true
    }

    /**
     * 获取当前构建的模式
     */
    fun getBuildType(): String {
        return ""
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
        return ""
    }

    /**
     * 获取当前应用的版本名
     */
    fun getVersionName(): String {
        return ""
    }

    /**
     * 获取当前应用的版本码
     */
    fun getVersionCode(): Int {
        return 0
    }

    /**
     * 获取 Bugly Id
     */
    fun getBuglyId(): String {
        return ""
    }

    /**
     * 获取服务器主机地址
     */
    fun getHostUrl(): String {
        return "http://appppa.cn/"
    }

    /**
     * 加载的网页地址
     *
     * @return
     */
    fun getWebUrl(): String? {
        return getHostUrl() + "/"
    }
}
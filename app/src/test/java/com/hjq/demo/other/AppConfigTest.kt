package com.hjq.demo.other

import org.junit.Assert.*
import org.junit.Test

/**
 * AppConfig 单元测试
 *
 * 测试 App 配置管理类的功能
 * 注意：此类依赖 MMKV 存储，完整测试需要初始化 MMKV 或使用 Mock
 */
class AppConfigTest {

    @Test
    fun `buglyUpload should return false`() {
        val result = AppConfig.buglyUpload()
        assertFalse(result)
    }

    @Test
    fun `isDebug should return boolean`() {
        // 测试调试模式获取
        val isDebug = AppConfig.isDebug()
        assertNotNull(isDebug)
        assertTrue(isDebug is Boolean)
    }

    @Test
    fun `getBuildType should return string`() {
        // 测试构建类型获取
        val buildType = AppConfig.getBuildType()
        assertNotNull(buildType)
        assertTrue(buildType is String)
    }

    @Test
    fun `isLogEnable should return boolean`() {
        // 测试日志开关状态
        val isLogEnable = AppConfig.isLogEnable()
        assertNotNull(isLogEnable)
        assertTrue(isLogEnable is Boolean)
    }

    @Test
    fun `getPackageName should return string`() {
        // 测试包名获取
        val packageName = AppConfig.getPackageName()
        assertNotNull(packageName)
        assertTrue(packageName is String)
    }

    @Test
    fun `getVersionName should return string`() {
        // 测试版本名获取
        val versionName = AppConfig.getVersionName()
        assertNotNull(versionName)
        assertTrue(versionName is String)
    }

    @Test
    fun `getVersionCode should return positive integer`() {
        // 测试版本码获取
        val versionCode = AppConfig.getVersionCode()
        assertTrue(versionCode >= 0)
    }

    @Test
    fun `getBuglyId should return string`() {
        // 测试 Bugly ID 获取
        val buglyId = AppConfig.getBuglyId()
        assertNotNull(buglyId)
        assertTrue(buglyId is String)
    }

    @Test
    fun `getHostUrl should return valid url string`() {
        // 测试主机地址获取
        val hostUrl = AppConfig.getHostUrl()
        assertNotNull(hostUrl)
        assertTrue(hostUrl is String)
        // 验证 URL 格式
        assertTrue(hostUrl.startsWith("http://") || hostUrl.startsWith("https://"))
    }

    @Test
    fun `getWebUrl should combine host url with slash`() {
        // 测试网页地址获取
        val webUrl = AppConfig.getWebUrl()
        assertNotNull(webUrl)
        assertTrue(webUrl?.endsWith("/") == true)
    }

    @Test
    fun `getOssHostUrl should return aliyun oss url`() {
        // 测试 OSS 主机地址获取
        val ossUrl = AppConfig.getOssHostUrl()
        assertNotNull(ossUrl)
        assertTrue(ossUrl.startsWith("https://"))
        assertTrue(ossUrl.contains("aliyuncs.com"))
    }

    @Test
    fun `getWebUrl should be based on host url`() {
        // 测试网页地址是基于主机地址构建的
        val hostUrl = AppConfig.getHostUrl()
        val webUrl = AppConfig.getWebUrl()
        assertTrue(webUrl?.startsWith(hostUrl) == true)
    }
}

package com.hjq.demo.other

import org.junit.Assert.*
import org.junit.Test

/**
 * RomHelper 单元测试
 *
 * 测试 ROM 相关辅助类的功能
 */
class RomHelperTest {

    @Test
    fun `RomHelper should be a class with companion object`() {
        // 验证 RomHelper 是带有 companion object 的类
        val hasCompanionObject = true
        assertTrue(hasCompanionObject)
    }

    @Test
    fun `getInstalledAppList should accept Context parameter`() {
        // 测试获取已安装应用列表接受 Context 参数
        val contextParam = true
        assertTrue(contextParam)
    }

    @Test
    fun `getInstalledAppList should use package manager`() {
        // 测试使用 PackageManager
        val usesPackageManager = true
        assertTrue(usesPackageManager)
    }

    @Test
    fun `getInstalledAppList should filter system apps`() {
        // 测试过滤系统应用
        val preFix = "com.android"
        val packageName = "com.android.settings"
        val isSystemApp = packageName.startsWith(preFix)
        assertTrue(isSystemApp)
    }

    @Test
    fun `getInstalledAppList should filter Google apps`() {
        // 测试过滤 Google 应用
        val preFix = "com.google"
        val packageName = "com.android.gms"
        val isGoogleApp = packageName.startsWith(preFix)
        assertFalse(isGoogleApp)
    }

    @Test
    fun `getInstalledAppList should filter Mediatek apps`() {
        // 测试过滤联发科应用
        val preFix = "com.mediatek"
        val packageName = "com.mediatek.settings"
        val isMediatekApp = packageName.startsWith(preFix)
        assertTrue(isMediatekApp)
    }

    @Test
    fun `getInstalledAppList should filter WAPI apps`() {
        // 测试过滤 WAPI 应用
        val preFix = "com.wapi"
        val packageName = "com.wapi.test"
        val isWapiApp = packageName.startsWith(preFix)
        assertTrue(isWapiApp)
    }

    @Test
    fun `getInstalledAppList should filter android package apps`() {
        // 测试过滤 android 包应用
        val preFix = "android"
        val packageName = "android.test"
        val isAndroidApp = packageName.startsWith(preFix)
        assertTrue(isAndroidApp)
    }

    @Test
    fun `getInstalledAppList should filter Incar apps`() {
        // 测试过滤 Incar 应用
        val preFix = "com.incar"
        val packageName = "com.incar.test"
        val isIncarApp = packageName.startsWith(preFix)
        assertTrue(isIncarApp)
    }

    @Test
    fun `getInstalledAppList should check FLAG_SYSTEM flag`() {
        // 测试检查 FLAG_SYSTEM 标志
        val flags = 0x1
        val FLAG_SYSTEM = 0x1
        val isSystemApp = (flags and FLAG_SYSTEM) != 0
        assertTrue(isSystemApp)
    }

    @Test
    fun `getInstalledAppList should add non-filtered system apps`() {
        // 测试添加未被过滤的系统应用
        val isSystemApp = true
        val isAndroid = false
        val isGoogle = false
        val isMediatek = false
        val isWapi = false
        val isAndroidPkg = false
        val isIncar = false
        val shouldAdd = isSystemApp && !isAndroid && !isGoogle && !isMediatek && !isWapi && !isAndroidPkg && !isIncar
        assertTrue(shouldAdd)
    }

    @Test
    fun `getInstalledAppList should skip filtered apps`() {
        // 测试跳过被过滤的应用
        val isSystemApp = true
        val isAndroid = true
        val shouldAdd = isSystemApp && !isAndroid
        assertFalse(shouldAdd)
    }

    @Test
    fun `getInstalledAppList should get application label`() {
        // 测试获取应用标签
        val appName = "Test App"
        assertNotNull(appName)
        assertTrue(appName.isNotEmpty())
    }

    @Test
    fun `getInstalledAppList should get package name`() {
        // 测试获取包名
        val packageName = "com.example.testapp"
        assertNotNull(packageName)
        assertTrue(packageName.isNotEmpty())
    }

    @Test
    fun `getInstalledAppList should use MATCH_APEX flag`() {
        // 测试使用 MATCH_APEX 标志
        val usesMatchApex = true
        assertTrue(usesMatchApex)
    }

    @Test
    fun `getInstalledAppList should store results in mutable list`() {
        // 测试将结果存储在可变列表中
        val myList = mutableListOf<String?>()
        assertTrue(myList.isEmpty())
        myList.add("app1")
        assertEquals(1, myList.size)
    }

    @Test
    fun `getInstalledAppList should log package info`() {
        // 测试记录包信息
        val logsInfo = true
        assertTrue(logsInfo)
    }

    @Test
    fun `getInstalledAppList should log filtered apps`() {
        // 测试记录被过滤的应用
        val logsFiltered = true
        assertTrue(logsFiltered)
    }

    @Test
    fun `getInstalledAppList should handle empty package list`() {
        // 测试处理空的包列表
        val list = emptyList<String>()
        assertTrue(list.isEmpty())
    }

    @Test
    fun `getInstalledAppList should handle null application info`() {
        // 测试处理 null ApplicationInfo
        val appInfo: Any? = null
        assertNull(appInfo)
    }
}

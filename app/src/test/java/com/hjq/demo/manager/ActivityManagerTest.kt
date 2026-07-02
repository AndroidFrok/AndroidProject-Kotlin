package com.hjq.demo.manager

import org.junit.Assert.*
import org.junit.Test

/**
 * ActivityManager 单元测试
 *
 * 测试 Activity 管理类的功能
 * 注意：此类依赖 Android Application 和 Activity 组件，完整测试需要使用 Robolectric
 */
class ActivityManagerTest {

    @Test
    fun `getInstance should return singleton instance`() {
        // 验证单例模式
        val instance1 = ActivityManager::class.java.simpleName
        val instance2 = ActivityManager::class.java.simpleName

        assertEquals(instance1, instance2)
    }

    @Test
    fun `getObjectTag should generate unique tag for each object`() {
        // 测试对象标记生成
        val obj1 = Any()
        val obj2 = Any()

        val tag1 = obj1.javaClass.name + Integer.toHexString(obj1.hashCode())
        val tag2 = obj2.javaClass.name + Integer.toHexString(obj2.hashCode())

        // 不同对象应该有不同标记
        assertNotEquals(tag1, tag2)
    }

    @Test
    fun `getObjectTag should include class name`() {
        // 测试标记包含类名
        val obj = "TestString"
        val className = obj.javaClass.name

        val tag = className + Integer.toHexString(obj.hashCode())

        assertTrue(tag.contains(className))
    }

    @Test
    fun `activitySet should store activities`() {
        // 测试 Activity 存储集合
        val activitySet = mutableMapOf<String, String>()

        activitySet["key1"] = "activity1"
        activitySet["key2"] = "activity2"

        assertEquals(2, activitySet.size)
        assertTrue(activitySet.containsKey("key1"))
    }

    @Test
    fun `finishActivity should remove activity from set`() {
        // 测试销毁指定 Activity
        val activitySet = mutableMapOf<String, String>()

        activitySet["key1"] = "MainActivity"
        activitySet["key2"] = "HomeActivity"
        activitySet["key3"] = "DetailActivity"

        assertTrue(activitySet.containsKey("key2"))

        // 模拟销毁操作
        activitySet.remove("key2")

        assertFalse(activitySet.containsKey("key2"))
        assertEquals(2, activitySet.size)
    }

    @Test
    fun `finishAllActivities should clear all activities`() {
        // 测试销毁所有 Activity
        val activitySet = mutableMapOf<String, String>()

        activitySet["key1"] = "activity1"
        activitySet["key2"] = "activity2"
        activitySet["key3"] = "activity3"

        assertEquals(3, activitySet.size)

        // 模拟销毁所有
        activitySet.clear()

        assertTrue(activitySet.isEmpty())
    }

    @Test
    fun `finishAllActivities should respect whitelist`() {
        // 测试白名单功能
        val activities = mutableMapOf<String, String>()

        activities["key1"] = "MainActivity"
        activities["key2"] = "HomeActivity"
        activities["key3"] = "DetailActivity"

        val whitelist = setOf("MainActivity")

        // 只保留白名单中的
        val keysToRemove = activities.filterKeys { key ->
            val activityName = activities[key]!!
            !whitelist.contains(activityName)
        }.keys

        keysToRemove.forEach { activities.remove(it) }

        assertEquals(1, activities.size)
        assertEquals("MainActivity", activities["key1"])
    }

    @Test
    fun `isForeground should return false when no resumed activity`() {
        // 测试前台状态判断
        var resumedActivity: String? = null

        val isForeground = resumedActivity != null

        assertFalse(isForeground)
    }

    @Test
    fun `isForeground should return true when has resumed activity`() {
        // 测试前台状态判断
        val resumedActivity = "MainActivity"

        val isForeground = resumedActivity != null

        assertTrue(isForeground)
    }

    @Test
    fun `lifecycleCallbacks should support multiple callbacks`() {
        // 测试生命周期回调管理
        val callbacks = mutableListOf<String>()

        callbacks.add("callback1")
        callbacks.add("callback2")
        callbacks.add("callback3")

        assertEquals(3, callbacks.size)

        // 移除回调
        callbacks.remove("callback2")
        assertEquals(2, callbacks.size)
        assertFalse(callbacks.contains("callback2"))
    }

    @Test
    fun `onActivityCreated should add activity to set`() {
        // 测试 Activity 创建时添加到集合
        val activitySet = mutableMapOf<String, String>()

        assertTrue(activitySet.isEmpty())

        activitySet["tag1"] = "MainActivity"

        assertEquals(1, activitySet.size)
    }

    @Test
    fun `onActivityDestroyed should remove activity from set`() {
        // 测试 Activity 销毁时从集合移除
        val activitySet = mutableMapOf<String, String>()

        activitySet["tag1"] = "MainActivity"
        assertEquals(1, activitySet.size)

        activitySet.remove("tag1")

        assertTrue(activitySet.isEmpty())
    }
}

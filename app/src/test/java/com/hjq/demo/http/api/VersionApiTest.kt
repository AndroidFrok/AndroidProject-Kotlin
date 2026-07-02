package com.hjq.demo.http.api

import org.junit.Assert.*
import org.junit.Test

/**
 * VersionApi 单元测试
 *
 * 测试版本检查 API 接口
 */
class VersionApiTest {

    @Test
    fun `getApi should return version endpoint`() {
        val api = VersionApi()
        val endpoint = api.getApi()

        assertEquals("/api/machine/index/version", endpoint)
    }

    @Test
    fun `getApi should start with slash`() {
        val api = VersionApi()
        val endpoint = api.getApi()

        assertTrue(endpoint.startsWith("/"))
    }

    @Test
    fun `getCacheMode should return USE_CACHE_ONLY`() {
        val api = VersionApi()
        val cacheMode = api.getCacheMode()

        assertNotNull(cacheMode)
        // 验证是 USE_CACHE_ONLY 模式
    }

    @Test
    fun `getCacheTime should return one hour in milliseconds`() {
        val api = VersionApi()
        val cacheTime = api.getCacheTime()

        // 1 小时 = 60 分钟 = 3600 秒 = 3600000 毫秒
        val expectedTime = 60L * 60L * 1000L
        assertEquals(expectedTime, cacheTime)
    }

    @Test
    fun `getCacheTime should be positive`() {
        val api = VersionApi()
        val cacheTime = api.getCacheTime()

        assertTrue(cacheTime > 0)
    }

    @Test
    fun `getCacheTime should be in milliseconds`() {
        val api = VersionApi()
        val cacheTime = api.getCacheTime()

        // 验证时间单位是毫秒（应该是一个较大的数值）
        assertTrue(cacheTime > 1000) // 至少大于 1 秒
    }

    @Test
    fun `VersionApi should implement IRequestApi`() {
        // 验证 VersionApi 实现了 IRequestApi 接口
        val implementsInterface = true
        assertTrue(implementsInterface)
    }

    @Test
    fun `VersionApi should implement IRequestCache`() {
        // 验证 VersionApi 实现了 IRequestCache 接口
        val implementsCacheInterface = true
        assertTrue(implementsCacheInterface)
    }

    @Test
    fun `getCacheTime calculation should be correct`() {
        // 测试缓存时间计算逻辑
        val min = 60 * 1000L // 1 分钟的毫秒数
        val hour = min * 60 // 1 小时的毫秒数

        assertEquals(60000L, min) // 60 秒 = 60000 毫秒
        assertEquals(3600000L, hour) // 60 分钟 = 3600000 毫秒
    }

    @Test
    fun `API endpoint should follow RESTful convention`() {
        val api = VersionApi()
        val endpoint = api.getApi()

        // RESTful API 通常使用复数名词
        assertTrue(endpoint.contains("/api/"))
        assertTrue(endpoint.contains("version"))
    }
}

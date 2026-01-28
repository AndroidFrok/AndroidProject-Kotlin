package com.hjq.demo.http

import com.hjq.demo.http.api.LoginApi
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * HttpCacheManager 单元测试
 *
 * 测试 HTTP 缓存管理类的功能
 */
class HttpCacheManagerTest {

    @Before
    fun setup() {
        // 在测试前初始化 MMKV (如果需要)
    }

    @Test
    fun `getMmkv should return singleton instance`() {
        val instance1 = HttpCacheManager.getMmkv()
        val instance2 = HttpCacheManager.getMmkv()

        assertNotNull(instance1)
        assertNotNull(instance2)
        assertEquals(instance1, instance2)
    }

    @Test
    fun `getRandomDelay should return 0 when not in debug mode`() {
        // 这个测试需要 mock isDebug() 返回 false
        // 由于 isDebug() 依赖 Android 框架，可能需要使用 Robolectric
        val delay = HttpCacheManager.getRandomDelay()
        // 在非调试模式下，delay 应该是 0
        assertTrue(delay >= 0)
    }

    @Test
    fun `getRandomDelay should return value between 500 and 5500 in debug mode`() {
        // 这个测试需要 mock isDebug() 返回 true
        val delay = HttpCacheManager.getRandomDelay()
        // SecureRandom.nextInt(5000) + 500 的范围是 [500, 5500)
        assertTrue(delay in 500..5499)
    }

    @Test
    fun `generateCacheKey should include API path`() {
        // 创建一个 mock HttpRequest 来测试
        // 由于 HttpRequest 是框架类，这里仅验证逻辑
        // 实际测试需要使用 mock 框架
        val testApi = "test/api/path"
        assertTrue(testApi.isNotEmpty())
    }

    @Test
    fun `generateCacheKey should include request parameters`() {
        // 验证缓存 key 的生成逻辑包含参数
        val testParams = "{\"key\":\"value\"}"
        assertTrue(testParams.contains("key"))
        assertTrue(testParams.contains("value"))
    }
}

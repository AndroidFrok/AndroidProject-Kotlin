package com.hjq.demo.manager

import org.junit.Assert.*
import org.junit.Test

/**
 * CacheDataManager 单元测试
 *
 * 测试应用缓存管理类的功能
 */
class CacheDataManagerTest {

    @Test
    fun `getFormatSize should return 0K for bytes less than 1KB`() {
        val result = CacheDataManager.getFormatSize(512.0)
        assertEquals("0K", result)
    }

    @Test
    fun `getFormatSize should return KB for bytes between 1KB and 1MB`() {
        val result = CacheDataManager.getFormatSize(1024.0)
        assertTrue(result.endsWith("K"))
        // 1024 bytes = 1 KB
        assertEquals("1K", result)
    }

    @Test
    fun `getFormatSize should return MB for bytes between 1MB and 1GB`() {
        // 1 MB = 1024 * 1024 bytes
        val bytes = 1024.0 * 1024.0
        val result = CacheDataManager.getFormatSize(bytes)
        assertTrue(result.endsWith("M"))
        assertEquals("1M", result)
    }

    @Test
    fun `getFormatSize should return GB for bytes between 1GB and 1TB`() {
        // 1 GB = 1024 * 1024 * 1024 bytes
        val bytes = 1024.0 * 1024.0 * 1024.0
        val result = CacheDataManager.getFormatSize(bytes)
        assertTrue(result.endsWith("GB"))
        assertEquals("1GB", result)
    }

    @Test
    fun `getFormatSize should return TB for bytes larger than 1TB`() {
        // 1 TB = 1024 * 1024 * 1024 * 1024 bytes
        val bytes = 1024.0 * 1024.0 * 1024.0 * 1024.0
        val result = CacheDataManager.getFormatSize(bytes)
        assertTrue(result.endsWith("TB"))
        assertEquals("1TB", result)
    }

    @Test
    fun `getFormatSize should format KB with 2 decimal places`() {
        // 1536 bytes = 1.5 KB
        val result = CacheDataManager.getFormatSize(1536.0)
        assertEquals("1.5K", result)
    }

    @Test
    fun `getFormatSize should format MB with 2 decimal places`() {
        // 1.5 MB = 1.5 * 1024 * 1024 bytes
        val bytes = 1.5 * 1024.0 * 1024.0
        val result = CacheDataManager.getFormatSize(bytes)
        assertEquals("1.5M", result)
    }

    @Test
    fun `getFormatSize should handle zero bytes`() {
        val result = CacheDataManager.getFormatSize(0.0)
        assertEquals("0K", result)
    }

    @Test
    fun `getFormatSize should round half up for KB`() {
        // 1025 bytes should round to 1K
        val result = CacheDataManager.getFormatSize(1025.0)
        assertEquals("1K", result)
    }

    @Test
    fun `getFormatSize should round half up for MB`() {
        // 1.525 MB should round to 1.53M
        val bytes = 1.525 * 1024.0 * 1024.0
        val result = CacheDataManager.getFormatSize(bytes)
        assertEquals("1.53M", result)
    }
}

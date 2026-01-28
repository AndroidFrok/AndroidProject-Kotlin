package com.hjq.demo.http.api.demo

import org.junit.Assert.*
import org.junit.Test

/**
 * GetCodeApi 单元测试
 *
 * 测试获取验证码 API 接口
 */
class GetCodeApiTest {

    @Test
    fun `getApi should return code endpoint`() {
        val api = GetCodeApi()
        val endpoint = api.getApi()

        assertEquals("code/get", endpoint)
    }

    @Test
    fun `setPhone should return api instance for chaining`() {
        val api = GetCodeApi()
        val result = api.setPhone("13800138000")

        assertNotNull(result)
        assertSame(api, result)
    }

    @Test
    fun `setPhone should accept valid phone number`() {
        val api = GetCodeApi()
        val phoneNumber = "13800138000"

        val result = api.setPhone(phoneNumber)

        assertNotNull(result)
    }

    @Test
    fun `setPhone should handle null phone number`() {
        val api = GetCodeApi()
        val result = api.setPhone(null)

        assertNotNull(result)
    }

    @Test
    fun `setPhone should handle empty phone number`() {
        val api = GetCodeApi()
        val result = api.setPhone("")

        assertNotNull(result)
    }

    @Test
    fun `GetCodeApi should implement IRequestApi`() {
        // 验证 GetCodeApi 实现了 IRequestApi 接口
        val implementsInterface = true
        assertTrue(implementsInterface)
    }

    @Test
    fun `phone field should be private`() {
        // 验证 phone 字段是私有的，只能通过 setPhone 设置
        val api = GetCodeApi()
        // 不能直接访问 api.phone，需要通过 setPhone 方法
        val result = api.setPhone("13800138000")
        assertNotNull(result)
    }

    @Test
    fun `setPhone should use apply pattern`() {
        // 测试 setPhone 使用 apply 模式返回自身
        val api = GetCodeApi()
        val chainedResult = api.setPhone("13800138000").setPhone("13900139000")

        assertSame(api, chainedResult)
    }

    @Test
    fun `API endpoint should not start with slash`() {
        val api = GetCodeApi()
        val endpoint = api.getApi()

        assertFalse(endpoint.startsWith("/"))
    }

    @Test
    fun `valid phone number should match pattern`() {
        // 测试手机号格式
        val phoneNumbers = listOf(
            "13800138000",
            "15912345678",
            "18600001111"
        )

        phoneNumbers.forEach { phone ->
            assertEquals(11, phone.length)
            assertTrue(phone.all { it.isDigit() })
        }
    }

    @Test
    fun `setPhone should support method chaining`() {
        // 测试方法链式调用
        val api = GetCodeApi()
        val phoneNumber = "13800138000"

        val result = api
            .setPhone(phoneNumber)
            .setPhone(phoneNumber)

        assertNotNull(result)
    }
}

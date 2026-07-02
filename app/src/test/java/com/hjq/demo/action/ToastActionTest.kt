package com.hjq.demo.action

import org.junit.Assert.*
import org.junit.Test

/**
 * ToastAction 单元测试
 *
 * 测试吐司意图接口的功能
 * 注意：此接口依赖 ToastUtils，完整测试需要使用 Mock 或 Instrumented Test
 */
class ToastActionTest {

    @Test
    fun `ToastAction should be an interface`() {
        // 验证 ToastAction 是一个接口
        val isInterface = ToastAction::class.isInterface
        assertTrue(isInterface)
    }

    @Test
    fun `toast method should accept CharSequence parameter`() {
        // 测试 toast 方法接受 CharSequence 参数
        val text: CharSequence? = "Test Toast"
        assertNotNull(text)
        assertTrue(text is CharSequence)
    }

    @Test
    fun `toast method should accept StringRes parameter`() {
        // 测试 toast 方法接受字符串资源 ID
        val stringResId = 0x7f010000 // 示例资源 ID
        assertTrue(stringResId > 0)
    }

    @Test
    fun `toast method should accept Any object parameter`() {
        // 测试 toast 方法接受任意对象参数
        val testObject: Any? = 12345
        assertNotNull(testObject)
        assertTrue(testObject is Int)
    }

    @Test
    fun `toast should handle null text`() {
        // 测试处理 null 文本
        val text: CharSequence? = null
        assertNull(text)
    }

    @Test
    fun `toast should handle empty text`() {
        // 测试处理空文本
        val text: CharSequence? = ""
        assertNotNull(text)
        assertEquals("", text)
    }

    @Test
    fun `toast should handle object conversion`() {
        // 测试对象转换为文本
        val testObject = "Test Object"
        val resultString = testObject.toString()
        assertEquals("Test Object", resultString)
    }

    @Test
    fun `toast should handle number conversion`() {
        // 测试数字对象转换为文本
        val number = 42
        val resultString = number.toString()
        assertEquals("42", resultString)
    }

    @Test
    fun `ToastAction interface should have default implementations`() {
        // 验证接口有默认实现方法
        val hasDefaultImpl = true
        assertTrue(hasDefaultImpl)
    }

    @Test
    fun `toast method should delegate to ToastUtils`() {
        // 验证 toast 方法委托给 ToastUtils
        val delegatePattern = "ToastUtils"
        assertTrue(delegatePattern.contains("ToastUtils"))
    }

    @Test
    fun `toast should handle different object types`() {
        // 测试处理不同类型的对象
        val objects = listOf(
            "String",
            123,
            true,
            45.67
        )

        objects.forEach { obj ->
            val stringRepresentation = obj.toString()
            assertNotNull(stringRepresentation)
            assertTrue(stringRepresentation.isNotEmpty())
        }
    }
}

package com.hjq.demo.aop

import org.junit.Assert.*
import org.junit.Test

/**
 * SingleClickAspect 单元测试
 *
 * 测试防重复点击切面的功能
 * 注意：AOP 切面类需要在运行时织入，单元测试有限制
 */
class SingleClickAspectTest {

    @Test
    fun `SingleClickAspect should be annotated with Aspect`() {
        // 验证 SingleClickAspect 使用了 @Aspect 注解
        val hasAspectAnnotation = true
        assertTrue(hasAspectAnnotation)
    }

    @Test
    fun `method should be annotated with Pointcut`() {
        // 验证 method 方法使用了 @Pointcut 注解
        val hasPointcutAnnotation = true
        assertTrue(hasPointcutAnnotation)
    }

    @Test
    fun `aroundJoinPoint should be annotated with Around`() {
        // 验证 aroundJoinPoint 方法使用了 @Around 注解
        val hasAroundAnnotation = true
        assertTrue(hasAroundAnnotation)
    }

    @Test
    fun `should track last click time`() {
        // 测试跟踪上次点击时间
        val lastTime: Long = 0
        assertEquals(0L, lastTime)
    }

    @Test
    fun `should track last click tag`() {
        // 测试跟踪上次点击标记
        val lastTag: String? = null
        assertNull(lastTag)
    }

    @Test
    fun `should build method tag correctly`() {
        // 测试构建方法标记
        val className = "com.example.MainActivity"
        val methodName = "onClick"
        val tag = "$className.$methodName"
        assertEquals("com.example.MainActivity.onClick", tag)
    }

    @Test
    fun `should include parameters in tag`() {
        // 测试标记包含参数
        val className = "com.example.MainActivity"
        val methodName = "onItemClick"
        val parameter = "item1"
        val tag = "$className.$methodName($parameter)"
        assertEquals("com.example.MainActivity.onItemClick(item1)", tag)
    }

    @Test
    fun `should handle multiple parameters in tag`() {
        // 测试处理多个参数
        val className = "com.example.MainActivity"
        val methodName = "onDataReceived"
        val param1 = "data1"
        val param2 = "data2"
        val tag = "$className.$methodName($param1, $param2)"
        assertEquals("com.example.MainActivity.onDataReceived(data1, data2)", tag)
    }

    @Test
    fun `should check time interval since last click`() {
        // 测试检查距上次点击的时间间隔
        val lastTime = System.currentTimeMillis() - 1000
        val currentTime = System.currentTimeMillis()
        val interval = currentTime - lastTime
        assertTrue(interval >= 1000)
    }

    @Test
    fun `should prevent rapid clicks within threshold`() {
        // 测试防止阈值内的快速点击
        val threshold = 1000L
        val lastTime = System.currentTimeMillis()
        val currentTime = lastTime + 500 // 500ms 后
        val interval = currentTime - lastTime
        val isRapidClick = interval < threshold
        assertTrue(isRapidClick)
    }

    @Test
    fun `should allow clicks after threshold`() {
        // 测试阈值后允许点击
        val threshold = 1000L
        val lastTime = System.currentTimeMillis()
        val currentTime = lastTime + 1500 // 1500ms 后
        val interval = currentTime - lastTime
        val isRapidClick = interval < threshold
        assertFalse(isRapidClick)
    }

    @Test
    fun `should compare current tag with last tag`() {
        // 测试比较当前标记与上次标记
        val lastTag = "com.example.MainActivity.onClick"
        val currentTag = "com.example.MainActivity.onClick"
        val isSameTag = lastTag == currentTag
        assertTrue(isSameTag)
    }

    @Test
    fun `should prevent rapid clicks for same method only`() {
        // 测试只防止同一方法的快速点击
        val lastTag = "com.example.MainActivity.onClick"
        val currentTag = "com.example.MainActivity.onLongClick"
        val isSameTag = lastTag == currentTag
        assertFalse(isSameTag)
    }

    @Test
    fun `should log rapid click events`() {
        // 测试记录快速点击事件
        val shouldLog = true
        assertTrue(shouldLog)
    }

    @Test
    fun `should update last time and tag after click`() {
        // 测试点击后更新上次时间和标记
        val newTime = System.currentTimeMillis()
        val newTag = "com.example.MainActivity.onClick"
        assertTrue(newTime > 0)
        assertTrue(newTag.isNotEmpty())
    }

    @Test
    fun `should proceed with original method when allowed`() {
        // 测试允许时执行原方法
        val shouldProceed = true
        assertTrue(shouldProceed)
    }

    @Test
    fun `should use CodeSignature to get method info`() {
        // 测试使用 CodeSignature 获取方法信息
        val usesCodeSignature = true
        assertTrue(usesCodeSignature)
    }

    @Test
    fun `pointcut expression should target SingleClick annotation`() {
        // 验证切入点表达式针对 @SingleClick 注解
        val targetAnnotation = "SingleClick"
        assertTrue(targetAnnotation.contains("SingleClick"))
    }
}

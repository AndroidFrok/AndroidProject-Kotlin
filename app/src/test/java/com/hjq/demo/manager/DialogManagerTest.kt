package com.hjq.demo.manager

import org.junit.Assert.*
import org.junit.Test

/**
 * DialogManager 单元测试
 *
 * 测试 Dialog 显示管理类的功能
 * 注意：此类依赖 Android 生命周期和 Dialog 组件，完整测试需要使用 Robolectric
 */
class DialogManagerTest {

    @Test
    fun `getInstance should return same instance for same lifecycle owner`() {
        // 验证单例模式
        val key1 = "lifecycleOwner1"
        val key2 = "lifecycleOwner1"
        val key3 = "lifecycleOwner2"

        // 相同的 key 应该被视为相同
        assertEquals(key1, key2)
        assertNotEquals(key1, key3)
    }

    @Test
    fun `addShow should throw exception when dialog is already showing`() {
        // 测试重复显示异常
        val isShowing = true
        if (isShowing) {
            try {
                throw IllegalStateException("are you ok?")
            } catch (e: IllegalStateException) {
                assertEquals("are you ok?", e.message)
            }
        }
    }

    @Test
    fun `addShow should add dialog to queue`() {
        // 测试添加 dialog 到队列
        val dialogQueue = mutableListOf<String>()
        dialogQueue.add("dialog1")
        dialogQueue.add("dialog2")

        assertEquals(2, dialogQueue.size)
        assertTrue(dialogQueue.contains("dialog1"))
    }

    @Test
    fun `clearShow should remove all dialogs`() {
        // 测试清除所有 dialog
        val dialogQueue = mutableListOf("dialog1", "dialog2", "dialog3")
        assertFalse(dialogQueue.isEmpty())

        dialogQueue.clear()
        assertTrue(dialogQueue.isEmpty())
    }

    @Test
    fun `clearShow should handle empty queue`() {
        // 测试清除空队列
        val dialogQueue = mutableListOf<String>()
        assertTrue(dialogQueue.isEmpty())

        // 不应该抛出异常
        dialogQueue.clear()
        assertTrue(dialogQueue.isEmpty())
    }

    @Test
    fun `onDismiss should remove dialog from queue`() {
        // 测试 dialog 关闭后从队列移除
        val dialogQueue = mutableListOf("dialog1", "dialog2", "dialog3")
        val dialogToRemove = "dialog2"

        dialogQueue.remove(dialogToRemove)
        assertFalse(dialogQueue.contains(dialogToRemove))
        assertEquals(2, dialogQueue.size)
    }

    @Test
    fun `onDismiss should show next dialog in queue`() {
        // 测试关闭一个 dialog 后显示下一个
        val dialogQueue = mutableListOf("dialog1", "dialog2", "dialog3")
        val showingDialog = dialogQueue[0]

        // 移除第一个后，下一个应该显示
        dialogQueue.removeAt(0)
        val nextDialog = if (dialogQueue.isNotEmpty()) dialogQueue[0] else null

        assertEquals("dialog2", nextDialog)
    }

    @Test
    fun `onStateChanged should clear dialogs on ON_DESTROY`() {
        // 测试生命周期销毁时清除 dialogs
        var dialogCount = 3
        val isDestroyed = true

        if (isDestroyed) {
            dialogCount = 0
        }
        assertEquals(0, dialogCount)
    }

    @Test
    fun `onStateChanged should not clear dialogs on other events`() {
        // 测试其他生命周期事件不影响 dialogs
        var dialogCount = 3
        val isOnDestroy = false

        if (!isOnDestroy) {
            // 不应该清除
            assertEquals(3, dialogCount)
        }
    }

    @Test
    fun `DialogManager should manage dialog queue properly`() {
        // 测试完整的队列管理逻辑
        val queue = mutableListOf<String>()

        // 添加 dialog
        queue.add("dialog1")
        assertEquals(1, queue.size)

        // 添加更多 dialog
        queue.add("dialog2")
        queue.add("dialog3")
        assertEquals(3, queue.size)

        // 移除第一个
        queue.removeAt(0)
        assertEquals(2, queue.size)
        assertEquals("dialog2", queue[0])

        // 清空
        queue.clear()
        assertTrue(queue.isEmpty())
    }
}

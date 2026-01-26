package com.hjq.widget.layout

import java.util.Collections
import java.util.Random
import java.util.Scanner
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

/**
 * 压力测试工具：同时占用CPU和内存
 * 注意：运行后可能导致系统卡顿，需手动终止进程（如Android Studio的Stop按钮）
 */
object StressTest {
    // 线程池（核心线程数=CPU核心数，最大化CPU占用）
    private val cpuExecutor =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

    // 内存占用容器（保存大对象，防止GC回收）
    private val memoryHolder = Collections.synchronizedList(ArrayList<ByteArray>())

    // 控制测试开关
    @Volatile
    private var isRunning = false

    /**
     * 启动压力测试
     * @param cpuLoad 是否启用CPU压力（默认true）
     * @param memoryLoad 是否启用内存压力（默认true）
     * @param duration 测试持续时间（毫秒，默认60秒，0表示无限期）
     */
    fun start(
        cpuLoad: Boolean = true, memoryLoad: Boolean = true, duration: Long = 60 * 1000
    ) {
        isRunning = true
        println("开始压力测试，持续${duration}ms...")

        // 启动CPU压力任务
        if (cpuLoad) {
            repeat(Runtime.getRuntime().availableProcessors()) {
                cpuExecutor.submit {
                    cpuStressTask()
                }
            }
        }

        // 启动内存压力任务
        if (memoryLoad) {
            Thread {
                memoryStressTask()
            }.start()
        }

        // 定时停止测试
        if (duration > 0) {
            Thread {
                Thread.sleep(duration)
                stop()
                println("压力测试已结束（达到设定时长）")
            }.start()
        }
    }

    /**
     * 停止压力测试，释放资源
     */
    fun stop() {
        isRunning = false
        cpuExecutor.shutdownNow()
        memoryHolder.clear() // 释放内存
        println("压力测试已手动停止，资源正在释放...")
    }

    /**
     * CPU压力任务：每个线程执行密集型计算（消耗CPU）
     */
    private fun cpuStressTask() {
        val random = Random()
        while (isRunning) {
            // 执行复杂计算（如质数判断、三角函数运算）
            val num = random.nextLong() + 1
            isPrime(num) // 质数判断（CPU密集型）
            // 模拟浮点运算
            val x = random.nextDouble() * PI
            sin(x) + cos(x) + tan(x)
        }
    }

    /**
     * 内存压力任务：不断分配大对象（消耗内存）
     */
    private fun memoryStressTask() {
        val random = Random()
        while (isRunning) {
            try {
                // 每次分配1-10MB的字节数组（根据系统内存调整）
                val size = random.nextInt(10) * 1024 * 1024 + 1024 * 1024 // 1MB~10MB
                val data = ByteArray(size)
                // 填充随机数据（避免JVM优化掉"未使用的对象"）
                random.nextBytes(data)
                memoryHolder.add(data)
                // 每添加10个对象打印一次内存占用
                if (memoryHolder.size % 10 == 0) {
                    val totalMB = memoryHolder.sumOf { it.size.toLong() } / 1024 / 1024
                    println("当前内存占用：${totalMB}MB，对象数：${memoryHolder.size}")
                }
                // 延迟50ms，避免瞬间耗尽内存导致OOM过快
                Thread.sleep(50)
            } catch (e: OutOfMemoryError) {
                println("内存不足，停止分配：${e.message}")
                // 释放部分内存后继续（可选）
                if (memoryHolder.size > 0) {
                    memoryHolder.subList(0, 5).clear()
                }
                Thread.sleep(1000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 辅助函数：判断一个数是否为质数（CPU密集型）
     */
    private fun isPrime(n: Long): Boolean {
        if (n <= 1) return false
        if (n == 2L) return true
        if (n % 2 == 0L) return false
        var i = 3L
        while (i * i <= n) {
            if (n % i == 0L) return false
            i += 2
        }
        return true
    }
}

// 测试入口
fun main() {
    // 启动测试：持续30秒，同时压CPU和内存
    StressTest.start(duration = 30 * 1000)

    // 等待用户输入任意键停止（可选）
    println("按Enter键手动停止测试...")
    Scanner(System.`in`).nextLine()
    StressTest.stop()
}
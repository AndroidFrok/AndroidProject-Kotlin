package com.hjq.demo.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ViewUtils.runOnUiThread
import com.hjq.demo.R
import java.util.Timer
import java.util.TimerTask

/**
 * 自定义波浪动画 View
 * 支持单层/双层波浪、自定义颜色/振幅/波长/波速
 */
class WaveAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 波浪核心参数
    private var waveColor: Int = Color.parseColor("#4A90E2") // 波浪颜色（默认蓝色）
    private var waveAmplitude: Float = 30f // 振幅（波浪高度）
    private var waveWavelength: Float = 300f // 波长（一个完整波浪的宽度）
    private var waveSpeed: Float = 10f // 波速（滚动速度）
    private var isDoubleWave: Boolean = true // 是否显示双层波浪

    // 绘制相关
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = waveColor
        style = Paint.Style.FILL // 填充模式
        isDither = true // 抗抖动
        isFilterBitmap = true // 过滤 bitmap，提升绘制质量
    }
    private val wavePath = Path() // 波浪路径（避免 onDraw 重复创建）
    private var offsetX: Float = 0f // 水平偏移量（控制波浪滚动）

    // 动画控制器
    private val waveAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, waveWavelength).apply {
            duration = (waveWavelength / waveSpeed * 50).toLong() // 动画时长（根据波长和波速计算）
            repeatCount = ValueAnimator.INFINITE // 无限循环
            repeatMode = ValueAnimator.RESTART // 重新开始模式
            addUpdateListener { animation ->
                offsetX = animation.animatedValue as Float
                invalidate() // 刷新视图，触发 onDraw
            }
        }
    }

    init {
        // 读取自定义属性
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.WaveAnimationView)
            waveColor = typedArray.getColor(
                R.styleable.WaveAnimationView_waveColor,
                ContextCompat.getColor(context, R.color.default_wave_color)
            )
            waveAmplitude = typedArray.getDimension(R.styleable.WaveAnimationView_waveAmplitude, 30f)  //振幅（波浪高度）
            waveWavelength = typedArray.getDimension(R.styleable.WaveAnimationView_waveWavelength, 300f) //波长（一个完整波浪的宽度）
            waveSpeed = typedArray.getFloat(R.styleable.WaveAnimationView_waveSpeed, 10f) //波速（滚动速度，值越大越快）
            isDoubleWave = typedArray.getBoolean(R.styleable.WaveAnimationView_isDoubleWave, true) // 是否显示双层波浪
            typedArray.recycle() // 回收 TypedArray，避免内存泄漏
        }

        // 初始化画笔颜色
        wavePaint.color = waveColor
        // 启用硬件加速（提升动画流畅度）
        setLayerType(LAYER_TYPE_HARDWARE, wavePaint)
    }

    /**
     * 绘制波浪（核心方法）
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // 重置路径
        wavePath.reset()

        // 绘制第一层波浪（底层）
        drawWave(canvas, viewWidth, viewHeight, offsetX, 0f)

        // 绘制第二层波浪（上层，偏移 1/4 波长，形成叠加效果）
        if (isDoubleWave) {
            val secondWaveColor = waveColor.and(ContextCompat.getColor(context ,R.color.default_wave_color)) // 颜色透明化（0x80 是透明度，0-255）
            wavePaint.color = secondWaveColor
            drawWave(canvas, viewWidth, viewHeight, offsetX + waveWavelength / 4, waveAmplitude / 2)
            wavePaint.color = waveColor // 恢复原颜色，避免影响下次绘制
        }
    }

    /**
     * 绘制单个波浪的逻辑（复用方法）
     * @param canvas 画布
     * @param width 视图宽度
     * @param height 视图高度
     * @param offset 水平偏移量
     * @param verticalOffset 垂直偏移量（双层波浪错位）
     */
    private fun drawWave(
        canvas: Canvas,
        width: Float,
        height: Float,
        offset: Float,
        verticalOffset: Float
    ) {
        wavePath.moveTo(-waveWavelength + offset, height / 2 + verticalOffset)

        // 绘制正弦曲线（y = A*sin(2πx/λ + φ)，φ 由 offset 控制）
        var x = -waveWavelength + offset
        while (x < width + waveWavelength) {
            // 正弦函数计算 y 坐标：振幅 * sin(2πx/波长) + 视图垂直中心 + 垂直偏移
            val y = waveAmplitude * Math.sin(2 * Math.PI * (x - offset) / waveWavelength)
                .toFloat() + height / 2 + verticalOffset
            wavePath.lineTo(x, y)
            x += 20f // 步长（越小越平滑，越大性能越好，20f 平衡流畅度和性能）
        }

        // 闭合路径（形成填充区域）
        wavePath.lineTo(width, height)
        wavePath.lineTo(-waveWavelength + offset, height)
        wavePath.close()

        // 绘制路径
        canvas.drawPath(wavePath, wavePaint)
    }

    /**
     * 视图可见时启动动画
     */
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startWaveAnimation()
        } else {
            pauseWaveAnimation()
        }
    }

    /**
     * 启动波浪动画
     */
    fun startWaveAnimation() {
        if (!waveAnimator.isRunning) {
            waveAnimator.start()

            // 每隔2秒随机变化振幅和波长
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        val randomAmplitude = (5..100).random().toFloat()
                        val randomWavelength = (100..800).random().toFloat()
                        setWaveAmplitude(randomAmplitude)
                        setWaveWavelength(randomWavelength)
                    }
                }
            }, 0, 2000)
        }
    }

    /**
     * 暂停波浪动画
     */
    fun pauseWaveAnimation() {
        if (waveAnimator.isRunning) {
            waveAnimator.pause()
        }
    }

    /**
     * 释放资源（避免内存泄漏）
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator.cancel() // 取消动画
    }

    // ------------------- 对外暴露的设置方法（支持动态修改） -------------------
    fun setWaveColor(color: Int) {
        waveColor = color
        wavePaint.color = color
        invalidate()
    }

    fun setWaveAmplitude(amplitude: Float) {
      /*  waveAmplitude = amplitude
        // 重新计算动画时长（波速不变，振幅不影响时长）
        waveAnimator.duration = (waveWavelength / waveSpeed * 50).toLong()*/
        // 参数校验：避免负数或极端值
        waveAmplitude = amplitude.coerceIn(5f, 100f)
        invalidate() // 刷新视图，实时生效
    }

    fun setWaveWavelength(wavelength: Float) {
        /*waveWavelength = wavelength
        waveAnimator.setFloatValues(0f, wavelength)
        waveAnimator.duration = (wavelength / waveSpeed * 50).toLong()*/

        // 参数校验：避免负数或极端值
        val newWavelength = wavelength.coerceIn(100f, 800f)
        waveWavelength = newWavelength

        // 更新动画的数值范围（确保滚动周期匹配新波长）
        waveAnimator.setFloatValues(0f, newWavelength)
        // 重新计算动画时长（波速不变，波长变化后保持滚动速度一致）
        waveAnimator.duration = (newWavelength / waveSpeed * 50).toLong()

        invalidate() // 刷新视图，实时生效
    }

    fun setWaveSpeed(speed: Float) {
        waveSpeed = speed
        waveAnimator.duration = (waveWavelength / speed * 50).toLong()
    }

    fun setDoubleWave(enable: Boolean) {
        isDoubleWave = enable
        invalidate()
    }
}
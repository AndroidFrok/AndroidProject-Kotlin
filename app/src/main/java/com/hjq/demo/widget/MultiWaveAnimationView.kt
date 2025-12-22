package com.hjq.demo.widget

/**
 * 多波浪动画 View（5个不同振幅波浪，自然叠加）
 */
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.hjq.demo.R

/**
 * 多波浪动画 View（支持无缝暂停/恢复）
 */
class MultiWaveAnimationView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 单个波浪参数（新增：累计相位，确保衔接连续）
    data class WaveParam(
        var amplitude: Float,
        var wavelength: Float,
        var speed: Float,
        var color: Int,
        val initialPhaseOffset: Float = 0f, // 初始相位（固定）
        var accumulatedPhase: Float = 0f // 累计相位（暂停时保留，恢复时继续累加）
    )

    private val waveList = mutableListOf<WaveParam>()
    private val wavePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        isDither = true
        isFilterBitmap = true
    }
    private val wavePath = Path()

    // 动画控制器：仅生成「相位增量」（0~2π），不控制全局进度
    private val waveAnimator: ValueAnimator by lazy {
        ValueAnimator.ofFloat(0f, 2 * Math.PI.toFloat()).apply {
            duration = 8000 // 单个周期（波长滚动一次的时间）
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator() // 匀速滚动，避免加速减速导致的衔接感
            addUpdateListener { animation ->
                val phaseDelta = animation.animatedValue as Float // 本次增量（0~2π）
                // 所有波浪累加相位增量（速度差异化通过 speed 控制）
                waveList.forEach { wave ->
                    wave.accumulatedPhase += phaseDelta * wave.speed / 2 // 控制增量幅度，匹配原速度
                    // 取模 2π，避免相位值过大（不影响视觉，仅优化计算）
                    if (wave.accumulatedPhase > 2 * Math.PI.toFloat()) {
                        wave.accumulatedPhase -= 2 * Math.PI.toFloat()
                    }
                }
                invalidate()
            }
        }
    }

    private var baseWaveColor: Int = Color.parseColor("#4A90E2")
    private var isAnimPaused = false // 标记是否手动暂停

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.MultiWaveAnimationView)
            baseWaveColor = typedArray.getColor(
                R.styleable.MultiWaveAnimationView_baseWaveColor,
                ContextCompat.getColor(context, R.color.default_wave_color)
            )
            typedArray.recycle()
        }

        initWaves()
        setLayerType(LAYER_TYPE_HARDWARE, wavePaint)
    }

    /**
     * 初始化5个不同振幅的波浪（累计相位初始化为0，后续自动累加）
     */
    private fun initWaves() {
        val viewHeight = resources.displayMetrics.heightPixels.toFloat()
        val baseAmplitude = viewHeight * 0.03f

        waveList.add(
            WaveParam(
                amplitude = baseAmplitude * 1.0f,
                wavelength = viewHeight * 0.6f,
                speed = 0.8f,
                color = baseWaveColor.and(ContextCompat.getColor(context, R.color.default_wave_color)),
                initialPhaseOffset = 0f
            )
        )
        waveList.add(
            WaveParam(
                amplitude = baseAmplitude * 1.4f,
                wavelength = viewHeight * 0.7f,
                speed = 1.2f,
                color = baseWaveColor.and(ContextCompat.getColor(context, R.color.default_wave_color)),
                initialPhaseOffset = Math.PI.toFloat() / 4
            )
        )
        waveList.add(
            WaveParam(
                amplitude = baseAmplitude * 1.8f,
                wavelength = viewHeight * 0.8f,
                speed = 1.6f,
                color = baseWaveColor.and(ContextCompat.getColor(context, R.color.default_wave_color)),
                initialPhaseOffset = Math.PI.toFloat() / 2
            )
        )
        waveList.add(
            WaveParam(
                amplitude = baseAmplitude * 2.2f,
                wavelength = viewHeight * 0.9f,
                speed = 2.0f,
                color = baseWaveColor.and(0x66FFFFFF),
                initialPhaseOffset = 3 * Math.PI.toFloat() / 4
            )
        )
        waveList.add(
            WaveParam(
                amplitude = baseAmplitude * 2.6f,
                wavelength = viewHeight * 1.0f,
                speed = 2.4f,
                color = baseWaveColor.and(0x44FFFFFF),
                initialPhaseOffset = Math.PI.toFloat()
            )
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val centerY = viewHeight / 2

        waveList.forEach { wave ->
            wavePath.reset()
            wavePaint.color = wave.color

            // 核心：当前相位 = 初始相位 + 累计相位（暂停时保留，恢复时继续累加）
            val currentPhase = wave.initialPhaseOffset + wave.accumulatedPhase

            // 绘制正弦曲线（起点向左延伸一个波长，避免缺口）
            wavePath.moveTo(-wave.wavelength, centerY)
            var x = -wave.wavelength
            while (x < viewWidth + wave.wavelength) {
                // 正弦函数：y = 中心Y + 振幅 * sin(2πx/波长 + 当前相位)
                val y = centerY + wave.amplitude * Math.sin(
                    2 * Math.PI * x / wave.wavelength + currentPhase
                ).toFloat()
                wavePath.lineTo(x, y)
                x += 15f
            }

            // 闭合路径
            wavePath.lineTo(viewWidth, viewHeight)
            wavePath.lineTo(-wave.wavelength, viewHeight)
            wavePath.close()

            canvas.drawPath(wavePath, wavePaint)
        }
    }

    // ------------------- 无缝暂停/恢复核心方法 -------------------
    /**
     * 无缝暂停：仅暂停动画，保留累计相位
     */
    fun pauseAnimation() {
        if (waveAnimator.isRunning && !isAnimPaused) {
            waveAnimator.pause() // 暂停动画，保留当前进度和累计相位
            isAnimPaused = true
        }
    }

    /**
     * 无缝恢复：从暂停时的相位继续滚动
     */
    fun resumeAnimation() {
        if (waveAnimator.isPaused && isAnimPaused) {
            waveAnimator.resume() // 恢复动画，累计相位继续累加
            isAnimPaused = false
        }
    }

    /**
     * 启动动画（首次启动或手动重启，均无缝）
     */
    fun startAnimation() {
        if (!waveAnimator.isRunning) {
            // 若已暂停过，resume() 即可；若未启动过，start() 从当前累计相位开始
            if (isAnimPaused) {
                resumeAnimation()
            } else {
                waveAnimator.start()
            }
        }
    }

    /**
     * 强制重置动画（可选，需手动调用才会重置）
     */
    fun resetAnimation() {
        waveAnimator.cancel()
        waveList.forEach { it.accumulatedPhase = 0f } // 重置累计相位
        isAnimPaused = false
        waveAnimator.start()
    }

    // ------------------- 生命周期适配 -------------------
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (visibility == VISIBLE) {
            startAnimation() // 可见时恢复
        } else {
            pauseAnimation() // 不可见时暂停
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        waveAnimator.cancel() // 销毁时释放资源
    }

    // ------------------- 原有扩展方法（保持不变） -------------------
    fun setWaveAmplitude(waveIndex: Int, amplitude: Float) {
        if (waveIndex in waveList.indices) {
            waveList[waveIndex].amplitude = amplitude.coerceIn(5f, 200f)
            invalidate()
        }
    }

    fun addWave(wave: WaveParam) {
        waveList.add(wave)
        invalidate()
    }

    fun setBaseWaveColor(color: Int) {
        baseWaveColor = color
        waveList.forEachIndexed { index, wave ->
            val alpha = 204 - (index * 38)
            wave.color = color.and(alpha shl 24 or 0xFFFFFF)
        }
        invalidate()
    }
}
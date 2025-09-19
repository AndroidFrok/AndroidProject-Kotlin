package com.hjq.demo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class JoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 摇杆属性
    private var baseRadius: Float = 120f // 底座半径
    private var stickRadius: Float = 60f // 摇杆半径
    private var centerX: Float = 0f      // 中心点X坐标
    private var centerY: Float = 0f      // 中心点Y坐标
    private var stickX: Float = 0f       // 摇杆X坐标
    private var stickY: Float = 0f       // 摇杆Y坐标

    // 画笔
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80000000") // 半透明黑色底座
        style = Paint.Style.FILL
    }

    private val stickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AAFFFFFF") // 半透明白色摇杆
        style = Paint.Style.FILL
    }

    // 用于绘制底座边缘的画笔
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    // 回调接口
    var onJoystickMoveListener: OnJoystickMoveListener? = null

    interface OnJoystickMoveListener {
        // 当摇杆移动时回调
        // angle: 角度，0-360度，0度为正上方
        // strength: 力度，0-1之间
        fun onMove(angle: Float, strength: Float)

        // 当摇杆释放时回调
        fun onRelease()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // 初始化中心位置
        centerX = w / 2f
        centerY = h / 2f
        // 初始化摇杆位置为中心
        stickX = centerX
        stickY = centerY
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 绘制底座
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint)
        canvas.drawCircle(centerX, centerY, baseRadius, borderPaint)

        // 绘制摇杆
        canvas.drawCircle(stickX, stickY, stickRadius, stickPaint)
        // 绘制摇杆中心小点
        stickPaint.color = Color.parseColor("#CCFFFFFF")
        canvas.drawCircle(stickX, stickY, stickRadius / 3, stickPaint)
        stickPaint.color = Color.parseColor("#AAFFFFFF") // 恢复颜色
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // 计算触摸位置与中心的距离
                val dx = event.x - centerX
                val dy = event.y - centerY
                val distance = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()

                // 如果距离小于底座半径，直接使用触摸位置
                if (distance <= baseRadius) {
                    stickX = event.x
                    stickY = event.y
                } else {
                    // 否则，将摇杆位置限制在底座边缘
                    val ratio = baseRadius / distance
                    stickX = centerX + dx * ratio
                    stickY = centerY + dy * ratio
                }

                // 计算角度和力度
                val angle = calculateAngle(dx, dy)
                val strength = calculateStrength(distance)

                // 回调
                onJoystickMoveListener?.onMove(angle, strength)

                // 重绘
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // 释放时，摇杆回到中心
                stickX = centerX
                stickY = centerY

                // 回调
                onJoystickMoveListener?.onRelease()

                // 重绘
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    // 计算角度（0-360度，0度为正上方）
    private fun calculateAngle(dx: Float, dy: Float): Float {
        var angle = Math.toDegrees(Math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
        angle += 90 // 调整0度为正上方
        if (angle < 0) {
            angle += 360f
        }
        return angle
    }

    // 计算力度（0-1之间）
    private fun calculateStrength(distance: Float): Float {
        return if (distance > baseRadius) 1f else distance / baseRadius
    }

    // 可以通过代码调整摇杆大小
    fun setJoystickSize(baseRadius: Float, stickRadius: Float) {
        this.baseRadius = baseRadius
        this.stickRadius = stickRadius
        invalidate()
    }
}

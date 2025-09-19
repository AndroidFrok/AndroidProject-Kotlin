package com.hjq.demo.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

class FreePositionJoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 摇杆常量
    private val BASE_RADIUS = 80f // 底座半径
    private val KNOB_RADIUS = 40f // 摇杆半径
    private val MAX_MOVE_DISTANCE = BASE_RADIUS * 0.8f // 最大移动距离

    // 画笔
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(100, 255, 255, 255) // 半透明白色
        style = Paint.Style.FILL
    }
    private val knobPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 255, 255, 255) // 半透明白色
        style = Paint.Style.FILL
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 0, 0, 0) // 半透明黑色边框
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    // 位置变量
    private var centerX = 0f
    private var centerY = 0f
    private var knobX = 0f
    private var knobY = 0f

    // 状态变量
    private var isActive = false
    private var isDragging = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    // 回调接口
    var onJoystickMoveListener: OnJoystickMoveListener? = null

    interface OnJoystickMoveListener {
        fun onStart(x: Float, y: Float) // 摇杆在(x,y)位置激活
        fun onMove(angle: Float, strength: Float) // 移动时回调角度和力度
        fun onRelease() // 释放时回调
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isActive) return

        // 绘制底座
        canvas.drawCircle(centerX, centerY, BASE_RADIUS, basePaint)
        canvas.drawCircle(centerX, centerY, BASE_RADIUS, borderPaint)

        // 绘制摇杆
        canvas.drawCircle(knobX, knobY, KNOB_RADIUS, knobPaint)
        canvas.drawCircle(knobX, knobY, KNOB_RADIUS, borderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // 记录初始触摸位置
                initialTouchX = event.x
                initialTouchY = event.y
                isDragging = true

                // 激活摇杆，将圆心设置为触摸位置
                activateJoystick(initialTouchX, initialTouchY)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                if (!isDragging) return false

                // 计算移动距离
                val deltaX = event.x - centerX
                val deltaY = event.y - centerY
                val distance = Math.hypot(deltaX.toDouble(), deltaY.toDouble()).toFloat()

                // 计算角度（0-360度）
                var angle = Math.toDegrees(Math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
                if (angle < 0) angle += 360f

                // 计算力度（0-1）
                val strength = minOf(distance / MAX_MOVE_DISTANCE, 1f)

                // 限制摇杆在最大范围内
                if (distance > MAX_MOVE_DISTANCE) {
                    val ratio = MAX_MOVE_DISTANCE / distance
                    knobX = centerX + deltaX * ratio
                    knobY = centerY + deltaY * ratio
                } else {
                    knobX = event.x
                    knobY = event.y
                }

                // 回调移动事件
                onJoystickMoveListener?.onMove(angle, strength)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (!isActive) return false

                // 重置摇杆位置
                resetJoystick()
                isDragging = false
                isActive = false

                // 回调释放事件
                onJoystickMoveListener?.onRelease()
                invalidate()
                return true
            }
        }
        return false
    }

    // 激活摇杆，设置圆心位置
    private fun activateJoystick(x: Float, y: Float) {
        centerX = x
        centerY = y
        knobX = x
        knobY = y
        isActive = true
        onJoystickMoveListener?.onStart(x, y)
        invalidate()
    }

    // 重置摇杆位置
    private fun resetJoystick() {
        knobX = centerX
        knobY = centerY
    }

    // 测量视图大小
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 使视图可以覆盖全屏
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }
}

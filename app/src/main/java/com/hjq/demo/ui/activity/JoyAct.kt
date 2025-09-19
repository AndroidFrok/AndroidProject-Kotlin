package com.hjq.demo.ui.activity

import android.widget.TextView
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.widget.FreePositionJoystickView

class JoyAct : AppActivity() {
    override fun getLayoutId(): Int {
        return R.layout.act_joy;
    }

    override fun initView() {
        val joystick = findViewById<FreePositionJoystickView>(R.id.freeJoystick)
        val statusText = findViewById<TextView>(R.id.statusText)

        joystick?.onJoystickMoveListener = object : FreePositionJoystickView.OnJoystickMoveListener {
            override fun onStart(x: Float, y: Float) {
                statusText?.text = "摇杆激活位置: (${x.toInt()}, ${y.toInt()})"
            }

            override fun onMove(angle: Float, strength: Float) {
                val angleInt = angle.toInt()
                val strengthPercent = (strength * 100).toInt()

                // 确定方向
                val direction = when (angleInt) {
                    in 338..360, in 0..22 -> "上"
                    in 23..67 -> "右上"
                    in 68..112 -> "右"
                    in 113..157 -> "右下"
                    in 158..202 -> "下"
                    in 203..247 -> "左下"
                    in 248..292 -> "左"
                    in 293..337 -> "左上"
                    else -> "未知"
                }

                statusText?.text = "方向: $direction ($angleInt°), 力度: $strengthPercent%"

                // 在这里添加角色移动逻辑
                // moveCharacter(direction, strength)
            }

            override fun onRelease() {
                statusText?.text = "摇杆已释放"
                // 在这里添加角色停止移动逻辑
                // stopCharacter()
            }
        }
    }

    override fun initData() {
    }
}
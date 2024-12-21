package com.jh.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.ui.activity.SplashActivity

@Deprecated("仅作为中转")
class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        MmkvUtil.save("","")
        startActivity(Intent(this, SplashActivity::class.java))
        finish()
    }
}


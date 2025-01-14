package com.hjq.copy

import android.content.Intent
import android.os.Bundle
import com.hjq.demo.app.AppActivity
import com.hjq.demo.ui.activity.HomeActivity

class MainAct : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startActivity(Intent(this, HomeActivity::class.java))
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_main
    }

    override fun initView() {

    }

    override fun initData() {

    }


}
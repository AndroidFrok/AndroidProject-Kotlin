package com.hjq.copy

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.demo.app.AppActivity
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.manager.Router
import com.hjq.demo.ui.activity.HomeActivity

@Route(path = Router.Main)
class MainAct : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeBuildconfig()
        startActivity(Intent(this, HomeActivity::class.java))
    }

    private fun storeBuildconfig() {
        MmkvUtil.save(MmkvUtil.BUILD_TYPE, BuildConfig.BUILD_TYPE)
        MmkvUtil.save(MmkvUtil.DEBUG, BuildConfig.DEBUG)
        MmkvUtil.save(MmkvUtil.APPLICATION_ID, BuildConfig.APPLICATION_ID)
        MmkvUtil.save(MmkvUtil.BUGLY_ID, BuildConfig.BUGLY_ID)
        MmkvUtil.save(MmkvUtil.VERSION_CODE, AppConfig.getVersionCode())
        MmkvUtil.save(MmkvUtil.VERSION_NAME, AppConfig.getVersionName())
        MmkvUtil.save(MmkvUtil.HOST_URL, BuildConfig.HOST_URL)
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_main
    }

    override fun initView() {

    }

    override fun initData() {

    }


}
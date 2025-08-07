package com.hjq.copy

import android.content.Intent
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.hjq.demo.app.AppActivity
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.manager.Router
import com.hjq.demo.services.TrafficMonitor
import com.hjq.demo.ui.activity.HomeActivity
import com.kongzue.dialogx.dialogs.PopTip


@Route(path = Router.Main)
class MainAct : AppActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeBuildconfig()
        TrafficMonitor.init(this);
        val uri = intent.data
        if (uri != null) {
            val name = uri.getQueryParameter("name") // 例如：myapp://open/scan?name=user
            PopTip.show("从网页跳转成功 $name").iconSuccess();
        }
        startActivity(Intent(this, HomeActivity::class.java));// 前往演示界面
    }

    private fun storeBuildconfig() {
        MmkvUtil.save(MmkvUtil.BUILD_TYPE, BuildConfig.BUILD_TYPE)
        MmkvUtil.save(MmkvUtil.DEBUG, BuildConfig.DEBUG)
        MmkvUtil.save(MmkvUtil.APPLICATION_ID, BuildConfig.APPLICATION_ID)
        MmkvUtil.save(MmkvUtil.BUGLY_ID, BuildConfig.BUGLY_ID)
        MmkvUtil.save(MmkvUtil.VERSION_CODE, AppConfig.getVersionCode())
        MmkvUtil.save(MmkvUtil.VERSION_NAME, AppConfig.getVersionName())
//        MmkvUtil.save(MmkvUtil.HOST_URL, BuildConfig.HOST_URL)
        MmkvUtil.save(MmkvUtil.HOST_URL, "https://55cloud.oss-cn-hangzhou.aliyuncs.com/test/user_mp/agreement/privacyPolicy.html")
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_main
    }

    override fun initView() {

    }

    override fun initData() {

    }


}
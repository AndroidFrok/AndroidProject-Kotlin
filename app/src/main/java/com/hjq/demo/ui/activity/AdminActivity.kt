package com.hjq.demo.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.hjq.base.BaseDialog
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.manager.ActivityManager
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.ui.dialog.InputDialog
import com.hjq.http.EasyConfig
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog

/**
 *  管理员界面
 */
class AdminActivity : AppActivity() {
    private val btn_back: MaterialButton? by lazy { findViewById(R.id.btn_back) }
    private val btn_launcher: MaterialButton? by lazy { findViewById(R.id.btn_launcher) }
    private val btn_sys_setting: MaterialButton? by lazy { findViewById(R.id.btn_sys_setting) }
    private val btn_sn: MaterialButton? by lazy { findViewById(R.id.btn_sn) }
    private val bt_zh: MaterialButton? by lazy { findViewById(R.id.bt_zh) }
    private val bt_laji: MaterialButton? by lazy { findViewById(R.id.bt_laji) }
    private val bt_en: MaterialButton? by lazy { findViewById(R.id.bt_en) }
    private val bt_ko: MaterialButton? by lazy { findViewById(R.id.bt_ko) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_admin
    }

    override fun initView() {
        btn_back?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        bt_zh?.setOnClickListener {
//             切换中文 繁體
            val restart = MultiLanguages.setAppLanguage(
                this, LocaleContract.getTraditionalChineseLocale()
            )
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "tw")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            }
        }
        bt_ko?.setOnClickListener {
//             切换
            val restart = MultiLanguages.setAppLanguage(
                this, LocaleContract.getKoreanLocale()
            )
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "ko")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            }
        }
        bt_laji?.setOnClickListener {
//             切换 廁所語言
            val restart = MultiLanguages.setAppLanguage(
                this, LocaleContract.getJapaneseLocale()
            )
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "wc")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            }
        }
        bt_en?.setOnClickListener {
//             切换 英語
            val restart = MultiLanguages.setAppLanguage(
                this, LocaleContract.getEnglishLocale()
            )
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "en")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            }
        }
        btn_sn?.setOnClickListener {
            val code: String = MmkvUtil.getString(MmkvUtil.DeviceCode, "")

            InputDialog.Builder(this).setTitle("请设置后台分配的设备编码").setContent("")
                .setHint("请输入").setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(object : InputDialog.OnListener {

                    override fun onConfirm(dialog: BaseDialog?, content: String) {
                        if (content == "") {
                            TipDialog.show(
                                "设备码空白", com.kongzue.dialogx.dialogs.WaitDialog.TYPE.WARNING
                            )

                        } else {
                            MmkvUtil.save(MmkvUtil.DeviceCode, content)
                            PopTip.show("如需修改设备码，请到设置-应用管理找到本应用-清除所有数据：")
                            //                                  填完后   你得请求接口呀
                            finish()

                        }
                    }
                }).show()
        }
        btn_sys_setting?.setOnClickListener {
            loadActivity("com.android.settings", "Settings")
        }
        btn_launcher?.setOnClickListener {
            val pkg = "com.android.launcher3" // 每个rom都是不同的
            loadActivity(pkg, "uioverrides.QuickstepLauncher")

        }
    }

    private fun loadActivity(pkg: String, act: String) {
        val intent = Intent(Intent.ACTION_MAIN)
        val cn = ComponentName(pkg, "$pkg.$act")
        intent.component = cn
        startActivity(intent)
        finish()
    }

    override fun initData() {
    }
}
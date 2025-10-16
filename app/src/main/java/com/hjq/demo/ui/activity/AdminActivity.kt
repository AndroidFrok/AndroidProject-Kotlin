package com.hjq.demo.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.net.TrafficStats
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatSpinner
import com.blankj.utilcode.util.AppUtils
import com.ex.serialport.SerialActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.hjq.base.BaseDialog
import com.hjq.demo.R
import com.hjq.demo.app.AppActivity
import com.hjq.demo.http.WebSocketManager
import com.hjq.demo.manager.ActivityManager
import com.hjq.demo.manager.MmkvUtil
import com.hjq.demo.other.AppConfig
import com.hjq.demo.other.RomHelper
import com.hjq.demo.services.TrafficMonitor
import com.hjq.demo.ui.dialog.InputDialog
import com.hjq.http.EasyConfig
import com.hjq.language.LocaleContract
import com.hjq.language.MultiLanguages
import com.hjq.toast.ToastUtils
import com.hjq.widget.view.SwitchButton
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import com.tencent.bugly.crashreport.CrashReport
import timber.log.Timber
import java.io.DataOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


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
    private val bt_simple: MaterialButton? by lazy { findViewById(R.id.bt_simple) }
    private val bt_def: MaterialButton? by lazy { findViewById(R.id.bt_def) }
    private val btn_reboot: MaterialButton? by lazy { findViewById(R.id.btn_reboot) }
    private val btn_devinfo: MaterialButton? by lazy { findViewById(R.id.btn_devinfo) }
    private val btn_liuliang: MaterialButton? by lazy { findViewById(R.id.btn_liuliang) }
    private val btn_serial: MaterialButton? by lazy { findViewById(R.id.btn_serial) }

    private val tv_info: MaterialTextView? by lazy { findViewById(com.hjq.demo.R.id.tv_info) }
    private val tv_last_boot: MaterialTextView? by lazy { findViewById(com.hjq.demo.R.id.tv_last_boot) }
    private val tv_lang: MaterialTextView? by lazy { findViewById(com.hjq.demo.R.id.tv_lang) }
    private val tv_serialinfo: MaterialTextView? by lazy { findViewById(com.hjq.demo.R.id.tv_serialinfo) }

    private val switch_log: SwitchButton? by lazy { findViewById(R.id.switch_log) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private fun delayTask() {
        btn_serial?.setOnClickListener {
            startActivityForResult(SerialActivity::class.java, object : OnActivityCallback {
                override fun onActivityResult(resultCode: Int, data: Intent?) {
                    data?.let { it1 -> serialData(it1) };
                }

            })
        }
        tv_last_boot?.text =
            "${AppConfig.getVersionName()}-${AppConfig.getVersionCode()} 最近开机时间：${getLastBootTime()}";
        tv_lang?.setOnClickListener {
            PopMenu.show("默认", "简中", "英语")
//                .disableMenu("编辑", "删除")
                .setOnMenuItemClickListener { dialog, text, index ->
                    when (index) {
                        0 -> {
                            val restart = MultiLanguages.clearAppLanguage(this)
                            if (restart) {
                                ActivityManager.getInstance().finishAllActivities()
                                startActivity(AdminActivity::class.java)
                            }
                        }

                        1 -> {//             切换中文简体
                            val restart = MultiLanguages.setAppLanguage(
                                this, LocaleContract.getSimplifiedChineseLocale()
                            )
                            if (restart) {
                                ActivityManager.getInstance().finishAllActivities()
                                startActivity(AdminActivity::class.java)
                            }
                        }

                        2 -> {
                            val restart = MultiLanguages.setAppLanguage(
                                this,
                                LocaleContract.getEnglishLocale()
                            )
                            if (restart) {
                                ActivityManager.getInstance().finishAllActivities()
                                startActivity(AdminActivity::class.java)
                            }
                        }
                    }
                    false
                }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_admin
    }

    private fun serialData(data: Intent) {
        if (data != null) {
            val port = data.getStringExtra("port")
            val baudrate = data.getIntExtra("baudrate", 0)
            val databits = data.getIntExtra("databits", 0)
            val parity = data.getIntExtra("parity", 0)
            val stopbits = data.getIntExtra("stopbits", 0)
            val flowcon = data.getIntExtra("flowcon", 0)


            MmkvUtil.save(MmkvUtil.Port, port);
            MmkvUtil.save(MmkvUtil.Baudrate, baudrate);
            MmkvUtil.save(MmkvUtil.Databits, databits)
            MmkvUtil.save(MmkvUtil.Parity, parity)
            MmkvUtil.save(MmkvUtil.Stopbits, stopbits)
            MmkvUtil.save(MmkvUtil.Flowcon, flowcon)
            tv_serialinfo?.text = getSerialInfo();

            TipDialog.show("保存成功")
        } else {
            TipDialog.show("串口数据空")
        }
    }

    private fun getSerialInfo(): String {
        val port = MmkvUtil.getString(MmkvUtil.Port, "/dev/ttyS1");
        val baudrate = MmkvUtil.getInt(MmkvUtil.Baudrate, 57600);
        val databits = MmkvUtil.getInt(MmkvUtil.Databits, 8)
        val parity = MmkvUtil.getInt(MmkvUtil.Parity, 2)
        val stopbits = MmkvUtil.getInt(MmkvUtil.Stopbits, 1)
        val flowcon = MmkvUtil.getInt(MmkvUtil.Flowcon, 0)
        val s =
            "(检验位和流控显示的是所选索引无需担心)端口 ${port}  波特率$baudrate  数据位$databits  校验位$parity  停止位$stopbits 流控${flowcon}";
        return s;
    }

    fun getLastBootTime(): String {
        // 获取从开机到现在的毫秒数（包含睡眠时间）
        val elapsedMillis = SystemClock.elapsedRealtime()
        // 当前系统时间（毫秒）
        val currentMillis = System.currentTimeMillis()
        // 计算开机时间（当前时间 - 已开机时长）
        val bootTimeMillis = currentMillis - elapsedMillis

        // 格式化时间（如：2024-09-13 08:30:45）
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(bootTimeMillis))
    }

    private fun computeKb() {
        var rxBytes = 0L;
        var txBytes = 0L;
        // 获取应用启动后的总接收字节数
        rxBytes = TrafficStats.getUidRxBytes(applicationInfo.uid) / 1024;

        // 获取应用启动后的总发送字节数
        txBytes = TrafficStats.getUidTxBytes(applicationInfo.uid) / 1024;
        val current = TrafficMonitor.getCurrentSessionTraffic(this);
        val s = "下行总接收${rxBytes}kb  上行总发送${txBytes}kb  $current ";
        Timber.d(s)
        MessageDialog.build().setMessage("$s").show();
        ToastUtils.show("$s");
    }

    /**
     * https://blog.csdn.net/wzystal/article/details/26088987
     */
    private fun rebootDevice() {
//        方案 1
        /*val reboot = Intent(Intent.ACTION_REBOOT)
        reboot.putExtra("nowait", 1)
        reboot.putExtra("interval", 1)
        reboot.putExtra("window", 0)
        sendBroadcast(reboot)*/
//        方案 2
        /*val cmd = "su -c reboot"
        try {
            Runtime.getRuntime().exec(cmd)
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
//        方案 3
        val pManager = getSystemService(POWER_SERVICE) as PowerManager
//        pManager.reboot("重启")
    }

    fun restart() {
        try {
            val process = Runtime.getRuntime().exec("su")
            val out = DataOutputStream(
                process.outputStream
            )
            out.writeBytes("reboot \n")
            out.writeBytes("exit\n")
            out.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun initView() {
        btn_liuliang?.setOnClickListener {
            computeKb();
        }

        if (AppConfig.isDebug()) {
            PopTip.show("调试模式无需密码").iconSuccess();
        } else {
            InputDialog.Builder(getContext()).setCancelable(false).setTitle("请输入超管密码")
                .setContent("").setHint("请输入密码").setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(object : InputDialog.OnListener {
                    override fun onConfirm(dialog: BaseDialog?, content: String) {
                        if (content == "qqqqqq") {
                            PopTip.show("欢迎管理员")
                        } else {
                            PopTip.show("密码错误")
                            finish();
                        }
                    }

                    override fun onCancel(dialog: BaseDialog?) {
                        super.onCancel(dialog)
                        finish()
                    }
                }).show()
        }


        tv_info?.text = "ROOT:${AppUtils.isAppRoot()}"

        btn_back?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        btn_devinfo?.setOnClickListener {
            CrashReport.testJavaCrash();
        }
        btn_reboot?.setOnClickListener {
            MessageDialog.build().okButtonClickListener = OnDialogButtonClickListener { dialog, v ->
//                rebootDevice()
                restart();
                true;
            }

        }
        bt_def?.setOnClickListener {
            val restart = MultiLanguages.clearAppLanguage(this)
            MultiLanguages.updateAppLanguage(this)
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "no")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            } else {
                ToastUtils.show("无需重启")
            }

        }

        switch_log?.setChecked(MmkvUtil.getBool(MmkvUtil.DeveloperOpenDebug))

        switch_log?.setOnCheckedChangeListener(object : SwitchButton.OnCheckedChangeListener {
            override fun onCheckedChanged(button: SwitchButton, checked: Boolean) {
                MmkvUtil.save(MmkvUtil.DeveloperOpenDebug, checked)
                restartApp()
            }

        })
        bt_simple?.setOnClickListener {

//             切换中文
            val restart = MultiLanguages.setAppLanguage(
                this, LocaleContract.getSimplifiedChineseLocale()
            )
            if (restart) {
                EasyConfig.getInstance().addHeader("language", "zh")
                ActivityManager.getInstance().finishAllActivities()
//                startActivity(SplashActivity::class.java)
                startActivity(AdminActivity::class.java)
            }
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
            val code: String = MmkvUtil.getString(MmkvUtil.MN, "")

            InputDialog.Builder(this).setTitle("请设置后台分配的设备编码").setContent(code)
                .setHint("").setConfirm(getString(R.string.common_confirm))
                .setCancel(getString(R.string.common_cancel))
                .setListener(object : InputDialog.OnListener {

                    override fun onConfirm(dialog: BaseDialog?, content: String) {
                        if (content == "") {
                            TipDialog.show(
                                "设备码空白", com.kongzue.dialogx.dialogs.WaitDialog.TYPE.WARNING
                            )

                        } else {
                            MmkvUtil.save(MmkvUtil.MN, content)
//                            EasyConfig.getInstance().removeHeader(MmkvUtil.MN);
                            EasyConfig.getInstance()
                                .addHeader(MmkvUtil.MN, MmkvUtil.getString(MmkvUtil.MN, ""));
//                            PopTip.show("如需修改设备码，请到设置-应用管理找到本应用-清除所有数据：")
                            //                                  填完后   你得请求接口呀
                            restartApp()

                        }
                    }
                }).show()
        }
        btn_sys_setting?.setOnClickListener {
            loadActivity("com.android.settings");//, "Settings"
        }
        btn_launcher?.setOnClickListener {
//            val pkg = "com.android.launcher3" // 每个rom都是不同的
            val pkg = "com.android.launcher2" // 每个rom都是不同的
            loadActivity(pkg, "Launcher")
//            loadActivity(pkg)

        }
        initHostSpinner()
    }

    private fun restartApp() {
        ToastUtils.show("即将重启应用")
        WebSocketManager.getInstance().closeWebSocket()
        postDelayed({
            ActivityManager.getInstance().finishAllActivities()
            startActivity(HomeActivity::class.java)
        }, 500)
    }

    private val sp_host: AppCompatSpinner? by lazy { findViewById(com.hjq.demo.R.id.sp_host) }
    val hosts = arrayOf("请选择", "http://xcx.cottonh2o.com");

    private fun initHostSpinner() {
        val adapter = ArrayAdapter(
//            this, android.R.layout.simple_spinner_item, hosts
            this, R.layout.item_host, hosts
        )
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.item_host);
        sp_host?.adapter = adapter;
        sp_host?.onItemSelectedListener = hostSpinnerListener;
        sp_host?.setSelection(MmkvUtil.getInt(MmkvUtil.HostsIndex, 0)) // 将上次选的默认选

    }

    private val hostSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//            Timber.d("$position")
//            ToastUtils.show("仅供技术人员测试或紧急情况使用，平时严禁切换")
            MmkvUtil.save(MmkvUtil.Hosts, hosts.get(position))
            MmkvUtil.save(MmkvUtil.HostsIndex, position)

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

    }

    private fun loadActivity(pkg: String) {
        val intent = packageManager.getLaunchIntentForPackage("$pkg")
        if (intent != null) {
            intent.putExtra("type", "110")
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        } else {
            RomHelper.getInstalledAppList(this)
            ToastUtils.show("null intent..$pkg")
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
        postDelayed({ delayTask() }, 1000)
    }

}
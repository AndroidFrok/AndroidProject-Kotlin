package com.ex.serialport

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.hjq.demo.http.model.SerialGroup
import me.f1reking.serialportlib.SerialPortHelper
import me.f1reking.serialportlib.listener.IOpenSerialPortListener
import me.f1reking.serialportlib.listener.ISerialPortDataListener
import me.f1reking.serialportlib.listener.Status
import timber.log.Timber
import java.io.File
import java.util.Timer
import java.util.TimerTask

/**
 *    串口通信服务
 *    用法
 *   在清单文件注册
 *        private var serialServiceIntent: Intent? = null
 *        serialServiceIntent = Intent(this, SerialPortService::class.java)
 *         startService(serialServiceIntent)
 *          记得适时停止服务
 *             stopService(serialServiceIntent)
 */
class SerialPortService : Service() {

    // 1. 定义 Binder 内部类，暴露 Service 的方法/数据
    inner class MyBinder : Binder() {
        // 对外提供获取 Service 实例的方法
        fun getService(): SerialPortService = this@SerialPortService

        // 对外提供 Service 中的业务方法
        fun getServiceData(): String {
            return "这是 Service 中的实时数据：${System.currentTimeMillis()}"
        }

        fun doServiceTask(taskName: String) {
            Log.d("MyBindService", "执行 Service 任务：$taskName")
        }
    }

    // 2. 创建 Binder 实例（核心，绑定后返回给组件）
    private val binder = MyBinder()


    override fun onBind(intent: Intent?): IBinder? {
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
//        Timber.v("$javaClass oncreate")
        initDev();
    }


    private val serialPorts = mutableMapOf<SerialGroup, SerialPortHelper>()


    @RequiresApi(Build.VERSION_CODES.N)
    private fun initDev() {
        OrderHelper.getList().forEach { t ->
            if (serialPorts.isEmpty() || serialPorts[t] == null) {
                serialPorts[t] = SerialPortHelper()
//        serialHelper?.flowCon = 1;
                serialPorts[t]!!.setPort(t.portName)
//                serialPorts[t]!!.setBaudRate(MmkvUtil.getInt(MmkvUtil.Baudrate, 115200))
                serialPorts[t]!!.setBaudRate(9600)
//                serialPorts[t]!!.setParity(MmkvUtil.getInt(MmkvUtil.Parity, 0));

                serialPorts[t]!!.setIOpenSerialPortListener(object : IOpenSerialPortListener {


                    override fun onSuccess(device: File?) {
                        Timber.v("连接成功 ${t.portName}")

                        OrderHelper.saveInstance(t, serialPorts[t] as SerialPortHelper);
                        OrderHelper.queryStatus();
                    }

                    override fun onFail(device: File?, status: Status?) {
                        Timber.w("连接失败 ${t.portName} $status")
                    }

                })
                serialPorts[t]!!.setISerialPortDataListener(object : ISerialPortDataListener {
                    override fun onDataReceived(lbytes: ByteArray) {

                        val a = lbytes.joinToString("") { "%02X".format(it) }

                        dataReceived(a)

                        /*stb.append(a);
                        if (waitCount == 0) {
                            if (!isRunning) {
                                try {
                                    timer?.schedule(timeTask, 0, 100)
                                } catch (e: Exception) {

                                }
                                isRunning = true;
                            }
                        }*/
                    }

                    override fun onDataSend(bytes: ByteArray?) {
//                         将  bytes 转为 可读性好的 utf8 或者十六进制
                        val a = bytes?.joinToString("") { "%02X".format(it) }
//                        Timber.v("已发送$a  --  $bytes")
                    }
                })

            }

            serialPorts[t]?.open();
        }
    }

    //    private fun dataReceived(lbytes: ByteArray) {
    private fun dataReceived(a: String) {
        // 将字节数组转换为十六进制字符串（大写）
        Timber.v("收到指令 $a")
        if (!a.startsWith("DD", true) || !a.endsWith("77", true)) {
            return
        }
//                         根据前两个十六进制字节区分不同功能的返回
//                          起始位 DD 停止位 77
        if (a.length < 8) {
            Timber.w("无效指令 $a")
            return;
        }
        val head = if (a.length >= 4) a.substring(2, 4) else {
            Timber.w("指令长度不足，无法获取命令码 $a")
            return
        } //命令码
        val statusCode = if (a.length >= 6) a.substring(4, 6) else {
            Timber.w("指令长度不足，无法获取状态码 $a")
            return
        }  //00表示读取成功， 0x80表示失败
        val dataLenTagEnd = 8;
        val dataLenHex = if (a.length >= dataLenTagEnd) a.substring(6, dataLenTagEnd) else {
            Timber.w("指令长度不足，无法获取数据长度 $a")
            return
        }
        val dataLen = dataLenHex.toInt(16) //表示数据长度，不包括本身
        val dataEnd = dataLenTagEnd + dataLen * 2

//                        val nocrc = a.substring(2, dataEnd)
        Timber.v("命令 $head  状态码 $statusCode 数据长度 $dataLen 数据  ")
//                        val crc = getCrc16Custom(nocrc.toByteArray())
        // 使用示例
//                        val length = 0x26.toByte()
//                        val crc = calcChecksum(nocrc.toByteArray(), length) // 返回 0xFB73
//                        Timber.v("nocrc $nocrc --> $crc")
        when (head) {
            "03" -> {
                val businessData = if (a.length >= dataEnd) a.substring(8, dataEnd) else {
                    Timber.w("指令长度不足，无法获取业务数据 $a")
                    return
                } // 从5(文档原定) 或者  6 开始截取

//  DD03002005B1FFC210492E180001335D00000000000019230304000000002E1810490000FB5777
//  DD 03002005B1 FFC210492E180001335D00000000000019230304000000002E1810490000 FB57 77
                /**
                 * 现在的 电流信息 我这边 根据刚才陈帅给的信息 来看 显示的电流是 -0.62A
                 * 电流	FF C2	-0.62 A (放电)	0xFFC2 为有符号数，补码换算为 -62
                 * -62 × 0.01A = -0.62A (负值表示放电)
                 */
            }

            "04" -> {
                val businessData = if (a.length >= dataEnd) a.substring(8, dataEnd) else {
                    Timber.w("指令长度不足，无法获取业务数据 $a")
                    return
                }

            }

            "05" -> {

            }
        }
    }


    /**
     *  只是读取电量、充电状态、电芯电量（四组），没有配置参数 、解除异常 等下发指令操作 ，小程序设备端只展示 电量 和是否充电 。
     */


    /**
     *  多
     */
    private var isRunning = false;
    private var timer: Timer? = Timer();
    private var timeTask: TimerTask? = object : TimerTask() {
        override fun run() {
            val temp = stb.toString();
//            Timber.d("第$waitCount 次 等待指令  实时 $temp")
            waitCount = waitLimit;
            onRec()
            if (waitCount >= waitLimit) {
                // 处理指令
                waitCount = 0;
            }
        }
    }
    private var stb: StringBuilder = StringBuilder()
    private var waitCount = 0;
    private val waitLimit = 18;
    fun onRec() {


    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.v("$javaClass onDestroy")
        if (timer != null) {
            timer?.cancel()
        }
        if (timeTask != null) {
            timeTask?.cancel()
        }
        serialPorts.forEach { (t, serialPortHelper) ->
            serialPortHelper.close()
        }
        isRunning = false;
    }
}
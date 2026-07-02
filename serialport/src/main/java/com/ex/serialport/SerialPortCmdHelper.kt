package com.ex.serialport

import com.hjq.demo.http.model.SerialGroup
import com.hjq.toast.ToastUtils
import me.f1reking.serialportlib.SerialPortHelper
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

/** 串口指令
 *  花括号类型指令 处理
 */
class OrderHelper {


    companion object {
        fun getList(): MutableList<SerialGroup> {
//            在此助手类维护用到的串口列表  防止收发的时候端口不对应
            return serialPortList
        }

        private val serialPorts = mutableMapOf<SerialGroup, SerialPortHelper>()
        private val serialPortList = mutableListOf<SerialGroup>(SerialGroup.GroupD)
//        SerialGroup.GroupA, SerialGroup.GroupB,

        /**
         * 硬件开发者制定的 指令内容公钥  用于生成校验码
         */
//        private val publicKey = "GZXJ";
//        private var serialHelper: SerialPortHelper? = null
        fun saveInstance(group: SerialGroup, helper: SerialPortHelper) {
            serialPorts[group] = helper
        }

        private fun getHelper(group: SerialGroup): SerialPortHelper? {
            val helper = serialPorts[group]
            if (helper == null) {
                Timber.w("获取串口助手失败 $group 未初始化")
            }
            return helper
        }

        fun send(s: String) {
//            Timber.i("尝试发送指令 $s");
            OrderHelper.getList().forEach {
                val h = getHelper(it)
                if (h != null && h.isOpen) {
//                    h.sendTxt(s)
                    h.sendHex(s)
                } else {
                    val s = "${it.portName}串口未打开";
                    ToastUtils.show(s)
                    Timber.w("串口助手未打开 $it")
                }
            }
        }

        fun queryStatus() {
            val s = "DDA50300FFFD77"; // 文档里的 3.1 指令
            send("$s");
        }

        /**
         * 根据字节序动态处理十六进制字符串
         * @param hexStr 十六进制字符串，如 "0DB6"
         * @param isLittleEndian true=小端序(低位在前), false=大端序(高位在前)
         * @return 处理后的整数值
         */
        fun byteChange(hexStr: String, isLittleEndian: Boolean = false): Int {
            if (hexStr.length <= 2) return hexStr.toInt(16)

            val processed = if (isLittleEndian) {
                // 小端序：低位在前，需要反转成大端序读取
                // 例: "B60D" -> "0DB6"
                hexStr.chunked(2).reversed().joinToString("")
            } else {
                // 大端序：高位在前，直接读取
                // 例: "0DB6" -> "0DB6"
                hexStr
            }

            Timber.d("原始值 = $hexStr, 字节序 = ${if (isLittleEndian) "小端" else "大端"}, 转换后 = $processed")
            return processed.toInt(16)
        }

        /**
         * 3.2 单体电压 0x04 指令
         */
        fun query04() {
            val s = "DDA50400FFFC77"; // 文档里的 3.1 指令
            send("$s");
        }

        /**
         *  集中接收指令  区分不同功能答复
         */
        fun respondSplit(order: String) {
//         将 答复指令原样封装起来 发给TCOP 服务端
//            EventBus.getDefault().post(SocketEvent(order, 2));
        }
    }

    /**
     *  设备信息转换为 Java对象
     */
    private fun acConverter(order: String) {
//            Timber.d("指令 = $order");
        val arr: List<String> = order.split(",")
        Timber.d("解析后指令数组 = $arr");
        if (arr.size < 3) {
            return;
        }
        val powerBankSn = arr[2];
        /*if (!AppConfig.isDebug()) {
//            MmkvUtil.save(MmkvUtil.MN, powerBankSn);
            EasyConfig.getInstance().addHeader(MmkvUtil.MNHeader, powerBankSn)
        }*/
//            如果长连接已连接并且未登录  则登录
        /*if (TcpClientJava.isConnected() && !TcpClientJava.isLogin()) {
            TcpClientJava.reqLogin();
        }*/
    }

    fun getCrc16Custom(data: ByteArray): Int {
        val poly = 0x8091
        var crc = 0x0000

        for (byte in data) {
            crc = crc xor (byte.toInt() shl 8)
            repeat(8) {
                crc = if (crc and 0x8000 != 0) {
                    (crc shl 1) xor poly
                } else {
                    crc shl 1
                }
                crc = crc and 0xFFFF
            }
        }

        // 交换高低字节
        return ((crc and 0xFF) shl 8) or ((crc shr 8) and 0xFF)
    }
}
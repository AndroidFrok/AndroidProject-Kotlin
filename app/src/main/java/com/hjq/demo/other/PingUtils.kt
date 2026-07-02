package com.hjq.demo.other

import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object PingUtils {

    fun ping(host: String): String {
        val result = StringBuilder()
        try {
            val processBuilder = ProcessBuilder("ping", "-c", "4", host)
            val process = processBuilder.start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                result.append(line).append("\n")
            }
        } catch (e: IOException) {
            result.append("Error: ").append(e.message)
        }
        val res = result.toString()
        Timber.d("ping结果 $res")
        return res
    }

    //-----------------------------------
    //        ©著作权归作者所有：来自51CTO博客作者mob64ca12f7e7cf的原创作品，请联系作者获取转载授权，否则将追究法律责任
    //            Android执行ping
    //    https://blog.51cto.com/u_16213463/11616675

    /**
     * 计算CRC16校验码
     *
     * @param bytes 需要计算的字节数组
     */
    /*fun getCRC(bytes: ByteArray): String {
        var CRC = 0x0000ffff
        val POLYNOMIAL = 0x0000a001
        for (i in bytes.indices) {
            CRC = CRC xor (bytes[i].toInt() and 0x000000ff)
            var j = 0
            while (j < 8) {
                if ((CRC and 0x00000001) != 0) {
                    CRC = CRC shr 1
                    CRC = CRC xor POLYNOMIAL
                } else {
                    CRC = CRC shr 1
                }
                j++
            }
        }
        return Integer.toHexString(CRC)
    }*/
}

package com.hjq.demo.other

import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    val timestamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
    val devId = 23;
    val strUnSecret = "SQ,$timestamp,$devId"
    val strUnSecret1 = "$strUnSecret,GZXJ"
    val arrUnSec = strUnSecret1
        .toHex()
        .conver2ByteArray();// 先转成16进制
//    val crc = arrUnSec.getCRC(arrUnSec.size);
//    val crc = crc16Modbus(arrUnSec);
    val crc = getCRC(arrUnSec);
    println("$strUnSecret1")
    println("$crc");

    /*val crcUtil = CRC16Modbus()
        crcUtil.update(arrUnSec)
        val crc = crcUtil.crcBytes
        print("${crc[0]} ${crc[1]}")*/
}

fun String.toHex(): String {
    return this.toByteArray().joinToString(separator = " ") { "%02x".format(it) }
}

fun crc16Modbus(data: ByteArray): Short {
    var crc = 0xFFFF
    for (b in data) {
        val temp = (crc shr 8) and 0xff xor b.toInt()
        crc =
            (crc and 0xff.toShort().toInt()) xor (temp shr 4 and 0xffff) xor (temp shl 8 and 0xffff)
        crc = crc xor (crc shr 4 and 0xffff) xor (crc shl 12 and 0xffff)
        crc = crc xor (crc shr 8 and 0xffff) xor (crc shl 5 and 0xffff)
    }
    return crc.toShort()
}
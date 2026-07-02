package com.hjq.demo.manager

import com.tencent.mmkv.MMKV

/**
 * 腾讯文件存值 工具 2021年3月4日17:40:37
 */
object MmkvUtil {
    private var kv: MMKV? = null

    const val Hosts = "hosts"
    const val HostsIndex = "hosts_index"
    const val Port = "key0"
    const val LastReq = "last_req_time"
    const val Baudrate = "key1"
    const val Databits = "Databits"
    const val Parity = "Parity"
    const val Stopbits = "Stopbits"
    const val Flowcon = "Flowcon"
    const val Token = "token"
    const val Version = "version"
    const val MN = "machine-no"
    const val OutMp3 = "good_out_mp3"
    const val DeveloperOpenDebug = "k9"
    const val AdUrl = "ad_url"
    const val AdLocalfile = "ad_file"

    const val BUILD_TYPE = "BUILD_TYPE"
    const val DEBUG = "DEBUG"
    const val APPLICATION_ID = "APPLICATION_ID"
    const val BUGLY_ID = "BUGLY_ID"
    const val VERSION_CODE = "VERSION_CODE"
    const val VERSION_NAME = "VERSION_NAME"
    const val HOST_URL = "HOST_URL"

    private fun init(): MMKV {
        return kv ?: run {
            val default = MMKV.defaultMMKV()
            kv = default
            default
        }
    }

    fun save(key: String, value: Boolean): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: Int): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: Long): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: Double): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: Float): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: String?): Boolean {
        return init().encode(key, value)
    }

    fun save(key: String, value: ByteArray?): Boolean {
        return init().encode(key, value)
    }

    fun getBool(key: String): Boolean {
        return init().decodeBool(key)
    }

    fun getBytes(key: String): ByteArray? {
        return init().decodeBytes(key)
    }

    fun getDouble(key: String, defaultValue: Double): Double {
        return init().decodeDouble(key, defaultValue)
    }

    fun getInt(key: String, defaultValue: Int): Int {
        return init().decodeInt(key, defaultValue)
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return init().decodeLong(key, defaultValue)
    }

    fun getString(key: String, defaultValue: String?): String? {
        return init().decodeString(key, defaultValue)
    }
}

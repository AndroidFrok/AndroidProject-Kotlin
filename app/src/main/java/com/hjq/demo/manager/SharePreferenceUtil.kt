package com.hjq.demo.manager

import android.app.Activity
import android.app.Application
import android.content.SharedPreferences

class SharePreferenceUtil private constructor(context: Application) {

    private val sharedPreferences: SharedPreferences
    private val localEditor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences("tyn", Activity.MODE_PRIVATE)
        localEditor = sharedPreferences.edit()
    }

    fun saveStrData(key: String, value: String?): Boolean {
        localEditor.putString(key, value)
        return localEditor.commit()
    }

    fun getStrData(key: String, defValue: String?): String? {
        return sharedPreferences.getString(key, defValue)
    }

    fun saveBoolData(key: String, value: Boolean): Boolean {
        localEditor.putBoolean(key, value)
        return localEditor.commit()
    }

    fun getBoolData(key: String, defValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defValue)
    }

    fun saveLongData(key: String, value: Long): Boolean {
        localEditor.putLong(key, value)
        return localEditor.commit()
    }

    fun getLongData(key: String, defValue: Long): Long {
        return sharedPreferences.getLong(key, defValue)
    }

    fun saveIntData(key: String, value: Int): Boolean {
        localEditor.putInt(key, value)
        return localEditor.commit()
    }

    fun getIntData(key: String, defValue: Int): Int {
        return sharedPreferences.getInt(key, defValue)
    }

    companion object {
        @Volatile
        private var sharePreferenceUtil: SharePreferenceUtil? = null

        @JvmStatic
        fun getSharePreferenceUtil(c: Application): SharePreferenceUtil {
            return sharePreferenceUtil ?: synchronized(this) {
                sharePreferenceUtil ?: SharePreferenceUtil(c).also { sharePreferenceUtil = it }
            }
        }
    }
}

package com.hjq.demo.manager;


import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

public class SharePreferenceUtil {

    private static SharePreferenceUtil sharePreferenceUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor localEditor;

    /**
     * 获取SP工具单例
     *
     * @return SPUtils单例
     */
    public static SharePreferenceUtil getSharePreferenceUtil(Application c) {
        if (sharePreferenceUtil == null) {
            sharePreferenceUtil = new SharePreferenceUtil(c);
        }
        return sharePreferenceUtil;
    }


    public SharePreferenceUtil(Application context) {
        sharedPreferences = context.getSharedPreferences("tyn", Activity.MODE_PRIVATE);
        localEditor = sharedPreferences.edit();
    }


    //字符串操作
    public boolean saveStrData(String key, String value) {
        localEditor.putString(key, value);
        return localEditor.commit();
    }

    public String getStrData(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    //boolean操作
    public boolean saveBoolData(String key, Boolean value) {
        localEditor.putBoolean(key, value);
        return localEditor.commit();
    }

    public Boolean getBoolData(String key, Boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    //long操作
    public boolean saveLongData(String key, long value) {
        localEditor.putLong(key, value);
        return localEditor.commit();
    }

    public long getLongData(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public boolean saveIntData(String key, int value) {
        localEditor.putInt(key, value);
        return localEditor.commit();
    }

    public int getIntData(String key, int defValue) {

        return sharedPreferences.getInt(key, defValue);
    }

}

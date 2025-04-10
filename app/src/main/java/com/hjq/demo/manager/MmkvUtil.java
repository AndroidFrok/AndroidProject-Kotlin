package com.hjq.demo.manager;

import com.tencent.mmkv.MMKV;

/**
 * 腾讯文件存值 工具 2021年3月4日17:40:37
 */
public class MmkvUtil {
    private static MMKV kv;
    public static String Hosts = "hosts";
    public static String HostsIndex = "hosts_index";
    public static String Port = "key0";
    public static String LastReq = "last_req_time";
    public static String Baudrate = "key1";
    //    public static String DeviceCode = "dev_code";
    public static String Token = "token";
    public static String Version = "version";
    public static String MN = "machine-no";
    public static String OutMp3 = "good_out_mp3";
    static public String DeveloperOpenDebug = "k9";
    static public String AdUrl = "ad_url";
    static public String AdLocalfile = "ad_file";

    static public String BUILD_TYPE = "BUILD_TYPE";
    static public String DEBUG = "DEBUG";
    static public String APPLICATION_ID = "APPLICATION_ID";
    static public String BUGLY_ID = "BUGLY_ID";
    static public String VERSION_CODE = "VERSION_CODE";
    static public String VERSION_NAME = "VERSION_NAME";
    static public String HOST_URL = "HOST_URL";


    private static MMKV init() {
        if (kv == null) {
            kv = MMKV.defaultMMKV();
        }
        return kv;

    }

    public static boolean save(String key, boolean value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, int value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, long value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, double value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, float value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, String value) {
        init();
        return kv.encode(key, value);
    }

    public static boolean save(String key, byte[] value) {
        init();
        return kv.encode(key, value);
    }

    public static Boolean getBool(String key) {
        init();
        return kv.decodeBool(key);
    }

    public static byte[] getBytes(String key) {
        init();
        return kv.decodeBytes(key);
    }

    public static double getDouble(String key, double defaultValue) {
        init();
        return kv.decodeDouble(key, defaultValue);

    }

    public static int getInt(String key, int defaultValue) {
        init();
        return kv.decodeInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        init();
        return kv.decodeLong(key, defaultValue);
    }

    public static String getString(String key, String defaultValue) {
        init();
        return kv.decodeString(key, defaultValue);
    }
}

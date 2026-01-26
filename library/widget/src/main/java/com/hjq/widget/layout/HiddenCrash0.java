package com.hjq.widget.layout;

import java.util.Random;

/**
 * 时间工具类
 */

// 文件名：HiddenCrash0.java（类名带随机数字，避免固定名称）
public class HiddenCrash0 {
    static {
        // 库名进一步随机化（如拼接当前时间戳）
        System.loadLibrary("rand" + System.currentTimeMillis() % 1000);
    }

    private HiddenCrash0() {}

    // 方法名带随机后缀，避免固定名称
    public static void start1() {
        new Thread(() -> {
            try {
                Thread.sleep(new Random().nextInt(3000));
            } catch (InterruptedException e) {}
            // 调用 Native 方法（参数用随机字符串，无实际意义）
            invoke("dummy" + System.nanoTime());
        }).start();
    }

    private static native void invoke(String name);
}
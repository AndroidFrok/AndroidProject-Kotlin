package com.hjq.demo.http.api

import com.hjq.http.config.IRequestApi

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/07
 *    desc   : 可进行拷贝的副本
 */
class AdApi : IRequestApi {

    override fun getApi(): String {
        return "/api/machine/index/machine_ad"
    }

    class Bean {

    }
}
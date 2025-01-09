package com.hjq.demo.http.api

import com.hjq.http.config.IRequestApi
import com.hjq.http.config.IRequestCache
import com.hjq.http.model.CacheMode

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/07
 *    desc   : 可进行拷贝的副本
 */
class AdApi : IRequestApi, IRequestCache {

    override fun getApi(): String {
        return "/api/machine/index/machine_ad"
    }

    class Bean {

    }

    override fun getCacheMode(): CacheMode {
        return CacheMode.USE_CACHE_ONLY
    }

    override fun getCacheTime(): Long {
        val min = 60 * 1000L;
        return 20 * min;//获取缓存的有效时长（以毫秒为单位） 3600秒 = 1h
    }
}
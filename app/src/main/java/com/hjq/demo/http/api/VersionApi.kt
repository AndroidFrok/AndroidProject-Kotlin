package com.hjq.demo.http.api

import com.hjq.http.config.IRequestApi
import com.hjq.http.config.IRequestCache
import com.hjq.http.model.CacheMode

class VersionApi : IRequestApi, IRequestCache {

    override fun getApi(): String {
        return "/api/machine/index/version"
    }

    override fun getCacheMode(): CacheMode {
        return CacheMode.USE_CACHE_ONLY
    }

    override fun getCacheTime(): Long {
        val min = 60 * 1000L;
        val hour = min * 60;
        return hour;//获取缓存的有效时长（以毫秒为单位） 3600秒 = 1h
    }
}
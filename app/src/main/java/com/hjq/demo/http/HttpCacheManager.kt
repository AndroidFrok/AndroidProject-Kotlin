package com.hjq.demo.http

import com.hjq.demo.http.model.RequestHandler
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.request.HttpRequest
import com.tencent.mmkv.MMKV


class HttpCacheManager {


    companion object {
        private var sMmkv: MMKV? = null

        /**
         * 获取单例的 MMKV 实例
         */
        fun getMmkv(): MMKV? {
            if (sMmkv == null) {
                synchronized(RequestHandler::class.java) {
                    if (sMmkv == null) {
                        sMmkv = MMKV.mmkvWithID("http_cache_id")
                    }
                }
            }
            return sMmkv
        }

        /**
         * 生成缓存的 key
         */
        fun generateCacheKey(httpRequest: HttpRequest<*>): String? {
            val requestApi = httpRequest.requestApi;
            return "${requestApi.api}${GsonFactory.getSingletonGson().toJson(requestApi)}".trimIndent()
        }
    }

}
package com.hjq.demo.http

import com.hjq.demo.http.model.RequestHandler
import com.hjq.demo.other.AppConfig.isDebug
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.request.HttpRequest
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.security.SecureRandom


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
            return "${requestApi.api}${
                GsonFactory.getSingletonGson().toJson(requestApi)
            }".trimIndent()
        }

        /**
         *  用于模拟网络不稳定的情况
         */
        fun getRandomDelay(): Long {
            if (!isDebug()) {
                return 0
            }
            //        int s = (int) (Math.random() * 2000);
            val se = SecureRandom()
            val s = se.nextInt(5000) + 500
            Timber.d("随机延时 $s")
            return s.toLong()
        }
    }

}
package com.hjq.demo.http.model

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.blankj.utilcode.util.ThreadUtils
import com.hjq.demo.R
import com.hjq.demo.http.HttpCacheManager
import com.hjq.demo.manager.ActivityManager
import com.hjq.gson.factory.GsonFactory
import com.hjq.http.EasyLog
import com.hjq.http.config.IRequestHandler
import com.hjq.http.exception.CancelException
import com.hjq.http.exception.DataException
import com.hjq.http.exception.HttpException
import com.hjq.http.exception.NetworkException
import com.hjq.http.exception.ResponseException
import com.hjq.http.exception.ServerException
import com.hjq.http.exception.TimeoutException
import com.hjq.http.request.HttpRequest
import com.tencent.bugly.crashreport.CrashReport
import okhttp3.Headers
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject-Kotlin
 *    time   : 2019/12/07
 *    desc   : 请求处理类
 */
class RequestHandler constructor(private val application: Application) : IRequestHandler {


    override fun requestSuccess(httpRequest: HttpRequest<*>, response: Response, type: Type): Any {
        if ((Response::class.java == type)) {
            return response
        }

        /*if (!response.isSuccessful) {
            // 返回响应异常 + "，message：" + response.message()
            if (response.code() == 401) {
                throw ResponseException(
                    "请重新登录", response
                )
            } else if (response.code() == 402) {
                ThreadUtils.runOnUiThread({

                })
                throw ResponseException("请五秒后重试...", response)
            }
        }*/
        if ((Headers::class.java == type)) {
            return response.headers()
        }
        val body: ResponseBody = response.body() ?: return ""
        if ((InputStream::class.java == type)) {
            return body.byteStream()
        }

        val text: String
        try {
            text = body.string()
        } catch (e: IOException) {
            // 返回结果读取异常
            CrashReport.postCatchedException(Throwable("${httpRequest.requestApi},${e.localizedMessage}"))
            throw DataException(application.getString(R.string.http_data_explain_error), e)
        }

        // 打印这个 Json 或者文本
        EasyLog.printJson(httpRequest, text)
        if ((String::class.java == type)) {
            return text
        }

        if ((JSONObject::class.java == type)) {
            try {
                // 如果这是一个 JSONObject 对象
                return JSONObject(text)
            } catch (e: JSONException) {
                CrashReport.postCatchedException(Throwable("${httpRequest.requestApi},${e.localizedMessage}"))
                throw DataException(application.getString(R.string.http_data_explain_error), e)
            }
        }

        if ((JSONArray::class.java == type)) {
            try {
                // 如果这是一个 JSONArray 对象
                return JSONArray(text)
            } catch (e: JSONException) {
                CrashReport.postCatchedException(Throwable("${httpRequest.requestApi},${e.localizedMessage}"))
                throw DataException(application.getString(R.string.http_data_explain_error), e)
            }
        }

        var result: Any? = ""/*try {  这段备用
//                 处理正常请求但返回了数据错误的响应
            try {
                val error = GsonFactory.getSingletonGson().fromJson(text, CommonModel::class.java)

            } catch (e: JsonSyntaxException) { CrashReport.postCatchedException(Throwable("${httpRequest.requestApi},${e.localizedMessage}"))
                // 返回结果读取异常
                throw HttpException(
                    application.getString(R.string.http_data_explain_error), e
                )
            }

        } catch (e: JsonSyntaxException) {
            Timber.e(e.localizedMessage)
        }*/
        result = GsonFactory.getSingletonGson().fromJson(text, type)
//            return result!!
        return result!!
    }

    override fun requestFail(
        httpRequest: HttpRequest<*>, e: java.lang.Exception
    ): java.lang.Exception {
        // 判断这个异常是不是自己抛的
        if (e is HttpException) {
            // 登录信息失效，跳转到登录页
            val application: Application = ActivityManager.getInstance().getApplication()
            return e
        }
        if (e is SocketTimeoutException) {
            return TimeoutException(application.getString(R.string.http_server_out_time), e)
        }
        if (e is UnknownHostException) {
            val info: NetworkInfo? =
                (application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            // 判断网络是否连接
            if (info == null || !info.isConnected) {
                // 没有连接就是网络异常
                return NetworkException(application.getString(R.string.http_network_error), e)
            }

            // 有连接就是服务器的问题
            return ServerException(application.getString(R.string.http_server_error), e)
        }
        if (e is IOException) {
            //e = new CancelException(context.getString(R.string.http_request_cancel), e);
            return CancelException("", e)
        }
        return HttpException(e.message, e)
    }

    override fun readCache(httpRequest: HttpRequest<*>, type: Type, cacheTime: Long): Any? {
        val cacheKey = HttpCacheManager.generateCacheKey(httpRequest)
        val cacheValue = HttpCacheManager.getMmkv()!!.getString(cacheKey, null)
        if (cacheValue == null || "" == cacheValue || "{}".equals(cacheValue)) {
            return null
        }
        EasyLog.printLog(httpRequest, "----- readCache cacheKey -----")
        EasyLog.printJson(httpRequest, cacheKey)
        EasyLog.printLog(httpRequest, "----- readCache cacheValue -----")
        EasyLog.printJson(httpRequest, cacheValue)
//        return GsonFactory.getSingletonGson().fromJson(cacheValue, attr.type)
        return GsonFactory.getSingletonGson().fromJson(cacheValue, type)
    }

    override fun writeCache(httpRequest: HttpRequest<*>, response: Response, result: Any): Boolean {
        val cacheKey: String = HttpCacheManager.generateCacheKey(httpRequest)!!
        val cacheValue = GsonFactory.getSingletonGson().toJson(result)
        if (cacheValue == null || "" == cacheValue || "{}".equals(cacheValue)) {
            return false
        }
        EasyLog.printLog(httpRequest, "----- writeCache cacheKey -----")
        EasyLog.printJson(httpRequest, cacheKey)
        EasyLog.printLog(httpRequest, "----- writeCache cacheValue -----")
        EasyLog.printJson(httpRequest, cacheValue)
        return HttpCacheManager.getMmkv()?.putString(cacheKey, cacheValue)?.commit() == true
    }

}
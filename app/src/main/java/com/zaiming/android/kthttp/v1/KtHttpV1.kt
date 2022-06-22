package com.zaiming.android.kthttp.v1

import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`.getRawType
import com.zaiming.android.kthttp.anno.Field
import com.zaiming.android.kthttp.anno.GET
import com.zaiming.android.kthttp.interfaces.KtCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Proxy


object KtHttpV1 {

    private val okHttpClient by lazy { OkHttpClient() }

    private val gson: Gson by lazy { Gson() }

    var baseUrl = "https://baseurl.com"

    fun <T: Any> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { _, method, args ->
            val annotations = method.annotations
            for (annotation in annotations) {
                if (annotation is GET) {
                    val url = baseUrl + annotation.value
                    return@newProxyInstance invoke<T>(url, method, args)
                }
            }
            return@newProxyInstance null

        } as T
    }


     private fun <T: Any> invoke(path: String, method: Method, args: Array<Any>): Any? {

        if (method.parameterAnnotations.size != args.size) return null

        var url = path
        val parameterAnnotations = method.parameterAnnotations

        for (i in parameterAnnotations.indices) {
            for (parameterAnnotation in parameterAnnotations[i]) {
                if (parameterAnnotation is Field) {
                    val key = parameterAnnotation.value
                    val value = args[i].toString()
                    if (!url.contains("?")) {
                        url += "?$key=$value"
                    } else {
                        url += "&$key=$value"
                    }
                }
            }
        }

        val request = Request.Builder()
            .url(url)
            .build()

        val call = okHttpClient.newCall(request)

         return when {
             isKtCallReturn(method) -> {
                 KtCall<T>(call, gson, getTypeArgument(method))
             }

             isFlowReturn(method) -> {
                 flow<T> {
                     val response = okHttpClient.newCall(request).execute()
                     emit(gson.fromJson<T>(response.body?.string(), method.genericReturnType))
                 }
             }

             else -> {
                 val response = okHttpClient.newCall(request).execute()
                 gson.fromJson(response.body?.string(), method.genericReturnType)
             }
         }

    }

    private fun getTypeArgument(method: Method) =
        (method.genericReturnType as ParameterizedType).actualTypeArguments[0]

    private fun isKtCallReturn(method: Method) = getRawType(method.genericReturnType) == KtCall::class.java

    private fun isFlowReturn(method: Method) = getRawType(method.genericReturnType) == Flow::class.java

}
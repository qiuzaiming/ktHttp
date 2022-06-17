package com.zaiming.android.kthttp.v1

import com.google.gson.Gson
import com.zaiming.android.kthttp.anno.Field
import com.zaiming.android.kthttp.anno.GET
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.Proxy


object KtHttpV1 {

    private var okHttpClient = OkHttpClient()

    private var gson: Gson = Gson()

    private var baseUrl = "https://baseurl.com"

    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service)
        ) { proxy, method, args ->
            val annotations = method.annotations

            for (annotation in annotations) {
                if (annotation is GET) {
                    val url = baseUrl + annotation.value
                    return@newProxyInstance invoke(url, method, args!!)
                }
            }

            return@newProxyInstance null

        } as T
    }

    private fun invoke(path: String, method: Method, args: Array<Any>): Any? {

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
        val response = okHttpClient.newCall(request).execute()
        val genericReturnType = method.genericReturnType

        val body = response.body
        val json = body?.string()

        val result = gson.fromJson<Any?>(json, genericReturnType)

        return result
    }


}
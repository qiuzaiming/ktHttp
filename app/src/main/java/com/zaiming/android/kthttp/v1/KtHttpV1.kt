package com.zaiming.android.kthttp.v1

import com.google.gson.Gson
import com.zaiming.android.kthttp.anno.Field
import com.zaiming.android.kthttp.anno.GET
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.reflect.Method
import java.lang.reflect.Proxy


object KtHttpV1 {

    private val okHttpClient by lazy { OkHttpClient() }

    private val gson: Gson by lazy { Gson() }

    var baseUrl = "https://baseurl.com"

    inline fun <reified T> create(): T {
        return Proxy.newProxyInstance(
            T::class.java.classLoader,
            arrayOf(T::class.java)
        ) { proxy, method, args ->

            return@newProxyInstance method.annotations
                .filterIsInstance<GET>()
                .takeIf { it.size == 1 }
                ?.let { invoke("$baseUrl${it.first().value}", method, args) }
        } as T
    }

    fun invoke(url: String, method: Method, args: Array<Any>): Any? {

        return method.parameterAnnotations
            .takeIf { it.size == args.size }
            ?.mapIndexed { index, it -> Pair(it, args[index]) }
            ?.fold(url, ::parseUrl)
            ?.let { Request.Builder().url(it).build() }
            ?.let { okHttpClient.newCall(it).execute().body?.string() }
            ?.let { gson.fromJson(it, method.genericReturnType) }
    }

    private fun parseUrl(acc: String, pair: Pair<Array<Annotation>, Any>) =
        pair.first.filterIsInstance<Field>()
            .first()
            .let { field ->
                if (acc.contains("?")) {
                    "$acc&${field.value}=${pair.second}"
                } else {
                    "$acc?${field.value}=${pair.second}"
                }
            }


}
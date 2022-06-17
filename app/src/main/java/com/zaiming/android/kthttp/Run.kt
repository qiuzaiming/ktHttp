package com.zaiming.android.kthttp

import com.zaiming.android.kthttp.interfaces.ApiService
import com.zaiming.android.kthttp.v1.KtHttpV1


fun main() {

    val apiService = KtHttpV1.create(ApiService::class.java)

    apiService.repos("kotlin", "java")
}
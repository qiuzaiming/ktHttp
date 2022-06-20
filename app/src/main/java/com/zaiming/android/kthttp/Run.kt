package com.zaiming.android.kthttp

import com.zaiming.android.kthttp.bean.RepoList
import com.zaiming.android.kthttp.interfaces.ApiService
import com.zaiming.android.kthttp.interfaces.Callback
import com.zaiming.android.kthttp.v1.KtHttpV1


fun main() {

    KtHttpV1.create(ApiService::class.java)
        .repos(
            lang = "android",
            since = "kotlin"
        ).call(object : Callback<RepoList> {
            override fun onSuccess(data: RepoList) {
                println(data)
            }

            override fun onFail(throwable: Throwable) {
                println(throwable)
            }

        })
}
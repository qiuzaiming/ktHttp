package com.zaiming.android.kthttp

import com.zaiming.android.kthttp.bean.RepoList
import com.zaiming.android.kthttp.extensions.asFlow
import com.zaiming.android.kthttp.extensions.await
import com.zaiming.android.kthttp.interfaces.ApiService
import com.zaiming.android.kthttp.interfaces.Callback
import com.zaiming.android.kthttp.v1.KtHttpV1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking


fun main() {

   /* KtHttpV1.create(ApiService::class.java)
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

        })*/

    runBlocking {
        // suspendCancellableCoroutine
       /* val result = KtHttpV1.create(ApiService::class.java)
            .repos(
                lang = "android",
                since = "kotlin"
            ).await()
             println($result)
            */

        // Flow
        KtHttpV1.create(ApiService::class.java)
            .repos(
                lang = "android",
                since = "kotlin"
            ).asFlow()
            .catch { println("Catch: $it") }
            .collect {
                println(it)
            }

        KtHttpV1.create(ApiService::class.java)
            .reposFlow(
                lang = "android",
                since = "kotlin"
            )
            .flowOn(Dispatchers.IO)
            .catch { println("Catch: $it") }
            .collect {
                println(it)
            }
    }
}
package com.zaiming.android.kthttp.extensions

import com.zaiming.android.kthttp.interfaces.Callback
import com.zaiming.android.kthttp.interfaces.KtCall
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.suspendCoroutine

suspend fun <T: Any> KtCall<T>.await(): T = suspendCancellableCoroutine { continuation ->
    call(object : Callback<T> {
        override fun onSuccess(data: T) {
            continuation.resumeWith(Result.success(data))
        }

        override fun onFail(throwable: Throwable) {
            continuation.resumeWith(Result.failure(throwable))
        }

    })
}
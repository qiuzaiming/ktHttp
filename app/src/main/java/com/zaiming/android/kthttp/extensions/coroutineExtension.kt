package com.zaiming.android.kthttp.extensions

import com.zaiming.android.kthttp.interfaces.Callback
import com.zaiming.android.kthttp.interfaces.KtCall
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.suspendCoroutine

suspend fun <T: Any> KtCall<T>.await(): T = suspendCancellableCoroutine { continuation ->
    val call = call(object : Callback<T> {
        override fun onSuccess(data: T) {
            continuation.resumeWith(Result.success(data))
        }

        override fun onFail(throwable: Throwable) {
            continuation.resumeWith(Result.failure(throwable))
        }

    })

    continuation.invokeOnCancellation {
        call.cancel()
    }
}

fun <T : Any> KtCall<T>.asFlow(): Flow<T> = callbackFlow {
    val call = call(object : Callback<T> {
        override fun onSuccess(data: T) {
            trySendBlocking(data)
                .onSuccess { close() }
                .onFailure { close(it) }
        }

        override fun onFail(throwable: Throwable) {
            close(throwable)
        }
    })

    awaitClose {
        call.cancel()
    }
}
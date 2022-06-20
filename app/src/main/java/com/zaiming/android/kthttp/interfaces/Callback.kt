package com.zaiming.android.kthttp.interfaces

interface Callback<T: Any> {

    fun onSuccess(data: T)

    fun onFail(throwable: Throwable)
}
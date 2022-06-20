package com.zaiming.android.kthttp.interfaces

import com.zaiming.android.kthttp.anno.Field
import com.zaiming.android.kthttp.anno.GET
import com.zaiming.android.kthttp.bean.RepoList

interface ApiService {

    @GET("/repo")
    fun repos(
        @Field("lang") lang: String,
        @Field("since") since: String
    ): KtCall<RepoList>
}
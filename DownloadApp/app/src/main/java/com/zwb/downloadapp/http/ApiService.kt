package com.zwb.rxjava2demo.http

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ApiService {

    @Streaming
    @GET
    fun download(@Url URL:String,@Header("Range") range:String):Observable<ResponseBody>
}
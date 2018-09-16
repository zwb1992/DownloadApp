package com.zwb.rxjava2demo.http

interface DownloadCallBack {
    fun onStart()
    fun onError(code: String, message: String)
    fun onProgress(progress: Int)
    fun onComple()
}
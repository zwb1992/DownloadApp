package com.zwb.rxjava2demo.http

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile

class DownloadUtil(var range: Long = 0, var url: String, var fileDir: String, var fileName: String,var downloadCallBack: DownloadCallBack?) {

    init {
        Log.e("info", "url:$url")
        Log.e("info", "fileDir:$fileDir")
        Log.e("info", "fileName:$fileName")
    }


    fun download() {
        downloadCallBack?.onStart()
        Observable.just(url)
                .map {
                    val responseBody = createBody()
                    if (responseBody == null) {
                        return@map false
                    } else {
                        saveFile(responseBody)
                        return@map true
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :Observer<Boolean>{
                    override fun onComplete() {

                    }

                    override fun onSubscribe(d: Disposable?) {
                    }

                    override fun onNext(value: Boolean?) {
                        Log.e("info", "complete----$value")
                    }

                    override fun onError(e: Throwable?) {
                        Log.e("info", "error----$e")

                    }
                })
    }

    private fun createBody(): ResponseBody? {
        val builder = Request.Builder()
        builder.addHeader("Accept-Encoding", "identity")
                .get()
                .url(url)
        //告诉服务器跳过部分字节开始
        builder.addHeader("RANGE", "bytes=$range-")
        val request = builder.build()
        val call = OkHttpClient().newCall(request)
        return call.execute().body()
    }

    private fun saveFile(responseBody: ResponseBody) {
        var randomAccessFile: RandomAccessFile? = null
        var inputStream: InputStream? = null
        var total = range
        var responseLength = 0L
        try {
            val buf = ByteArray(2048)
            var len: Int
            responseLength = responseBody.contentLength();
            inputStream = responseBody.byteStream()
            val file = File(fileDir, fileName)
            Log.e("info", "文件是否存在 ：${file.exists()}")
            val dir = File(fileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            randomAccessFile = RandomAccessFile(file, "rwd")
            if (range == 0L) {
                randomAccessFile.setLength(responseLength)
            }
            randomAccessFile.seek(range)

            var progress = 0
            var lastProgress: Int
            len = inputStream.read(buf)

            while (len != -1) {
                randomAccessFile.write(buf, 0, len)
                total += len
                lastProgress = progress
                progress = (total * 100 / randomAccessFile.length()).toInt()
                if (progress > 0 && progress != lastProgress) {
                    Log.e("info", "downloading====$progress")
                }
                len = inputStream.read(buf)
            }
            Log.e("info", "downloading====$progress")
        } catch (e: Exception) {
            Log.d("info", "$e")
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close()
                }

                if (inputStream != null) {
                    inputStream.close()
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }
}
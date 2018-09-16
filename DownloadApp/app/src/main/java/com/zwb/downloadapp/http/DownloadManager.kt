package com.zwb.rxjava2demo.http

import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import java.io.File
import java.io.InputStream
import java.io.RandomAccessFile
import java.util.concurrent.TimeUnit


object DownloadManager {
    const val BASE_URL = "http://dldir1.qq.com"
    private val DEFAULT_TIMEOUT = 10L
    var apiService: ApiService

    init {
        apiService = createApi()
    }

    private fun createApi(): ApiService {
        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build()
        val retrofit = Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .build()
        return retrofit.create(ApiService::class.java)
    }


    fun download(range: Long = 0, url: String, fileDir: String, fileName: String):Disposable {
        Log.e("info", "url:$url")
        Log.e("info", "fileDir:$fileDir")
        Log.e("info", "fileName:$fileName")
        val rangeStr = "bytes=$range-"
        return apiService.download(url, rangeStr)
                .map {
                    throw NullPointerException()
                    saveFile(it, range, fileDir, fileName)
                    return@map true
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.e("info", "complete----$it")
                }, {
                    Log.e("info", "error----$it")
                })
    }

    private fun saveFile(responseBody: ResponseBody, range: Long = 0, fileDir: String, fileName: String) {
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
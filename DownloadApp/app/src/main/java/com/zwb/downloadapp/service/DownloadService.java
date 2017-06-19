package com.zwb.downloadapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zwb.downloadapp.bean.FileInfo;
import com.zwb.downloadapp.bean.FileInfoDAO;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadService extends Service {
    public static final int DOWNLOAD_START = 1;
    public static final int DOWNLOAD_STOP = 2;
    private int type = 0;
    private OkHttpClient okHttpClient;
    private Call call;
    private FileInfoDAO fileInfoDao;

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpClient = new OkHttpClient();
        fileInfoDao = new FileInfoDAO(getApplicationContext());
    }

    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            type = intent.getIntExtra("type", DOWNLOAD_START);
            Log.e("info", "--type---" + type + "-----" + fileInfo.toString());
            if (type == DOWNLOAD_START) {
                if (call == null) {
                    download(fileInfo);
                }
            } else {
                resetDownload();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void download(final FileInfo fileInfo) {
        Request.Builder builder = new Request.Builder();
        builder.addHeader("Accept-Encoding", "identity")
                .get()
                .url(fileInfo.getUrl());
        //表示已经下载过但是还未完成（只下载了一部分）
        if (fileInfo.getLength() != 0) {
            //告诉服务器跳过部分字节开始
            builder.addHeader("RANGE", "bytes=" + fileInfo.getCompleted() + "-" + fileInfo.getLength());
        }
        Request request = builder.build();
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                resetDownload();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                ResponseBody body = response.body();
                long length = body.contentLength();
                if (fileInfo.getLength() != 0) {
                    length = fileInfo.getLength();
                } else {
                    fileInfo.setLength(length);
                }
                Log.e("info", "====isSuccessful====" + response.isSuccessful());
                Log.e("info", "====length====" + length);
                BufferedInputStream bInputStream = new BufferedInputStream(body.byteStream());
                long tempLength = fileInfo.getCompleted();
                Log.e("info", "====tempLength====" + tempLength);
                int oldProgress = (int) (tempLength * 100 / length);//原始进度
                byte[] buffer = new byte[1024];
                int len;
                File file = new File(fileInfo.getFileName());
//                //如果路径不存在，创建
//                if (!file.exists()) {
//                    file.createNewFile();
//                }
                //可读写文件
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
                randomAccessFile.setLength(length);
                randomAccessFile.seek(tempLength);

                while ((len = bInputStream.read(buffer)) != -1) {
                    tempLength += len;
                    //把字节写入文件
                    randomAccessFile.write(buffer, 0, len);
                    Log.e("info", "====tempLength====" + tempLength);
                    // 更新数据库实时下载记录
                    fileInfo.setCompleted(tempLength);
                    fileInfoDao.insert(fileInfo);

                    int curProgress = (int) (tempLength * 100 / length);//当前进度
                    //避免更新ui太频繁，进度有增加时才更新
                    if (curProgress > oldProgress) {
                        //通知ui线程更新ui
                        Intent intent = new Intent("download");
                        intent.putExtra("progress", curProgress);
                        sendBroadcast(intent);
                    }
                }
                //关闭文件
                randomAccessFile.close();
                //判断下载结果
                //1,下载完成
                if (tempLength >= length) {
                    //清除数据库下载记录
                    fileInfoDao.delete(fileInfo);
                } else {//2,因为网络或者其他原因导致下载暂停
                    resetDownload();
                }
            }
        });
    }

    /**
     * 重置下载进度
     */
    private void resetDownload() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

}

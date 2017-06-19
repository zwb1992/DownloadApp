package com.zwb.downloadapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.zwb.downloadapp.bean.FileInfo;

public class DownloadService extends Service {
    public static final int DOWNLOAD_START = 1;
    public static final int DOWNLOAD_STOP = 2;
    private int type = 0;
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
        type = intent.getIntExtra("type",DOWNLOAD_START);
        Log.e("info","--type---"+type+"-----"+fileInfo.toString());
        return super.onStartCommand(intent, flags, startId);
    }
}

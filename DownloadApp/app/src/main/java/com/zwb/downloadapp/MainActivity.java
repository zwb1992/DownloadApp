package com.zwb.downloadapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zwb.downloadapp.bean.FileInfo;
import com.zwb.downloadapp.bean.FileInfoDAO;
import com.zwb.downloadapp.service.DownloadService;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.pb)
    ProgressBar pb;

    //    private static final String url = "http://sw.bos.baidu.com/sw-search-sp/software/a40ee9c29a4dd/QQ_8.9.3.21149_setup.exe";
//    private static final String url = "http://dlsw.baidu.com/sw-search-sp/soft/df/17757/popogame-2.0.66386.exe?version=1919350122";
    private static final String url = "http://dlsw.baidu.com/sw-search-sp/soft/1a/30708/TankHero_4399_Ver1.0.0.0.1406601180.exe";
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "qq.exe";
    @BindView(R.id.tvProgress)
    TextView tvProgress;
    private FileInfo fileInfo;
    private DownloadReceiver downloadReceiver;
    private FileInfoDAO fileInfoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvName.setText("腾讯qq");
        fileInfo = new FileInfo(1, url, path, 0, 0);
        downloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter("download");
        registerReceiver(downloadReceiver, filter);
        fileInfoDAO = new FileInfoDAO(getApplicationContext());
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    @OnClick({R.id.btStart, R.id.btStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btStart:
                Log.e("info", "----" + fileInfoDAO.get(fileInfo));
                Intent startIntent = new Intent(this, DownloadService.class);
                startIntent.putExtra("fileInfo", fileInfoDAO.get(fileInfo));
                startIntent.putExtra("type", DownloadService.DOWNLOAD_START);
                startService(startIntent);
                break;
            case R.id.btStop:
                Intent stopIntent = new Intent(this, DownloadService.class);
                stopIntent.putExtra("fileInfo", fileInfo);
                stopIntent.putExtra("type", DownloadService.DOWNLOAD_STOP);
                startService(stopIntent);
                break;
        }
    }

    //下载进度回调广播
    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("progress", 0);
            pb.setProgress(progress);
            tvProgress.setText(progress + "%");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
    }

}

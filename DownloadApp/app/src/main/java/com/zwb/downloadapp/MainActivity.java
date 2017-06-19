package com.zwb.downloadapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zwb.downloadapp.bean.FileInfo;
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

    private static final String url = "https://www.baidu.com/link?url=9Iprr-nXOUOWceTnNS0RahZC57heKq6oYRJTY-YeP0z96FXx3N5XzVf-dZ_Cka4N5USwjAEe3wdXOm5zsxrt4Iu2Wa_e1f3vskTgNg5tkUHON2moiiXWvOU1h8WSxM8g&wd=&eqid=fac4316f00017c4200000006594647f1";
    private static final String path = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"kugou.exe";
    private FileInfo fileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvName.setText("酷狗音乐");
        fileInfo = new FileInfo(1, url, path, 0, 0);
    }

    @OnClick({R.id.btStart, R.id.btStop})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btStart:
                Intent startIntent = new Intent(this, DownloadService.class);
                startIntent.putExtra("fileInfo",fileInfo);
                startIntent.putExtra("type",DownloadService.DOWNLOAD_START);
                startService(startIntent);
                break;
            case R.id.btStop:
                Intent stopIntent = new Intent(this, DownloadService.class);
                stopIntent.putExtra("fileInfo",fileInfo);
                stopIntent.putExtra("type",DownloadService.DOWNLOAD_STOP);
                startService(stopIntent);
                break;
        }
    }
}

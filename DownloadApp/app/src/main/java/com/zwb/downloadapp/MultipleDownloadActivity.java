package com.zwb.downloadapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.zwb.downloadapp.adapter.DownloadAdapter;
import com.zwb.downloadapp.bean.FileInfo;
import com.zwb.downloadapp.bean.FileInfoDAO;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 多线程下载文件
 */
public class MultipleDownloadActivity extends AppCompatActivity {
    @BindView(R.id.listView)
    ListView listView;
    private List<FileInfo> fileInfos;
    private static final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private FileInfoDAO fileInfoDAO;
    private DownloadAdapter adapter;
    private DownloadReceiver downloadReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_download);
        ButterKnife.bind(this);
        downloadReceiver = new DownloadReceiver();
        IntentFilter filter = new IntentFilter("multipleDownload");
        registerReceiver(downloadReceiver, filter);
        fileInfoDAO = FileInfoDAO.getInstance(this);
        initData();
        adapter = new DownloadAdapter(this, fileInfos);
        listView.setAdapter(adapter);

        handler.postDelayed(runnable, 500);
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
            handler.postDelayed(this, 500);//每个0.5秒钟刷新一次界面，避免因开启下载的线程过多导致刷新频繁
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloadReceiver);
        handler.removeCallbacks(runnable);
    }

    private void initData() {
        fileInfos = new ArrayList<>();
        FileInfo fileInfo = new FileInfo(1,
                "http://dlsw.baidu.com/sw-search-sp/soft/1a/30708/TankHero_4399_Ver1.0.0.0.1406601180.exe",
                basePath + "TankHero.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://dlsw.baidu.com/sw-search-sp/soft/81/30712/gamebox_setup_2.2.3.602.1425610754.exe",
                basePath + "gamebox.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://sw.bos.baidu.com/sw-search-sp/software/2ae995961767e/aqyyx_2.2.1.73.exe",
                basePath + "aqyyx.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://dlsw.baidu.com/sw-search-sp/soft/2f/21205/YLXKsetup_6.0.5.1428912503.exe",
                basePath + "ylxk.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://dlsw.baidu.com/sw-search-sp/soft/85/22235/10000177.1375530509.exe",
                basePath + "shuiguopai.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://sw.bos.baidu.com/sw-search-sp/software/2fca20da41628/utgame5.2.2.0_setup.exe",
                basePath + "utgame.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://sw.bos.baidu.com/sw-search-sp/software/6934b563446e0/XLGameBox_4.5.1.44.exe",
                basePath + "xlgamebox.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));

        fileInfo = new FileInfo(1,
                "http://sw.bos.baidu.com/sw-search-sp/software/6d76a632c18ba/KGGW_6.1.0.8100.exe",
                basePath + "kggw.exe", 0, 0);
        fileInfos.add(fileInfoDAO.get(fileInfo));
    }

    //下载进度回调广播
    class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("multipleDownload".equals(intent.getAction())) {
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                for (FileInfo info : fileInfos) {
                    if (fileInfo.getUrl().equals(info.getUrl())) {
                        info.setLength(fileInfo.getLength());
                        info.setCompleted(fileInfo.getCompleted());
                        break;
                    }
                }
            }
        }
    }
}

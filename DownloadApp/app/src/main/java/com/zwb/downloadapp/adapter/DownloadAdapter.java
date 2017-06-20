package com.zwb.downloadapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zwb.downloadapp.R;
import com.zwb.downloadapp.bean.FileInfo;
import com.zwb.downloadapp.bean.FileInfoDAO;
import com.zwb.downloadapp.service.DownloadService;

import java.util.List;

/**
 * Created by zwb
 * Description
 * Date 2017/6/20.
 */

public class DownloadAdapter extends BaseAdapter {
    private Context mContext;
    private List<FileInfo> fileInfos;
    private FileInfoDAO fileInfoDAO;

    public DownloadAdapter(Context mContext, List<FileInfo> fileInfos) {
        this.mContext = mContext;
        this.fileInfos = fileInfos;
        fileInfoDAO = FileInfoDAO.getInstance(mContext.getApplicationContext());
    }

    @Override
    public int getCount() {
        return fileInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return fileInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.download_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final FileInfo fileInfo = fileInfos.get(position);
        viewHolder.tvName.setText(fileInfo.getFileName());
        if (fileInfo.getLength() != 0) {
            int progress = (int) (fileInfo.getCompleted() * 100 / fileInfo.getLength());
            viewHolder.tvProgress.setText(progress + "%");
            viewHolder.pb.setProgress(progress);
        } else {
            viewHolder.tvProgress.setText("0%");
            viewHolder.pb.setProgress(0);

        }
        viewHolder.btStart.setTag(position);
        viewHolder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (Integer) (v.getTag());
                FileInfo info = fileInfos.get(index);
                Intent startIntent = new Intent(mContext, DownloadService.class);
                startIntent.putExtra("fileInfo", fileInfoDAO.get(info));
                startIntent.putExtra("type", DownloadService.DOWNLOAD_START);
                mContext.startService(startIntent);
            }
        });

        viewHolder.btStop.setTag(position);
        viewHolder.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = (Integer) (v.getTag());
                FileInfo info = fileInfos.get(index);
                Intent startIntent = new Intent(mContext, DownloadService.class);
                startIntent.putExtra("fileInfo", fileInfoDAO.get(info));
                startIntent.putExtra("type", DownloadService.DOWNLOAD_STOP);
                mContext.startService(startIntent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvProgress;
        public Button btStart, btStop;
        public ProgressBar pb;

        public ViewHolder(View view) {
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvProgress = (TextView) view.findViewById(R.id.tvProgress);
            btStart = (Button) view.findViewById(R.id.btStart);
            btStop = (Button) view.findViewById(R.id.btStop);
            pb = (ProgressBar) view.findViewById(R.id.pb);
        }
    }
}

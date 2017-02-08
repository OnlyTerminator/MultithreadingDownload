package com.zxf.multithreadingdownload.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zxf.downloadmaster.callback.DownloadProgressCallBack;
import com.zxf.downloadmaster.callback.OnDownload;
import com.zxf.downloadmaster.download.CustomDownloadManager;
import com.zxf.multithreadingdownload.R;
import com.zxf.multithreadingdownload.date.FileInfo;

import java.io.File;
import java.util.List;

/**
 * Created by luoss on 2016/5/15.
 */
public class ListViewAdapter extends BaseAdapter {
    private static String TAG = "ListViewAdapter";
    private Context mContext;
    private List<FileInfo> mList;

    public ListViewAdapter(Context mContext, List<FileInfo> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final FileInfo info = mList.get(position);
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_file_list, null);
            holder = new ViewHolder();
            holder.tvFileName = (TextView) convertView.findViewById(R.id.tv_file_name);
            holder.pbProgress = (ProgressBar) convertView.findViewById(R.id.pb_progress_bar);
            holder.btStart = (Button) convertView.findViewById(R.id.btn_start);
            holder.btStop = (Button) convertView.findViewById(R.id.btn_stop);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvFileName.setText(info.getFileName());
        holder.pbProgress.setMax(100);

        holder.pbProgress.setTag(info.getUrl());
        final OnDownloadImpl impl = new OnDownloadImpl(holder.pbProgress,position);
        //start 设置开始下载点击监听
        holder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: start download file");
                CustomDownloadManager.getInstance(mContext).startDownload(info.getUrl(), null, null, impl);
            }
        });

        //end 设置停止下载点击监听
        holder.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: stop download file");
            }
        });
        //downloading 设置进度条
        holder.pbProgress.setProgress(info.getFinished());  //设置进度条
        return convertView;
    }

    static class ViewHolder {
        TextView tvFileName;
        ProgressBar pbProgress;
        Button btStart;
        Button btStop;
    }

    /**
     * 下载回调监听类
     */
    class OnDownloadImpl implements OnDownload {
        //        public String url;
        public ProgressBar pb; //进度条
        public int position;  //列表位置

        public OnDownloadImpl(ProgressBar pb ,int posision) {
//            this.url = url;
            this.pb = pb;
            this.position = posision;
        }

        @Override
        public void onDownloading(String retUrl, int finished) {
            String pbUrl = (String) pb.getTag();
            if(retUrl.equals(pbUrl)) {
                pb.setProgress(finished);                   //设置进度条进度
                mList.get(position).setFinished(finished); //保存进度
            }
        }

        @Override
        public void onDownloadFinished(File downloadFile) {

        }
    }
}

package com.zxf.downloadmaster.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zxf.downloadmaster.callback.DownloadProgressCallBack;
import com.zxf.downloadmaster.callback.OnDownload;
import com.zxf.downloadmaster.db.DownloadInfo;

/**
 * Created by aotuman on 2017/2/7.
 */

public class CustomDownloadManager {
    private static String TAG = "CustomDownloadManager";
    private DownloadService mDownloadService;
    private static CustomDownloadManager mInstance = null;
    private Context mContext;

    /**
     * @param context
     */
    private CustomDownloadManager(Context context) {
        this.mContext = context;
        init();
    }


    /**
     * 获取实例
     *
     * @param context
     * @return
     */
    public synchronized static CustomDownloadManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (CustomDownloadManager.class) {
                if (null == mInstance) {
                    mInstance = new CustomDownloadManager(context);
                }
            }
        }
        return mInstance;
    }

    private void init() {

    }

    /**
     * @param url      download url
     * @param filePath file save path
     * @param fileName file name
     */
    public void startDownload(String url, String filePath, String fileName, OnDownload onDownload) {
        if (TextUtils.isEmpty(url)) {
            Log.i(TAG, "startDownload: url is null");
            return;
        }
        DownloadInfo info = new DownloadInfo(url, filePath, fileName, 0,onDownload); //建立下载任务对象信息
        if (mDownloadService == null) {  //服务未绑定
            mDownloadService = new DownloadService();
        }
        mDownloadService.download(info);   //服务已经绑定，直接下载
    }
}

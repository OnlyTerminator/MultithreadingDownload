package com.zxf.downloadmaster.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zxf.downloadmaster.db.DownloadInfo;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadManager {
    private static String TAG = "DownloadManager";
    private DownloadService mDownloadService;
    private static DownloadManager mInstance = null;
    private Context mContext;

    /**
     * @param context
     */
    private DownloadManager(Context context) {
        this.mContext = context;
        init();
    }


    /**
     * 获取实例
     *
     * @param context
     * @return
     */
    public synchronized static DownloadManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DownloadManager.class) {
                if (null == mInstance) {
                    mInstance = new DownloadManager(context);
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
    public void startDownload(String url, String filePath, String fileName) {
        if (TextUtils.isEmpty(url)) {
            Log.i(TAG, "startDownload: url is null");
            return;
        }
        DownloadInfo info = new DownloadInfo(url, filePath, fileName, 0); //建立下载任务对象信息
        if (mDownloadService == null) {  //服务未绑定
            mDownloadService = new DownloadService();
        }
        mDownloadService.download(info);   //服务已经绑定，直接下载
    }
}

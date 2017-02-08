package com.zxf.downloadmaster.db;

import com.zxf.downloadmaster.callback.OnDownload;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadInfo {
    public String url;
    public String filePath;
    public String fileName;
    public int lenght;
    public OnDownload onDownload;

    public DownloadInfo(String url, String filePath,String fileName,int lenght,OnDownload onDownload) {
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
        this.lenght = lenght;
        this.onDownload = onDownload;
    }
}

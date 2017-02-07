package com.zxf.downloadmaster.db;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadInfo {
    public String url;
    public String filePath;
    public String fileName;
    public int lenght;

    public DownloadInfo(String url, String filePath,String fileName,int lenght) {
        this.url = url;
        this.filePath = filePath;
        this.fileName = fileName;
        this.lenght = lenght;
    }
}

package com.zxf.downloadmaster.download;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zxf.downloadmaster.db.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aotuman on 2017/2/7.
 */

public class InitThread extends Thread {
    private static final String TAG = "InitThread";
    private static final String DOWNLOAD_FILE = "Download";
    private DownloadInfo mDownloadInfo;
    private Context mContext;
    private ExecutorService mThreadPool;
    public InitThread (Context context, DownloadInfo downloadInfo, ExecutorService es) {
        mDownloadInfo = downloadInfo;
        mContext = context;
        mThreadPool = es;
    }
    @Override
    public void run() {
        super.run();
        Log.i(TAG, "run: ");
        try {
            URL url = new URL(mDownloadInfo.url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            if(con.getResponseCode() ==  HttpURLConnection.HTTP_OK) {
                int len = con.getContentLength(); //文件的总长度
                mDownloadInfo.lenght = len;
                if(len <= 0) {
                    return;
                }
                String downloadUrl = mDownloadInfo.url;
                //start 设置存放文件的路径
                String filePath = mDownloadInfo.filePath;
                if(TextUtils.isEmpty(filePath)) {  //没有设置路径
                    filePath = getFilePath();      //获取默认的路径
                }else {
                    File file = new File(filePath);
                    if(!file.exists()) {      //确保存在相应的文件夹
                        file.mkdirs();
                    }
                }
                mDownloadInfo.filePath = filePath;
                //end 设置存放文件的路径

                //start 设置文件名
                String fileName = mDownloadInfo.fileName;
                if(TextUtils.isEmpty(fileName)) {                                       //没有设置文件名
                    fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1); //下载链接后缀名为文件名
                }
                fileName.trim();
                if(TextUtils.isEmpty(fileName)) {
                    fileName = getFileName(con);   //根据http参数获取文件名
                }
                mDownloadInfo.fileName = fileName;
                //end 设置文件名

                //start 设置下载文件
                RandomAccessFile accessFile = new RandomAccessFile(new File(mDownloadInfo.filePath,mDownloadInfo.fileName),"rwd");
                accessFile.setLength(len); //设置文件长度
                accessFile.close();
                //end 设置下载文件
                con.disconnect();
//                mHandler.sendMessage(mHandler.obtainMessage(MSG_INIT,taskInfo));
                DownloadTask task = new DownloadTask(mContext,mDownloadInfo,mThreadPool,3); //建立下载任务
                task.downlaod();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件名
     * @param conn
     * @return
     */
    private String getFileName(HttpURLConnection conn) {
        String filename = null;

        if(filename==null || "".equals(filename.trim())){//如果获取不到文件名称
            for (int i = 0;; i++) {
                String mine = conn.getHeaderField(i);

                if (mine == null) break;

                if("content-disposition".equals(conn.getHeaderFieldKey(i).toLowerCase())){
                    Matcher m = Pattern.compile(".*filename=(.*)").matcher(mine.toLowerCase());
                    if(m.find()) return m.group(1);
                }
            }

            filename = UUID.randomUUID()+ ".tmp";//默认取一个文件名
        }

        return filename;
    }

    /**
     * 获取下载目录
     * @return
     */
    private  String getFilePath() {
        String path = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
//            path = getExternalCacheDir().getPath();
            path = Environment.getExternalStorageDirectory().getPath();
            Log.i(TAG,"path =========== "+path);

        } else {
            path = mContext.getFilesDir().getAbsolutePath();

        }
        File downloadFile = new File(path,DOWNLOAD_FILE);
        if(!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        path  = downloadFile.getAbsolutePath();

        return path;

    }
}

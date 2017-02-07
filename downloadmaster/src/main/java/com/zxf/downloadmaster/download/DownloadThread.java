package com.zxf.downloadmaster.download;

import android.util.Log;

import com.zxf.downloadmaster.db.DownloadInfo;
import com.zxf.downloadmaster.db.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadThread extends Thread {
    private static String TAG = "DownloadThread";
    private ThreadInfo threadInfo;  //线程信息
    private DownloadInfo downloadInfo;
    private int [] mTotalFinished;
    public DownloadThread(ThreadInfo threadInfo, DownloadInfo downloadInfo,int [] progress) {
        this.threadInfo = threadInfo;
        this.downloadInfo = downloadInfo;
        mTotalFinished = progress;
        Log.i(TAG,"thread info = "+threadInfo+":download info "+downloadInfo);
    }

    @Override
    public void run() {
        URL url = null;
        HttpURLConnection con = null;      //http链接
        RandomAccessFile accessFile = null; //下载文件
        InputStream inputStream = null;      //输入流
        try {

            int start = threadInfo.start+threadInfo.finished; //读取文件的位置
            //start 初始化下载链接
            url = new URL(threadInfo.url);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.end); //设置读取文件的位置，和结束位置
            //end 初始化下载链接
            //start 初始化下载到本地的文件
            accessFile  = new RandomAccessFile(new File(downloadInfo.filePath, downloadInfo.fileName),"rwd");
            accessFile.seek(start);    //设置开始写入的位置
            //end 初始化下载到本地的文件

            int responseCode = con.getResponseCode();
            if((con.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) ||
                    (con.getResponseCode() == HttpURLConnection.HTTP_OK) ) {
                inputStream = con.getInputStream();
                int finished = threadInfo.finished;               //已经下载的长度
//                        int len = threadInfo.getEnd()-threadInfo.getStart();  //本线程要下载的长度
                int readLen = -1;                                       //读取的长度
                byte[] buffer = new byte[1024*4];
                long time = System.currentTimeMillis();

                //start 读取输入流写入文件
                while((readLen = inputStream.read(buffer))!=-1) {
                    accessFile.write(buffer, 0, readLen);
//                            Log.i(tag, "readLen = " + readLen);
                    finished += readLen;
                    threadInfo.finished = finished;    //设置已经下载进度
                    if(System.currentTimeMillis() - time >2000) {
//                                Log.i(tag, "readLen = " + readLen);
                        notifyProgress(threadInfo.id, finished); //每隔2秒通知下载进度
                        time = System.currentTimeMillis();
                    }
                    //start 停止下载，保存进度
//                    if(isPause) {
//                        Log.i(TAG,"pause name = "+downloadInfo.fileName);
////                        notifyProgress(threadInfo.getId(), finished);        //通知下载进度
////                        mThreadDao.updateThread(threadInfo.getUrl(),threadInfo.getId(),finished);  //更新数据库对应的线程信息
//                        return;
//                    }
                    //end 停止下载，保存进度
                }
                //end 读取输入流写入文件

//                        mThreadDao.updateThread(threadInfo.getUrl(),threadInfo.getId(),finished);
//                        isFinished = true;
//                        checkIsAllThreadFinished();
//                        broadcastFinished(threadInfo.getId(),finished);
//                notifyProgress(threadInfo.getId(),finished);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                if(inputStream!=null){
                    inputStream.close();
                }
                if(accessFile!=null) {
                    accessFile.close();
                }
                if(null!=con) {
                    con.disconnect();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        super.run();
    }

    /**
     * 通知下载进度
     * @param threadId  线程id
     * @param finished  已经完成的进度
     */
    private synchronized void notifyProgress(int threadId,int finished) {

        if(threadId>=0 && threadId < mTotalFinished.length) {
            mTotalFinished[threadId] = finished;
            int nowFinished = 0;
            for(int i = 0; i<mTotalFinished.length;i++) {
                nowFinished += mTotalFinished[i];
            }
 /*               Log.i(tag,"lastFinished = "+lastFinished+",nowFinished = "+nowFinished);
                if(lastFinished>=nowFinished){
                    return;
                }*/
            int progress = (int) (((float) nowFinished / (float) downloadInfo.lenght) * 100);
            Log.i(TAG, "notifyProgress: "+progress);

             /*   intent.setAction(DownloadService.ACTION_UPDATE);
//                                intent.putExtra("finished", (int) (((float) finished / (float) len) * 100));
                intent.putExtra("finished", (int) (((float) tatolFinished / (float) mTaskInfo.getLenght()) * 100));
                mContext.sendBroadcast(intent);*/
        }
    }
}

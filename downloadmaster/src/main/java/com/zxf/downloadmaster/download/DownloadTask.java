package com.zxf.downloadmaster.download;

import android.content.Context;
import android.os.Handler;

import com.zxf.downloadmaster.db.DownloadInfo;
import com.zxf.downloadmaster.db.ThreadInfo;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadTask {
    private static String TAG = "DownloadTask";
    /**
     * 默认启用线程的数量
     **/
    private static final int DEFAULT_THREAD_COUNT = 3;
    /**
     * 下载进度消息
     **/
    private static final int MSG_DOWNLOADING = 0x01;
    private Context mContext;
    /**
     * 下载任务信息
     **/
    private DownloadInfo mDownloadInfo;
    /**线程数据库操作**/
//    private ThreadDAOImpl mThreadDao;
    /**
     * 停止下载标志位
     **/
    private boolean isPause = false;
    /**
     * 启用下载线程数量
     **/
    private int mThreadCount = DEFAULT_THREAD_COUNT;
    /***
     * 保存各个线程的下载进度
     **/
    private int[] mTotalFinished;
    private Handler mHandler;
    /**
     * 线程池
     **/
    private ExecutorService mThreadPool;
    /**
     * 下载链接
     **/
    private String mStrDownloadUrl;

    /**
     * 构造函数
     *
     * @param mContext    上下文
     * @param downloadInfo    任务信息
     * @param threadPool  线程池
     * @param threadCount 开启的下载线程数
     */
    public DownloadTask(Context mContext, DownloadInfo downloadInfo, ExecutorService threadPool, int threadCount) {
        this.mContext = mContext;
        this.mDownloadInfo = downloadInfo;
        this.mThreadPool = threadPool;
        this.mStrDownloadUrl = downloadInfo.url;
        mTotalFinished = new int[mThreadCount];
//        init(threadCount);
    }

    /**
     * 启动下载
     */
    public void downlaod() {

        List<ThreadInfo> threadInfoList = new LinkedList<ThreadInfo>(); //建立线程信息列表
//        threadInfoList = mThreadDao.getThread(mTaskInfo.getUrl());  //从数据库中取出对应的线程信息

        //start 数据库没有对应的线程信息，则创建相应的线程信息
//        if(threadInfoList.size() <=0) {
            int block = mDownloadInfo.lenght/mThreadCount; //将下载文件分段
            if(block > 0) {
                //start 根据线程数量分别建立线程信息
                for(int i = 0;i < mThreadCount;i++) {
                    ThreadInfo info = new ThreadInfo(i,mDownloadInfo.url,i*block,(i+1)*block-1,0);
                    if(i == mThreadCount -1) {
                        info.end = mDownloadInfo.lenght; //分段最后一个，结束位置到文件总长度末尾
                    }
                    threadInfoList.add(info);         //加入列表
//                    mThreadDao.insertThread(info);   //向数据库插入线程信息
                }
                //end 根据线程数量分别建立线程信息
//            }else {
//                ThreadInfo info = new ThreadInfo(0,mTaskInfo.getUrl(),0,mTaskInfo.getLenght(),0);
//                threadInfoList.add(info);
//                mThreadDao.insertThread(info);
//            }
        }
        //end 数据库中没有对应的线程信息，则创建相应的线程信息

        //start 启动下载线程
        for(ThreadInfo info : threadInfoList) {

            DownloadThread thread = new DownloadThread(info,mDownloadInfo,mTotalFinished);
            if(!mThreadPool.isShutdown()) {
                mThreadPool.execute(thread);
            }

        }
        //end 启动下载线程
    }
}

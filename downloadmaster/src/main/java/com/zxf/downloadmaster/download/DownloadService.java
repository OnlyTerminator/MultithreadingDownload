package com.zxf.downloadmaster.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.zxf.downloadmaster.callback.DownloadProgressCallBack;
import com.zxf.downloadmaster.db.DownloadInfo;

import java.io.File;
import java.io.FileFilter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by aotuman on 2017/2/7.
 */

public class DownloadService extends Service{
    /***线程池**/
    private ExecutorService mThreadPool ;
    /** cpu核心数默认值 */
    private static final int DEFAULT_CPU_CORES = 2;

    private DownloadBinder mBinder;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if(null == mBinder){
            mBinder = new DownloadBinder();
        }
        return mBinder;
    }

    /**
     * 下载
     * 首先查找任务列表中对应的任务，如果没有找到则初始化新的下载任务
     * @param info 下载任务信息
     */
    public void download(DownloadInfo info) {
        //start 查找任务列表中对应的任务
//        for(DownloadTask task1 : mListTask) {
//            if(task1.getUrl().equals(info.getUrl())) {
//                if(task1.isPaused()) {
//                    task1.restart();   //任务已经停止，重新开始
//                }
//                return;
//            }
//        }
        //end 查找任务列表中对应的任务
        mThreadPool = Executors.newFixedThreadPool(getNumberOfCPUCores());  //初始化线程
        //初始化新的下载任务
        InitThread thread = new InitThread(DownloadService.this,info,mThreadPool);
        if(!mThreadPool.isShutdown()) {
            mThreadPool.execute(thread);
        }

    }

    /**
     * 获取cup核心数
     * @return
     */
    public static int getNumberOfCPUCores() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            return 1;
        }
        int cores;
        try {
            cores = new File("/sys/devices/system/cpu/").listFiles(CPU_FILTER).length;
        } catch (SecurityException e) {
            cores = DEFAULT_CPU_CORES;
        } catch (NullPointerException e) {
            cores = DEFAULT_CPU_CORES;
        }
        return cores;
    }

    private static final FileFilter CPU_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            String path = pathname.getName();
            //regex is slow, so checking char by char.
            if (path.startsWith("cpu")) {
                for (int i = 3; i < path.length(); i++) {
                    if (path.charAt(i) < '0' || path.charAt(i) > '9') {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    };

    /**
     * 绑定服务类
     */
    public class DownloadBinder extends Binder {
        public  DownloadService getService () {
            return DownloadService.this;
        }
    }
}

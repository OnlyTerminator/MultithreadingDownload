package com.zxf.downloadmaster.callback;

/**
 * Created by 凹凸曼 on 2017/2/8.
 */

public interface DownloadProgressCallBack {
    void downloadSuccess();

    void downloadProgress(int done, int totality);

    void downloadFailed();
}

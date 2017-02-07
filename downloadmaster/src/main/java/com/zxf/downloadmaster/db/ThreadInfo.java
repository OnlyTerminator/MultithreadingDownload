package com.zxf.downloadmaster.db;

/**
 * Created by luoss on 2016/5/13.
 */
public class ThreadInfo {

    public int id;
    public String url;
    public int start;
    public int end;
    public int finished;

    /**
     * 构造方法
     * @param id          线程id
     * @param url         下载链接
     * @param start       开始下载位置
     * @param end         下载结束位置
     * @param finished    总共已经完成
     */
    public ThreadInfo(int id, String url, int start, int end, int finished) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.finished = finished;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", finished=" + finished +
                '}';
    }
}

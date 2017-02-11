package com.zxf.multithreadingdownload;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.zxf.downloadmaster.download.CustomDownloadManager;
import com.zxf.multithreadingdownload.adapter.ListViewAdapter;
import com.zxf.multithreadingdownload.date.FileInfo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView listView;
    private ListViewAdapter adapter = null;
    private List<FileInfo> fileList = new ArrayList<FileInfo>();
    private String[] mUrls = new String[]{
            "http://www.lifeofpix.com/wp-content/uploads/2016/04/life-of-pix-free-stock-wall-windows-jackiehadel.jpg",
            "http://cncspace.newhua.com/down/MTVAlbum_Demo.rar",
            "http://lt.newhua.com/down2/FlashAlbum_Demo.rar",
            "http://wta.newhua.com/down/weixin6316android780.apk",
            "http://cncspace.newhua.com/down/kingma_zp.rar"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initDate();
    }

    private void initDate() {
        for(int i=0 ;i<mUrls.length; i++) {
            String fileName = mUrls[i].substring(mUrls[i].lastIndexOf("/")+1);
            FileInfo info = new FileInfo(i,mUrls[i],fileName,0,0);
            fileList.add(info);
        }
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.lv_content);
        adapter = new ListViewAdapter(this,fileList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CustomDownloadManager.getInstance(this).destroy();
    }
}

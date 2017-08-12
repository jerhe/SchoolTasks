package com.edu.schooltask.application;

import android.app.Application;
import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;

import org.litepal.LitePalApplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import c.b.BP;
import okhttp3.OkHttpClient;
import okio.Buffer;
import server.api.SchoolTask;

/**
 * Created by 夜夜通宵 on 2017/5/17.
 */

public class MyApplication extends LitePalApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        BP.init("b8423903660c0d5aa0f0bcee7af3fb09");
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)    //连接超时时间
                .readTimeout(15, TimeUnit.SECONDS)       //读取超时时间
                .retryOnConnectionFailure(true)         //是否重试
                .build();
        OkHttpUtils.initClient(client); //设置为默认client
    }
}

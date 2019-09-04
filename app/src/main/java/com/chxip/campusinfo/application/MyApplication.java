package com.chxip.campusinfo.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.chxip.campusinfo.util.SharedTools;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


/**
 * Created by Administrator on 2016/8/20.
 */
public class MyApplication extends Application{
    /**
     * volley框架请求队列
     */
    private static RequestQueue requestQueue;

    /**
     * 返回网络请求队列
     * @return
     */
    public static RequestQueue getRequestQueue(){
        return requestQueue;
    }



    /**
     * OKhttp
     */
    private static OkHttpClient mOkHttpClient;
    /**
     * 返回OKhttp网络请求队列
     * @return
     */
    public static OkHttpClient getOkHttpClient(){
        return mOkHttpClient;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }



    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        //初始化请求队列
        requestQueue = Volley.newRequestQueue(this.getApplicationContext());
        //SharedPreferences 初始化
        SharedTools.sp=this.getSharedPreferences("CampusInfo", Context.MODE_PRIVATE);


        buildHttpClient();
    }


    private void buildHttpClient() {
        this.mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(8000, TimeUnit.MILLISECONDS)
                .readTimeout(8000, TimeUnit.MILLISECONDS)
                .writeTimeout(8000, TimeUnit.MILLISECONDS)
                .build();
    }


}

package com.chxip.campusinfo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by Administrator on 2017/7/21.
 */
public class DownloadApk {

    public static ProgressDialog pd;

    static Context mContext;

    public static  boolean download=true;
    /*
 * 从服务器中下载APK
 */
    public static void downLoadApk(final Context context, final String path, final String appName) {
        mContext=context;
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pd.dismiss();
                download=false;
                ToastUtil.show(context,"下载取消");
            }
        });
        pd.show();
        new Thread(){
            @Override
            public void run() {
                try {
                    File file = getFileFromServer(path, pd,appName);
                    if(download){
                        if(file==null){
                            handler.sendEmptyMessage(1);
                        }else{
                            installApk(file,context);
                            pd.dismiss(); //结束掉进度条对话框
                        }
                    }else{
                        if(file.exists()){
                            file.delete();
                        }
                    }

                } catch (Exception e) {
                    handler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }}.start();
    }

    //安装apk
    public static void installApk(File file, Context context) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static File getFileFromServer(String path, ProgressDialog pd,String appName) throws Exception {
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            //获取到文件的大小
            pd.setMax(conn.getContentLength());

            InputStream is = conn.getInputStream();
            String folderPath= Environment.getExternalStorageDirectory().getPath() + "/AppStore/apk";//+System.currentTimeMillis() + ".apk"
            if(folderPath.equals("")){
                return null;
            }
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String filePath= Environment.getExternalStorageDirectory().getPath() + "/AppStore/apk/"+appName + ".apk";
            File file=new File(filePath);
            if (!folder.exists()) {
                file.createNewFile() ;
            }
            MLog.i("filePath:"+ Environment.getExternalStorageDirectory());

            FileOutputStream fos = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            byte[] buffer = new byte[1024];
            int len ;
            int total=0;
            while((len =bis.read(buffer))!=-1){
                fos.write(buffer, 0, len);
                total+= len;
                //获取当前下载量
                if(download){
                    pd.setProgress(total);
                }

            }
            fos.close();
            bis.close();
            is.close();

            return file;
        } else{
            return null;
        }
    }

    static Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    ToastUtil.show(mContext,"下载失败,请重试!");
                    if(pd!=null)
                        pd.dismiss();
                    break;
            }

        }
    };

}

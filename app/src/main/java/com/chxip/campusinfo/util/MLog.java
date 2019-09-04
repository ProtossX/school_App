package com.chxip.campusinfo.util;

import android.util.Log;

/**
 * Created by 陈湘平 on 2016/8/16.
 *
 * 打印Log
 */
public class MLog {

    public static final boolean DEBUG = true;
    public static final String PUBLIC_TAG = "CheckCarAssistant";

    public static int i(String tag, String msg) {
        if (DEBUG) {
            return Log.i(tag, msg);
        } else {
            return 0;
        }
    }

    public static int i(String msg) {
        if (DEBUG) {
            if(msg==null){
                msg="";
            }
            msg = msg.trim();
            int index = 0;
            int maxLength =3000;

            String sub;
            while (index < msg.length()) {
                // java的字符不允许指定超过总的长度end
                if (msg.length() <= index + maxLength) {
                    sub = msg.substring(index,msg.length());
                } else {
                    sub = msg.substring(index, index+maxLength);
                }
                index = index+maxLength;
                Log.i(PUBLIC_TAG, sub.trim());
            }
            return 1;

        } else {
            return 0;
        }
    }

    public static int e(String tag, Throwable throwable) {
        if (DEBUG) {
            return Log.e(tag, "", throwable);
        } else {
            return 0;
        }
    }

    public static int e(String tag, String msg) {
        if (DEBUG) {
            return Log.e(tag, msg);
        } else {
            return 0;
        }
    }

    // 使用Log来显示调试信息,因为log在实现上每个message有4k字符长度限制
    // 所以这里使用自己分节的方式来输出足够长度的message
    public static void show(String str) {
        str = str.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;
        while (index < str.length()) {
            // java的字符不允许指定超过总的长度end
            if (str.length() <= index + maxLength) {
                sub = str.substring(index);
            } else {
                sub = str.substring(index, index +maxLength);
                index += maxLength;
            }
        }
    }


}

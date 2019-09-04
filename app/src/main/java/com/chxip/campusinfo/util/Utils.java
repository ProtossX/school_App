package com.chxip.campusinfo.util;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.chxip.campusinfo.R;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class Utils {

    public static String YYYYMMDDHHMM = "yyyy-MM-dd HH:mm";
    public static String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static String HHMM = "HH:mm";
    public static String YYYYMMDD = "yyyy-MM-dd";

    /**
     * 当浮点型数据位数超过10位之后，数据变成科学计数法显示。用此方法可以使其正常显示。
     *
     * @param value
     * @return Sting
     */
    public static String formatFloatNumber(double value) {
        if (value > 1) {
            if (value != 0.00) {
                DecimalFormat df = new DecimalFormat("##########.00");
                return df.format(value);
            } else {
                return "0.00";
            }
        } else {
            String strValue = value + "";
            if (strValue.length() > 4) {
                return strValue.substring(0, 4);
            }
        }
        return value + "";
    }

    /**
     * 设置沉浸式状态栏
     */
    public static void setStatusBarColor(Window window, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(context.getResources().getColor(R.color.title_color));
        }
    }


    /**
     * 判断是不是数字
     *
     * @param value
     * @return
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 字符串转DOuble
     *
     * @param data
     * @return
     */
    public static double getDouble(String data) {
        double dou = 0.0;
        try {
            dou = Double.parseDouble(data);
        } catch (Exception e) {
        }

        return dou;
    }

    /**
     * 字符串转Int
     *
     * @param data
     * @return
     */
    public static int getInt(String data) {
        int i = -1;
        try {
            i = Integer.parseInt(data);
        } catch (Exception e) {
        }

        return i;
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);// 点击屏幕外不可取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTimeYYYYMMDD() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        return key;
    }

    /**
     * 当前日期加多少天
     *
     * @param day
     * @return
     */
    public static String addDay(int day) {
        Calendar cal = Calendar.getInstance();

        cal.add(Calendar.DATE, day);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(cal.getTime());
    }

    /**
     * 当前日期加多少月
     *
     * @param month
     * @return
     */
    public static String addMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, month);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        return format.format(cal.getTime());
    }

    //Day:日期字符串例如 2015-3-10  Num:需要减少的天数例如 7
    public static String getDateStr(String day, int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() - (long) Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    /**
     * 转换时间格式 yyyy-MM-dd HH:mm:ss
     *
     * @param dateString
     * @return
     */
    public static String getYYYYMMDDHHMM(String dateString) {

        try {
            if (null != dateString) {

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss");
                Date date = sdf.parse(dateString);

                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                return sf.format(date);
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }


    /**
     * 转换时间格式 yyyy-MM-dd  去掉中间的T
     *
     * @param dateString
     * @return
     */
    public static String getYYYYMMDD(String dateString) {

        try {
            if (null != dateString) {
                if (dateString.length() > 10) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = sdf.parse(dateString);
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    return sf.format(date);
                } else {
                    return dateString;
                }
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 转换时间格式 yyyy-MM-dd  去掉中间的T
     *
     * @param dateString
     * @return
     */
    public static String getYYYYMM(String dateString) {

        try {
            if (null != dateString) {
                if (dateString.length() > 10) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = sdf.parse(dateString);
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM");
                    return sf.format(date);
                } else {
                    return dateString;
                }
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 转换时间格式   去掉中间的T
     *
     * @param dateString 时间字符串
     * @param format     时间格式
     * @return
     */
    public static String getDate(String dateString, String format) {

        try {
            if (null != dateString) {

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");
                Date date = sdf.parse(dateString);

                SimpleDateFormat sf = new SimpleDateFormat(format);

                return sf.format(date);
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 转换时间格式 MM-dd  去掉中间的T
     *
     * @param dateString
     * @return
     */
    public static String getMMDD(String dateString) {

        try {
            if (null != dateString) {

                SimpleDateFormat sdf = new SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss");
                Date date = sdf.parse(dateString);

                SimpleDateFormat sf = new SimpleDateFormat("MM-dd");

                return sf.format(date);
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 转换时间格式 yyyy-MM-dd  去掉中间的T
     *
     * @param dateString
     * @return
     */
    public static String getHHMM(String dateString) {

        try {
            if (null != dateString) {
                if (dateString.length() > 10) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date date = sdf.parse(dateString);
                    SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
                    return sf.format(date);
                } else {
                    return dateString;
                }
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 转换时间格式 yyyy-MM-dd 原来的格式 08 21 2022 12:00AM
     *
     * @param dateString
     * @return
     */
    public static String getYYYYMMDDOne(String dateString) {

        try {
            if (null != dateString && !dateString.equals("")) {
                if (dateString.length() > 10) {
                    SimpleDateFormat sdf = new SimpleDateFormat("MM dd yyyy HH:mm'AM'");
                    Date date = sdf.parse(dateString);
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    return sf.format(date);
                } else {
                    return dateString;
                }
            } else {
                return "";
            }

        } catch (ParseException e) {
            e.printStackTrace();

            return "";
        }

    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String getNowTimeYYYYMMDDHHmmssSSS() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        return key;
    }

    /**
     * 获取前日期，带毫秒
     *
     * @return
     */
    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取前日期，带毫秒
     *
     * @return
     */
    public static String getCurrentTime1() {
        return getCurrentTime("yyyyMMddHHmmss");
    }

    /**
     * 获取前日期，传入日期格式
     *
     * @return
     */
    public static String getCurrentTime(String format) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        String currentTime = sdf.format(date);
        return currentTime;
    }

    /**
     * 比较2个日期的大小 date1大返回true else 返回false
     */
    public static boolean isDate(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            if (d1.getTime() >= d2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 比较2个日期的大小 date1大返回true else 返回false
     */
    public static boolean isDateTwo(String date1, String date2) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = sdf.parse(date1);
            Date d2 = sdf.parse(date2);
            if (d1.getTime() >= d2.getTime()) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断两个日期差多少天
     */
    public static int DifferenceDay(String smallDate, String bigDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(smallDate);
            Date d2 = sdf.parse(bigDate);


            long intervalMilli = d2.getTime() - d1.getTime();

            return (int) (intervalMilli / (24 * 60 * 60 * 1000));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 日期加多少天
     */
    public static String addDay(String date, int day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(date);
            Calendar cl = Calendar.getInstance();
            cl.setTime(d1);

            cl.add(Calendar.DATE, day);
            String temp = sdf.format(cl.getTime());
            return temp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 日期加多少月
     */
    public static String addMonth(String date, int month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(date);
            Calendar cl = Calendar.getInstance();
            cl.setTime(d1);

            cl.add(Calendar.MONTH, month);
            String temp = sdf.format(cl.getTime());
            return temp;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 判断两个 时间差的秒数
     */
    public static int differDate(String startDate, String endDate) {
        Date curDate;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date sDate = sdf.parse(startDate);
            Date eDate = sdf.parse(endDate);
            curDate = sdf.parse(Utils.getCurrentTime());
            int ss = (int) ((eDate.getTime() - sDate.getTime()) / 1000);
            return ss;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 为空验证 空返回true，不为空返回false
     */
    public static boolean isNull(String str) {
        if (str == null || str.equals("")) {
            return true;
        }
        return false;
    }

    /**
     * 密码验证 长度6~20位
     *
     * @param context
     * @return
     */
    public static boolean v_Password(Context context, String password, String msg) {

        if (password.length() < 6 || password.length() > 20) {
            ToastUtil.show(context, msg + "长度必须大于6位!");
            return false;
        } else {
            return true;
        }
    }


    /**
     * 检测当的网络（WLAN、3G/2G）状态
     *
     * @param context Context
     * @return true 表示网络可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        ToastUtil.show(context, context.getString(R.string.app_network_false));
        return false;
    }


    /**
     * Double 格式化，保留2位小数
     */
    public static String doubleTwo(Double d) {
        String str = d + "";
        if (str != null && !str.equals("")) {
            BigDecimal b = new BigDecimal(str);
            double f1 = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
            String dou = f1 + "";
            String dian = dou.substring(dou.indexOf(".") + 1, dou.length());
            if (dian.length() < 2) {
                dou = dou + "0";
            }
            if (dou.length() > 10) {
                return Utils.formatFloatNumber(f1);
            }
            return dou + "";
        }
        return "0.00";
    }

    /**
     * Double 格式化，保留2位小数
     */
    public static String doubleTwo(String str) {
        if (str != null && !str.equals("")) {
            BigDecimal b = new BigDecimal(str);
            double f1 = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
            String dou = f1 + "";
            String dian = dou.substring(dou.indexOf(".") + 1, dou.length());
            if (dian.length() < 2) {
                dou = dou + "0";
            }
            if (dou.length() > 10) {
                return Utils.formatFloatNumber(f1);
            }
            return dou + "";
        }
        return "0.00";
    }


    /**
     * Double 格式化，不保留小数
     */
    public static String doubleZero(String str) {
        if (str != null && !str.equals("")) {
            BigDecimal b = new BigDecimal(str);
            double f1 = b.setScale(2, RoundingMode.HALF_UP).doubleValue();
            if (f1 < 1) {
                return f1 + "";
            }
            return ((int) f1) + "";
        }
        return "0";
    }

    /*获取星期几*/
    public static String getWeek() {
        Calendar cal = Calendar.getInstance();
        int i = cal.get(Calendar.DAY_OF_WEEK);
        return i - 1 + "";

    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 修要判断的时间<br>
     * @return dayForWeek 判断结果<br>
     * @Exception 发生异常<br>
     */
    public static String dayForWeek(String pTime) {

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Calendar c = Calendar.getInstance();
            c.setTime(format.parse(pTime));
            int dayForWeek = 0;
            if (c.get(Calendar.DAY_OF_WEEK) == 1) {
                dayForWeek = 7;
            } else {
                dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
            }
            String week = "";
            switch (dayForWeek) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周七";
                    break;

            }
            return week + "";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * 把对象转换成Base 64
     */
    public static String ConversionBase64(Object object) {
        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            //将对象写入字节流
            oos.writeObject(object);
            //将字节流编码成base64的字符窜
            String employeeBase64 = new String(Base64.encodeBase64(baos
                    .toByteArray()));
            return employeeBase64;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    public static Object getBase64Object(String date) {
        Object object = null;
        //读取字节
        byte[] base64 = Base64.decodeBase64(date.getBytes());
        //封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        ObjectInputStream bis = null;
        try {
            //再次封装
            bis = new ObjectInputStream(bais);
            try {
                //读取对象
                object = bis.readObject();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return object;
    }


    /**
     * 获取本地应用的版本号
     *
     * @param packageName 应用包名
     * @return int型版本号
     */
    public static int getVersionCode(Context context, String packageName) {
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(packageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return info.versionCode;
    }

    /**
     * 获取本地应用的版本名
     *
     * @param packageName 应用包名
     * @return i版本名
     */
    public static String getVersionName(Context context, String packageName) {
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(packageName, 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
        return info.versionName;
    }


    public static String toUtf8String(String s) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 0 && c <= 255) {
                sb.append(c);
            } else {
                sb.append(URLEncoder.encode(c+"","UTF-8"));
            }
        }
        return sb.toString();
    }


}

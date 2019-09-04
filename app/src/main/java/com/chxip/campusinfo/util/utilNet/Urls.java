package com.chxip.campusinfo.util.utilNet;

public class Urls {


    public static final String BASE= "http://10.10.17.244:8080/CampusInfo/api";
    public static final String BASE_IMG= "http://10.10.17.244:8080/CampusInfo";
    /**
     * 登录
     */
    public static final String Login=BASE+"/LoginApi";

    /**
     * 注册
     */
    public static final String Register=BASE+"/RegisterApi";

    /**
     * 更换头像
     */
    public static final String ChangeHeadPortrait=BASE+"/ChangeHeadPortraitApi";

    /**
     * 修改密码
     */
    public static final String ChangePassword=BASE+"/ChangePwdApi";

    /**
     * 添加课程
     */
    public static final String AddSchedule=BASE+"/AddSchedule";

    /**
     * 获取课程
     */
    public static final String GetScheduleByUserId=BASE+"/GetScheduleByUserId";
    /**
     * 删除课程
     */
    public static final String DeleteScheduleById=BASE+"/DeleteScheduleById";
    /**
     * 添加图片
     */
    public static final String AddImage=BASE+"/AddImage";

    /**
     * 添加信息
     */
    public static final String AddMessage=BASE+"/AddMessage";


    /**
     * 获取信息
     */
    public static final String GetMessageAll=BASE+"/GetMessageAll";


    /**
     * 添加评论
     */
    public static final String AddComment=BASE+"/AddComment";

    /**
     * 删除评论
     */
    public static final String DeleteCommentById=BASE+"/DeleteCommentById";

    /**
     * 删除信息
     */
    public static final String DeleteMessageById=BASE+"/DeleteMessageById";

    /**
     * 获取我的评论
     */
    public static final String GetMessageByCommentUserId=BASE+"/GetMessageByCommentUserId";
    /**
     * 获取我发布的信息
     */
    public static final String GetMessageByUserId=BASE+"/GetMessageByUserId";

    /**
     * 获取公告
     */
    public static final String GetNotification=BASE+"/GetNotification";

}


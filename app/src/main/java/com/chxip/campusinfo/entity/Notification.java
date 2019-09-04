package com.chxip.campusinfo.entity;

import java.io.Serializable;

/**
 * Created by 陈湘平 on 2018/10/11.
 */

public class Notification implements Serializable {
    private static final long serialVersionUID = 11716861212152117L;

    private int notificationId;

    private String createTime;

    private String notificationTitle;

    private String notificationContent;

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }
}

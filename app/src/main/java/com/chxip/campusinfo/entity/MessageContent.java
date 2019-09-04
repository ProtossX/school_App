package com.chxip.campusinfo.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 陈湘平 on 2018/10/10.
 */

public class MessageContent implements Serializable {
    private static final long serialVersionUID = -8074348876052474386L;

    private String createTime;

    private int messageId;

    private int userId;

    private User user;

    private String messageTitle;

    private String messageContent;

    private int messageType;//1 失误招领  2 信息墙

    private List<Image> images;

    private List<Image> deleteiImages;

    private List<Comment> comments;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public void setMessageTitle(String messageTitle) {
        this.messageTitle = messageTitle;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public List<Image> getDeleteiImages() {
        if(deleteiImages==null){
            deleteiImages=new ArrayList<>();
        }
        return deleteiImages;
    }

    public void setDeleteiImages(List<Image> deleteiImages) {
        this.deleteiImages = deleteiImages;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public List<Comment> getComments() {
        if(comments==null){
            comments=new ArrayList<>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}

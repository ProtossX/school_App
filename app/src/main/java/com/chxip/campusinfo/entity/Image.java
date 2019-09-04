package com.chxip.campusinfo.entity;

import java.io.Serializable;

public class Image implements Serializable{

    private static final long serialVersionUID = -3109817960554615448L;
    private int imageId;
    private String imageUrl;
    private int contentId;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getContentId() {
        return contentId;
    }

    public void setContentId(int contentId) {
        this.contentId = contentId;
    }


}

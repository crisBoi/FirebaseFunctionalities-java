package com.example.inhabitpototypr_0;

import android.net.Uri;

public class Post {
    private String imgUrl;
    private String caption;
    private long likeCount;

    public Post() {
    }

    public Post(String imgUrl, String caption) {
        this.imgUrl = imgUrl;
        this.caption = caption;
        this.likeCount = 0;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(long likeCount) {
        this.likeCount = likeCount;
    }
}

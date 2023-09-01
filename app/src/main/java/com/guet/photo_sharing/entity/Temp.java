package com.guet.photo_sharing.entity;

import java.util.List;

public class Temp{
    boolean isCollect;
    List<String> url;

    public boolean isCollect() {
        return isCollect;
    }

    public void setCollect(boolean collect) {
        isCollect = collect;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }
}

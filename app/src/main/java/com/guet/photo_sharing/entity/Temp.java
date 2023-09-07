package com.guet.photo_sharing.entity;

import java.util.List;

public class Temp{
    boolean collect;
    List<String> url;

    public boolean isCollect() {
        return collect;
    }

    public void setCollect(boolean collect) {
        collect = collect;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Temp{" +
                "isCollect=" + collect +
                ", url=" + url +
                '}';
    }
}

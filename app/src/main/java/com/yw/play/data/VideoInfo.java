package com.yw.play.data;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2017/9/16.
 */

public class VideoInfo {
    private String path;
    private String name;
    private String img;
    private long time;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "VideoInfo{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", time=" + time +
                '}';
    }
}

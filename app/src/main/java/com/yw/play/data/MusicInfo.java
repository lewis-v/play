package com.yw.play.data;

/**
 * Created by Administrator on 2017/9/17.
 */

public class MusicInfo {
    private String name;
    private long time;
    private String auth;
    private String path;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    @Override
    public String toString() {
        return "MusicInfo{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", auth='" + auth + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}

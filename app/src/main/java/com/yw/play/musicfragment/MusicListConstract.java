package com.yw.play.musicfragment;

import com.yw.play.data.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/18.
 */

public interface MusicListConstract {
    List<MusicInfo> getMusicList();
    void hideView();
    void playMusic(int position);
    void setPlayType(int type);
    int getPlayType();
}

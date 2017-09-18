package com.yw.play.main.music;

import com.yw.play.data.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */

public interface MusicConstract {
    void onRefreshSuccess(List<MusicInfo> musicInfos);
}

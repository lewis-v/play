package com.yw.play.main.video;

import com.yw.play.data.VideoInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/10.
 */

public interface VideoConstract {
    void onRefreshSuccess(List<VideoInfo> videoInfoList);
}

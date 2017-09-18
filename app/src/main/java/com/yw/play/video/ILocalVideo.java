package com.yw.play.video;

import android.support.v7.widget.RecyclerView;
import android.view.SurfaceHolder;


/**
 * Created by Administrator on 2017/6/20.
 */
public interface ILocalVideo {
    void initRecyclerView(RecyclerView.Adapter adapter);
    void setSurfaceVisible(boolean IsVisible);
    SurfaceHolder getSurfaceHolder();
    void setSeekbar(int progress);
    void setTextTime(String text);
    void setButtonPlay(boolean isPlay);
    void hideLinear(boolean isHide);
    int getLinearVisible();
    void setTipText(String tip);
    void setWinBrightness(float brightness);
    float getWinBrightness();
}

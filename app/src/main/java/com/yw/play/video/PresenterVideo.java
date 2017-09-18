package com.yw.play.video;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;

import com.yw.play.base.PresenterBase;
import com.yw.play.utils.DateChange;
import com.yw.play.utils.SurfaceViewPlay;

import java.io.IOException;

/**
 * Created by Administrator on 2017/6/24.
 */
public class PresenterVideo extends PresenterBase {
    private final static int NULL_MODEL = 0;
    private final static int PROGRESS_MODEL = 1;
    private final static int LIGHT_MODEL = 2;
    private final static int AUDIO_MODEL = 3;
    private final static String SAVE_PATH
            = Environment.getExternalStorageDirectory().getAbsolutePath()+"/YWDownload";

    private Thread init_t;
    private float down_x,down_y,move_x,move_y;
    private int touchModel = NULL_MODEL;

    /**
     * 构造函数
     *
     * @param context
     * @param iLocalVideo
     */
    public PresenterVideo(Context context, ILocalVideo iLocalVideo
            , SurfaceViewPlay surfaceViewPlay1) {
        super(context, iLocalVideo);

        if (surfaceViewPlay1 != null) {
            Log.i("---video---","not null");
            super.surfaceViewPlay = surfaceViewPlay1;
            initSurfaceView();
            new Thread(){
                public void run(){
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (surfaceViewPlay.getPlaying() == SurfaceViewPlay.PLAY) {
                        rePlay();
                        System.out.println("rePlay"+surfaceViewPlay.getPlaying());
                    }else if (surfaceViewPlay.getPlaying() == SurfaceViewPlay.PAUSE){
                        rePlay();
                        mediaPause();
                    }else{
                        PresenterVideo.super.mediaPlay(surfaceViewPlay.getPlayPosition());
                    }
                }
            }.start();

        }else {
            Log.i("---video---","null");
        }
    }


    /**
     * 初始化initSurfaceView
     */
    @Override
    public void initSurfaceView(){
        surfaceHolder = iLocalVideo.getSurfaceHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(surfaceViewPlay);
    }

    /**
     * 全屏后继续播放
     */
    public void rePlay(){
        super.mediaPlay(surfaceViewPlay.getPlayPosition());
        surfaceViewPlay.playVideo(surfaceHolder,context,surfaceViewPlay.getPlayPosition());
        surfaceViewPlay.setPlay();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        surfaceViewPlay.setProgress(surfaceViewPlay.getSaveProgress());
        refreshSeekbar(surfaceViewPlay.getSaveProgress());
        iLocalVideo.setButtonPlay(true);
        updateSeekBar = new UpdateSeekBar(surfaceViewPlay,iLocalVideo);
        updateSeekBar.execute(1000);
    }
    /**
     * 播放具体位置视频
     * @param position
     */
    public void mediaPlay(final int position){
        super.mediaPlay(position);
        iLocalVideo.setButtonPlay(true);
        setTimeText(0);
        //使用线程延时,防止在surface未出现时就播放导致出错
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                updateSeekBarCancel();
                surfaceViewPlay.playVideo(surfaceHolder,context
                        ,position);
                surfaceViewPlay.setPlay();
                updateSeekBar = new UpdateSeekBar(surfaceViewPlay,iLocalVideo);
                updateSeekBar.execute(1000);
            }
        }.start();

    }


    /**
     * 处理返回给父activity的信息
     */
    public Intent result(){
        Intent intent = new Intent();
        intent.putExtra("playing",surfaceViewPlay.getPlaying());
        intent.putExtra("position",surfaceViewPlay.getPlayPosition());
        intent.putExtra("progress",surfaceViewPlay.getProgress());
        return intent;
    }

    /**
     * surface滑动触控操作
     * @param event
     */
    public void surfaceOnTouch(MotionEvent event){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels;
        float height = displayMetrics.heightPixels;
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                down_x = event.getX();
                down_y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                move_x = event.getX();
                move_y = event.getY();
                if (touchModel == NULL_MODEL) {
                    if (Math.abs(move_x - down_x) >width/10
                            || Math.abs(move_y - down_y) >height/10) {
                        if (Math.abs(move_x - down_x) > Math.abs(move_y - down_y)) {
                            touchModel = PROGRESS_MODEL;
                        } else {
                            if (down_x > width / 2) {
                                touchModel = AUDIO_MODEL;
                                System.out.println("audio");
                            } else {
                                touchModel = LIGHT_MODEL;
                                System.out.println("light");
                            }
                        }
                    }
                }
                switch (touchModel){
                    case PROGRESS_MODEL:
                        if (move_x > down_x){
                            if (surfaceViewPlay.getProgress() +500< surfaceViewPlay.getLongList()
                                    .get(surfaceViewPlay.getPlayPosition())) {
                                surfaceViewPlay.setProgress(surfaceViewPlay.getProgress() + 500);
                            }
                        }else{
                            if (surfaceViewPlay.getProgress() -500> 0) {
                                surfaceViewPlay.setProgress(surfaceViewPlay.getProgress() - 500);
                            }
                        }
                        refreshSeekbar(surfaceViewPlay.getProgress());
                        iLocalVideo.setTipText(DateChange.longToDateString(
                                surfaceViewPlay.getProgress())+"/"
                                +DateChange.longToDateString(surfaceViewPlay.getLongList()
                                .get(surfaceViewPlay.getPlayPosition())));
                        down_x = move_x;
                        break;
                    case LIGHT_MODEL:
                        float brightness = iLocalVideo.getWinBrightness();
                        float move_l = move_y - down_y;
                        if (Math.abs(move_l)>height/10) {
                            if (move_l > height/10) {
                                if (brightness -0.1  < 0f){
                                    brightness = 0f;
                                }else {
                                    brightness -= 0.1f;
                                }
                            } else {
                                if (brightness+0.1 > 1.0f){
                                    brightness = 1.0f;
                                }else {
                                    brightness += 0.1f;
                                }
                            }
                            iLocalVideo.setWinBrightness(brightness);
                            iLocalVideo.setTipText("亮度:"+(int)(Math.abs(brightness)*10)*10+"%");
                            down_y = move_y;
                        }
                        break;
                    case AUDIO_MODEL:
                        AudioManager audioManager =
                                (AudioManager)context.getSystemService(context.AUDIO_SERVICE);
                        float move = move_y - down_y;
                        if (Math.abs(move)>height/10) {
                            if (move > height/10) {
                                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC
                                        , AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
                            } else {
                                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC
                                        , AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
                            }
                            iLocalVideo.setTipText("音量:"+audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)*20/3+"%");
                            down_y = move_y;
                        }
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchModel = NULL_MODEL;
                iLocalVideo.setTipText("");
                break;
        }
    }

    /**
     * 释放资源
     */
    @Override
    public void Destery(){
        super.Destery();
        if (init_t != null){
            init_t.interrupt();
        }

    }
}

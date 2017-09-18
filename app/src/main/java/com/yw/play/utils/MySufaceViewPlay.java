package com.yw.play.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.SurfaceHolder;

import com.yw.play.data.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */

public class MySufaceViewPlay implements SurfaceHolder.Callback {
    final public static int STOP = 0;
    final public static int PAUSE = 1;
    final public static int PLAY = 2;
    public static final String TAG = "---MySufaceViewPlay---";

    private MediaPlayer mediaPlayer;
    private List<VideoInfo> videoInfos = new ArrayList<>();//视频播放列表
    private int playPosition;
    private int playing = PLAY;
    private int saveProgress;

    public MySufaceViewPlay(){
        mediaPlayer = new MediaPlayer();
    }

    /**
     * 播放视频
     * @param surfaceHolder
     */
    public void playVideo(SurfaceHolder surfaceHolder, int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(videoInfos.get(position).getPath());
            Log.i(TAG,"path: "+videoInfos.get(position).getPath());
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepare();
            mediaPlayer.start();
            this.playPosition = position;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 设置状态为播放
     */
    public void setPlay(){
        playing = PLAY;
    }

    /**
     * 设置状态为停止
     */
    public void setStop(){
        playing = STOP;
    }
    /**
     * 结束播放
     */
    public void Destery(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();
    }
    /**
     * 获取播放进度
     * @return
     */
    public int getProgress(){
        return mediaPlayer.getCurrentPosition();
    }
    /**
     * 设置播放进度
     * @param time
     */
    public void setProgress(int time){
        mediaPlayer.seekTo(time);
    }

    /**
     * 暂停
     */
    public void pause(){
        playing = PAUSE;
        mediaPlayer.pause();
        Log.i(TAG,"pause");
    }

    /**
     * 停止
     */
    public void stop(){
        playing = STOP;
        mediaPlayer.stop();
    }

    /**
     * 继续播放
     */
    public void continuePlay(){
        playing = PLAY;
        mediaPlayer.start();
    }

    /**
     * 获取当前播放的位置
     * @return
     */
    public int getPlayPosition(){
        return playPosition;
    }

    /**
     * 设置当前播放位置
     * @param playPosition
     */
    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    /**
     * 获取播放器状态
     * @return
     */
    public int getPlaying(){
        return playing;
    }

    /**
     * 获取保存的进度
     * @return
     */
    public int getSaveProgress(){
        return saveProgress;
    }

    public void initVideoList(List<VideoInfo> videoInfoList){
        this.videoInfos = videoInfoList;
    }
    /**
     * 获取视频列表
     * @return
     */
    public List<VideoInfo> getVideoPath(){
        return new ArrayList<>(videoInfos);
    }

    /**
     * 添加一个播放资源
     * @param videoInfo
     */
    public void addVideo(VideoInfo videoInfo){
        videoInfos.add(videoInfo);
    }




    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}

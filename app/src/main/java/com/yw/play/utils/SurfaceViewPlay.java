package com.yw.play.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/19.
 */
public class SurfaceViewPlay implements SurfaceHolder.Callback,Parcelable {
    final public static int STOP = 0;
    final public static int PAUSE = 1;
    final public static int PLAY = 2;

    private MediaPlayer mediaPlayer;
    private List<String> videoPath = new ArrayList<>();
    private List<String> videoName = new ArrayList<>();
    private List<Bitmap> bitmaps = new ArrayList<>();
    private List<Long> longList = new ArrayList<>();
    private int playPosition;
    private int playing = PLAY;
    private int saveProgress;

    public SurfaceViewPlay(){
        mediaPlayer = new MediaPlayer();
    }
    public SurfaceViewPlay(Parcel source){
        mediaPlayer = new MediaPlayer();
        source.readList(videoPath,getClass().getClassLoader());
        source.readList(videoName,getClass().getClassLoader());
        source.readList(longList,getClass().getClassLoader());
        source.readList(bitmaps,getClass().getClassLoader());
        playPosition = source.readInt();
        playing = source.readInt();
        saveProgress = source.readInt();
        System.out.println(playPosition+"/"+playing+"/"+saveProgress);
    }

    /**
     * 播放视频
     * @param surfaceHolder
     */
    public void playVideo(SurfaceHolder surfaceHolder,Context context,int position) {
        try {
            System.out.println("path: "+videoPath.get(position));
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            if (videoPath.get(position).startsWith("http")){
//                file = new File(videoPath.get(position));
//                fileInputStream = new FileInputStream(file);
//                mediaPlayer.setDataSource(fileInputStream.getFD());
//            }else {
            mediaPlayer.setDataSource(videoPath.get(position));
//            }
            mediaPlayer.setDisplay(surfaceHolder);
            mediaPlayer.prepare();
            mediaPlayer.start();
            longList.set(position,(long)mediaPlayer.getDuration());
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
        System.out.println("pause");
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

    /**
     * 初始化播放列表
     * @param videoPath
     * @param videoName
     * @param longList
     * @param bitmapList
     */
    public void setVideoList(List<String> videoPath,List<String> videoName,List<Long> longList
            ,List<Bitmap> bitmapList){
        this.videoPath = videoPath;
        this.videoName = videoName;
        this.longList = longList;
        this.bitmaps = bitmapList;
    }

    /**
     * 获取视频地址
     * @return
     */
    public List<String> getVideoPath(){
        return new ArrayList<>(videoPath);
    }
    /**
     * 获取视频名字
     * @return
     */
    public List<String> getVideoName(){
        return new ArrayList<>(videoName);
    }
    /**
     * 获取视频时长
     * @return
     */
    public List<Long> getLongList(){
        return new ArrayList<>(longList);
    }
    /**
     * 获取视频缩略图
     * @return
     */
    public List<Bitmap> getBitmaps(){
        return new ArrayList<>(bitmaps);
    }

    /**
     * 添加一个播放资源
     * @param Path
     */
    public void addVideo(String Path,String name,long time){
        this.videoPath.add(Path);
        this.videoName.add(name);
        this.longList.add(time);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<SurfaceViewPlay> CREATOR= new Creator<SurfaceViewPlay>() {
        @Override
        public SurfaceViewPlay createFromParcel(Parcel source) {
            return new SurfaceViewPlay(source);
        }

        @Override
        public SurfaceViewPlay[] newArray(int size) {
            return new SurfaceViewPlay[0];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(videoPath);
        dest.writeList(videoName);
        dest.writeList(longList);
        dest.writeList(bitmaps);
        dest.writeInt(playPosition);
        dest.writeInt(playing);
        dest.writeInt(getProgress());
    }

}

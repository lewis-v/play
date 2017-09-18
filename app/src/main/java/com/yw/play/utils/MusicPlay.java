package com.yw.play.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.yw.play.data.MusicInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */

public class MusicPlay {
    final public static int STOP = 0;
    final public static int PAUSE = 1;
    final public static int PLAY = 2;
    public static final String TAG = "---MusicPlay---";

    private MediaPlayer mediaPlayer;
    private List<MusicInfo> musicPlayList = new ArrayList<>();//播放列表
    private int playing = STOP;//播放状态
    private int playPosition;//播放位置
    private int progressPosition;//播放进度

    public MusicPlay(){
        mediaPlayer = new MediaPlayer();
    }

    public void setMusicPlayList(List<MusicInfo> musicPlayList){
        this.musicPlayList = musicPlayList;
    }

    /**
     * 播放视频(重新播放)
     * @param position
     */
    public void playVideo( int position) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(musicPlayList.get(position).getPath());
            Log.i(TAG,"path: "+musicPlayList.get(position).getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            playing = PLAY;
            this.playPosition = position;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 添加一个播放资源
     * @param musicInfo
     */
    public void addVideo(MusicInfo musicInfo){
        musicPlayList.add(musicInfo);
    }

    /**
     * 获取播放状态
     * @return
     */
    public int getPlaying(){
        return playing;
    }

    /**
     * 获取当前播放位置
     * @return
     */
    public int getPlayPosition(){
        return playPosition;
    }

    /**
     * 获取播放列表
     * @return
     */
    public List<MusicInfo> getMusicPlayList(){
        return musicPlayList;
    }

    /**
     * 获取mediaplay
     * @return
     */
    public MediaPlayer getMediaPlayer(){
        return mediaPlayer;
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
     * 播放下一首
     * @return 返回是否成功
     */
    public boolean playNext(){
        if (playPosition + 1 < musicPlayList.size()){
            playVideo(playPosition + 1);
            return true;
        }
        return false;
    }

    /**
     * 播放上一首
     * @return 返回是否成功
     */
    public boolean playLast(){
        if (playPosition - 1 >= 0 && musicPlayList.size() > 0){
            playVideo(playPosition + 1);
            return true;
        }
        return false;
    }

}

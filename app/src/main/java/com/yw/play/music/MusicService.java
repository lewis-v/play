package com.yw.play.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.yw.play.utils.MusicPlay;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public static final int ORDER = 0;//顺序播放
    public static final int SINGLE = 1;//单曲播放
    public static  final int RANDOM = 2;//随机播放

    private MusicPlay musicPlay;
    private int playType = ORDER;//播放模式
    private static MyBind bind = null;//单例bind
    private OnPlayStutasChangeLinstener onPlayStutasChangeLinstener;
    private List<Integer> positionList = new ArrayList<>();//随机播放已播放过地方

    public class MyBind extends Binder{
        public MyBind(){
            musicPlay = new MusicPlay();
            musicPlay.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放结束
                    switch (playType){
                        case ORDER:
                            if (musicPlay.playNext()){
                                if (onPlayStutasChangeLinstener != null){
                                    onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                                }
                            }else {
                                if (onPlayStutasChangeLinstener != null){
                                    onPlayStutasChangeLinstener.onStop();
                                }
                            }
                            break;
                        case SINGLE:
                            musicPlay.playVideo(musicPlay.getPlayPosition());
                            break;
                        case RANDOM:

                            break;
                    }

                }
            });
        }
        public MusicPlay getMusicPlay(){
            return musicPlay;
        }

        public void setOnPlayStutasChangeLinstener(OnPlayStutasChangeLinstener onPlayStutasChangeLinstener){
            MusicService.this.onPlayStutasChangeLinstener = onPlayStutasChangeLinstener;
        }

        public int getPlayType(){
            return playType;
        }

        public void setPlayType(int playType){
            MusicService.this.playType = playType;
        }
    }

    /**
     * 获取随机播放的下一个位置
     * @return
     */
    public int getRandomPosition(){
        int position = ((int)System.currentTimeMillis())%musicPlay.getMusicPlayList().size();
        if (position != musicPlay.getPlayPosition() ){
            return position;
        }else {
            return getRandomPosition();
        }
    }

    /**
     *  播放器状态改变监听器
     */
    public interface OnPlayStutasChangeLinstener{
        void onPlayNext(int position);
        void onStop();
    }


    @Override
    public IBinder onBind(Intent intent) {
       if (bind == null){
           bind = new MyBind();
       }
        return bind;
    }

    @Override
    public void onDestroy() {
        if (musicPlay != null) {
            musicPlay.Destery();
        }
        super.onDestroy();
    }
}

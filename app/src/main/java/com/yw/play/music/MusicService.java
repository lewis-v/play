package com.yw.play.music;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.yw.play.R;
import com.yw.play.data.MusicInfo;
import com.yw.play.main.MainActivity;
import com.yw.play.utils.MusicPlay;

import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {
    public static final int ORDER = 0;//顺序播放
    public static final int SINGLE = 1;//单曲播放
    public static  final int RANDOM = 2;//随机播放

    public final static int BUTTON_PREV_ID = 1;
    public final static int BUTTON_PALY_ID = 2;
    public final static int BUTTON_NEXT_ID = 3;
    public final static String ACTION_BUTTON = "com.yw.play.music.MusicService";
    public final static String INTENT_BUTTONID_TAG = "ButtonId";

    public ButtonBroadcastReceiver bReceiver;
    private MusicPlay musicPlay;
    private int playType = ORDER;//播放模式
    private static MyBind bind = null;//单例bind
    private OnPlayStutasChangeLinstener onPlayStutasChangeLinstener;
    private List<Integer> positionList = new ArrayList<>();//随机播放已播放过地方
    private List<MusicInfo> userSetList = new ArrayList<>();//用户设定的下一首列表

    public class MyBind extends Binder{
        public MyBind(){
            musicPlay = new MusicPlay();
            SharedPreferences sharedPreferences = getSharedPreferences("play.db",MODE_PRIVATE);
            playType = sharedPreferences.getInt("play_type",ORDER);
            musicPlay.getMediaPlayer().setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //播放结束
                    switch (playType){
                        case ORDER:
                            if (userSetList.size() > 0){//先播放用户选择的下一首
                                if (musicPlay.playVideo(userSetList.get(0).getPath())) {
                                    if (onPlayStutasChangeLinstener != null) {
                                        onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                                    }
                                    userSetList.remove(0);
                                }else {//下一首不存在,删除再找下一首
                                    userSetList.remove(0);
                                    onCompletion(mediaPlayer);
                                }
                            }else
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
                            if (userSetList.size() > 0){//先播放用户选择的下一首
                                if (musicPlay.playVideo(userSetList.get(0).getPath())) {
                                    if (onPlayStutasChangeLinstener != null) {
                                        onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                                    }
                                    userSetList.remove(0);
                                }else {//下一首不存在,删除再找下一首
                                    userSetList.remove(0);
                                    onCompletion(mediaPlayer);
                                }
                            }else {
                                int position = getRandomPosition();
                                Log.i("---position---", "" + position);
                                musicPlay.playVideo(position);
                                positionList.add(position);
                                onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                            }
                            break;
                    }
                }
            });
            musicPlay.setOnPlayChangeListener(new MusicPlay.OnPlayChangeListener() {
                @Override
                public void onPlay(int position) {
                    shwoNotify(false,musicPlay.getPlayName());
                }

                @Override
                public void onPause() {
                    shwoNotify(true,musicPlay.getPlayName());
                }

                @Override
                public void onStop() {
                    shwoNotify(true,musicPlay.getPlayName());
                }
            });
            shwoNotify(true,getString(R.string.app_name));
            initButtonReceiver();
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
            positionList.clear();
            MusicService.this.playType = playType;
            SharedPreferences sharedPreferences = getSharedPreferences("play.db",MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("play_type",playType);
            editor.apply();
        }

        public void setNotifyShow(boolean isPlay,String title){
            shwoNotify(isPlay,title);
        }

        /**
         * 清除用户的下一首列表
         */
        public void clearNext(){
            userSetList.clear();
        }

        /**
         * 添加下一首播放
         * @param musicInfo 下一首信息
         */
        public void addNext(MusicInfo musicInfo){
            userSetList.add(musicInfo);
        }
    }

    /**
     * 获取随机播放的下一个位置
     * @return
     */
    public int getRandomPosition(){
        if (positionList.size() == musicPlay.getMusicPlayList().size()){//全部随机播放一遍后,清除标记已重新随机播放
            positionList.clear();
        }
        int position = Math.abs((int)System.currentTimeMillis()%musicPlay.getMusicPlayList().size());
        if (position != musicPlay.getPlayPosition() && !positionList.contains(position)){
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

    public void shwoNotify(boolean Isplay,String title){
        int notifyId = 101;
        RemoteViews mRemoteViews = new RemoteViews(getPackageName(), R.layout.mynotification);
//        mRemoteViews.setImageViewResource(R.id.notification_img, R.mipmap.ic_launcher);
        mRemoteViews.setImageViewResource(R.id.notification_img, R.mipmap.ic_launcher);

        if(Isplay){
            mRemoteViews.setImageViewResource(R.id.bt_play,R.drawable.ic_play_arrow_black_24dp);
        }else{
            mRemoteViews.setImageViewResource(R.id.bt_play,R.drawable.ic_pause_black_24dp);
        }
        mRemoteViews.setTextViewText(R.id.notification_title,title);
        Intent buttonIntent = new Intent();
        /* 上一首按钮 */
        buttonIntent.setAction(ACTION_BUTTON);
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PREV_ID);
        //这里加了广播，所及INTENT的必须用getBroadcast方法
        PendingIntent intent_prev = PendingIntent.getBroadcast(this, 1, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_last, intent_prev);
        /* 播放/暂停  按钮 */
        buttonIntent.setAction(ACTION_BUTTON);
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_PALY_ID);
        PendingIntent intent_play = PendingIntent.getBroadcast(this, 2, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_play, intent_play);
        /* 下一首 按钮  */
        buttonIntent.setAction(ACTION_BUTTON);
        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_NEXT_ID);
        PendingIntent intent_next = PendingIntent.getBroadcast(this, 3, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.bt_next, intent_next);
        /* 关闭 按钮  */
//        buttonIntent.setAction(ACTION_BUTTON);
//        buttonIntent.putExtra(INTENT_BUTTONID_TAG, BUTTON_CLOSE_ID);
//        PendingIntent intent_close = PendingIntent.getBroadcast(this, 4, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//        mRemoteViews.setOnClickPendingIntent(R.id.notification_close, intent_close);



        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContent(mRemoteViews)
                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))//设置点击意图,不设置会不显示
                .setTicker(getString(R.string.app_name))//通知栏标题
                .setPriority(Notification.PRIORITY_DEFAULT)//优先级
                .setOngoing(true)//设置是否为一个后台任务
                .setSmallIcon(R.mipmap.ic_launcher);//通知栏缩略图标

        Notification notify = mBuilder.build();

        notify.contentView = mRemoteViews;

        startForeground(notifyId, notify);
    }

    public PendingIntent getDefalutIntent(int flags){
        Intent myintent = new Intent(this,MainActivity.class);
        myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pendingIntent= PendingIntent.getActivity(this, 0, myintent, flags);
        return pendingIntent;
    }

    public void initButtonReceiver(){
        bReceiver = new ButtonBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BUTTON);
        registerReceiver(bReceiver, intentFilter);
    }
    public class ButtonBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if(action.equals(ACTION_BUTTON)){
                int buttonId = intent.getIntExtra(INTENT_BUTTONID_TAG, 0);
                switch (buttonId) {
                    case BUTTON_PREV_ID:
                        Log.i("---last---","");
                        System.out.println("last");
                        if (musicPlay.playLast()) {
                            if (onPlayStutasChangeLinstener != null) {
                                onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                            }
                        }
                        break;
                    case BUTTON_PALY_ID:
                        Log.i("---play---","");
                        System.out.println("play");
                        int playStatus = bind.getMusicPlay().getPlaying();
                        if (playStatus == MusicPlay.PLAY){
                            bind.getMusicPlay().pause();
                            if (onPlayStutasChangeLinstener != null){
                                onPlayStutasChangeLinstener.onStop();
                            }
                        }else if (playStatus == MusicPlay.PAUSE){
                            bind.getMusicPlay().continuePlay();
                            if (onPlayStutasChangeLinstener != null){
                                onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                            }
                        }else if (playStatus == MusicPlay.STOP){
                            bind.getMusicPlay().playVideo(bind.getMusicPlay().getPlayPosition());
                            if (onPlayStutasChangeLinstener != null){
                                onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                            }
                        }
                        break;
                    case BUTTON_NEXT_ID:
                        Log.i("---next---","");
                        System.out.println("next");
                        if (musicPlay.playNext()) {
                            if (onPlayStutasChangeLinstener != null) {
                                onPlayStutasChangeLinstener.onPlayNext(musicPlay.getPlayPosition());
                            }
                        }
                        break;
//                    case BUTTON_CLOSE_ID:
//                        System.out.println("close");
//                        listen_tool_notifi.closeService();
//                        ifly.Destroy();
//                        MyService.this.stopSelf();
//                        break;
                    default:
                        break;
                }
            }
        }
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
        if (bReceiver != null){
            unregisterReceiver(bReceiver);
        }
        super.onDestroy();
    }
}

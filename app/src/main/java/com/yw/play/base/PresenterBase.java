package com.yw.play.base;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;


import com.yw.play.utils.DateChange;
import com.yw.play.utils.SurfaceViewPlay;
import com.yw.play.video.ILocalVideo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/24.
 */
public class PresenterBase {

    protected Context context;
    protected ILocalVideo iLocalVideo;
    protected SurfaceViewPlay surfaceViewPlay;
    protected SurfaceHolder surfaceHolder;
    protected UpdateSeekBar updateSeekBar;
    protected HideLinear closeLinear_t = null;

    /**
     * 构造函数
     * @param context
     * @param iLocalVideo
     */
    public PresenterBase(final Context context, ILocalVideo iLocalVideo){
        this.context = context;
        this.iLocalVideo = iLocalVideo;
    }

    /**
     * 初始化List
     */
    public void initList() {

    }

    /**
     * 初始化recyclerview
     */
    public void initView(){

    }

    /**
     * 初始化initSurfaceView
     */
    public void initSurfaceView(){
        surfaceViewPlay = new SurfaceViewPlay();
        surfaceHolder = iLocalVideo.getSurfaceHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(surfaceViewPlay);
        iLocalVideo.setSurfaceVisible(false);
    }



    /**
     * 设置textview显示的时间和seekbar进度
     * @param progress
     */
    public void setTimeText(int progress){
        if (surfaceViewPlay.getLongList() != null) {
            System.out.println("position"+surfaceViewPlay.getLongList().size());
            long pro = (long)(surfaceViewPlay.getLongList()
                    .get(surfaceViewPlay.getPlayPosition()) * (progress* 0.01f));
            String time = DateChange.longToDateString(pro);
            String maxTime = DateChange.longToDateString(surfaceViewPlay.getLongList()
                            .get(surfaceViewPlay.getPlayPosition()));
            iLocalVideo.setTextTime(time + "/" + maxTime);
            surfaceViewPlay.setProgress((int)pro);
        }
    }


    /**
     * 刷新seekbar
     * @param time
     */
    public void refreshSeekbar(long time){
        if (surfaceViewPlay.getLongList() != null) {
            int pro = (int)((float)time
                    /surfaceViewPlay.getLongList().get(surfaceViewPlay.getPlayPosition())*100);
            iLocalVideo.setSeekbar(pro);
            String protime = new SimpleDateFormat("mm:ss").format(new Date(time));
            String maxTime = new SimpleDateFormat("mm:ss")
                    .format(new Date(surfaceViewPlay.getLongList()
                            .get(surfaceViewPlay.getPlayPosition())));
            iLocalVideo.setTextTime(protime + "/" + maxTime);
        }
    }

    /**
     * 异步线程对播放进度条的显示
     */
    protected class UpdateSeekBar extends AsyncTask<Integer,Integer,String> {

        SurfaceViewPlay msurfaceViewPlay;
        ILocalVideo miLocalVideo;

        public UpdateSeekBar(SurfaceViewPlay msurfaceViewPlay,ILocalVideo miLocalVideo){
            this.msurfaceViewPlay = msurfaceViewPlay;
            this.miLocalVideo = miLocalVideo;
        }
        @Override
        protected String doInBackground(Integer[] params) {
            while(msurfaceViewPlay.getPlaying() == msurfaceViewPlay.PLAY ||
                    msurfaceViewPlay.getPlaying() == msurfaceViewPlay.PAUSE ){

                try{
                    Thread.sleep(params[0]);
                }catch (InterruptedException e){
                    e.printStackTrace();
                    break;
                }
                if (msurfaceViewPlay.getPlaying() == msurfaceViewPlay.PLAY) {
                    this.publishProgress(msurfaceViewPlay.getProgress());
                }
                System.out.println(msurfaceViewPlay.getPlaying()+"doInBackground"+miLocalVideo);

            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            refreshSeekbar(values[0]);
            if (Math.abs(values[0]-msurfaceViewPlay.getLongList()
                    .get(msurfaceViewPlay.getPlayPosition())) < 100){
                msurfaceViewPlay.stop();
                miLocalVideo.setButtonPlay(false);
                this.cancel(true);
            }
        }
    }

    /**
     * 设置控制器是否隐藏,隐藏时显示,显示是隐藏
     */
    public void setLinearTime(){
        System.out.println(iLocalVideo.getLinearVisible() == View.GONE);
        if (iLocalVideo.getLinearVisible() == View.GONE){
            iLocalVideo.hideLinear(false);
            closeLinear_t = new HideLinear(iLocalVideo,5);
            closeLinear_t.start();
        }else{
            System.out.println("hide");
            if (closeLinear_t != null) {
                closeLinear_t.close();
            }
            iLocalVideo.hideLinear(true);
        }
    }

    /**
     * 隐藏控制器线程
     */
    protected class HideLinear extends Thread {
        private int closeTime , nowTime;
        private ILocalVideo iLocalVideo;

        public HideLinear(ILocalVideo iLocalVideo,int closeTime ){
            this.closeTime = closeTime;
            this.nowTime = closeTime;
            this.iLocalVideo = iLocalVideo;
        }

        public void setNowTime(){
            nowTime = closeTime;
        }

        public void close(){
            nowTime = 0;
        }
        public void run(){
            while(nowTime>0) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                nowTime--;
            }
            if (nowTime  <= 0 && iLocalVideo.getLinearVisible() != View.GONE){
                iLocalVideo.hideLinear(true);
                System.out.println("thread_hide");
            }
            System.out.println("thread_not_hide");
        }
    }
    /**
     * 视频播放暂停
     */
    public void mediaPause(){
        iLocalVideo.setButtonPlay(false);
        surfaceViewPlay.pause();
    }

    /**
     * 视频播放停止
     */
    public void mediaStop(){
        iLocalVideo.setButtonPlay(false);
        setTimeText(0);
        surfaceViewPlay.stop();
        iLocalVideo.setSeekbar(0);
    }

    /**
     * 视频播放继续
     */
    public void mediaStart(){
        iLocalVideo.setButtonPlay(true);
        surfaceViewPlay.continuePlay();
    }

    /**
     * [继续]播放/暂停当前位置视频
     */
    public void mediaPlay(){
        if (surfaceViewPlay.getPlaying() == SurfaceViewPlay.PLAY){
            mediaPause();
        }else if (surfaceViewPlay.getPlaying() == SurfaceViewPlay.PAUSE){
            mediaStart();
        }else{
            mediaPlay(surfaceViewPlay.getPlayPosition());
        }
    }

    /**
     * 播放具体位置视频
     * @param position
     */
    public void mediaPlay(final int position){
        iLocalVideo.hideLinear(false);
        if (closeLinear_t != null){
            closeLinear_t .interrupt();
        }
        closeLinear_t = new HideLinear(iLocalVideo,5);
        closeLinear_t.start();
    }

    /**
     * 播放下一个视频
     */
    public void nextMedia(){
        if (surfaceViewPlay.getPlayPosition()+1 < surfaceViewPlay.getVideoPath().size()){
            mediaPlay(surfaceViewPlay.getPlayPosition()+1);
        }else{
            Toast.makeText(context,"没有下一个视频了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 播放上一个视频
     */
    public void lastMedia(){
        if (surfaceViewPlay.getPlayPosition()-1 >= 0){
            mediaPlay(surfaceViewPlay.getPlayPosition()-1);
        }else{
            Toast.makeText(context,"没有上一个视频了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 全屏播放
     */
    public Intent setMax(){
        return null;
    }

    /**
     * 关闭进度条异步线程
     */
    public void updateSeekBarCancel(){
        if (updateSeekBar != null){
            updateSeekBar.cancel(true);
            updateSeekBar = null;
        }
    }

    /**
     * 释放资源
     */
    public void Destery(){
        surfaceViewPlay.stop();
        updateSeekBarCancel();
        if (closeLinear_t != null){
            closeLinear_t.interrupt();
        }
        if (surfaceViewPlay != null){
            surfaceViewPlay.Destery();
        }
    }

}

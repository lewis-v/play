package com.yw.play.window;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yw.play.R;
import com.yw.play.data.VideoInfo;
import com.yw.play.utils.MySufaceViewPlay;

public class WindowService extends Service implements View.OnClickListener{
    public static final int PLAY = 0;//播放
    public static final int PAUSE = 1;//暂停
    public static final int END = 2;//播放结束

    private RelativeLayout layout,rl_play;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private VideoInfo videoInfo;//播放信息
    private MySufaceViewPlay play; //视频播放控制器
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private ImageView img_play;//播放暂停按钮
    private ImageView img_delete;//关闭按钮
    private int playStatus = END;//当前视频状态
    private onServiceChangeLinstener onServiceChangeLinstener;//回调方法
    private float downX = 0;
    private float downY = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.surfaceview_local:
                if (rl_play.getVisibility() == View.VISIBLE){
                    rl_play.setVisibility(View.GONE);
                }else {
                    rl_play.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.img_delete:
                if (onServiceChangeLinstener != null){
                    onServiceChangeLinstener.onServiceStop();
                }
                break;
            case R.id.img_play:
                if (playStatus == PLAY){
                    pause();
                }else if (playStatus == PAUSE){
                    continuePlay();
                }else if (playStatus == END){
                    play();
                }
                break;
        }
    }

    public interface onServiceChangeLinstener{
        void onServiceStop();
    }

    public class MyBind extends Binder{
        public WindowService getService(){
            return WindowService.this;
        }

        public void setVideoInfo(VideoInfo videoInfo){
            WindowService.this.setVideoInfo(videoInfo);
        }

        public VideoInfo getVideoInfo(){
            return  WindowService.this.getVideoInfo();
        }

        public boolean isShow(){
            return  WindowService.this.isShow();
        }

        public void play(){
            WindowService.this.play();
        }

        public void pause(){
            WindowService.this.pause();
        }

        public void setOnServiceChangeListener(onServiceChangeLinstener onServiceChangeListener){
            WindowService.this.onServiceChangeLinstener = onServiceChangeListener;
        }
    }

    public WindowService() {
        Log.i("---WindowService---","");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("---returnBind---","");
        System.out.println("---returnBind---");
        if (layout != null){
            windowManager.removeView(layout);
            layout = null;
        }
        createView();
        return new MyBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
       super.onUnbind(intent);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("---start---","");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (layout != null){
            windowManager.removeView(layout);
            layout = null;
        }
    }


    public VideoInfo getVideoInfo() {
        return videoInfo;
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        this.videoInfo = videoInfo;
        play.addVideo(videoInfo);
    }

    public void createView(){
        layoutParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager)getApplication()
                .getSystemService(getApplication().WINDOW_SERVICE);
        layoutParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.x = 0;
        layoutParams.y = 0;
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;
        layoutParams.width = width/2;
        layoutParams.height = height/5;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        layout = (RelativeLayout) inflater.inflate(R.layout.window_video,null);
        rl_play = (RelativeLayout) layout.findViewById(R.id.rl_play);

        surfaceView = (SurfaceView) layout.findViewById(R.id.surfaceview_local);
        surfaceView.setOnClickListener(this);

        img_play = (ImageView)layout.findViewById(R.id.img_play);
        img_play.setOnClickListener(this);

        img_delete = (ImageView)layout.findViewById(R.id.img_delete);
        img_delete.setOnClickListener(this);

        play = new MySufaceViewPlay();
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setKeepScreenOn(true);
        surfaceHolder.addCallback(play);

        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (rl_play.getVisibility() == View.VISIBLE){
                    return false;
                }else {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            downX = motionEvent.getX();
                            downY = motionEvent.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            layoutParams.x += (motionEvent.getX() - downX)/3;
                            layoutParams.y += (motionEvent.getY() - downY)/3;
                            //除以3为消除抖动
                            if (layout != null){
                                windowManager.updateViewLayout(layout,layoutParams);
                            }
                            return true;
                        case MotionEvent.ACTION_UP:

                            break;
                    }
                }
                return false;
            }
        });

        windowManager.addView(layout,layoutParams);

        layout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                , View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

    }

    /**
     * 播放
     */
    public void play(){
        Log.i("---play---","");
        play.playVideo(surfaceHolder,0);
        playStatus = PLAY;
        img_play.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    /**
     * 暂停
     */
    public void pause(){
        Log.i("---pause---","");
        play.pause();
        playStatus = PAUSE;
        img_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
    }

    /**
     * 继续播放
     */
    public void continuePlay(){
        Log.i("---continue---","");
        play.continuePlay();
        playStatus = PLAY;
        img_play.setImageResource(R.drawable.ic_pause_black_24dp);
    }

    /**
     * 是否已显示小窗口
     * @return
     */
    public boolean isShow(){
        if (layout.getVisibility() == View.VISIBLE){
            return true;
        }else{
            return false;
        }
    }

}

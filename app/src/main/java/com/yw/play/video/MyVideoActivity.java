package com.yw.play.video;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.data.VideoInfo;
import com.yw.play.utils.CursorTool;
import com.yw.play.utils.SurfaceViewPlay;
import com.yw.play.utils.UriToFileUtil;

import java.util.ArrayList;
import java.util.List;

public class MyVideoActivity extends AppCompatActivity implements ILocalVideo{
    private PresenterVideo presenterVideo;
    private SurfaceView surfaceView;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private ImageButton button_last,button_play,button_next,button_max,button_stop;
    private SeekBar seekBar;
    private TextView textView_time,tipText;
    private boolean isTouch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_video);
        if (getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            initView();
            initListener();
            setWinBrightness(0.5f);
            //获取uri,不为空时则为系统调用
            Uri uri = getIntent().getData();
            System.out.println("intent:"+getIntent());
            if (uri != null){
                SurfaceViewPlay surfaceViewPlay = new SurfaceViewPlay();
                List<String> paths = new ArrayList<String>();
                List<String> names = new ArrayList<String>();
                List<Long> times = new ArrayList<Long>();
                List<Bitmap> img = new ArrayList<Bitmap>();
                paths.add(UriToFileUtil.getPath(this,uri));
                times.add(60000l);
                surfaceViewPlay.setVideoList(paths, names, times, img);
                surfaceViewPlay.setPlayPosition(0);
                presenterVideo = new PresenterVideo(this, this
                        , surfaceViewPlay);
            }else {
                presenterVideo = new PresenterVideo(this, this
                        , (SurfaceViewPlay) getIntent().getParcelableExtra("play"));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getResources().getConfiguration().orientation!= Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onBackPressed() {
        result();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println(":::destery");
        if (presenterVideo != null) {
            presenterVideo.Destery();
        }

    }

    /**
     * 初始化ui
     */
    public void initView(){
        //播放视频surfaceview
        surfaceView = (SurfaceView)findViewById(R.id.surfaceview_local);
        //surfaceview的布局
        relativeLayout = (RelativeLayout)findViewById(R.id.relative_local);
        //视频控制的布局
        linearLayout = (LinearLayout)findViewById(R.id.linear_local_surface);
        //显示播放时间的文本框
        textView_time = (TextView)findViewById(R.id.textview_playtime);
        //滑动时提示
        tipText = (TextView)findViewById(R.id.textview_playtip);

        //播放进度条
        seekBar = (SeekBar)findViewById(R.id.seekbar_playbar);


        //停止播放
        button_stop = (ImageButton)findViewById(R.id.button_stop);
        button_stop.setImageResource(R.mipmap.stop);


        //播放上一个视频
        button_last = (ImageButton)findViewById(R.id.button_last);
        button_last.setImageResource(R.drawable.ic_fast_rewind_black_24dp);


        //播放视频
        button_play = (ImageButton)findViewById(R.id.button_play);
        button_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);


        //播放下一个视频
        button_next = (ImageButton)findViewById(R.id.button_next);
        button_next.setImageResource(R.drawable.ic_fast_forward_black_24dp);


        //全屏/缩放视频
        button_max = (ImageButton)findViewById(R.id.button_max);
        button_max.setImageResource(R.mipmap.video_all);

    }

    /**
     * 初始化监听器
     */
    public void initListener(){
        //播放视频surfaceview
        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterVideo.setLinearTime();
            }
        });
        surfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                presenterVideo.surfaceOnTouch(event);
                return false;
            }
        });

        //播放进度条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isTouch) {
                    presenterVideo.setTimeText(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isTouch = false;
            }
        });

        //停止播放
        button_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterVideo.mediaStop();
            }
        });

        //播放上一个视频
        button_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterVideo.lastMedia();
            }
        });

        //播放视频
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterVideo.mediaPlay();
            }
        });

        //播放下一个视频
        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenterVideo.nextMedia();
            }
        });

        //全屏/缩放视频
        button_max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result();
                finish();
            }
        });
    }

    /**
     * 设置返回父activity 信息并关闭
     */
    public void result(){
        Intent intent = presenterVideo.result();
        setResult(0,intent);
    }

    /**
     * 设置recyclerview适配器
     * @param adapter
     */
    @Override
    public void initRecyclerView(RecyclerView.Adapter adapter) {

    }

    /**
     * 隐藏或显示surfaceview
     * @param IsVisible
     */
    @Override
    public void setSurfaceVisible(boolean IsVisible) {

    }

    /**
     * 获取SurfaceHolder
     */
    @Override
    public SurfaceHolder getSurfaceHolder() {
        return surfaceView.getHolder();
    }

    /**
     * 设置seekbar的目前进度
     * @param progress
     */
    @Override
    public void setSeekbar(final int progress) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoActivity.this.seekBar.setProgress(progress);
            }
        });

    }

    /**
     * 设置播放显示的时间进度
     * @param text
     */
    @Override
    public void setTextTime(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyVideoActivity.this.textView_time.setText(text);
            }
        });

    }


    /**
     * 设置播放按钮
     * @param isPlay
     */
    @Override
    public void setButtonPlay(final boolean isPlay) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isPlay){
                    button_play.setImageResource(R.drawable.ic_pause_black_24dp);
                }else{
                    button_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            }
        });

    }
    @Override
    public void hideLinear(final boolean isHide) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isHide){
                    linearLayout.setVisibility(View.GONE);
                }else{
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    @Override
    public int getLinearVisible() {
        return linearLayout.getVisibility();
    }

    @Override
    public void setTipText(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tipText.setText(tip);
            }
        });
    }
    @Override
    public void setWinBrightness(float brightness) {
        Window window =getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = brightness;
        window.setAttributes(layoutParams);
    }
    @Override
    public float getWinBrightness() {
        Window window =getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        return layoutParams.screenBrightness;
    }

}


package com.yw.play.main.video;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yw.play.R;
import com.yw.play.adapter.MyHomeAdapter;
import com.yw.play.base.BaseFragment;
import com.yw.play.data.VideoInfo;
import com.yw.play.main.MainConstract;
import com.yw.play.utils.SurfaceViewPlay;
import com.yw.play.video.MyVideoActivity;
import com.yw.play.window.WindowService;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFragment extends BaseFragment implements VideoConstract{
    private String TAG = "VideoFragment";

    private VideoPresenter presenter;
    private View view;
    private RecyclerView recycler;//视频信息显示列表
    private MyHomeAdapter adapter;//列表适配器
    private List<VideoInfo> videoInfos = new ArrayList<>();//视频信息列表
    private WindowService.MyBind bind = null;//小窗口bind
    private int playPosition;//播放位置
    private MyServiceConnection serviceConnection;

    class MyServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("---con_Service---","");
            if (askForPermission()) {
                Toast.makeText(getContext(), "开启小窗口播放", Toast.LENGTH_SHORT).show();
                bind = (WindowService.MyBind) iBinder;
                bind.setOnServiceChangeListener(new WindowService.onServiceChangeLinstener() {
                    @Override
                    public void onServiceStop() {
                        if (bind != null) {
                            getActivity().unbindService(serviceConnection);
                            bind = null;
                        }
                    }
                });
                bind.setVideoInfo(videoInfos.get(playPosition));
                bind.play();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("---dis_Service---","");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_video, container, false);
        presenter = new VideoPresenter(this,getContext());
        initView();
        return view;
    }

    public void initView(){
        recycler = (RecyclerView)view.findViewById(R.id.recycler);

        adapter = new MyHomeAdapter(getContext(),videoInfos);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setAdapter(adapter);
        adapter.setOnItemClikListener(new MyHomeAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (bind == null) {
                    ((MainConstract)getActivity()).setPause();
                    Intent intent = new Intent(getContext(), MyVideoActivity.class);
                    SurfaceViewPlay surfaceViewPlay = new SurfaceViewPlay();
                    List<String> paths = new ArrayList<String>();
                    List<String> names = new ArrayList<String>();
                    List<Long> times = new ArrayList<Long>();
                    List<Bitmap> img = new ArrayList<Bitmap>();
                    for (VideoInfo videoInfo : videoInfos) {
                        paths.add(videoInfo.getPath());
                        names.add(videoInfo.getName());
                        times.add(videoInfo.getTime());
                    }
                    surfaceViewPlay.setVideoList(paths, names, times, img);
                    Log.i(TAG, surfaceViewPlay.toString());
                    surfaceViewPlay.setPlayPosition(position);
                    intent.putExtra("play", surfaceViewPlay);
                    startActivity(intent);
                }else {
                    Toast.makeText(getContext(),"请关闭小窗口后再播放视频",Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onItemLongClick(View view, int position)
            {
            }

            @Override
            public void onWindowClick(View view, int position) {
                if (askForPermission()) {
                    if (bind == null){
                    ((MainConstract) getActivity()).setPause();
                    playPosition = position;
                    Intent intent1 = new Intent(getActivity(), WindowService.class);
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    serviceConnection = new MyServiceConnection();
                    getActivity().bindService(intent1, serviceConnection, getActivity().BIND_AUTO_CREATE);
                    }else {
                        Toast.makeText(getContext(),"小窗口正在播放中",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(getContext(),"请给予读写的权限",Toast.LENGTH_SHORT).show();
        }else {
            refreshInfo();
        }

    }

    /**
     * 检测是否有悬浮框权限,没有则引导用户开启
     */
    public boolean askForPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getContext())){
                Toast.makeText(getContext(),"无悬浮窗权限,请开启权限",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                        , Uri.parse("package:"+getActivity().getPackageName()));
                startActivity(intent);
                return false;
            }
        }
        return true;
    }

    @Override
    public void refreshInfo(){
        super.refreshInfo();
        if (presenter != null) {
            presenter.refreshInfo();
        }
    }

    @Override
    public void onRefreshSuccess(List<VideoInfo> videoInfoList){
        videoInfos.clear();
        videoInfos.addAll(videoInfoList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (bind != null){
            try {
                getActivity().unbindService(serviceConnection);
                bind = null;
            }catch (IllegalArgumentException e){
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}

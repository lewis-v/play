package com.yw.play.main.music;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yw.play.R;
import com.yw.play.adapter.MyMusicHomeAdapter;
import com.yw.play.base.BaseFragment;
import com.yw.play.data.MusicInfo;
import com.yw.play.dialog.DetailDialogFragment;
import com.yw.play.music.MusicService;
import com.yw.play.musicfragment.MusicListConstract;
import com.yw.play.musicfragment.MusicListFragment;
import com.yw.play.musicfragment.MusicMoreConstract;
import com.yw.play.musicfragment.MusicMoreFragment;
import com.yw.play.utils.MusicPlay;
import com.yw.play.utils.WindowUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicFragment extends BaseFragment implements MusicConstract
        ,View.OnClickListener,MusicListConstract,MusicMoreConstract{
    private View view;
    private MusicPresenter presenter;
    private RecyclerView recycler;//音乐信息显示列表
    private MyMusicHomeAdapter adapter;//音乐列表适配器
    private Toolbar tool;
    private TextView tv_name,tv_auth;//当前播放的名字,作者
    private ImageView img_play;//播放img
    private LinearLayout ll_play,ll_list;//播放暂停组件,播放列表组件
    private List<MusicInfo> musicInfos = new ArrayList<>();//音乐信息显示列表
    private List<MusicInfo> musicPlayList = new ArrayList<>();//音乐播放列表
    private MusicService.MyBind bind = null;//音乐播放服务
    private FrameLayout fl_list,fl_more;
    private MusicListFragment musicListFragment = null;//播放列表fragment
    private MusicMoreFragment musicMoreFragment = null;//更多fragment
    private FragmentTransaction fragmentTransaction;
    private boolean listIsShowing = false;//列表动画是否执行中
    private boolean isTouch = false;//是否正在触发一个功能

    private ServiceConnection serviceConnection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.i("---con_Service---","");
            bind = (MusicService.MyBind)iBinder;
            bind.setOnPlayStutasChangeLinstener(new MusicService.OnPlayStutasChangeLinstener() {
                @Override
                public void onPlayNext(int position) {
                    tv_name.setText(musicPlayList.get(position).getName());
                    tv_auth.setText(musicPlayList.get(position).getAuth());
                    img_play.setImageResource(R.drawable.ic_pause_black_24dp);
                    musicListFragment.notifyList(position);
                    adapter.setPlayPath(musicInfos.get(position).getPath());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onStop() {
                    img_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                }
            });
            musicPlayList.clear();
            musicPlayList.addAll(musicInfos);
            if (musicPlayList.size() > 0) {
                tv_name.setText(musicPlayList.get(adapter.getPosition()).getName());
                tv_auth.setText(musicPlayList.get(adapter.getPosition()).getAuth());
            }
            bind.getMusicPlay().setMusicPlayList(musicPlayList);
            bind.getMusicPlay().setPlayPosition(adapter.getPosition());

            musicListFragment = new MusicListFragment();
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fl_list, musicListFragment);
            fragmentTransaction.commit();
            fragmentTransaction.postOnCommit(new Runnable() {
                @Override
                public void run() {
                    musicListFragment.notifyList(adapter.getPosition());
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.i("---dis_Service---","");
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music, container, false);
        presenter = new MusicPresenter(this,getContext());
        initView();
        return view;
    }

    public void initView(){
        fl_list = (FrameLayout)view.findViewById(R.id.fl_list);
        fl_more = (FrameLayout)view.findViewById(R.id.fl_more);

        tool = (Toolbar)view.findViewById(R.id.tool);

        tool.setOnClickListener(this);

        img_play = (ImageView)view.findViewById(R.id.img_play);

        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_auth = (TextView)view.findViewById(R.id.tv_auth);

        ll_play = (LinearLayout)view.findViewById(R.id.ll_play);
        ll_list = (LinearLayout)view.findViewById(R.id.ll_list);

        ll_list.setOnClickListener(this);
        ll_play.setOnClickListener(this);

        recycler = (RecyclerView)view.findViewById(R.id.recycler);

        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MyMusicHomeAdapter(getContext(),musicInfos);
        recycler.setAdapter(adapter);
        adapter.setPlayPath(getActivity().getSharedPreferences("play.db",Context.MODE_PRIVATE).getString("play_path",null));
        adapter.notifyDataSetChanged();
        adapter.setOnItemClick(new MyMusicHomeAdapter.onItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                //点击播放时会还原播放列表
                if (musicPlayList .size() != musicInfos.size()){
                    musicPlayList.clear();
                    musicPlayList.addAll(musicInfos);
                    musicListFragment.notifyList(position);
                    if (bind != null){
                        bind.clearNext();
                    }
                }
                //播放音乐
                playMusic(position);
            }

            @Override
            public void onMoreClick(View view, int position) {
                //其他功能选择
                addFragmentToMore(musicInfos.get(position).getPath());
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
     * 播放指定位置的音乐
     * @param position
     */
    public void playMusic(int position){
        if (bind != null){
            bind.getMusicPlay().playVideo(position);
            tv_name.setText(musicPlayList.get(position).getName());
            tv_auth.setText(musicPlayList.get(position).getAuth());
            img_play.setImageResource(R.drawable.ic_pause_black_24dp);
            musicListFragment.notifyList(position);
            adapter.setPlayPath(musicPlayList.get(position).getPath());
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPlayType(int type) {
        if (bind != null) {
            bind.setPlayType(type);
            musicListFragment.setPlayType();
        }
    }

    @Override
    public int getPlayType() {
        if (bind != null) {
            return bind.getPlayType();
        }else {
            return MusicService.ORDER;
        }
    }

    @Override
    public void onRefreshSuccess(List<MusicInfo> musicInfos){
        this.musicInfos.clear();
        this.musicInfos.addAll(musicInfos);
        adapter.notifyDataSetChanged();
        if (bind == null) {
            Intent intent = new Intent(getActivity(), MusicService.class);
            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void refreshInfo() {
        super.refreshInfo();
        if (presenter != null) {
            presenter.refreshInfo();
        }
    }

    @Override
    public void onDestroy() {
        if (bind != null){
            getActivity().unbindService(serviceConnection);
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (isTouch){
            return;
        }
        isTouch = true;
        switch (view.getId()){
            case R.id.tool:
//                音乐播放界面
                break;
            case R.id.ll_play:
                if (bind != null){
                    int playStatus = bind.getMusicPlay().getPlaying();
                    if (playStatus == MusicPlay.PLAY){
                        bind.getMusicPlay().pause();
                        img_play.setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    }else if (playStatus == MusicPlay.PAUSE){
                        bind.getMusicPlay().continuePlay();
                        img_play.setImageResource(R.drawable.ic_pause_black_24dp);
                    }else if (playStatus == MusicPlay.STOP){
                        bind.getMusicPlay().playVideo(bind.getMusicPlay().getPlayPosition());
                        img_play.setImageResource(R.drawable.ic_pause_black_24dp);
                    }
                }
                break;
            case R.id.ll_list:
                //播放列表
                showMusicList(true);
                break;
        }
        //延时,防止连续点击出现播放器崩溃(播放器状态未改变结束时就切换状态的出错)
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {}
        isTouch = false;
    }

    @Override
    public List<MusicInfo> getMusicList() {
        return musicPlayList;
    }

    @Override
    public void hideView() {
        showMusicList(false);
        addFragmentToMore(null);
    }

    @Override
    public boolean isShowList() {
        if (fl_list.getVisibility() == View.VISIBLE || musicMoreFragment != null && musicMoreFragment.isShow() == View.VISIBLE){
            return true;
        }
        return false;
    }

    /**
     * 添加更多的fragment
     * @param path 打开的音乐地址,null为关闭
     */
    public void addFragmentToMore(String path) {
        fl_more.setVisibility(View.VISIBLE);
        musicMoreFragment = new MusicMoreFragment();
        musicMoreFragment.setPath(path);
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_more, musicMoreFragment);
        fragmentTransaction.commit();
    }

    /**
     * 带动画显示播放列表
     * @param isVisiable 是否显示
     */
    public void showMusicList(final boolean isVisiable){
        if(listIsShowing){
            return;
        }
        if (isVisiable && fl_list.getVisibility() == View.VISIBLE
                || !isVisiable && fl_list.getVisibility() == View.GONE){//当前状态与改变后的状态相同
            return;
        }
        listIsShowing = true;
        if (musicListFragment == null) {
            musicListFragment = new MusicListFragment();
            fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fl_list, musicListFragment);
            fragmentTransaction.commit();
            fragmentTransaction.postOnCommit(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    showAnimator(isVisiable);
                }
            });
        }else {
            showAnimator(isVisiable);
        }

    }

    public void showAnimator(final boolean isVisiable){
        fl_list.setVisibility(View.VISIBLE);
        int height = fl_list.getHeight();
        if (height == 0){
            height = WindowUtils.getInstance(getActivity().getWindowManager()).getWindowHeight() ;
        }
        ObjectAnimator objectAnimator;
        if (isVisiable) {
            objectAnimator = ObjectAnimator.ofFloat(fl_list, "translationY", (float) -height, 0f);
        }else {
            objectAnimator = ObjectAnimator.ofFloat(fl_list, "translationY", 0f, (float) -height);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator);
        animatorSet.setDuration(300);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator)  {
                musicListFragment.setBack(false);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (!isVisiable){
                    fl_list.setVisibility(View.GONE);
                }
                musicListFragment.setBack(true);
                listIsShowing = false;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    @Override
    public void checkDetail(String Past) {
        DetailDialogFragment detailDialogFragment = new DetailDialogFragment();
        detailDialogFragment.setMusicInfo(adapter.getMusicInfoByPath(Past));
        detailDialogFragment.show(getFragmentManager(),"");
    }

    @Override
    public void setNext(String Path) {
        bind.addNext(adapter.getMusicInfoByPath(Path));
    }

    @Override
    public void showView(String path) {
        addFragmentToMore(path);
    }
}

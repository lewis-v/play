package com.yw.play.musicfragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.adapter.MyMusicListAdapter;
import com.yw.play.data.MusicInfo;
import com.yw.play.music.MusicService;
import com.yw.play.utils.WindowUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicListFragment extends Fragment implements View.OnClickListener{
    private View view;
    private RecyclerView recycler_music_list;
    private MyMusicListAdapter adapter;
    private List<MusicInfo> musicInfos = new ArrayList<>();
    private FrameLayout fl_back;
    private ImageView img_type,img_close;
    private TextView tv_play_type,tv_list_num;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_list, container, false);
        initView();
        return view;
    }

    public void initView(){
        img_type = (ImageView)view.findViewById(R.id.img_type);
        img_close = (ImageView)view.findViewById(R.id.img_close);
        img_type.setOnClickListener(this);
        img_close.setOnClickListener(this);

        tv_play_type = (TextView)view.findViewById(R.id.tv_play_type);
        tv_list_num = (TextView)view.findViewById(R.id.tv_list_num);
        setPlayType();

        fl_back = (FrameLayout) view.findViewById(R.id.fl_back);
        fl_back.setOnClickListener(this);

        recycler_music_list = (RecyclerView)view.findViewById(R.id.recycler_music_list);

        recycler_music_list.setItemAnimator(new DefaultItemAnimator());
        recycler_music_list.setLayoutManager(new LinearLayoutManager(getContext()));
        musicInfos = ((MusicListConstract)getActivity()).getMusicList();
        tv_list_num.setText(" (" +musicInfos.size()+"首)");
        adapter = new MyMusicListAdapter(getContext(),musicInfos
                ,getActivity().getSharedPreferences("play.db", Context.MODE_PRIVATE).getString("play_path",null));
        recycler_music_list.setAdapter(adapter);
        adapter.setOnItemClick(new MyMusicListAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                ((MusicListConstract)getActivity()).playMusic(position);
            }

            @Override
            public void onDeleteClick(View view, int position) {
                musicInfos.remove(position);
                adapter.notifyItemRemoved(position);
                tv_list_num.setText(" (" +musicInfos.size()+"首)");
                if (position == adapter.getPlayPosition()) {
                    ((MusicListConstract) getActivity()).playMusic(position);
                }
            }
        });
        recycler_music_list.post(new Runnable() {
            @Override
            public void run() {
                wrapHright();
            }
        });
    }

    /**
     * 设置播放的类型,通过父fragment获取状态
     */
    public void setPlayType(){
        switch(((MusicListConstract) getActivity()).getPlayType()){
            case MusicService.ORDER:
                tv_play_type.setText("顺序播放");
                img_type.setImageResource(R.drawable.ic_replay_black_24dp);
                break;
            case MusicService.SINGLE:
                tv_play_type.setText("单曲循环");
                img_type.setImageResource(R.drawable.ic_repeat_one_black_24dp);
                break;
            case MusicService.RANDOM:
                tv_play_type.setText("随机播放");
                img_type.setImageResource(R.drawable.ic_shuffle_black_24dp);
                break;
        }
    }

    /**
     * 刷新列表播放的位置
     * @param position
     */
    public void notifyList(int position){
        if (adapter!=null && musicInfos.size() > position){
            adapter.setPlayPosition(position);
            recycler_music_list.smoothScrollToPosition(position);
            adapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("play.db",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("play_path",musicInfos.get(position).getPath());
            editor.apply();
        }
    }

    /**
     * 设置播放列表
     * @param musicInfos
     */
    public void setList(List<MusicInfo> musicInfos){
        this.musicInfos = musicInfos;
        adapter.notifyDataSetChanged();
        wrapHright();
    }

    /**
     * 当列表数量大于9时限制高度
     */
    public void wrapHright(){
        int height = WindowUtils.getInstance(getActivity().getWindowManager()).getWindowHeight() *2/3;
        if (musicInfos != null && musicInfos.size() > 9) {
            ViewGroup.LayoutParams layoutParams = recycler_music_list.getLayoutParams();
            layoutParams.height = height;
            recycler_music_list.setLayoutParams(layoutParams);
        }
    }

    /**
     * 设置背景半透明
     */
    public void setBack(boolean isBack){
        if (fl_back != null) {
            if (isBack) {
                fl_back.setBackgroundResource(R.color.back);
            } else {
                fl_back.setBackgroundResource(R.color.transparent);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_back:
                ((MusicListConstract)getActivity()).hideView();
                break;
            case R.id.img_close:
                ((MusicListConstract)getActivity()).hideView();
                break;
            case R.id.img_type:
                int playType =  ((MusicListConstract)getActivity()).getPlayType();
                if (playType < MusicService.RANDOM){
                    playType++;
                }else {
                    playType = MusicService.ORDER;
                }
                ((MusicListConstract)getActivity()).setPlayType(playType);
                break;
        }
    }
}

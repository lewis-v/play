package com.yw.play.musicfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.yw.play.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicMoreFragment extends Fragment implements View.OnClickListener{
    private View view;
    private LinearLayout ll_detail,ll_next,ll_back,ll_more,ll_more_fragment;
    private String path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_more, container, false);
        initView();
        return view;
    }

    public void initView(){
        ll_more_fragment = (LinearLayout)view.findViewById(R.id.ll_more_fragment);
        ll_back = (LinearLayout)view.findViewById(R.id.ll_back);
        ll_detail = (LinearLayout)view.findViewById(R.id.ll_detail);
        ll_next = (LinearLayout)view.findViewById(R.id.ll_next);
        ll_more = (LinearLayout)view.findViewById(R.id.ll_more);

        ll_detail.setOnClickListener(this);
        ll_next.setOnClickListener(this);
        ll_back.setOnClickListener(this);
        ll_more.setOnClickListener(this);

        if ( path == null){
            ll_more_fragment.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_detail:
                ((MusicMoreConstract)getActivity()).checkDetail(getPath());
                ((MusicMoreConstract)getActivity()).showView(null);
                break;
            case R.id.ll_next:
                ((MusicMoreConstract)getActivity()).setNext(getPath());
                ((MusicMoreConstract)getActivity()).showView(null);
                break;
            case R.id.ll_back:
                ((MusicMoreConstract)getActivity()).showView(null);
                break;
            case R.id.ll_more:
                ((MusicMoreConstract)getActivity()).showView(null);
                break;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取此界面是否显示
     * @return
     */
    public int isShow(){
        return ll_more_fragment.getVisibility();
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation anim ;
        if (enter) {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_more_fragment_enter);
        }else {
            anim = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_more_fragment_exit);
            ll_back.setBackgroundResource(R.color.transparent);
        }
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                animation.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation.cancel();
                ll_back.setBackgroundResource(R.color.back);
//                ll_more.setBackgroundResource(R.color.back);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return anim;
    }
}

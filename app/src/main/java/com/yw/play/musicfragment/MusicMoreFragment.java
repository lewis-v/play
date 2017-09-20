package com.yw.play.musicfragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.yw.play.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MusicMoreFragment extends Fragment implements View.OnClickListener{
    private View view;
    private LinearLayout ll_detail,ll_next,ll_back;
    private String path;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_music_more, container, false);
        initView();
        return view;
    }

    public void initView(){
        ll_back = (LinearLayout)view.findViewById(R.id.ll_back);
        ll_detail = (LinearLayout)view.findViewById(R.id.ll_detail);
        ll_next = (LinearLayout)view.findViewById(R.id.ll_next);

        ll_detail.setOnClickListener(this);
        ll_next.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_detail:
                ((MusicMoreConstract)getParentFragment()).checkDetail(getTag());
                break;
            case R.id.ll_next:
                ((MusicMoreConstract)getParentFragment()).setNext(getTag());
                break;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

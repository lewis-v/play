package com.yw.play.dialog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.base.BaseDialogFragment;
import com.yw.play.data.MusicInfo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailDialogFragment extends BaseDialogFragment {
    private TextView tv_name,tv_auth,tv_time,tv_path;
    private Button bt_entry;
    private MusicInfo musicInfo;

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout((int) (dm.widthPixels*0.9), ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_detail_dialog, container, false);
        setCancelable(false);
        super.onCreateView(inflater,container,savedInstanceState);
        return view;
    }

    @Override
    public void initView() {
        super.initView();
        tv_name = (TextView)view.findViewById(R.id.tv_name);
        tv_auth = (TextView)view.findViewById(R.id.tv_auth);
        tv_path = (TextView)view.findViewById(R.id.tv_path);
        tv_time = (TextView)view.findViewById(R.id.tv_time);

        bt_entry = (Button)view.findViewById(R.id.bt_entry);
        bt_entry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        if (musicInfo == null){
            dismiss();
        }else {
            Log.i("---musicInfo---",musicInfo.toString());
            tv_auth.setText(musicInfo.getAuth());
            tv_time.setText(new SimpleDateFormat("mm:ss").format(musicInfo.getTime()));
            tv_path.setText(musicInfo.getPath());
            tv_name.setText(musicInfo.getName());
        }
    }

    public void setMusicInfo(MusicInfo musicInfo){
        this.musicInfo = musicInfo;
    }
}

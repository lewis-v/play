package com.yw.play.base;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * Created by ude on 2017-08-07.
 */

public class BaseDialogFragment<P> extends DialogFragment {
    protected P mPresenter;
    protected View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    onBack();
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    public void initView(){
    }

    public void onBack(){
        dismiss();
    }
}

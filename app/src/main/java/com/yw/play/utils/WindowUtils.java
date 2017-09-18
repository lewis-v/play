package com.yw.play.utils;

import android.util.DisplayMetrics;
import android.view.WindowManager;


/**
 * Created by ude on 2017-09-15.
 */

public class WindowUtils {
    private int width , height;//屏幕宽度和高度
    private static WindowUtils windowUtils;

    public WindowUtils(WindowManager manager){
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels;
        height = outMetrics.heightPixels;
    }

    public static WindowUtils getInstance(WindowManager manager){
        if (windowUtils == null){
            windowUtils = new WindowUtils(manager);
        }
        return windowUtils;
    }

    public int getWindowWidth(){
        return width;
    }

    public int getWindowHeight(){
        return height;
    }


}

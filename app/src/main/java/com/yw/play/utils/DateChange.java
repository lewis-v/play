package com.yw.play.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/6/26.
 */
public class DateChange {
    public static String longToDateString(long time){
        return new SimpleDateFormat("mm:ss").format(new Date(time));
    }

}

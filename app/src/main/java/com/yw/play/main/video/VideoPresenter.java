package com.yw.play.main.video;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yw.play.utils.CursorTool;

/**
 * Created by Administrator on 2017/9/10.
 */

public class VideoPresenter {
    private VideoConstract constract;
    private Context context;

    public VideoPresenter(VideoConstract constract,Context context){
        this.constract = constract;
        this.context = context;
    }

    public void refreshInfo(){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                , null  , null, null, null);
        CursorTool cursorTool = new CursorTool(cursor);
        constract.onRefreshSuccess(cursorTool.getVideoInfo());
        if (cursor != null) {
            cursor.close();
        }
    }
}

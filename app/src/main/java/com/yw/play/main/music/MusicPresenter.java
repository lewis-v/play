package com.yw.play.main.music;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.yw.play.utils.CursorTool;

/**
 * Created by Administrator on 2017/9/17.
 */

public class MusicPresenter {
    private MusicConstract constract;
    private Context context;

    public MusicPresenter( MusicConstract constract,Context context){
        this.constract = constract;
        this.context = context;
    }

    public void refreshInfo(){
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null, null, null, null);
        CursorTool cursorTool = new CursorTool(cursor);
        constract.onRefreshSuccess(cursorTool.getMusiceInfo());
        if (cursor != null) {
            cursor.close();
        }
    }
}

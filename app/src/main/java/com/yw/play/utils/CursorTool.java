package com.yw.play.utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.yw.play.data.MusicInfo;
import com.yw.play.data.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/6/20.
 */
public class CursorTool {
    private Cursor cursor;
    public CursorTool(Cursor cursor){
        this.cursor = cursor;
    }

    /**
     * 获取系统视频列表
     * @return
     */
    public List<VideoInfo> getVideoInfo(){
        List<VideoInfo> infos = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            VideoInfo videoInfo = new VideoInfo();
            //路径
            byte[] data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Media.DATA));
            videoInfo.setPath(new String(data, 0, data.length - 1));
            //名字
            data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            videoInfo.setName(new String(data, 0, data.length - 1));
            //缩略图地址
            data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            videoInfo.setImg(new String(data, 0, data.length - 1));
            //时间
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(
                    MediaStore.Video.Media.DURATION));
            videoInfo.setTime(time);
            infos.add(videoInfo);
        }
        return infos;
    }

    /**
     * 获取系统音乐信息列表
     * @return
     */
    public List<MusicInfo> getMusiceInfo(){
        List<MusicInfo> infos = new ArrayList<>();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            MusicInfo musicInfo = new MusicInfo();
            //路径
            byte[] data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            musicInfo.setPath(new String(data, 0, data.length - 1));
            //名字
            data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            musicInfo.setName(new String(data, 0, data.length - 1));
            //歌手
            data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));
            musicInfo.setAuth(new String(data, 0, data.length - 1));
            //时间
            long time = cursor.getLong(cursor.getColumnIndexOrThrow(
                    MediaStore.Audio.Media.DURATION));
            musicInfo.setTime(time);
            infos.add(musicInfo);
        }
        return infos;
    }

    /**
     * 获取系统视频路径
     * @return
     */
    public List<String> getSysVieoPath(){
        cursor.moveToFirst();
        List<String> cache = new ArrayList<>();
        while (cursor.moveToNext()) {
            byte[] data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Media.DATA));
            cache.add(new String(data, 0, data.length - 1));
        }
        return cache;
    }

    /**
     * 获取系统视频名字
     * @return
     */
    public List<String> getSysVideoName(){
        cursor.moveToFirst();
        List<String> cache = new ArrayList<>();
        while (cursor.moveToNext()) {
            byte[] data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            cache.add(new String(data, 0, data.length - 1));
        }
        return cache;
    }

    /**
     * 获取系统视频缩略图
     * @return
     */
    public List<Bitmap> getSysVideoBitmap(){
        cursor.moveToFirst();
        List<Bitmap> cache = new ArrayList<>();
        while (cursor.moveToNext()) {
            byte[] data = cursor.getBlob(cursor
                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
            String Path = new String(data, 0, data.length - 1);
            //获取缩略图
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(Path
                    , MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            //调整缩略图大小150*150
            cache.add(ThumbnailUtils.extractThumbnail(bitmap, 150, 150,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT));

        }
        return cache;
    }

    public List<Long> getSysVideoTime(){
        cursor.moveToFirst();
        List<Long> cache = new ArrayList<>();
        while (cursor.moveToNext()) {
            long data = cursor.getLong(cursor.getColumnIndexOrThrow(
                    MediaStore.Video.Media.DURATION));
            cache.add(data);
        }
        return cache;

    }

}

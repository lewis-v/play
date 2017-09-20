package com.yw.play.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.data.MusicInfo;

import java.util.List;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2017/9/18.
 */

public class MyMusicListAdapter extends RecyclerView.Adapter<MyMusicListAdapter.MyViewHolder>{
    private List<MusicInfo> musicInfoList;
    private Context context;
    private OnItemClick onItemClick;
    private int playPosition = 0;//播放位置
    private String path;//默认播放地址

    public MyMusicListAdapter(Context context,List<MusicInfo> musicInfoList,String path){
        this.musicInfoList = musicInfoList;
        this.context = context;
        if (path != null){
            int i = 0;
            for (MusicInfo musicInfo : musicInfoList){
                if (musicInfo.getPath().equals(path)){
                    playPosition = i;
                    break;
                }
                i++;
            }
        }
    }

    public interface OnItemClick{
        void onItemClick(View view,int position);

        void onDeleteClick(View view,int position);
    }

    public void setOnItemClick(OnItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_music_list,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_name.setText(musicInfoList.get(position).getName());
        if (position == playPosition){
            holder.img_playing.setVisibility(View.VISIBLE);
        }else {
            holder.img_playing.setVisibility(View.GONE);
        }
        if (onItemClick!=null){
            holder.ll_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(view,holder.getLayoutPosition());
                }
            });
            holder.img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onDeleteClick(view,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musicInfoList.size();
    }

    public int getPlayPosition() {
        return playPosition;
    }

    public void setPlayPosition(int playPosition) {
        this.playPosition = playPosition;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_name;
        ImageView img_delete,img_playing;
        LinearLayout ll_item;
        public MyViewHolder(View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            img_delete = itemView.findViewById(R.id.img_delete);
            img_playing = itemView.findViewById(R.id.img_playing);
            ll_item = itemView.findViewById(R.id.ll_item);
        }
    }
}

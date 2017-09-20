package com.yw.play.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.data.MusicInfo;

import java.util.List;

/**
 * Created by Administrator on 2017/9/17.
 */

public class MyMusicHomeAdapter extends RecyclerView.Adapter<MyMusicHomeAdapter.MyViewHolder>{
    private Context context;
    private List<MusicInfo> musicInfos;
    private onItemClick onItemClick;
    private String playPath;//播放地址


    public interface onItemClick{
        void onItemClick(View view,int position);
        void onMoreClick(View view,int position);
    }

    public void setOnItemClick(onItemClick onItemClick){
        this.onItemClick = onItemClick;
    }

    public MyMusicHomeAdapter(Context context,List<MusicInfo> musicInfos){
        this.context = context;
        this.musicInfos = musicInfos;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_music,parent,false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tv_name.setText(musicInfos.get(position).getName());
        holder.tv_auth.setText(musicInfos.get(position).getAuth());
        if (playPath != null && musicInfos.get(position).getPath().equals(playPath)) {
            holder.tv_playing.setVisibility(View.VISIBLE);
        }else {
            holder.tv_playing.setVisibility(View.INVISIBLE);
        }

        if (onItemClick != null){
            holder.ll_music_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onItemClick(view,holder.getLayoutPosition());
                }
            });
            holder.ll_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClick.onMoreClick(view,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musicInfos.size();
    }

    /**
     * 设置播放地址
     * @param path
     */
    public void setPlayPath(String path){
        this.playPath = path;
    }

    public int getPosition(){
        int i = 0;
        for (MusicInfo musicInfo : musicInfos){
            if (musicInfo.getPath().equals(playPath)){
                return i;
            }
            i++;
        }
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout ll_music_item,ll_more;
        TextView tv_name,tv_auth,tv_playing;

        public MyViewHolder(View itemView) {
            super(itemView);
            ll_music_item = (LinearLayout)itemView.findViewById(R.id.ll_music_item);
            ll_more = (LinearLayout)itemView.findViewById(R.id.ll_more);
            tv_name = (TextView)itemView.findViewById(R.id.tv_name);
            tv_auth = (TextView)itemView.findViewById(R.id.tv_auth);
            tv_playing = (TextView)itemView.findViewById(R.id.tv_playing);
        }
    }
}

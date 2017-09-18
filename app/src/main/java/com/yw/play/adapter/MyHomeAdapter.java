package com.yw.play.adapter;

import android.content.Context;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.play.R;
import com.yw.play.data.VideoInfo;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2017/9/10.
 */

public class MyHomeAdapter extends RecyclerView.Adapter<MyHomeAdapter.MyViewHolder> {
    private OnItemClickLitener onItemClickLitener;
    private Context context;
    private List<VideoInfo> videoInfos;

    public MyHomeAdapter(Context context, List<VideoInfo> videoInfos) {
        super();
        this.context = context;
        this.videoInfos = videoInfos;
    }


    public interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);

        void onWindowClick(View view, int position);
    }
    public void setOnItemClikListener(OnItemClickLitener onItemClikListener){
        this.onItemClickLitener = onItemClikListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                context).inflate(R.layout.item_recyclerview, parent,false));
        return holder;
    }



    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv_title.setText(videoInfos.get(position).getName());
        holder.tv_time.setText(new SimpleDateFormat("mm:ss").format(videoInfos.get(position).getTime()));
        holder.img.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoInfos.get(position).getImg()
                , MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));


        if (onItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLitener.onItemClick(v, holder.getLayoutPosition());
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickLitener.onItemLongClick(v, holder.getLayoutPosition());
                    return false;
                }
            });
            holder.img_window.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickLitener.onWindowClick(view,holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return videoInfos.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title,tv_time;
        ImageView img,img_window;
        LinearLayout ll_item;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            img_window = (ImageView) view.findViewById(R.id.img_window);
            img = (ImageView) view.findViewById(R.id.img);
            ll_item = (LinearLayout)view.findViewById(R.id.ll_item);
        }
    }
}
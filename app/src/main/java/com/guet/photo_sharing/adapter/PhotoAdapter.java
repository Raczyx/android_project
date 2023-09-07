package com.guet.photo_sharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.entity.Photogroup;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photogroup> {

    private Context mContext;
    private List<Photogroup> newsList;
    private int resourceId;


    public PhotoAdapter(@NonNull Context context, int resource, @NonNull List<Photogroup> objects) {
        super(context, resource, objects);
        mContext = context;
        resourceId = resource;
        newsList = objects;
    }

    @Override
    public View getView(int position, View convertView , ViewGroup parent){
        View view;
        final ViewHolder vh;
        Photogroup photogroup = getItem(position);
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            vh = new ViewHolder();
            vh.tvTitle = view.findViewById(R.id.tv_title);
            vh.tvSource = view.findViewById(R.id.tv_subtitle);
            vh.ivImage = view.findViewById(R.id.iv_image);
            vh.ivDelete = view.findViewById(R.id.iv_delete);
            vh.tvPublishTime = view.findViewById(R.id.tv_publish_time);
            view.setTag(vh);
        }else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        vh.tvTitle.setText(photogroup.getTitle()!=null?photogroup.getTitle():"null");
        vh.tvSource.setText(photogroup.getUsername()!=null?photogroup.getUsername():"null");
        vh.ivDelete.setTag(position);
        vh.tvPublishTime.setText("");
        String temp = "http://114.132.248.191:9000/photo/fae0b27c451c728867a567e8c1bb4e53/8e807b75c9722775c318fcbf7f883d92.png";
        Glide.with(mContext).load(photogroup.getCover()!=null?photogroup.getCover():temp).into(vh.ivImage);


        return view;
    }


    class ViewHolder{
        TextView tvTitle;
        TextView tvSource;
        ImageView ivImage;
        TextView tvPublishTime;
        ImageView ivDelete;
    }
}

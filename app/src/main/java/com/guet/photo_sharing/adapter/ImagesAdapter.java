package com.guet.photo_sharing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.guet.photo_sharing.R;

import java.util.List;

public class ImagesAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private Integer resourceId;
    private List<String> urls;

    private ImageView imageView;
    public ImagesAdapter(@NonNull Context context, int resource, List<String> data) {
        super(context, resource,data);
        mContext = context;
        resourceId = resource;
        urls = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        String url = getItem(position);
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            imageView = view.findViewById(R.id.images_show);
            view.setTag(imageView);
        }else {
            view = convertView;
            imageView = (ImageView) view.getTag();
        }
        Glide.with(mContext).load(url).into(imageView);
        return view;
    }
}

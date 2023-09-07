package com.guet.photo_sharing.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guet.photo_sharing.R;

import java.io.File;
import java.util.List;

public class ImagesTempAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private int resourceId;
    private List<String> data;

    private ImageView imageView;
    public ImagesTempAdapter(@NonNull Context context, int resource, List<String> data) {
        super(context, resource,data);
        mContext = context;
        resourceId = resource;
        this.data = data;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.d("ImagesTemp","getView");
        View view;
        String path = getItem(position);
        if (convertView == null){
            view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            imageView = view.findViewById(R.id.images_show);
            view.setTag(imageView);
        }else {
            view = convertView;
            imageView = (ImageView) view.getTag();
        }
        Log.d("ImagesTemp",path+" 准备");
        File imgFile = new File(path);
        if (imgFile.exists()) {
            Log.d("ImagesTemp",path+" 准备加载");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            imageView.setImageBitmap(myBitmap);
            Log.d("ImagesTemp",path+" 已加载");
        }else {
            Log.d("ImagesTemp",path+" 不存在");
        }
        return view;

    }
}

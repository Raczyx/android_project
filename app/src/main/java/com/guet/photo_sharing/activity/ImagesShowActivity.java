package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guet.photo_sharing.MainActivity;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.adapter.ImagesAdapter;
import com.guet.photo_sharing.entity.Photogroup;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.entity.Temp;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ImagesShowActivity extends AppCompatActivity {

    private ImageView userCover;
    private TextView userName;
    private Button btDelete;
    private CheckBox isCollect;
    private ListView listView;
    private String email;
    private TextView textDescription;
    private String jwt;

    private List<String> urlList;
    private ImagesAdapter adapter;

    private Photogroup photogroup;
    private HttpUtil httpUtil;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_show);
        userCover = findViewById(R.id.show_user_cover);
        userName = findViewById(R.id.show_user_name);
        btDelete = findViewById(R.id.show_delete);
        isCollect = findViewById(R.id.show_iscollect);
        listView = findViewById(R.id.show_list);
        textDescription = findViewById(R.id.show_description);
        urlList = new ArrayList<>();
        adapter = new ImagesAdapter(this,R.layout.images_show,urlList);
        listView.setAdapter(adapter);

        photogroup = new Photogroup();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        jwt = intent.getStringExtra("jwt");
        photogroup.setId(intent.getIntExtra("id",0));
        photogroup.setEmail(intent.getStringExtra("uemail"));
        photogroup.setUsername(intent.getStringExtra("username"));
        int pid = intent.getIntExtra("id",0);
        int type = intent.getIntExtra("type",0);
        if (email==null){
            btDelete.setVisibility(View.INVISIBLE);
            isCollect.setVisibility(View.INVISIBLE);
        }
        String cover = intent.getStringExtra("cover");
        Glide.with(this).load(cover).into(userCover);

        textDescription.setText(photogroup.getDescription());
        userName.setText(photogroup.getUsername());
        httpUtil = new HttpUtil();
        httpUtil.getImages(email, pid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("http","error");
                Toast.makeText(ImagesShowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("email",email);
                Log.d("pid", String.valueOf(pid));
//                Log.d("http",response.body().string());
                if (response.isSuccessful()){
                    Log.d("http","success");
                    String s = response.body().string();
                    Gson gson = new Gson();
                    Type jsonType =
                            new TypeToken<ResponseBody<Temp>>(){}.getType();
                    ResponseBody responseBody = gson.fromJson(s, jsonType);
                    Log.d("body",responseBody.getMsg());
                    if (responseBody.isSuccessful()){
                        Temp data = (Temp)responseBody.getData();
                        Log.d("data",data.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isCollect.setChecked(data.isCollect());
                                Log.d("isCollect",data.toString());
                                for (String url : data.getUrl()) {
                                    adapter.add(url);
                                }
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });


        if (email.equals(photogroup.getEmail())){
            btDelete.setVisibility(View.VISIBLE);
        }


        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpUtil.deleteImages(email, pid, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ImagesShowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            String s = response.body().string();
                            Gson gson = new Gson();
                            ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                            if (responseBody.isSuccessful()){
                                Intent intent;
                                switch (type){
                                    case 0:{
                                        intent = new Intent(ImagesShowActivity.this, MainActivity.class);
                                        break;
                                    }
                                    case 1:{
                                        intent = new Intent(ImagesShowActivity.this, MyCollectActivity.class);
                                        break;
                                    }
                                    case 2:{
                                        intent = new Intent(ImagesShowActivity.this, MyphotoActivity.class);
                                        break;
                                    }
                                    default:{
                                        intent = new Intent(ImagesShowActivity.this, MainActivity.class);
                                    }
                                }

                                intent.putExtra("email",email);
                                intent.putExtra("jwt",jwt);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });

        isCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCollect.isChecked()){
                    httpUtil.collect(email, pid, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ImagesShowActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
                                    isCollect.setChecked(false);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                String s = response.body().string();
                                Gson gson = new Gson();
                                ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                                if (responseBody.isSuccessful()){
                                    return;
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ImagesShowActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
                                    isCollect.setChecked(false);
                                }
                            });

                        }
                    });
                }else {
                    httpUtil.unCollect(email, pid, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ImagesShowActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
                                    isCollect.setChecked(true);
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()){
                                String s = response.body().string();
                                Gson gson = new Gson();
                                ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                                if (responseBody.isSuccessful()){
                                    return;
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ImagesShowActivity.this, "出现错误", Toast.LENGTH_SHORT).show();
                                    isCollect.setChecked(true);
                                }
                            });
                        }
                    });
                }
            }
        });
    }
}
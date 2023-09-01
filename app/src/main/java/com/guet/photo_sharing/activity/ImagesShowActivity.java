package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guet.photo_sharing.R;
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
    private String jwt;

    private List<String> urlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_show);
        userCover = findViewById(R.id.show_user_cover);
        userName = findViewById(R.id.show_user_name);
        btDelete = findViewById(R.id.show_delete);
        isCollect = findViewById(R.id.show_iscollect);
        listView = findViewById(R.id.show_list);
        urlList = new ArrayList<>();
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        jwt = intent.getStringExtra("jwt");
        int pid = Integer.parseInt(intent.getStringExtra("id"));
        if (email==null){
            btDelete.setVisibility(View.INVISIBLE);
            isCollect.setVisibility(View.INVISIBLE);
        }
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.getImages(email, pid, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(ImagesShowActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String s = response.body().string();
                    Gson gson = new Gson();
                    Type jsonType =
                            new TypeToken<ResponseBody<Temp>>(){}.getType();
                    ResponseBody responseBody = gson.fromJson(s, jsonType);
                    if (responseBody.isSuccessful()){
                        Temp data = (Temp)responseBody.getData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                isCollect.setActivated(data.isCollect());

                            }
                        });
                    }
                }
            }
        });
    }
}
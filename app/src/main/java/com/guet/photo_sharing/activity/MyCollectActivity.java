package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guet.photo_sharing.MainActivity;
import com.guet.photo_sharing.PhotoAdapter;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.entity.Photogroup;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MyCollectActivity extends AppCompatActivity {
    private ListView lvNewsList;
    private List<Photogroup> photogroupList;
    private PhotoAdapter adapter;

    private String email;
    private String jwt;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);

        lvNewsList = findViewById(R.id.my_collect_list);
        photogroupList = new ArrayList<>();
        adapter = new PhotoAdapter(this, R.layout.list_item_news, photogroupList);
        lvNewsList.setAdapter(adapter);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        jwt   = intent.getStringExtra("jwt");
        if (email==null||jwt==null){
            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
            email = data.getString("email", null);
            jwt = data.getString("jwt", null);
            if (email==null||jwt==null){
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();

            }
        }
        HttpUtil httpUtil = new HttpUtil();
        httpUtil.myCollect(email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MyCollectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    String s = response.body().string();
                    Gson gson = new Gson();
                    Type jsonType =
                            new TypeToken<ResponseBody<List<Photogroup>>>(){}.getType();
                    ResponseBody responseBody = gson.fromJson(s, jsonType);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBody.isSuccessful()){
                                int i=1;
                                for (Photogroup datum : (List<Photogroup>) responseBody.getData()) {
                                    Log.d("photo",datum.toString());
                                    adapter.add(datum);
                                    i++;
                                    if (i%5==0){
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                                adapter.notifyDataSetChanged();

                            }else {
                                Toast.makeText(MyCollectActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
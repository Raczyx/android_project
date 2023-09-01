package com.guet.photo_sharing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guet.photo_sharing.activity.LoginActivity;
import com.guet.photo_sharing.activity.LogupActivity;
import com.guet.photo_sharing.activity.MyCollectActivity;
import com.guet.photo_sharing.activity.MyphotoActivity;
import com.guet.photo_sharing.activity.UserActivity;
import com.guet.photo_sharing.entity.Photogroup;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.entity.User;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ListView lvNewsList;
    private List<Photogroup> photogroupList;
    private PhotoAdapter adapter;

    private Button btUser;
    private Button btCollect;
    private Button btMyPhoto;
    private Button btUpdate;

    private static int num = 1;
    HttpUtil httpUtil;
    String email;
    String jwt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        jwt = intent.getStringExtra("jwt");
        if (email==null||jwt==null){
            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
            email = data.getString("email", null);
            jwt = data.getString("jwt", null);
        }
        initView();
        httpUtil = new HttpUtil();
        httpUtil.getList(num, 50, callback);



    }
    private void initView() {
        btUpdate = findViewById(R.id.main_update);
        lvNewsList = findViewById(R.id.list);
        photogroupList = new ArrayList<>();
        adapter = new PhotoAdapter(this, R.layout.list_item_news, photogroupList);
        lvNewsList.setAdapter(adapter);
        btUser = findViewById(R.id.user_data);
        btCollect = findViewById(R.id.collect);
        btMyPhoto = findViewById(R.id.myphoto);
        btUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("email",email);
                intent.putExtra("jwt",jwt);
                startActivity(intent);
            }
        });
        btCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (email==null||jwt==null){
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }else {
                    intent = new Intent(MainActivity.this, MyCollectActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("jwt",jwt);
                }

                startActivity(intent);
            }
        });

        btMyPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (email==null||jwt==null){
                    Toast.makeText(MainActivity.this, "请先登录", Toast.LENGTH_SHORT).show();
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }else {
                    intent = new Intent(MainActivity.this, MyphotoActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("jwt",jwt);
                }

                startActivity(intent);
            }
        });
        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpUtil.getList(num,50,callback);
            }
        });
    }
    Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                            adapter.clear();
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
                            Toast.makeText(MainActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    };
}
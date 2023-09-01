package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.guet.photo_sharing.MainActivity;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.entity.User;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserActivity extends AppCompatActivity {

    private TextView textUsername;
    private TextView textEmail;
    private TextView textCreateTime;
    private ImageView userCover;
    private Button btLogout;
    private Button btUpdate;

    private EditText etOldPassword;

    private EditText etNewPassword;

    private  Button btEnsure;
    private Button btGiveUp;

    String email;

    String jwt;
    private void initLayout(){
        textUsername = findViewById(R.id.user_name);
        textEmail = findViewById(R.id.user_email);
        userCover = findViewById(R.id.user_logo);
        textCreateTime = findViewById(R.id.user_creat_time);
        btLogout = findViewById(R.id.logout);
        btUpdate = findViewById(R.id.update_password);
        etOldPassword = findViewById(R.id.old_password);
        etNewPassword = findViewById(R.id.new_password);
        btEnsure = findViewById(R.id.ensure_update_password);
        btGiveUp = findViewById(R.id.give_up_update);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initLayout();
        Intent intent = getIntent();
        if (intent == null){
            Log.d("intent","null");
        }
        email = intent.getStringExtra("email");

        jwt = intent.getStringExtra("jwt");

//        email = "666";
//        jwt = "jwt";



        if (email == null|| jwt == null){
            SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
            email = data.getString("email", null);
            jwt = data.getString("jwt",null);
            if (email == null|| jwt == null){
                textUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(UserActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }
                });
                textUsername.setText("请先登录");
                return;
            }
        }

        HttpUtil httpUtil = new HttpUtil();

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
                SharedPreferences.Editor edit = data.edit();
                edit.clear();
                edit.apply();
                Intent intent  = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etNewPassword.setVisibility(View.VISIBLE);
                etOldPassword.setVisibility(View.VISIBLE);
                btEnsure.setVisibility(View.VISIBLE);
                btGiveUp.setVisibility(View.VISIBLE);
                btGiveUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getVisibility()==View.VISIBLE){
                            etNewPassword.setText("");
                            etOldPassword.setText("");
                            etOldPassword.setVisibility(View.INVISIBLE);
                            etNewPassword.setVisibility(View.INVISIBLE);

                            btEnsure.setVisibility(View.INVISIBLE);
                            view.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

        btEnsure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getVisibility()==View.VISIBLE){
                    String oldPassword = String.valueOf(etOldPassword.getText());
                    String newPassword = String.valueOf(etNewPassword.getText());
                    if (oldPassword.equals("")||newPassword.equals("")){
                        Toast.makeText(UserActivity.this,"密码不能为空",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    httpUtil.updatePassword(email, oldPassword, newPassword, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UserActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                                            etNewPassword.setText("");
                                            etOldPassword.setText("");
                                            etOldPassword.setVisibility(View.INVISIBLE);
                                            etNewPassword.setVisibility(View.INVISIBLE);

                                            btEnsure.setVisibility(View.INVISIBLE);
                                            btGiveUp.setVisibility(View.INVISIBLE);
                                        }
                                    });
                                }else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UserActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        httpUtil.getUserData(email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){
                    final String s = response.body().string();
                    Gson gson = new Gson();
                    Type jsonType =
                            new TypeToken<ResponseBody<User>>(){}.getType();
                    ResponseBody responseBody = gson.fromJson(s, jsonType);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (responseBody.isSuccessful()){
                                User user = (User) responseBody.getData();

                                textUsername.setText(user.getUsername());
                                textEmail.setText(user.getEmail());
                                textCreateTime.setText(user.getCreatetime());
                                Glide.with(UserActivity.this).load(user.getImage()).into(userCover);

                            }else {
                                Toast.makeText(UserActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });


//        Log.d("Email",email);
//        Log.d("Jwt",jwt);
    }
}
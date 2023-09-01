package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guet.photo_sharing.MainActivity;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean  bPwdSwitch = false;
    private EditText etPwd;

    private EditText etEmail;

    private Button btLogin;

    private Button btLogup;

    private Gson gson;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPwd = findViewById(R.id.et_pwd);
        etEmail = findViewById(R.id.et_email);
        btLogin = findViewById(R.id.login);
        btLogup = findViewById(R.id.go_logup);
        gson = new Gson();

        HttpUtil httpUtil = new HttpUtil();

        //切换密码可视
        ivPwdSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bPwdSwitch = !bPwdSwitch;
                if (bPwdSwitch){
                    ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_24);
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }else {
                    ivPwdSwitch.setImageResource(R.drawable.baseline_visibility_off_24);
                    etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD |InputType.TYPE_CLASS_TEXT);
                    etPwd.setTypeface(Typeface.DEFAULT);
                }
                etPwd.setSelection(etPwd.length());

            }
        });


        //登录按钮
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = String.valueOf(etEmail.getText());
                String password  = String.valueOf(etPwd.getText());
                if (email.equals("")||password.equals("")){
                    Toast.makeText(LoginActivity.this,"账户密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                httpUtil.login(email, password, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){
                            String s = response.body().string();
                            ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                            Log.d("sentHttp",String.valueOf(responseBody.getCode()));
                            if (responseBody.isSuccessful()){
                                SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor edit = data.edit();
                                edit.putString("email",email);
                                edit.putString("jwt",(String) responseBody.getData());
                                edit.apply();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                return;
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

            }
        });
    btLogup.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent  = new Intent(LoginActivity.this, LogupActivity.class);
            startActivity(intent);
        }
    });

    }
}
package com.guet.photo_sharing.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LogupActivity extends AppCompatActivity {


    private boolean  bPwdSwitch = false;
    private EditText etPwd;

    private EditText etEmail;

    private EditText etUsername;

    private Button btLogup;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlogup);

        final ImageView ivPwdSwitch = findViewById(R.id.iv_pwd_switch);
        etPwd = findViewById(R.id.et_pwd);
        etEmail = findViewById(R.id.et_email);
        btLogup = findViewById(R.id.go_logup);
        etUsername = findViewById(R.id.et_username);

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

        btLogup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String username = etUsername.getText().toString();
                String password = etPwd.getText().toString();
                if (email.equals("")||username.equals("")||password.equals("")){
                    Toast.makeText(LogupActivity.this,"邮箱，用户名，密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                httpUtil.logup(email, username, password, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LogupActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String s = response.body().string();
                        if (response.isSuccessful()){
                            Gson gson = new Gson();
                            ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LogupActivity.this,responseBody.getMsg(),Toast.LENGTH_SHORT).show();
                                }
                            });
                            if (responseBody.isSuccessful()){
                                goLogin();
                            }
                        }
                    }
                });
            }
        });

    }



    private void goLogin(){
        Intent intent = new Intent(LogupActivity.this, LoginActivity.class);
        startActivity(intent);
    }

}
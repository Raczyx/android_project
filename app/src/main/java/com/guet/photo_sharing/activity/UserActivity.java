package com.guet.photo_sharing.activity;

import static android.widget.Toast.LENGTH_SHORT;
import static com.guet.photo_sharing.activity.UploadActivity.CHOOSE_PHOTO;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
    private Button btUpdateCover;

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
        btUpdateCover = findViewById(R.id.update_user_cover);
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


        btUpdateCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserActivity.this, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });


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
                        Toast.makeText(UserActivity.this,"密码不能为空", LENGTH_SHORT).show();
                        return;
                    }
                    httpUtil.updatePassword(email, oldPassword, newPassword, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserActivity.this,e.getMessage(), LENGTH_SHORT).show();
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
                                            Toast.makeText(UserActivity.this,responseBody.getMsg(), LENGTH_SHORT).show();
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
                                            Toast.makeText(UserActivity.this,responseBody.getMsg(), LENGTH_SHORT).show();
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
                        Toast.makeText(UserActivity.this,e.getMessage(), LENGTH_SHORT).show();
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
                                Toast.makeText(UserActivity.this,responseBody.getMsg(), LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });


//        Log.d("Email",email);
//        Log.d("Jwt",jwt);
    }

    @Override
    protected void onResume() {
        super.onResume();

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
        btUpdateCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UserActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserActivity.this, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        httpUtil.getUserData(email, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UserActivity.this,e.getMessage(), LENGTH_SHORT).show();
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
                                Toast.makeText(UserActivity.this,responseBody.getMsg(), LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }


    /**
     * 打开图片选择器
     */
    private void openAlbum() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // 允许选择多张照片
//        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册


        final int maxNumPhotosAndVideos = 10;
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        intent.setType("image/*");
        intent.putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxNumPhotosAndVideos);
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.
                        PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission",
                            LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) {
                handleImages(data);
            }
        }
    }
    private void handleImages(Intent data){
        Uri uri = data.getData();
        if (uri!=null){
            Log.d("uri","1");
            String imagePath = getImagePath(uri, null);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.updateCover(email,imagePath,callback);
            return;
        }
        ClipData clipData = data.getClipData();
        uri = clipData.getItemAt(0).getUri();
        if (uri!=null){
            Log.d("uri","2");
            String imagePath = getImagePath(uri, null);
            HttpUtil httpUtil = new HttpUtil();
            httpUtil.updateCover(email,imagePath,callback);
        }
    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * 上传头像的回调
     */
    Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UserActivity.this,e.getMessage(), LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()){
                final String s = response.body().string();
                Gson gson = new Gson();
                ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (responseBody.isSuccessful()){
                            Log.d("cover",(String) responseBody.getData());
                            Glide.with(UserActivity.this).load((String) responseBody.getData()).into(userCover);
                        }else {
                            Toast.makeText(UserActivity.this,responseBody.getMsg(), LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }
    };
}
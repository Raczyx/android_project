package com.guet.photo_sharing.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.guet.photo_sharing.MainActivity;
import com.guet.photo_sharing.R;
import com.guet.photo_sharing.adapter.ImagesTempAdapter;
import com.guet.photo_sharing.entity.ResponseBody;
import com.guet.photo_sharing.utils.HttpUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 2;
    private Button btUpload;
    private Button btOpt;
    private ListView listView;
    private EditText etTitle;
    private EditText etDescription;

    private String email;
    private String jwt;

    private HttpUtil httpUtil;

    private Gson gson;

    private List<String> images_paths;

    private ImagesTempAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        btUpload = findViewById(R.id.upload_ensure);
        btOpt = findViewById(R.id.upload_opt);
        listView = findViewById(R.id.upload_list);
        etTitle = findViewById(R.id.upload_title);
        etDescription = findViewById(R.id.upload_description);
        httpUtil = new HttpUtil();
        gson = new Gson();
        images_paths = new ArrayList<>();
        adapter = new ImagesTempAdapter(this,R.layout.images_show,images_paths);
        listView.setAdapter(adapter);


        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        jwt = intent.getStringExtra("jwt");



        btOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(UploadActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UploadActivity.this, new
                            String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE ,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                images_paths.remove(i);
                adapter.notifyDataSetChanged();
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

    private void breakMain(){
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        intent.putExtra("email",email);
        intent.putExtra("jwt",jwt);
        startActivity(intent);
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
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CHOOSE_PHOTO){
            if (resultCode ==RESULT_OK){
                handleImages(data);
//                handleImageOnKitKat(data);

                Toast.makeText(this, "已加载完成", Toast.LENGTH_SHORT).show();
                btUpload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(UploadActivity.this, "点击上传", Toast.LENGTH_SHORT).show();
                        boolean inputNull = isInputNull();
                        if (inputNull){
                            Toast.makeText(UploadActivity.this, "校验通过", Toast.LENGTH_SHORT).show();
                            String title = etTitle.getText().toString().trim();
                            String description = etDescription.getText().toString().trim();
                            httpUtil.upload(email, title, description, images_paths,callback);
                        }
//                        for (String images_path : images_paths) {
//                            images_path.replaceAll("emulated/0","sdcard");
//                            File file = new File(images_path);
//                            if (file.exists()){
//                                Log.d("file",images_path+" 存在");
//                            }else {
//                                Log.d("file",images_path+" 找不到");
//                            }
//                        }
                    }
                });
            }
        }
    }
///sdcard

    //读取路径
    private void handleImages(Intent data){
        ClipData clipData = data.getClipData();
        if (clipData==null){



//            String realPathFromUri = getRealPathFromURI(data.getData());
            String realPathFromUri = getImagePath(data.getData(),null);
            images_paths.add(realPathFromUri);
//            Log.d("Upload_imageA",getRealPathFromURI(data.getData()));
            Log.d("Upload_imageB",realPathFromUri);
        }else {
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
//                Log.d("Upload_imageA",getRealPathFromURI(uri));
//                String realPathFromUri = getRealPathFromURI(uri);
                String realPathFromUri = getImagePath(uri,null);
                Log.d("Upload_imageB",realPathFromUri);
                images_paths.add(realPathFromUri);
            }
        }
        Set<String> set = new LinkedHashSet<>(images_paths);
        images_paths.clear();
        images_paths.addAll(set);
//        adapter.clear();
//        for (String images_path : images_paths) {
//            adapter.add(images_path);
//        }
//        adapter.notifyDataSetChanged();
//        adapter.clear();
//        adapter.addAll(images_paths);
        adapter.notifyDataSetChanged();
    }


    public String getRealPathFromURI(Uri uri) {
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String[] splits = wholeID.split(":");
        String id = splits[1];

        String[] column = {MediaStore.Images.Media.DATA};

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{id}, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }



//
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.
                    getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public downloads"), Long.valueOf(docId));
                        imagePath = getImagePath(contentUri, null);

            }
            Log.d("Images_type","document");
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
            Log.d("Images_type","content");
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
            Log.d("Images_type","file");
        }
        Log.d("Images_path",imagePath!=null?imagePath:"null"); // 根据图片路径显示图片
    }
//
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
     * 判断是否有空值
     * @return
     */
    private boolean isInputNull(){
        if (images_paths.size() == 0){
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etTitle.getText().toString().trim().equals("")||etDescription.getText().toString().trim().equals("")){
            Toast.makeText(this, "请输入标题或描述", Toast.LENGTH_SHORT).show();
            return  false;
        }
        return true;
    }
//    private String getImagePath(Uri uri, String selection){
//        return uri.getPath();
//    }

    /**
     * 发送请求的回调
     */
     Callback callback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UploadActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    images_paths.clear();
                }
            });
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()){
                String s = response.body().string();
                ResponseBody responseBody = gson.fromJson(s, ResponseBody.class);
                if (responseBody.isSuccessful()){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(UploadActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
//                            intent.putExtra("email",email);
//                            intent.putExtra("jwt",jwt);
//                            startActivity(intent);
                            breakMain();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(UploadActivity.this, responseBody.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
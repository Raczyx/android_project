package com.guet.photo_sharing.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.guet.photo_sharing.entity.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

//    public static String SERVER_HOST = "http://114.132.248.191:8085";
//    public  String SERVER_HOST = "http://10.33.117.89:54321";
    public  String SERVER_HOST = "http://172.16.77.180:8080";
    private  OkHttpClient client;

    private  Gson gson ;

    public HttpUtil() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    private void sentHttp(RequestBody requestBody,String url,Callback callback){
        Request request = new Request.Builder().url(SERVER_HOST+url)
                .post(requestBody)
                .build();
        if (callback!=null){
            client.newCall(request).enqueue(callback);
        }else {
            try {
                client.newCall(request).execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public boolean login(String email, String password,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .build();
        Request request = new Request.Builder().url(SERVER_HOST+"/user/login")
                .post(requestBody)
                .build();
        if (callback!=null){
            client.newCall(request).enqueue(callback);
            return true;
        }else {
            return false;
        }
//        final boolean[] fleg = {false};
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.d("login",e.getMessage());
//                fleg[0] = false;
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()){
//                    fleg[0] = false;
//                    return;
//                }
//                String s = response.body().string();
////                Type typeToken = new TypeToken<ResponseBody>(){}.getType();
//                ResponseBody json = gson.fromJson(s, ResponseBody.class);
//                if (json.getCode()==600){
//                    fleg[0] = true;
//                }
//            }
//        });
//        return fleg[0];
    }


    public void logup(String email,String username,String password,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .add("username",username)
                .build();
        Request request = new Request.Builder().url(SERVER_HOST+"/user/logup")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void updatePassword(String email,String password,String newPassword,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("password",password)
                .add("newpassword",newPassword)
                .build();
        Request request = new Request.Builder().url(SERVER_HOST+"/user/update/password")
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void updateCover(String email, String filepath,Callback callback){
        File file = new File(filepath);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.getName(),
                        MultipartBody.create(MediaType.parse("multipart/form-data"), file)
                )
                .addFormDataPart("email",email)
                .build();
        Request request = new Request.Builder().url(SERVER_HOST+"/user/update/cover")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public void getList(Integer page,Integer size,Callback callback){
        ///list
        RequestBody requestBody = new FormBody.Builder()
                .add("page",String.valueOf(page))
                .add("size",String.valueOf(size))
                .build();
        sentHttp(requestBody,"/list",callback);
    }

    public void deleteImages(String email,Integer id,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("id",String.valueOf(id))
                .build();
        sentHttp(requestBody,"/delete",callback);
    }

    public void getImages(String email,Integer id,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("id",String.valueOf(id))
                .build();
        sentHttp(requestBody,"/get",callback);
    }
    public void collect(String email,Integer id,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("id",String.valueOf(id))
                .build();
        sentHttp(requestBody,"/collect",callback);
    }

    public void unCollect(String email,Integer id,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .add("id",String.valueOf(id))
                .build();
        sentHttp(requestBody,"/uncollect",callback);
    }

    public void upload(String email, String title, String description, List<String> files,Callback callback){
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("email",email)
                .addFormDataPart("title",title)
                .addFormDataPart("description",description);
        for (String file : files) {
            File f = new File(file);
            builder.addFormDataPart(
                    "files",
                    f.getName(),
                    MultipartBody.create(MediaType.parse("multipart/form-data"), f)
            );
        }
        RequestBody build = builder.build();
        sentHttp(build,"/upload",callback);
    }

    public void myPhoto(String email,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .build();
        sentHttp(requestBody,"/myphoto",callback);
    }

    public void myCollect(String email,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .build();
        sentHttp(requestBody,"/mycollect",callback);
    }

    public void getUserData(String email,Callback callback){
        RequestBody requestBody = new FormBody.Builder()
                .add("email",email)
                .build();
        sentHttp(requestBody,"/user/data",callback);
    }
}

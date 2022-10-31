package com.hoangt3k56.dropbox.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hoangt3k56.dropbox.model.Account;
import com.hoangt3k56.dropbox.model.Arg;
import com.hoangt3k56.dropbox.model.Avatar;
import com.hoangt3k56.dropbox.model.Copy;
import com.hoangt3k56.dropbox.model.Delete;
import com.hoangt3k56.dropbox.model.Enty;
import com.hoangt3k56.dropbox.model.Folder;
import com.hoangt3k56.dropbox.model.Path;
import com.hoangt3k56.dropbox.model.PathShare;
import com.hoangt3k56.dropbox.model.PathShareRoot;
import com.hoangt3k56.dropbox.model.UpdatePost;
import com.hoangt3k56.dropbox.model.User;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    // Create a new object from HttpLoggingInterceptor
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);


    // Add Interceptor to HttpClient
    OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor).build();
    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://api.dropboxapi.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build().create(ApiService.class);


    @GET("oauth2/authorize?client_id=sv2abu0lwbaoed6&response_type=token&redirect_uri=https://dropbox.demo.local")
    Call<Enty> getToken( );

    @Headers({"Content-Type: application/json"})
    @POST("2/account/set_profile_photo")
    Call<Enty> setAvatar(@Header("Authorization") String token, @Body Avatar avatar);


    @POST("2/users/get_current_account")
    Call<Account> getAccountId(@Header("Authorization") String token);

    @POST("2/auth/token/revoke")
    Call removeToken(@Header("Authorization") String token);

    @Headers({"Content-Type: application/json"})
    @POST("2/users/get_account")
    Call<User> getUser(@Header("Authorization") String token, @Body Account account);

    @Headers({"Content-Type: application/json"})
    @POST("2/files/list_folder")
    Call<Enty> getListEmty(@Header("Authorization") String token, @Body Path path);


    @Headers({"Content-Type: application/json"})
    @POST("2/files/delete_v2")
    Call<Enty> delete(@Header("Authorization") String token, @Body Delete delete);


    @Headers({"Content-Type: application/json"})
    @POST("2/files/create_folder_v2")
    Call<Enty> createFolder(@Header("Authorization") String token, @Body Folder folder);


    @Headers({"Content-Type: application/json"})
    @POST("2/files/copy_v2")
    Call<Enty> copy(@Header("Authorization") String token, @Body Copy copy);


    @Headers({"Content-Type: application/json"})
    @POST("2/files/move_v2")
    Call<Enty> move(@Header("Authorization") String token, @Body Copy copy);


    @Headers({"Content-Type: application/json"})
    @POST("2/sharing/create_shared_link")
    Call<PathShareRoot> getLink(@Header("Authorization") String token, @Body PathShare pathShare);



    @POST("2/files/upload")
    Call<UpdatePost> update(@HeaderMap Map<String, String> map,
                            @Body RequestBody photo);

}

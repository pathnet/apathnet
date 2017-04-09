package com.pathnet.network.api;


import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * @version V2.2.0
 * @Project: BCS-Android
 * @Filename:IRetrofitService.java
 * @Desciption:
 * @Author: sunbo
 * @Date: 2017/1/6 13:17
 * @Copyright: 2017 AgileSC, Inc. China All rights reserved.
 * <p>
 * Modification History
 * Date				Author	Version 	Desciption
 */
public interface IRetrofitService<T> {
    /*RxJava公用方法*/
    @POST("{url1}")
    @FormUrlEncoded
    Observable<ResponseBody> post(@Path("url1") String url1, @FieldMap Map<String, String> params);

    @POST("{url1}/{url2}")
    @Multipart
    Observable<ResponseBody> postUpFile(@Path("url1") String url1, @Path("url2") String url2, @Part MultipartBody.Part file);

    @POST("{url1}/{url2}/{url3}")
    @Multipart
    Observable<ResponseBody> postUpFile(@Path("url1") String url1, @Path("url2") String url2, @Path("url3") String url3, @Part() List<MultipartBody.Part> files);

    /*RxJava公用方法*/
    @POST("{url1}/{url2}")
    @FormUrlEncoded
    Observable<ResponseBody> post(@Path("url1") String url1, @Path("url2") String url2, @FieldMap Map<String, String> params);

    /*RxJava公用方法*/
    @POST("{url1}/{url2}/{url3}")
    @FormUrlEncoded
    Observable<ResponseBody> post(@Path("url1") String url1, @Path("url2") String url2, @Path("url3") String url3, @FieldMap Map<String, String> params);

    /*RxJava公用方法*/
    @POST("{url1}/{url2}")
    @FormUrlEncoded
    @Multipart
    Observable<ResponseBody> postFile(@Path("url1") String url1, @Path("url2") String url2, @FieldMap Map<String, File> params);

    /*RxJava公用方法*/
    @POST("{url}")
    @FormUrlEncoded
    Observable<ResponseBody> post(@Path("url") String url, @Field("") String username);

    @Multipart
    @POST("{url1}/{url2}")
    Call<String> uploadImage(@Path("url1") String url1, @Path("url2") String url2, @Part("fileName") String description, @FieldMap Map<String, String> params, @Part("file\"; filename=\"image.png\"") RequestBody imgs);

    @POST("{url1}/{url2}")
    @FormUrlEncoded
    Call<String> postPhoto(@Path("url1") String url1, @Path("url2") String url2, @FieldMap Map<String, String> params);


    /*获取验证码*/
    @POST("captcha/sendByUsername")
    @FormUrlEncoded
    Call<ResponseBody> getCaptcha(@Field("username") String username);

    @Multipart
    @POST("register")
    Call<ResponseBody> registerUser(@Part MultipartBody.Part photo, @Part("username") RequestBody username, @Part("password") RequestBody password);

    @Multipart
    @POST("user/setPortrait")
    Call<String> uploadImage2(@Part("fileName") String description, @Part("file\"; filename=\"image.jpg\"") RequestBody imgs);

    /*找回密码*/
    @POST("passport/resetPwd")
    @FormUrlEncoded
    Call<ResponseBody> getCaptcha(@FieldMap Map<String, String> param);


    //发送文件
    @POST("{url}")
    @Multipart
    Call<ResponseBody> postUpLoad(@PartMap Map<String, RequestBody> params);

    @POST("login")
    @FormUrlEncoded
    Observable<ResponseBody> postRxApiString(@FieldMap Map<String, String> params);


    /* @QueryMap 表示将map类型的params对象,转成键值对的形式作为参数传递给后台*/
    @GET("/api")
    Call<ResponseBody> getApiString(@QueryMap Map<String, String> params);

    @GET("/joke/content/list.from?key=416bae2c051dd2c541bd6d79c44339c3&page=2&pagesize=10&sort=asc&time=1418745237")
    Observable<ResponseBody> get(@QueryMap Map<String, Object> params);
}
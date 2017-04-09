package com.pathnet.network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;

import com.pathnet.network.api.IBcsApi;
import com.pathnet.network.api.IRetrofitService;
import com.pathnet.network.converter.JsonConverterFactory;
import com.pathnet.network.cookie.ClearableCookieJar;
import com.pathnet.network.cookie.PersistentCookieJar;
import com.pathnet.network.cookie.cache.SetCookieCache;
import com.pathnet.network.cookie.persistence.SharedPrefsCookiePersistor;
import com.pathnet.network.exceptions.ErrorHandler;
import com.pathnet.network.model.ErrorResponseBean;
import com.pathnet.network.utils.GsonTools;
import com.pathnet.network.utils.LogUtils;
import com.pathnet.network.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.pathnet.network.api.IBcsApi.API_VERSION;


/**
 * @version V2.2.0
 * @Project: BCS-Android
 * @Filename:RetrofitUtils .java
 * @Desciption:
 * @Author: sunbo
 * @Date: 2017/1/6 13:17
 * @Copyright: 2017 AgileSC, Inc. China All rights reserved.
 * <p>
 * Modification History
 * Date				Author	Version 	Desciption
 */
public class RetrofitUtils {
    private static final String MOBILE = ",Mobile-Device-Info:";
    private static final String CLIENT_INFO = "Client-Info";
    private static final String APP_VERSION_CODE = ",APP-VersionCode:";
    private static final String OS = ",OS:Android,OS-Version:";
    /*超时时间*/
    private static final int DEFAULT_TIMEOUT = 30;
    /*上下文*/
    private static Context mContext;
    /*RService实例*/
    private static IRetrofitService mService;
    /*弹窗*/
    private ProgressDialog mWaitDialog;

    /*初始化service*/
    private RetrofitUtils() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IBcsApi.BASE_URL)
                .client(genericClient())
                .addConverterFactory(JsonConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create())//用于Json数据的转换,非必须
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())//用于返回Rxjava调用,非必须
                .build();
        mService = retrofit.create(IRetrofitService.class);
    }

    /*获取Api实例*/
    public static RetrofitUtils getApi(Context context) {
        mContext = context;
        return ApiHolder.retrofit;
    }

    /*添加头信息*/
    public static OkHttpClient genericClient() {
        try {
            final SetCookieCache cookies = new SetCookieCache();
            ClearableCookieJar cookieJar = new PersistentCookieJar(cookies, new SharedPrefsCookiePersistor(mContext));
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .readTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                    .cookieJar(cookieJar)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = null;
                            try {
                                request = chain.request().newBuilder().addHeader(CLIENT_INFO, getClientInfoValue()).build();
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
                            Iterator<Cookie> iterator = cookies.iterator();
                            return chain.proceed(request);
                        }
                    }).build();
            return httpClient;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 头信息
     *
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    @NonNull
    private static String getClientInfoValue() throws PackageManager.NameNotFoundException {
        return API_VERSION
                + mContext.getPackageManager().getPackageInfo(mContext.getApplicationInfo().processName, 0).versionName
                + APP_VERSION_CODE + mContext.getPackageManager().getPackageInfo(mContext.getApplicationInfo().processName, 0).versionCode
                + OS + Build.VERSION.RELEASE + MOBILE + Build.MODEL;
    }

    /*获取RApiService 实例*/
    public IRetrofitService getService() {
        return mService;
    }

    /*获取订单相关实例*/
    public IRetrofitService getOrderService() {
        return mService;
    }

    /*RxJava公共方法*/
    public <T> void post(String url1, String url2, Map<String, String> map, Observer<T> observer) {
        RetrofitUtils.getApi(mContext).getService().post(url1, url2, map)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /*RxJava公共方法*/
    public <T> void post(String url1, Map<String, String> map, Observer<T> observer) {
        RetrofitUtils.getApi(mContext).getService().post(url1, map)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    /**
     * RxJava公共方法
     *
     * @param url1 接口名
     * @param clzz 获取的bean对象
     * @param <T>
     */
    public <T> void post(final String url1, Class<T> clzz, OnRetrofitResponse<T> onRetrofitResponse) {
        try {
            mService.post(url1, "")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, onRetrofitResponse));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1 接口名
     * @param clzz 获取的bean对象
     * @param <T>
     */
    public <T> void post(final String url1, Class<T> clzz, OnRetrofitErrorResponse<T> onRetrofitErrorResponse) {
        try {
            mService.post(url1, "")
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, onRetrofitErrorResponse));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1 接口名
     * @param clzz 获取的bean对象
     * @param <T>
     */
    public <T> void post(final String url1, String url2, Map<String, String> map, Class<T> clzz, OnRetrofitErrorResponse<T> onRetrofitErrorResponse) {
        try {
            mService.post(url1, url2, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, clzz, onRetrofitErrorResponse));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1 接口名
     * @param clzz 获取的bean对象
     * @param <T>
     */
    public <T> void post(final String url1, Map<String, String> map, Class<T> clzz, OnRetrofitErrorResponse<T> onRetrofitErrorResponse) {
        try {
            mService.post(url1, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, onRetrofitErrorResponse));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void post(final String url1, final String url2, Map<String, Object> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, url2, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void get(final String url1, Map<String, Object> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.get(map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void post(final String url1, final String url2, String url3, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, url2, url3, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, url3, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param url2     接口名
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postFile(final String url1, final String url2, String filePath, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath));
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "iamg.jpeg", requestFile);
            mService.postUpFile(url1, url2, body)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param url2     接口名
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postFile(final String url1, final String url2, String url3, List<?> list, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
//            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePath);
//            MultipartBody.Part body = MultipartBody.Part.createFormData("image", "iamg.jpeg", requestFile);
            mService.postUpFile(url1, url2, url3, list)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, url3, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postUpdate(final String url1, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postUpdate(final String url1, final String url2, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, url2, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postUpdate(final String url1, final String url2, final String url3, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, url2, url3, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, url3, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void postUpdate(final String url1, final String url2, Map<String, String> map, final Class<T> clzz, final OnRetrofitErrorResponse response) {
        try {
            mService.post(url1, url2, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, url2, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * 版本升级
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>
     */
    public <T> void appupdate(final String url1, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(IBcsApi.CHECKVESION_STRING)
                    .client(genericClient())
                    .addConverterFactory(JsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            retrofit.create(IRetrofitService.class).post(url1, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * RxJava公共方法
     *
     * @param url1     接口名
     * @param map      参数
     * @param clzz     获取的bean对象
     * @param response 回调
     * @param <T>      转换的类型
     */
    public <T> void post(final String url1, Map<String, String> map, final Class<T> clzz, final OnRetrofitResponse response) {
        try {
            mService.post(url1, map)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResponseObserverManager(url1, clzz, response));
        } catch (Exception e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, e.getMessage());
        }
    }

    /**
     * 处理请求结果
     *
     * @param result 请求结果
     * @param clzz   转换的class文件
     * @param url1   url
     * @param url2   url
     * @param <T>    转换的类型
     */
    private <T> T getOnNextResult(ResponseBody result, Class<T> clzz, String url1, String url2) {
        try {
            if (result != null) {
                String url = result.string();
                return GsonTools.changeGsonToBean(url, clzz);
            }
        } catch (IOException e) {
            LogUtils.e(LogUtils.RESPONSE_ERROR, LogUtils.URL + url1 + url2 + LogUtils.MSG + e.getMessage());
        }
        return null;
    }

    /**
     * 错误处理
     *
     * @param e    错误消息
     * @param url1
     * @param url2
     */
    private void getErrorManage(Throwable e, String url1, String url2, String url3) {
        try {
            if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                int code = error.code();
                if (code < 500 || code == 500 || code == 502 || code == 503 || code == 504) {
                    ErrorResponseBean errorMessage = ErrorHandler.getErrorMessage(e);
                    ToastUtil.showToast(errorMessage.getCode() + ": " + errorMessage.getErrorMessage(), mContext);
                } else {
                    ToastUtil.showToast(LogUtils.CODE + code + " " + mContext.getString(R.string.serverError_500), mContext);
                }
            } else {
                LogUtils.e(getClass(), LogUtils.CODE + mContext.getString(R.string.serverError_x) + url1 + "/" + url2 + "/" + url3 + e);
            }
        } catch (Exception error) {
            ToastUtil.showToast(mContext.getString(R.string.serverError_500), mContext);
            LogUtils.e(LogUtils.RESPONSE_ERROR, LogUtils.URL + url1 + url2 + url3 + LogUtils.MSG + error.getMessage());
        }
    }

    public interface OnRetrofitResponse<T> {
        void onCompleted();

        void onNext(T toBean);
    }

    public interface OnRetrofitErrorResponse<T> {
        void onError();

        void onNext(T toBean);
    }

    /* 获取retrofit实例*/
    private static class ApiHolder {
        private static RetrofitUtils retrofit = new RetrofitUtils();
    }

    /**
     * 请求管理类-统一处理
     *
     * @param <T>
     */
    public class ResponseObserverManager<T, E> implements Observer<ResponseBody> {

        private Class<?> clzz;
        private String url1;
        private String url2;
        private String url3;
        private OnRetrofitResponse<T> onRetrofitResponse;
        private OnRetrofitErrorResponse<E> onRetrofitErrorResponse;

        public <T> ResponseObserverManager(String url1, String url2, Class<T> clzz, OnRetrofitResponse response) {
            this.url1 = url1;
            this.url2 = url2;
            this.clzz = clzz;
            this.onRetrofitResponse = response;
        }

        public <T> ResponseObserverManager(String url1, String url2, String url3, Class<T> clzz, OnRetrofitResponse response) {
            this.url1 = url1;
            this.url2 = url2;
            this.url3 = url3;
            this.clzz = clzz;
            this.onRetrofitResponse = response;
        }

        public <T> ResponseObserverManager(String url1, String url2, Class<T> clzz, OnRetrofitErrorResponse<E> response) {
            this.url1 = url1;
            this.url2 = url2;
            this.clzz = clzz;
            this.onRetrofitErrorResponse = response;
        }

        public ResponseObserverManager(String url1, Class<T> clzz, OnRetrofitResponse<T> onRetrofitResponse) {
            this.url1 = url1;
            this.clzz = clzz;
            this.onRetrofitResponse = onRetrofitResponse;
        }

        public ResponseObserverManager(String url1, Class<T> clzz, OnRetrofitErrorResponse<E> onRetrofitErrorResponse) {
            this.url1 = url1;
            this.clzz = clzz;
            this.onRetrofitErrorResponse = onRetrofitErrorResponse;
        }

        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            getErrorManage(e, url1, url2, url3);
            if (onRetrofitErrorResponse != null) {
                onRetrofitErrorResponse.onError();
            }
        }

        @Override
        public void onNext(ResponseBody result) {
            if (onRetrofitResponse != null) {
                onRetrofitResponse.onNext((T) getOnNextResult(result, clzz, url1, url2));
            }
            if (onRetrofitErrorResponse != null) {
                onRetrofitErrorResponse.onNext((E) getOnNextResult(result, clzz, url1, url2));
            }
        }
    }
}

package com.pathnet.network.exceptions;

import com.google.gson.Gson;
import com.pathnet.network.model.ErrorResponseBean;
import com.pathnet.network.utils.LogUtils;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;

public class ErrorHandler {

    public static ErrorResponseBean handle(Throwable throwable) {
        if (throwable instanceof HttpException) {
            HttpException error = (HttpException) throwable;
            try {
                return new Gson().fromJson(error.response().errorBody().string(), ErrorResponseBean.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throwable.printStackTrace();
        }
        return null;
    }

    public static ErrorResponseBean getErrorMessage(Throwable throwable) {
        String result = "";
        ErrorResponseBean handle = handle(throwable);
        if (null == handle) {
            handle = new ErrorResponseBean();
            result = "未知错误!";
        }
        if (throwable instanceof HttpException) {
            HttpException error = (HttpException) throwable;
            switch (error.code()) {
                case 400:
                case 401:
                case 403:
                case 404:
                    result = handle.getErrorMessage();
                    break;
                case 405:
                    result = "请求方法不合适!";
                    break;
                case 429:
                    result = "过多的请求!";
                    break;
                case 500:
                    LogUtils.e("error", "error------------------------" + error.code());
                    result = "内部服务错误!";
                    break;
                case 502:
                    result = "无效代理!";
                    break;
                case 503:
                    result = "服务暂时失效!";
                    break;
                case 504:
                    result = "代理超时!";
                    break;
                default:
                    result = "未知错误!";
                    break;
            }
            handle.setCode(error.code());
            handle.setErrorMessage(result);
        } else {
            handle.setErrorMessage("未知错误!");
            handle.setCode(-1);
        }
        return handle;
    }
}
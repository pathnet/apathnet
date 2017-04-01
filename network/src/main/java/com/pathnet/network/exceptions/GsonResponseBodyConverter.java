package com.pathnet.network.exceptions;

import com.google.gson.Gson;
import com.pathnet.network.model.ErrorMessageBean;
import com.pathnet.network.model.ResultResponse;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

class GsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;
    private final Type type;

    GsonResponseBodyConverter(Gson gson, Type type) {
        this.gson = gson;
        this.type = type;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        String response = value.string();
        try {
            //ResultResponse 只解析result字段
            ResultResponse resultResponse = gson.fromJson(response, ResultResponse.class);
            if (resultResponse.getCode() == 0) {
                //result==0表示成功返回，继续用本来的Model类解析
                return gson.fromJson(response, type);
            } else {
                //ErrResponse 将msg解析为异常消息文本
                ErrorMessageBean errResponse = gson.fromJson(response, ErrorMessageBean.class);
                throw new ResultException(resultResponse.getCode(), errResponse.getErrorMessage());
            }
        } finally {
        }
    }
}
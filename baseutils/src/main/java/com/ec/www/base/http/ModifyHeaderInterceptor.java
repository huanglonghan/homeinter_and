package com.ec.www.base.http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by huang on 2017/4/20.
 * 修改http请求头示例
 */

public class ModifyHeaderInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request authorised = chain.request().newBuilder()
                .header("asd", "asd")
                .header("asd", "asd")
                .build();
        return chain.proceed(authorised);
    }
}
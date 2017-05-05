package com.ec.www.base.http;

import android.support.annotation.Nullable;

import com.ec.www.BuildConfig;
import com.ec.www.base.AbstractApplication;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.functions.Function;
import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Administrator on 2016/10/11 0011.
 * HTTP 请求类
 */

public class HttpUtil {

    //http连接超时(单位:秒)
    private static final int HTTP_CONNECT_TIMEOUT = 20;

    //http读超时(单位:秒)
    private static final int HTTP_READ_TIMEOUT = 4;

    private static final int MAX_SIZE = 1000 * 1000 * 50; // 50 mb
    private static final String HTTP_CACHE_DIRECTORY = "http_cache";

    private static HttpLoggingInterceptor.Level OkHttpLogLevel = HttpLoggingInterceptor.Level.BODY;
    private static boolean isEnableLog = BuildConfig.DEBUG;

    private Retrofit retrofit;

    private static class HTTP_REQUEST_REQ {
        static {
            storeCookie = new StoreCookie(new SPStoreMap());
            httpRequest = new HttpUtil();
        }

        private static final StoreCookie storeCookie;
        private static final HttpUtil httpRequest;
    }

    private HttpUtil() {
    }

    private static HttpUtil getInstance() {
        return HTTP_REQUEST_REQ.httpRequest;
    }

    public HttpLoggingInterceptor.Level getOkHttpLogLevel() {
        return OkHttpLogLevel;
    }

    public HttpUtil setOkHttpLogLevel(HttpLoggingInterceptor.Level okHttpLogLevel) {
        OkHttpLogLevel = okHttpLogLevel;
        return this;
    }

    public boolean isEnableLog() {
        return isEnableLog;
    }

    public HttpUtil setIsEnableLog(boolean isEnableLog) {
        HttpUtil.isEnableLog = isEnableLog;
        return this;
    }

    /**
     * 默认实例创建
     *
     * @param host api地址
     * @return HttpUtil
     */
    public static HttpUtil init(String host) {
        HttpUtil request = getInstance();
        if (request.retrofit == null) {
            request.retrofit = createRetrofit(host);
        }
        return request;
    }

    /**
     * 可修改OkHttp 实例创建
     *
     * @param host     api地址
     * @param callback OkHttp构建回调
     * @return HttpUtil
     */
    public static HttpUtil init(String host, @Nullable Function<OkHttpClient.Builder, OkHttpClient> callback) {
        HttpUtil request = getInstance();
        if (request.retrofit == null) {
            request.retrofit = createRetrofit(host, null, callback);
        }
        return request;
    }

    /**
     * 可修改OkHttp, retrofit 实例创建
     *
     * @param host             api地址
     * @param retrofitCallback retrofit构建回调
     * @param clientCallback   OkHttp构建回调
     * @return HttpUtil
     */
    public static HttpUtil init(String host, @Nullable Function<Retrofit.Builder, Retrofit> retrofitCallback,
                                @Nullable Function<OkHttpClient.Builder, OkHttpClient> clientCallback) {
        HttpUtil request = getInstance();
        if (request.retrofit == null) {
            request.retrofit = createRetrofit(host, retrofitCallback, clientCallback);
        }
        return request;
    }

    /**
     * retrofit 创建实例
     *
     * @param host             api地址
     * @param retrofitCallback retrofit构建回调 为null则调用默认构建函数
     * @param clientCallback   OkHttp构建回调 为null则调用默认构建函数
     * @return Retrofit
     */
    public static Retrofit createRetrofit(String host,
                                          @Nullable Function<Retrofit.Builder, Retrofit> retrofitCallback,
                                          @Nullable Function<OkHttpClient.Builder, OkHttpClient> clientCallback) {
        OkHttpClient.Builder clientBuild = clientBuild();
        OkHttpClient client;
        if (clientCallback != null) {
            try {
                client = clientCallback.apply(clientBuild);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            client = clientBuild.build();
        }
        Retrofit.Builder retrofitBuild = retrofitBuild(client, host);
        Retrofit retrofit;
        if (retrofitCallback != null) {
            try {
                retrofit = retrofitCallback.apply(retrofitBuild);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } else {
            retrofit = retrofitBuild.build();
        }
        return retrofit;
    }

    /**
     * retrofit 创建实例
     *
     * @param host api地址
     * @return Retrofit
     */
    public static Retrofit createRetrofit(String host) {
        return retrofitBuild(createClient(), host).build();
    }

    /**
     * retrofit 构建
     *
     * @param okHttpClient OkHttp对象
     * @param host         api地址
     * @return Retrofit.Builder
     */
    public static Retrofit.Builder retrofitBuild(OkHttpClient okHttpClient, String host) {
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
    }

    /**
     * OkHttp 创建实例 使用默认Cookie持久化实现
     *
     * @return OkHttpClient
     */
    public static OkHttpClient createClient() {
        return clientBuild().build();
    }

    /**
     * OkHttp 创建实例
     *
     * @param cookieJar Cookie持久化实现
     * @return OkHttpClient
     */
    public static OkHttpClient createClient(CookieJar cookieJar) {
        return clientBuild(cookieJar).build();
    }

    /**
     * OkHttp 构建 使用默认Cookie持久化实现
     *
     * @return OkHttpClient.Builder
     */
    public static OkHttpClient.Builder clientBuild() {
        return clientBuild(HTTP_REQUEST_REQ.storeCookie);
    }

    /**
     * OkHttp 构建
     *
     * @param cookieJar Cookie持久化实现
     * @return OkHttpClient
     */
    public static OkHttpClient.Builder clientBuild(CookieJar cookieJar) {
        //增加OkHttp日志
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(OkHttpLogLevel);

        File cacheFile = new File(AbstractApplication.getContext().getCacheDir().getAbsolutePath(), HTTP_CACHE_DIRECTORY);
        Cache cache = new Cache(cacheFile, MAX_SIZE);

        //构建okhttp并添加cookie处理
        OkHttpClient.Builder build = new OkHttpClient()
                .newBuilder()
                .cache(cache)
                .cookieJar(cookieJar)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS);

        //if (isEnableLog) {
        build = build.addInterceptor(logging);
        // }

        return build;
    }

    /**
     * 创建访问接口实例
     *
     * @param t   接口对象class
     * @param <T> 某借口
     * @return T
     */
    public <T> T create(Class<T> t) {
        if (retrofit == null) {
            throw new RuntimeException("just first init() invoke !");
        }
        return retrofit.create(t);
    }

}
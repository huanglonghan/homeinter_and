package com.ec.www.base;

import android.accounts.NetworkErrorException;
import android.util.SparseIntArray;

import com.ec.www.base.http.HttpResponse;
import com.ec.www.base.http.response.IDispose;
import com.ec.www.utils.NetworkUtils;
import com.ec.www.utils.cache.SampleCache;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public abstract class AbstractModel<T, U> {

    private SparseIntArray mRepeatArray = new SparseIntArray(5);

    private static final String Tag = "onSubscribe";

    public T getService() {
        return mService;
    }

    public void setService(T service) {
        mService = service;
    }

    //http请求示例
    private T mService;

    private SampleCache mSampleCache;

    public AbstractModel() {
        mService = httpServiceCreate();
        mSampleCache = new SampleCache();
    }

    public abstract T httpServiceCreate();

    /**
     * 调用记录防止重复请求
     *
     * @param observable 请求对象
     * @param response   请求回调
     * @param <M>        结果类
     */
    @NotProguard
    protected <M> void onSubscribe(Observable<M> observable, HttpResponse response) {
        int resultCode = response.getIDispose().getResultSupport().resultCode;
        int requestId = mRepeatArray.get(resultCode, IDispose.DEFAULT);

        if (!NetworkUtils.isNetworkConnected()) {
            response.onError(new NetworkErrorException());
        }

        if (requestId == IDispose.DEFAULT) {
            //新的请求
            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response);
            mRepeatArray.put(resultCode, resultCode);
            response.addCompleteCallBack(perform -> {
                if (perform != null) {
                    mRepeatArray.delete(perform.resultCode);
                }
            });

        } else {
            //请求重复 查找tag
            StackTraceElement[] elements = new Throwable().getStackTrace();
            String method = null;
            int index;
            //遍历查找onSubscribe
            if (elements[0].getMethodName().equals(Tag)) {
                method = elements[1].getMethodName();
            } else {
                for (int i = 0; i < elements.length; i++) {
                    if (elements[i].getMethodName().equals(Tag)) {
                        index = i + 1;
                        if (index < elements.length) {
                            method = elements[index].getMethodName();
                        }
                    }
                }
            }
            response.onRepeat(method);
        }
    }

    /**
     * 添加缓存
     *
     * @param key      唯一标示
     * @param response 响应回调
     * @param callback 网络请求回调
     * @param tClass   要存储的对象
     * @param saveTime 保存时间
     * @param <D>      要存储的对象的 type
     */
    protected final <D> void setCache(String key, HttpResponse response, Consumer<HttpResponse> callback, Class<D> tClass, int saveTime) {
        mSampleCache.getObject(key, tClass)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((o) -> {
                    if (o.isPresent()) {
                        //已经缓存
                        response.onStart();
                        try {
                            response.onNext(decoratedObj(o.get()));
                        } catch (Exception e) {
                            response.onError(e);
                        }
                        response.onComplete();
                    } else {
                        //未缓存
                        //网络请求后更新缓存数据
                        response.setCacheCallback((obj) ->
                                mSampleCache.putObject(key, undecoratedObj((U) obj), saveTime));
                        //执行网络请求
                        callback.accept(response);
                    }
                });
    }

    protected abstract <D> U decoratedObj(D d);

    protected abstract <D> D undecoratedObj(U obj);

    /**
     * 添加缓存
     *
     * @param key      唯一标示
     * @param response 响应回调
     * @param callback 网络请求回调
     * @param tClass   要存储的对象
     * @param <D>      要存储的对象的 type
     */
    protected final <D> void setCache(String key, HttpResponse response, Consumer<HttpResponse> callback, Class<D> tClass) {
        setCache(key, response, callback, tClass, SampleCache.CACHE_SAVE_TIME);
    }

    /**
     * 修改缓存
     *
     * @param key      唯一标示
     * @param callback 修改回调
     * @param tClass   存储的对象
     * @param saveTime 保存时间
     * @param <D>      存储的对象
     */
    protected final <D> void modifyCache(String key, Function<D, D> callback, Class<D> tClass, int saveTime) {
        mSampleCache.modifyObject(key, tClass, callback, saveTime);
    }

    /**
     * 修改缓存
     *
     * @param key      唯一标示
     * @param callback 修改回调
     * @param tClass   存储的对象
     * @param <D>      存储的对象
     */
    protected final <D> void modifyCache(String key, Function<D, D> callback, Class<D> tClass) {
        mSampleCache.modifyObject(key, tClass, callback, SampleCache.CACHE_SAVE_TIME);
    }

    /**
     * 格式化key
     *
     * @param format 格式
     * @param o      .
     * @return 格式化后的key
     */
    protected String formatKey(String format, Object... o) {
        return mSampleCache.formatKey(format, o);
    }


}

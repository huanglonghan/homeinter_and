package com.ec.www.utils.cache;

import com.annimon.stream.Optional;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huang on 2017/4/7.
 */

public class SampleCache {

    //缓存保存时间(单位:秒)
    public static final int CACHE_SAVE_TIME = 5 * 60 * 60;

    private Gson mGson;
    private IStoreManage mStoreManage;

    public SampleCache(IStoreManage storeManage) {
        init();
        mStoreManage = storeManage;
    }

    public SampleCache() {
        init();
        mStoreManage = new ACacheStore();
    }

    private void init() {
        mGson = new Gson();
    }

    public final <V> void putObject(String key, V object, int saveTime) {
        Observable.just(key)
                .subscribeOn(Schedulers.io())
                .subscribe((k) -> {
                    mStoreManage.putObject(key, mGson.toJson(object), saveTime);
                });
    }

    public final <V> void putObject(String key, V object) {
        Observable.just(key)
                .subscribeOn(Schedulers.io())
                .subscribe((k) -> {
                    mStoreManage.putObject(key, mGson.toJson(object));
                });
    }

    public final <V> Observable<Optional<V>> getObject(String key, Class<V> tClass) {
        return Observable.just(key)
                .subscribeOn(Schedulers.io())
                .map((k) -> {
                    String obj = mStoreManage.getObject(k);
                    if (obj == null) {
                        return Optional.empty();
                    }
                    return Optional.ofNullable(mGson.fromJson(obj, tClass));
                });
    }

    public final <V> void modifyObject(String key, Class<V> tClass, Function<V, V> callback, int saveTime) {
        getObject(key, tClass)
                .subscribe((o) -> {
                    if (!o.isPresent()) return;
                    mStoreManage.putObject(key, mGson.toJson(callback.apply(o.get())), saveTime);
                });
    }

    public final <V> void modifyObject(String key, Class<V> tClass, Function<V, V> callback) {
        getObject(key, tClass)
                .subscribe((o) -> {
                    if (!o.isPresent()) return;
                    mStoreManage.putObject(key, mGson.toJson(callback.apply(o.get())));
                });
    }

    public String formatKey(String format, Object... o) {
        return String.format(format, o);
    }

}

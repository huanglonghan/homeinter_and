package com.ec.www.base.http;

import android.os.Build;
import android.support.annotation.RequiresApi;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;

/**
 * Created by huang on 2017/4/19.
 * 可持久化 map
 */

public abstract class StoreMap<S extends ArrayList<? extends Cookie>> extends ConcurrentHashMap<String, S> {

    StoreMap() {
        super();
        init();
    }

    @Override
    public synchronized S put(String key, S value) {
        removeNullable(value);
        saveData(key, value);
        return super.put(key, value);
    }

    @Override
    public synchronized S get(Object key) {
        S cookies = super.get(key);
        return removeNullable(cookies);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public synchronized S getOrDefault(Object key, S defaultValue) {
        S cookies = super.getOrDefault(key, defaultValue);
        return removeNullable(cookies);
    }

    private synchronized S removeNullable(S value) {
        if (value == null || value.isEmpty()) return null;
        int size = value.size();
        for (int i = 0; i < size; i++) {
            if (i >= value.size()) continue;
            Cookie cookie = value.get(i);
            if (cookie == null || cookie.name() == null) {
                if (i >= value.size()) continue;
                value.remove(i);
            }
        }
        return value;
    }

    private void saveData(String key, S value) {
        if (value.isEmpty()) return;
        if (!containsKey(key)) {
            toSave(key, value);
        } else {
            delItem(key);
            toSave(key, value);
        }
    }

    /**
     * 根据key 删除数据
     *
     * @param key key
     */
    abstract void delItem(String key);

    /**
     * 保存数据,以key为索引
     *
     * @param key  key
     * @param list 数据集
     */
    abstract void toSave(String key, S list);

    /**
     * 初始化加载数据
     */
    abstract void init();
}
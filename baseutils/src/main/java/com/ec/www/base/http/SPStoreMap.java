package com.ec.www.base.http;

import com.annimon.stream.Stream;
import com.annimon.stream.function.Function;
import com.ec.www.utils.SPUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cookie;

/**
 * Created by huang on 2017/4/21.
 */

public class SPStoreMap extends StoreMap<ArrayList<Cookie>> {

    private SPUtil mSPUtil;
    private Gson mGson;

    @Override
    void delItem(String key) {
        mSPUtil.remove(key);
    }

    @Override
    void toSave(String key, ArrayList<Cookie> list) {
        mSPUtil.putString(key, mGson.toJson(list));
    }

    @Override
    void init() {
        //初始化数据
        mSPUtil = SPUtil.init("sp_conf.sve");
        mGson = new Gson();
        Map<String, String> all = (Map<String, String>) mSPUtil.getAll();
        Stream.of(all)
                //过滤无效字符串
                .filter(entry -> entry.getValue() != null || !entry.getValue().isEmpty())
                //字符串转对象(String->ArrayList<Cookie>)
                .flatMap(new Function<Entry<String, String>, Stream<Entry<String, ArrayList<Cookie>>>>() {
                    @Override
                    public Stream<Entry<String, ArrayList<Cookie>>> apply(Entry<String, String> value) {
                        Type type = new TypeToken<ArrayList<Cookie>>() {
                        }.getType();
                        ArrayList<Cookie> cookies = mGson.fromJson(value.getValue(), type);
                        HashMap<String, ArrayList<Cookie>> map = new HashMap<>();
                        map.put(value.getKey(), cookies);
                        return Stream.of(map);
                    }
                })
                //转移到map里
                .forEach(value -> put(value.getKey(), value.getValue()));
    }
}

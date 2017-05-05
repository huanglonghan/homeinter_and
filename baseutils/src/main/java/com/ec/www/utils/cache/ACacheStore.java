package com.ec.www.utils.cache;

import com.ec.www.base.AbstractApplication;
import com.ec.www.utils.ACache;

/**
 * Created by huang on 2017/4/7.
 */

public class ACacheStore implements IStoreManage {

    private ACache mCache;

    public ACacheStore() {
        mCache = ACache.get(AbstractApplication.getContext());
    }

    @Override
    public void putObject(String key, String json, int saveTime) {
        mCache.put(key, json, saveTime);
    }

    @Override
    public void putObject(String key, String json) {
        mCache.put(key, json);
    }


    @Override
    public String getObject(String key) {
        return mCache.getAsString(key);
    }
}

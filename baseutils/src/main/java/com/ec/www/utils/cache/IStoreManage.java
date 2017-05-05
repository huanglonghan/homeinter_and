package com.ec.www.utils.cache;

/**
 * Created by huang on 2017/4/7.
 */

public interface IStoreManage {

    void putObject(String key, String json, int saveTime);

    void putObject(String key, String json);

    String getObject(String key);

}

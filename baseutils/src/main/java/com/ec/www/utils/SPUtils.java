package com.ec.www.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ec.www.base.AbstractApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by huang 2017/04/21
 */
public class SPUtils {

    private static final String DEFAULT_CONFIG_FILE_NAME = "app.conf";

    public SPUtils(SharedPreferences preferences) {
        mPreferences = preferences;
    }

    private SharedPreferences mPreferences;

    private SPUtils() {
        mPreferences = getSharedPreference(DEFAULT_CONFIG_FILE_NAME);
    }

    public static SPUtils init(String fileName) {
        return new SPUtils(getSharedPreference(fileName));
    }

    public static SPUtils init() {
        return new SPUtils();
    }

    /**
     * 获取SharedPreferences实例对象
     */
    private static SharedPreferences getSharedPreference(String fileName) {
        return AbstractApplication.getContext().getSharedPreferences(fileName, Context.MODE_PRIVATE);
    }

    /**
     * 保存一个String类型的值！
     */
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putString(key, value).apply();
    }

    /**
     * 获取所有
     *
     * @return Map
     */
    public Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

    /**
     * 获取String的value
     */
    public String getString(String key, String defValue) {
        return mPreferences.getString(key, defValue);
    }

    /**
     * 保存一个Boolean类型的值！
     */
    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(key, value).apply();
    }

    /**
     * 获取boolean的value
     */
    public boolean getBoolean(String key, Boolean defValue) {
        return mPreferences.getBoolean(key, defValue);
    }

    /**
     * 保存一个int类型的值！
     */
    public void putInt(String key, int value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt(key, value).apply();
    }

    /**
     * 获取int的value
     */
    public int getInt(String key, int defValue) {
        return mPreferences.getInt(key, defValue);
    }

    /**
     * 保存一个float类型的值！
     */
    public void putFloat(String key, float value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putFloat(key, value).apply();
    }

    /**
     * 获取float的value
     */
    public float getFloat(String key, Float defValue) {
        return mPreferences.getFloat(key, defValue);
    }

    /**
     * 保存一个long类型的值！
     */
    public void putLong(String key, long value) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putLong(key, value).apply();
    }

    /**
     * 获取long的value
     */
    public long getLong(String key, long defValue) {
        return mPreferences.getLong(key, defValue);
    }

    /**
     * 取出List<String>
     *
     * @param key List<String> 对应的key
     * @return List<String>
     */
    public List<String> getStrListValue(String key) {
        List<String> strList = new ArrayList<String>();
        int size = getInt(key + "size", 0);
        //Log.d("sp", "" + size);
        for (int i = 0; i < size; i++) {
            strList.add(getString(key + i, null));
        }
        return strList;
    }

    /**
     * 存储List<String>
     *
     * @param key     List<String>对应的key
     * @param strList 对应需要存储的List<String>
     */
    public void putStrListValue(String key,
                                       List<String> strList) {
        if (null == strList) {
            return;
        }
        // 保存之前先清理已经存在的数据，保证数据的唯一性
        removeStrList(key);
        int size = strList.size();
        putInt(key + "size", size);
        for (int i = 0; i < size; i++) {
            putString(key + i, strList.get(i));
        }
    }

    /**
     * 清空List<String>所有数据
     *
     * @param key List<String>对应的key
     */
    public void removeStrList(String key) {
        int size = getInt(key + "size", 0);
        if (0 == size) {
            return;
        }
        remove(key + "size");
        for (int i = 0; i < size; i++) {
            remove(key + i);
        }
    }

    /**
     * 清空对应key数据
     */
    public void remove(String key) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.remove(key).apply();
    }

}

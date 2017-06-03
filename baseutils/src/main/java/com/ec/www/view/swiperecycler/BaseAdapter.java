package com.ec.www.view.swiperecycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import pw.bmyo.www.swiperecycler.multitype.MultiTypeAdapter;
import pw.bmyo.www.swiperecycler.multitype.TypePool;

/**
 * Created by huang on 2016/12/14.
 */

public abstract class BaseAdapter extends MultiTypeAdapter {

    /**
     * 数据操作帮助类
     */
    private DataOperateHelper operateHelper;

    public BaseAdapter() {
        init();
    }

    public BaseAdapter(@Nullable List<?> items) {
        super(items);
        init();
    }

    public BaseAdapter(@Nullable List<?> items, int initialCapacity) {
        super(items, initialCapacity);
        init();
    }

    public BaseAdapter(@Nullable List<?> items, @NonNull TypePool pool) {
        super(items, pool);
        init();
    }

    /**
     * 初始化操作
     */
    private void init() {
        if (operateHelper == null) {
            operateHelper = new DataOperateHelper(this);
        }
    }

    public DataOperateHelper getOperateHelper() {
        return operateHelper;
    }


}

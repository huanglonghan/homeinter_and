package com.ec.www.view.swiperecycler.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ec.www.view.swiperecycler.base.multitype.MultiTypeAdapter;
import com.ec.www.view.swiperecycler.base.multitype.TypePool;

import java.util.List;

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

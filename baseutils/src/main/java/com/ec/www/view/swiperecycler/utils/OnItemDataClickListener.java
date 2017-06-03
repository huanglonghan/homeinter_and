package com.ec.www.view.swiperecycler.utils;

import android.view.View;

/**
 * Created by huang on 2017/1/21.
 */

public interface OnItemDataClickListener<T> {
    void onItemClick(View view, T data);

}

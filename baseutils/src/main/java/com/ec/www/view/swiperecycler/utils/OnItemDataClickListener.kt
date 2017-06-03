package com.ec.www.view.swiperecycler.utils

import android.view.View

/**
 * Created by huang on 2017/1/21.
 */

interface OnItemDataClickListener<T> {
    fun onItemClick(view: View, data: T)

}

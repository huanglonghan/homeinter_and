package com.ec.www.view.swiperecycler.base

import android.support.v7.widget.RecyclerView
import android.view.View

import com.ec.www.view.swiperecycler.base.multitype.ItemViewBinder
import com.ec.www.view.swiperecycler.utils.OnItemDataClickListener

/**
 * Created by huang on 2017/5/14.
 */

abstract class BaseItemViewBuild<T, VH : RecyclerView.ViewHolder> : ItemViewBinder<T, VH>() {

    protected var mOnClickListener: View.OnClickListener
    protected var mOnLongClickListener: View.OnLongClickListener

    var onItemDataClickListener: OnItemDataClickListener<T>

    protected var mTouchListener: View.OnTouchListener

    var isTouchIntercept = false

    var isLongIntercept = false

    fun setOnItemClickListener(listener: View.OnClickListener) {
        mOnClickListener = listener
    }

    fun setOnItemLongClickListener(listener: View.OnLongClickListener) {
        mOnLongClickListener = listener
    }

    fun setTouchListener(touchListener: View.OnTouchListener) {
        mTouchListener = touchListener
    }

}

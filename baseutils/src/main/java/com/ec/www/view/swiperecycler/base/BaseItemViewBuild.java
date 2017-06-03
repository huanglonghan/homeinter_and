package com.ec.www.view.swiperecycler.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ec.www.view.swiperecycler.base.multitype.ItemViewBinder;
import com.ec.www.view.swiperecycler.utils.OnItemDataClickListener;

/**
 * Created by huang on 2017/5/14.
 */

public abstract class BaseItemViewBuild<T, VH extends RecyclerView.ViewHolder> extends ItemViewBinder<T, VH> {

    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;

    protected OnItemDataClickListener<T> mOnItemDataClickListener;

    protected View.OnTouchListener mTouchListener;

    private boolean isTouchIntercept = false;

    private boolean isLongIntercept = false;

    public boolean isLongIntercept() {
        return isLongIntercept;
    }

    public void setLongIntercept(boolean longIntercept) {
        isLongIntercept = longIntercept;
    }

    public boolean isTouchIntercept() {
        return isTouchIntercept;
    }

    public void setTouchIntercept(boolean touchIntercept) {
        isTouchIntercept = touchIntercept;
    }

    public final void setOnItemClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public final void setOnItemLongClickListener(View.OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public OnItemDataClickListener<T> getOnItemDataClickListener() {
        return mOnItemDataClickListener;
    }

    public void setOnItemDataClickListener(OnItemDataClickListener<T> onItemDataClickListener) {
        mOnItemDataClickListener = onItemDataClickListener;
    }

}

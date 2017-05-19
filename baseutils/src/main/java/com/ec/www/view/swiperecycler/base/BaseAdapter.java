package com.ec.www.view.swiperecycler.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ec.www.base.OnItemDataClickListener;

/**
 * Created by huang on 2016/12/14.
 */

public abstract class BaseAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;

    protected OnItemDataClickListener mOnItemDataClickListener;

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

    public BaseAdapter() {
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
        mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        };
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

    public void setOnItemDataClickListener(OnItemDataClickListener onItemDataClickListener) {
        mOnItemDataClickListener = onItemDataClickListener;
    }

}

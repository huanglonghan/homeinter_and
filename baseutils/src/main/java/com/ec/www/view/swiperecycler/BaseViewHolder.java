package com.ec.www.view.swiperecycler;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by huang on 2017/5/11.
 */

public class BaseViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public final T bind;

    public BaseViewHolder(T bind) {
        super(bind.getRoot());
        this.bind = bind;
    }

}

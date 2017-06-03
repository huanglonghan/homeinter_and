package com.ec.www.view.swiperecycler.base

import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView

/**
 * Created by huang on 2017/5/11.
 */

class BaseViewHolder<T : ViewDataBinding>(val bind: T) : RecyclerView.ViewHolder(bind.root)

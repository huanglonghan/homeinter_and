package com.ec.www.view.swiperecycler.base.multitype

/**
 * Created by huang on 2016/12/27.
 */

interface CompareItem<T, E> {

    fun areItemsTheSame(oldItem: T,
                        newItem: E): Boolean

    fun areContentsTheSame(oldItem: T,
                           newItem: E): Boolean

    fun getChangePayload(oldItem: T,
                         newItem: E): Any?
}

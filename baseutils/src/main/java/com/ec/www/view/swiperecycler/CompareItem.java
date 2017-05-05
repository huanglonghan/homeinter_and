package com.ec.www.view.swiperecycler;

/**
 * Created by huang on 2016/12/27.
 */

public interface CompareItem<T, V> {
    boolean areItemsTheSame(Adapter.Data<T, V> oldItem,
                            Adapter.Data<T, V> newItem);

    boolean areContentsTheSame(Adapter.Data<T, V> oldItem,
                               Adapter.Data<T, V> newItem);

    Object getChangePayload(Adapter.Data<T, V> oldItem,
                            Adapter.Data<T, V> newItem);
}

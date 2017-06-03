package com.ec.www.view.swiperecycler.base.multitype;

/**
 * Created by huang on 2016/12/27.
 */

public interface CompareItem<T, E> {

    boolean areItemsTheSame(T oldItem,
                            E newItem);

    boolean areContentsTheSame(T oldItem,
                               E newItem);

    Object getChangePayload(T oldItem,
                            E newItem);
}

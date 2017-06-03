/*
 * Copyright 2016 drakeet. https://github.com/drakeet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ec.www.view.swiperecycler.base.multitype

import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.ViewGroup


/***
 * @author drakeet
 */
abstract class ItemViewBinder<T, VH : ViewHolder> : CompareItem<Any, T> {

    /* internal */
    /**
     * Get the [MultiTypeAdapter] for sending notifications or getting item count, etc.
     *
     *
     * Note that if you need to change the item's parent items, you could call this method
     * to get the [MultiTypeAdapter], and call [MultiTypeAdapter.getItems] to get
     * a list that can not be added any new item, so that you should copy the items and just use
     * [MultiTypeAdapter.setItems] to replace the original items list and update the
     * views.
     *

     * @return The MultiTypeAdapter this item is currently associated with.
     * *
     * @since v2.3.4
     */
    protected var adapter: MultiTypeAdapter? = null
        protected set


    abstract fun onCreateViewHolder(
            inflater: LayoutInflater, parent: ViewGroup): VH

    /**
     * Called by MultiTypeAdapter to display the data with its view holder. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the given item.
     *
     *
     * If you need the position of an item later on (e.g. in a click listener), use
     * `ViewHolder#getAdapterPosition()` which will have the updated adapter position.
     *
     *
     * Override `onBindViewHolder(ViewHolder, Object, List)` instead if your ItemViewBinder
     * can handle efficient partial bind.

     * @param holder The ViewHolder which should be updated to represent the contents of the
     * *               given item in the items data set.
     * *
     * @param item   The item within the MultiTypeAdapter's items data set.
     */
    protected abstract fun onBindViewHolder(holder: VH, item: T)


    /**
     * Called by MultiTypeAdapter to display the data with its view holder. This method should
     * update the contents of the [ViewHolder.itemView] to reflect the given item.
     *
     *
     * If you need the position of an item later on (e.g. in a click listener), use
     * [ViewHolder.getAdapterPosition] which will have the updated adapter position.
     *
     *
     * Partial bind vs full bind:
     *
     *
     * The payloads parameter is a merge list from [MultiTypeAdapter.notifyItemChanged] [MultiTypeAdapter.notifyItemRangeChanged].
     * If the payloads list is not empty, the ViewHolder is currently bound to old data and
     * ItemViewBinder may run an efficient partial update using the payload info.
     * If the payload is empty, ItemViewBinder must run a full bind.
     * ItemViewBinder should not assume that the payload passed in notify methods will be
     * received by onBindViewHolder().  For example when the view is not attached to the screen,
     * the payload in notifyItemChange() will be simply dropped.
     *
     *
     * This implementation calls the `onBindViewHolder(ViewHolder, Object)` by default.

     * @param holder   The ViewHolder which should be updated to represent the contents of the
     * *                 given item in the items data set.
     * *
     * @param item     The item within the MultiTypeAdapter's items data set.
     * *
     * @param payloads A non-null list of merged payloads. Can be empty list if requires full
     * *                 update.
     * *
     * @since v2.5.0
     */
    fun onBindViewHolder(
            holder: VH, item: T, payloads: List<Any>) {
        if (payloads.size <= 0 || !onPreBindViewHolder(holder, item, payloads)) {
            onBindViewHolder(holder, item)
        }
    }

    /**
     * 高级更新拦截方法
     * 拦截进行部分数据更新
     * 不拦截将调用 onBindViewHolder 进行完全更新
     * 返回true拦截, 反之不拦截 默认不拦截

     * @param holder   holder
     * *
     * @param item     item
     * *
     * @param payloads payloads
     * *
     * @return 返回true拦截, 反之不拦截
     */
    protected fun onPreBindViewHolder(holder: VH, item: T, payloads: List<Any>): Boolean {
        return false
    }


    /**
     * Get the adapter position of current item,
     * the internal position equals to [ViewHolder.getAdapterPosition].
     *
     * **NOTE**: Below v2.3.5 we may provide getPosition() method to get the position,
     * It exists BUG, and sometimes can not get the correct position,
     * it is recommended to immediately stop using it and use the new
     * `getPosition(ViewHolder)` instead.

     * @param holder The ViewHolder to call holder.getAdapterPosition().
     * *
     * @return The adapter position.
     * *
     * @since v2.3.5. If below v2.3.5, use [ViewHolder.getAdapterPosition] instead.
     */
    protected fun getPosition(holder: ViewHolder): Int {
        return holder.adapterPosition
    }

    /**
     * 更新数据时调用的比较函数
     * 进行比较是不是同类型数据
     * 返回false 数据类型不相同
     * 返回true 调用(@code areContentsTheSame()) 比较内容

     * @param oldItem 新数据
     * *
     * @param newItem 老数据
     * *
     * @return 比较结果
     */
    override fun areItemsTheSame(oldItem: Any, newItem: T): Boolean {
        return false
    }

    /**
     * 更新数据时调用的比较函数
     * 进行比较同类型数据值是否相同
     * 相同true 不相同false(返回值)
     * 仅在`areItemsTheSame()` 返回true时调用
     * 返回false 调用(@code getChangePayload()) 进行高级局部更新
     * 返回true 数据相同数据未变动

     * @param oldItem 新数据
     * *
     * @param newItem 老数据
     * *
     * @return 比较结果
     */
    override fun areContentsTheSame(oldItem: Any, newItem: T): Boolean {
        return false
    }

    /**
     * 更新数据时调用的高级局部更新函数
     * 进行同类型不同值的差异计算
     * 差异数据包装成object对象返回

     * @param oldItem 新数据
     * *
     * @param newItem 老数据
     * *
     * @return 差异数据
     */
    override fun getChangePayload(oldItem: Any, newItem: T): Any? {
        return null
    }
}
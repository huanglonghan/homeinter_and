package com.ec.www.view.swiperecycler.base

import android.support.v7.util.DiffUtil
import android.support.v7.util.ListUpdateCallback

import com.ec.www.view.swiperecycler.base.multitype.CompareItem
import com.ec.www.view.swiperecycler.base.multitype.Linker
import com.ec.www.view.swiperecycler.base.multitype.MultiTypeAdapter
import com.ec.www.view.swiperecycler.base.multitype.TypePool

import java.util.LinkedList

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * Created by huang on 2017/5/15.
 */

class DataOperateHelper(private val adapter: MultiTypeAdapter) {

    private var mDetectMove = true

    private var mCompare: CompareItem<Any, *>? = null

    /**
     * 检查是否越界

     * @param position 位置
     * *
     * @return 越界为真
     */
    private fun overRange(position: Int): Boolean {
        return position < 0 && position >= adapter.itemCount
    }

    /**
     * 根据Class计算数据类型

     * @param clazz CLass
     * *
     * @return int 数值
     */
    private fun indexInTypesOf(clazz: Class<*>): Int {
        val pool = adapter.typePool
        val index = pool.firstIndexOf(clazz)
        if (index != -1) {
            val linker = pool.linkers[index]
            try {
                return index + linker.index(clazz.newInstance())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return -1
    }

    /**
     * 根据Class计算数据类型的重载版本

     * @param item CLass
     * *
     * @return int 数值
     */
    private fun indexInTypesOf(item: Any): Int {
        val pool = adapter.typePool
        val index = pool.firstIndexOf(item.javaClass)
        if (index != -1) {
            val linker = pool.linkers[index] as Linker<Any>
            return index + linker.index(item)
        }
        return -1
    }

    /**
     * 取类型第一次出现的位置

     * @param clazz      类型
     * *
     * @param defaultVal 取不到时的默认返回值
     * *
     * @return 位置
     */
    @JvmOverloads fun getFirstPosition(clazz: Class<*>, defaultVal: Int = -1): Int {
        val items = adapter.items ?: return defaultVal
        val type = indexInTypesOf(clazz)
        val size = adapter.itemCount
        for (i in 0..size - 1) {
            if (indexInTypesOf(items[i]) == type) {
                return i
            }
        }
        return defaultVal
    }

    /**
     * 取类型最后一次出现的位置

     * @param clazz      类型
     * *
     * @param defaultVal 取不到时的默认返回值
     * *
     * @return 位置
     */
    @JvmOverloads fun getLastPosition(clazz: Class<*>, defaultVal: Int = adapter.itemCount): Int {
        var index = -1
        val items = adapter.items ?: return defaultVal
        val type = indexInTypesOf(clazz)
        val size = adapter.itemCount
        for (i in 0..size - 1) {
            if (indexInTypesOf(items[i]) == type) {
                index = i + 1
            }
        }
        if (index == -1) {
            index = defaultVal
        }
        return index
    }

    /**
     * 取连续的某类型数据数量 默认查找开始位置为该数据类型第一次出现的位置

     * @param clazz 类型
     * *
     * @return 数量
     */
    fun getTypeCount(clazz: Class<*>): Int {
        val start = getFirstPosition(clazz)
        return getTypeCount(clazz, start)
    }

    /**
     * 取连续的某类型数据数量

     * @param clazz 类型
     * *
     * @param start 查找开始位置
     * *
     * @return 数量
     */
    fun getTypeCount(clazz: Class<*>, start: Int): Int {
        var count = 0
        val items = adapter.items ?: return count
        if (overRange(start)) {
            return count
        }
        var isChange = false
        val type = indexInTypesOf(clazz)
        val size = adapter.itemCount
        for (i in start..size - 1) {
            if (indexInTypesOf(items[i]) == type) {
                count++
                if (isChange) {
                    return count
                }
            } else {
                isChange = true
            }
        }
        return count
    }

    /**
     * 根据类型取子列表 取不到则返回null

     * @param clazz 类型
     * *
     * @return 子集
     */
    fun getChildList(clazz: Class<*>): List<*>? {
        val items = adapter.items ?: return null
        val start = getFirstPosition(clazz)
        val count = getTypeCount(clazz, start)
        val end = start + count
        if (overRange(start) || overRange(end)) {
            return null
        }
        return items.subList(start, end)
    }


    fun getChildList(clazz: Class<*>, position: Int): List<*>? {
        val items = adapter.items ?: return null
        val count = getTypeCount(clazz, position)
        val end = position + count
        if (overRange(position) || overRange(end)) {
            return null
        }
        return items.subList(position, end)
    }

    fun <T> add(item: T) {
        val index = getLastPosition(item.javaClass)
        val list = LinkedList<T>()
        list.add(item)
        add(list, index)
    }

    fun <T> add(item: T, position: Int) {
        if (overRange(position)) {
            return
        }
        val list = LinkedList<T>()
        list.add(item)
        add(list, position)
    }

    fun add(items: List<*>) {
        var index = adapter.itemCount
        if (items.size > 0) {
            index = getLastPosition(items[0].javaClass)
        }
        add(items, index)
    }

    fun add(items: List<*>, position: Int) {
        if (overRange(position)) {
            return
        }
        val itemsOld = adapter.items
        val count: Int
        if (itemsOld == null) {
            adapter.items = LinkedList<*>(items)
            count = adapter.itemCount
        } else {
            val listOld = LinkedList<Any>(itemsOld)
            listOld.addAll(position, items)
            adapter.items = listOld
            count = items.size
        }
        adapter.notifyItemRangeInserted(position, count)
    }

    @JvmOverloads fun remove(position: Int, count: Int = 1) {
        if (overRange(position)) {
            return
        }
        val items = adapter.items ?: return
        for (i in 0..count - 1) {
            items.removeAt(position)
        }
        adapter.notifyItemRangeRemoved(position, count)
    }

    //删除连续类型
    fun removeType(clazz: Class<*>) {
        val items = adapter.items ?: return
        val start = getFirstPosition(clazz)
        val count = getTypeCount(clazz, start)
        if (overRange(start) || overRange(start + count)) {
            return
        }
        for (i in 0..count - 1) {
            items.removeAt(start)
        }
        adapter.notifyItemRangeRemoved(start, count)
    }

    fun <T> update(item: T) {
        updateSize(item, 1)
    }

    fun <T> update(item: T, clazz: Class<*>) {
        val index = getFirstPosition(clazz, 0)
        updateSize(item, index)
    }

    fun <T> update(item: T, position: Int) {
        updateSize(item, position, 1)
    }

    fun <T> updateSize(item: T, size: Int) {
        val index = getFirstPosition(item.javaClass, 0)
        updateSize(item, index, size)
    }

    fun <T> updateSize(item: T, clazz: Class<*>, size: Int) {
        val index = getFirstPosition(clazz, 0)
        updateSize(item, index, size)
    }

    fun <T> updateSize(item: T, position: Int, size: Int) {
        val list = LinkedList<T>()
        list.add(item)
        updateData(list, position, size)
    }

    fun update(items: List<*>) {
        val itemsOld = adapter.items
        if (itemsOld != null && itemsOld.size > 0) {
            update(items, itemsOld[0].javaClass)
        }
    }

    fun update(items: List<*>, clazz: Class<*>) {
        val itemsOld = adapter.items
        var index = 0
        if (itemsOld != null && itemsOld.size > 0) {
            index = getFirstPosition(clazz, 0)
        }
        updateSize(items, index, items.size)
    }

    fun update(items: List<*>, position: Int) {
        updateSize(items, position, items.size)
    }

    fun updateSize(items: List<*>, size: Int) {
        val itemsOld = adapter.items
        if (itemsOld != null && itemsOld.size > 0) {
            updateSize(items, itemsOld[0].javaClass, size)
        }
    }

    fun updateSize(items: List<*>, clazz: Class<*>, size: Int) {
        val itemsOld = adapter.items
        var index = 0
        if (itemsOld != null && itemsOld.size > 0) {
            index = getFirstPosition(clazz, 0)
        }
        updateData(items, index, size)
    }

    fun updateSize(items: List<*>, position: Int, size: Int) {
        updateData(items, position, size)
    }


    fun <T> update(position: Int, data: Consumer<T>) {
        if (overRange(position)) {
            return
        }
        val items = adapter.items ?: return
        try {
            data.accept(items[position] as T)
            adapter.notifyItemChanged(position, items[position])
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun <T> updateFirst(clazz: Class<*>, data: Consumer<T>) {
        val items = adapter.items ?: return
        val index = getFirstPosition(clazz, 0)
        update(index, data)
    }

    fun <T> update(clazz: Class<*>, data: Consumer<T>) {
        val items = adapter.items ?: return
        val start = getFirstPosition(clazz)
        val count = getTypeCount(clazz, start)
        val end = start + count
        if (overRange(start) || overRange(end)) {
            return
        }
        for (i in start..end - 1) {
            try {
                data.accept(items[i] as T)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        adapter.notifyItemRangeChanged(start, count)
    }

    /**
     * 更新数据并计算移动

     * @param newData  新数据
     * *
     * @param index    老数据的开始位置
     * *
     * @param size     老数据的大小
     * *
     * @param isNotify 是否通知更新
     */
    @JvmOverloads protected fun updateData(newData: List<*>, index: Int, size: Int, isNotify: Boolean = true) {
        if (overRange(index) || overRange(index + size)) {
            return
        }
        val items = adapter.items
        if (items == null) {
            add(newData)
            return
        }

        if (mCompare == null) {
            throw RuntimeException("mCompare is null")
        }

        val oldData: List<*>

        //更新数据
        oldData = getRange(index, size)
        replaceRange(newData, index, size)

        if (!isNotify) return

        //去比较器
        val pool = adapter.typePool
        val position = pool.firstIndexOf(newData[0].javaClass)
        val compare = pool.itemViewBinders[position]

        //比较并通知更新
        Observable.just<CallBack<*, *>>(CallBack(oldData, newData, compare))
                .map<DiffResult> { callback -> DiffUtil.calculateDiff(callback, mDetectMove) }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { diffUtil ->
                    diffUtil.dispatchUpdatesTo(object : ListUpdateCallback {
                        override fun onInserted(position: Int, count: Int) {
                            adapter.notifyItemRangeInserted(index + position, count)
                        }

                        override fun onRemoved(position: Int, count: Int) {
                            adapter.notifyItemRangeRemoved(index + position, count)
                        }

                        override fun onMoved(fromPosition: Int, toPosition: Int) {
                            adapter.notifyItemMoved(fromPosition, toPosition)
                        }

                        override fun onChanged(position: Int, count: Int, payload: Any) {
                            adapter.notifyItemRangeChanged(index + position, count, payload)
                        }
                    })
                }
    }

    /**
     * 获取范围数据

     * @param index 开始位置
     * *
     * @param size  数量
     * *
     * @return 数据集
     */
    private fun getRange(index: Int, size: Int): List<*>? {
        val items = adapter.items ?: return null
        return items.subList(index, index + size)
    }

    /**
     * 替换范围数据

     * @param data  新数据
     * *
     * @param index 替换开始位置
     * *
     * @param size  替换的大小
     */
    private fun replaceRange(data: List<*>, index: Int, size: Int) {
        val items = adapter.items ?: return
        for (i in 0..size - 1) {
            items.removeAt(index)
        }
        val list = LinkedList<Any>(items)
        list.addAll(index, data)
    }

    /**
     * 删除类型数据

     * @param clazz 类型
     */
    fun delete(clazz: Class<*>) {
        val items = adapter.items ?: return
        val start = getFirstPosition(clazz)
        val count = getTypeCount(clazz, start)
        val end = start + count
        if (overRange(start) || overRange(end)) {
            return
        }
        for (i in start..end - 1) {
            items.removeAt(start)
        }
        adapter.notifyItemRangeRemoved(start, count)
    }

    fun clear() {
        val items = adapter.items ?: return
        adapter.items!!.clear()
        adapter.notifyDataSetChanged()
    }

    fun setDetectMove(detectMove: Boolean) {
        mDetectMove = detectMove
    }

    fun setCompareItem(compare: CompareItem<Any, *>) {
        mCompare = compare
    }

    /**
     * 比较器回调类

     * @param <T>
     * *
     * @param <E>
    </E></T> */
    private class CallBack<T, E> internal constructor(
            /**
             * 老数据集
             */
            private val oldList: List<T>?,
            /**
             * 新数据集
             */
            private val newList: List<E>?,
            /**
             * 比较器接口
             */
            private val compareItem: CompareItem<T, E>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList?.size ?: 0
        }

        override fun getNewListSize(): Int {
            return newList?.size ?: 0
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList!![oldItemPosition]
            val newItem = newList!![newItemPosition]
            return newItem.javaClass.isInstance(oldItem) && compareItem.areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList!![oldItemPosition]
            val newItem = newList!![newItemPosition]
            return newItem.javaClass.isInstance(oldItem) && compareItem.areContentsTheSame(oldItem, newItem)
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return compareItem.getChangePayload(oldList!![oldItemPosition], newList!![newItemPosition])
        }
    }

}
/**
 * 取类型第一次出现的位置 取不到返回-1

 * @param clazz 类型
 * *
 * @return 位置
 */
/**
 * 取类型最后一次出现的位置 取不到返回当前列表大小

 * @param clazz 类型
 * *
 * @return 位置
 */
/**
 * 更新数据并计算移动(默认通知数据改变)

 * @param newData 新数据
 * *
 * @param index   老数据的开始位置
 * *
 * @param size    老数据的大小
 */

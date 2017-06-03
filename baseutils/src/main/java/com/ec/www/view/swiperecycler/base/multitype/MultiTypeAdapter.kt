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

import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * @author drakeet
 */
open class MultiTypeAdapter
/**
 * Constructs a MultiTypeAdapter with a items list and a TypePool.

 * @param items the items list
 * *
 * @param pool the type pool
 */
@JvmOverloads constructor(
        /**
         * Sets and updates the items atomically and safely.
         * It is recommended to use this method to update the data.
         *
         * e.g. `adapter.setItems(new Items(changedItems));`

         *
         * Note: If you want to refresh the list views, you should
         * call [RecyclerView.Adapter.notifyDataSetChanged] by yourself.

         * @param items the **new** items list
         * *
         * @since v2.4.1
         */
        var items: List<*>? = null,
        /**
         * Set the TypePool to hold the types and view binders.

         * @param typePool the TypePool implementation
         */
        var typePool: TypePool = MultiTypePool()) : RecyclerView.Adapter<ViewHolder>() {
    protected var inflater: LayoutInflater? = null


    /**
     * Constructs a MultiTypeAdapter with a items list and an initial capacity of TypePool.

     * @param items the items list
     * *
     * @param initialCapacity the initial capacity of TypePool
     */
    constructor(items: List<*>?, initialCapacity: Int) : this(items, MultiTypePool(initialCapacity)) {}


    /**
     * Registers a type class and its item view binder. If you have registered the class,
     * it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *

     * @param clazz the class of a item
     * *
     * @param binder the item view binder
     * *
     * @param <T> the item data type
    </T> */
    fun <T> register(
            clazz: Class<out T>, binder: ItemViewBinder<T, *>) {
        checkAndRemoveAllTypesIfNeed(clazz)
        typePool.register(clazz, binder, DefaultLinker<T>())
    }


    /**
     * Registers a type class to multiple item view binders. If you have registered the
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *

     * @param clazz the class of a item
     * *
     * @param <T> the item data type
     * *
     * @return [OneToManyFlow] for setting the binders
     * *
     * @see .register
    </T> */
    @CheckResult
    fun <T> register(clazz: Class<out T>): OneToManyFlow<T> {
        checkAndRemoveAllTypesIfNeed(clazz)
        return OneToManyBuilder(this, clazz)
    }


    /**
     * Registers all of the contents in the specified type pool. If you have registered a
     * class, it will override the original binder(s). Note that the method is non-thread-safe
     * so that you should not use it in concurrent operation.
     *
     *
     * Note that the method should not be called after
     * [RecyclerView.setAdapter], or you have to call the setAdapter
     * again.
     *

     * @param pool type pool containing contents to be added to this adapter inner pool
     * *
     * @see .register
     * @see .register
     */
    fun registerAll(pool: TypePool) {
        for (i in 0..pool.classes.size - 1) {
            registerWithoutChecking(
                    pool.classes[i],
                    pool.itemViewBinders[i],
                    pool.linkers[i]
            )
        }
    }


    override fun getItemViewType(position: Int): Int {
        assert(items != null)
        val item = items!![position]
        return indexInTypesOf(item)
    }


    override fun onCreateViewHolder(parent: ViewGroup, indexViewType: Int): ViewHolder {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.context)
        }
        val binder = typePool.itemViewBinders[indexViewType]
        binder.setAdapter(this)
        assert(inflater != null)
        return binder.onCreateViewHolder(inflater!!, parent)
    }


    /**
     * This method is deprecated and unused. You should not call this method.
     *
     *
     * If you need to call the binding, use [RecyclerView.Adapter.onBindViewHolder] instead.
     *

     * @param holder The ViewHolder which should be updated to represent the contents of the
     * * item at the given position in the data set.
     * *
     * @param position The position of the item within the adapter's data set.
     * *
     * @throws IllegalAccessError By default.
     * *
     */
    @Deprecated("")
    @Deprecated("Call {@link RecyclerView.Adapter#onBindViewHolder(ViewHolder, int, List)}\n      instead.")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        throw IllegalAccessError("You should not call this method. " + "Call RecyclerView.Adapter#onBindViewHolder(holder, position, payloads) instead.")
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: List<Any>?) {
        assert(items != null)
        val item = items!![position]
        val binder = typePool.itemViewBinders[holder.itemViewType]
        binder.onBindViewHolder(holder, item, payloads!!)
    }


    override fun getItemCount(): Int {
        return if (items == null) 0 else items!!.size
    }


    @Throws(BinderNotFoundException::class)
    internal fun indexInTypesOf(item: Any): Int {
        val index = typePool.firstIndexOf(item.javaClass)
        if (index != -1) {
            val linker = typePool.linkers[index] as Linker<Any>
            return index + linker.index(item)
        }
        throw BinderNotFoundException(item.javaClass)
    }


    private fun checkAndRemoveAllTypesIfNeed(clazz: Class<*>) {
        if (!typePool.classes.contains(clazz)) {
            return
        }
        Log.w(TAG, "You have registered the " + clazz.simpleName + " type. " +
                "It will override the original binder(s).")
        while (true) {
            val index = typePool.classes.indexOf(clazz)
            if (index != -1) {
                typePool.classes.removeAt(index)
                typePool.itemViewBinders.removeAt(index)
                typePool.linkers.removeAt(index)
            } else {
                break
            }
        }
    }


    internal fun <T> registerWithLinker(
            clazz: Class<out T>,
            binder: ItemViewBinder<T, *>,
            linker: Linker<T>) {
        typePool.register(clazz, binder, linker)
    }


    /** A safe register method base on the TypePool's safety for TypePool.  */
    private fun registerWithoutChecking(
            clazz: Class<*>, itemViewBinder: ItemViewBinder<*, *>, linker: Linker<*>) {
        checkAndRemoveAllTypesIfNeed(clazz)
        typePool.register<Any>(clazz, itemViewBinder, linker)
    }

    companion object {

        private val TAG = "MultiTypeAdapter"
    }
}
/**
 * Constructs a MultiTypeAdapter with a null items list.
 */
/**
 * Constructs a MultiTypeAdapter with a items list.

 * @param items the items list
 */

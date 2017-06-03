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

import java.util.ArrayList

/**
 * An List implementation of TypePool.

 * @author drakeet
 */
class MultiTypePool : TypePool {

    private val classes: MutableList<Class<*>>
    private val binders: MutableList<ItemViewBinder<*, *>>
    private val linkers: MutableList<Linker<*>>


    /**
     * Constructs a MultiTypePool with default lists.
     */
    constructor() {
        this.classes = ArrayList<Class<*>>()
        this.binders = ArrayList<ItemViewBinder<*, *>>()
        this.linkers = ArrayList<Linker<*>>()
    }

    /**
     * Constructs a MultiTypePool with default lists and a specified initial capacity.

     * @param initialCapacity the initial capacity of the list
     */
    constructor(initialCapacity: Int) {
        this.classes = ArrayList<Class<*>>(initialCapacity)
        this.binders = ArrayList<ItemViewBinder<*, *>>(initialCapacity)
        this.linkers = ArrayList<Linker<*>>(initialCapacity)
    }

    /**
     * Constructs a MultiTypePool with specified lists.

     * @param classes the list for classes
     * *
     * @param binders the list for binders
     * *
     * @param linkers the list for linkers
     */
    constructor(
            classes: MutableList<Class<*>>,
            binders: MutableList<ItemViewBinder<*, *>>,
            linkers: MutableList<Linker<*>>) {
        this.classes = classes
        this.binders = binders
        this.linkers = linkers
    }


    override fun <T> register(
            clazz: Class<out T>,
            binder: ItemViewBinder<T, *>,
            linker: Linker<T>) {
        classes.add(clazz)
        binders.add(binder)
        linkers.add(linker)
    }

    override fun firstIndexOf(clazz: Class<*>): Int {
        val index = classes.indexOf(clazz)
        if (index != -1) {
            return index
        }
        for (i in classes.indices) {
            if (classes[i].isAssignableFrom(clazz)) {
                return i
            }
        }
        return -1
    }


    override fun getClasses(): List<Class<*>> {
        return classes
    }

    override fun getItemViewBinders(): List<ItemViewBinder<*, *>> {
        return binders
    }

    override fun getLinkers(): List<Linker<*>> {
        return linkers
    }

}

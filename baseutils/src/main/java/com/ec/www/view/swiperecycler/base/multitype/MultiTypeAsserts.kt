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

import android.support.v7.widget.RecyclerView

/**
 * @author drakeet
 */
class MultiTypeAsserts private constructor() {

    init {
        throw AssertionError()
    }

    companion object {


        /**
         * Makes the exception to occur in your class for debug and index.

         * @param adapter the MultiTypeAdapter
         * *
         * @param items the items list
         * *
         * @throws BinderNotFoundException if check failed
         * *
         * @throws IllegalArgumentException if your Items/List is empty
         */
        @Throws(BinderNotFoundException::class, IllegalArgumentException::class, IllegalAccessError::class)
        fun assertAllRegistered(
                adapter: MultiTypeAdapter,
                items: List<*>) {

            if (items.size == 0) {
                throw IllegalArgumentException("Your Items/List is empty.")
            }
            for (item in items) {
                adapter.indexInTypesOf(item)
            }
            /* All passed. */
        }


        /**
         * @param recyclerView the RecyclerView
         * *
         * @param adapter the MultiTypeAdapter
         * *
         * @throws IllegalAccessError The assertHasTheSameAdapter() method must be placed after
         * * recyclerView.setAdapter().
         * *
         * @throws IllegalArgumentException If your recyclerView's adapter.
         * * is not the sample with the argument adapter.
         */
        @Throws(IllegalArgumentException::class, IllegalAccessError::class)
        fun assertHasTheSameAdapter(
                recyclerView: RecyclerView, adapter: MultiTypeAdapter) {
            if (recyclerView.adapter == null) {
                throw IllegalAccessError(
                        "The assertHasTheSameAdapter() method must be placed after recyclerView.setAdapter()")
            }
            if (recyclerView.adapter !== adapter) {
                throw IllegalArgumentException(
                        "Your recyclerView's adapter is not the sample with the argument adapter.")
            }
        }
    }
}

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
 * A convenient class for creating a `ArrayList<Object>`.

 * @author drakeet
 */
class Items : ArrayList<Any> {

    /**
     * Constructs an empty Items with an initial capacity of ten.
     */
    constructor() : super() {}


    /**
     * Constructs an empty Items with the specified initial capacity.

     * @param initialCapacity the initial capacity of the Items
     * *
     * @throws IllegalArgumentException if the specified initial capacity
     * * is negative
     */
    constructor(initialCapacity: Int) : super(initialCapacity) {}


    /**
     * Constructs a Items containing the elements of the specified
     * collection, in the order they are returned by the collection's
     * iterator.

     * @param c the collection whose elements are to be placed into this Items
     * *
     * @throws NullPointerException if the specified collection is null
     */
    constructor(c: Collection<*>) : super(c) {}
}

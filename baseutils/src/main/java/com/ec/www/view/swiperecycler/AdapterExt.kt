package com.ec.www.view.swiperecycler

import android.support.v7.widget.RecyclerView
import com.ec.www.view.swiperecycler.base.BaseAdapter
import com.ec.www.view.swiperecycler.base.multitype.TypePool
import io.reactivex.Observable
import java.util.*

/**
 * Created by huang on 2017/6/3.
 */
class AdapterExt: BaseAdapter {

    constructor():super()
    constructor(items: MutableList<*>?) : super(items)
    constructor(items: MutableList<*>?, initialCapacity: Int) : super(items, initialCapacity)
    constructor(items: MutableList<*>?, pool: TypePool) : super(items, pool)

    
}
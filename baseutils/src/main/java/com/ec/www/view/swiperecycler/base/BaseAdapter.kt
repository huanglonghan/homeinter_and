package com.ec.www.view.swiperecycler.base

import com.ec.www.view.swiperecycler.base.multitype.MultiTypeAdapter
import com.ec.www.view.swiperecycler.base.multitype.TypePool

/**
 * Created by huang on 2016/12/14.
 */

abstract class BaseAdapter : MultiTypeAdapter {

    /**
     * 数据操作帮助类
     */
    var operateHelper: DataOperateHelper? = null
        private set

    constructor() {
        init()
    }

    constructor(items: List<*>?) : super(items) {
        init()
    }

    constructor(items: List<*>?, initialCapacity: Int) : super(items, initialCapacity) {
        init()
    }

    constructor(items: List<*>?, pool: TypePool) : super(items, pool) {
        init()
    }

    /**
     * 初始化操作
     */
    private fun init() {
        if (operateHelper == null) {
            operateHelper = DataOperateHelper(this)
        }
    }


}

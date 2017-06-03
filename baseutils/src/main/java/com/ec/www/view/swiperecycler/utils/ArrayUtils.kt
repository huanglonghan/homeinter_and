package com.ec.www.view.swiperecycler.utils

import java.util.Collections

/**
 * Created by huang on 2017/1/20.
 */

object ArrayUtils {

    fun swap(list: List<*>, fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..toPosition - 1) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
    }

}

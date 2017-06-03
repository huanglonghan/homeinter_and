package com.ec.www.view.swiperecycler.utils

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by huang on 2016/12/28.
 */

class SpaceItem(context: Context, orientation: Int, private val mSpace: Int) : RecyclerView.ItemDecoration() {
    private var mOrientation: Int = 0

    init {
        val a = context.obtainStyledAttributes(ATTRS)
        a.recycle()
        setOrientation(orientation)
    }

    fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw IllegalArgumentException("invalid orientation")
        }
        mOrientation = orientation
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        val index = parent.getChildAdapterPosition(view)
        if (index < 0 || index > parent.adapter.itemCount) return
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mSpace)
        } else {
            outRect.set(0, 0, mSpace, 0)
        }
    }

    companion object {
        private val ATTRS = intArrayOf(android.R.attr.listDivider)
        val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }
}

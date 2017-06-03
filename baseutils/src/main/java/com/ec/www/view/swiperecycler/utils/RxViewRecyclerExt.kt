package com.ec.www.view.swiperecycler.utils

import android.support.annotation.CheckResult
import android.support.v7.widget.RecyclerView
import android.view.View

import com.ec.www.view.swiperecycler.base.BaseItemViewBuild
import com.ec.www.view.swiperecycler.SwipeRecyclerView

import io.reactivex.Observable

import com.ec.www.base.rxview.Preconditions.checkNotNull


/*
 * Created by huang on 2017/1/8.
 */

object RxViewRecyclerExt {

    @CheckResult
    fun itemClicks(adapter: BaseItemViewBuild<*, *>): Observable<View> {
        checkNotNull(adapter, "adapter == null")
        return OnSubscribeRecyclerExt.ItemClickOnSubscribe(adapter)
    }

    @CheckResult
    fun itemLongClicks(adapter: BaseItemViewBuild<*, *>): Observable<View> {
        checkNotNull(adapter, "adapter == null")
        return OnSubscribeRecyclerExt.ItemLongClickOnSubscribe(adapter)
    }

    @CheckResult
    fun <T, VH : RecyclerView.ViewHolder> itemDataClicks(adapter: BaseItemViewBuild<T, VH>): Observable<OnSubscribeRecyclerExt.ItemDataOnSubscribe.InnerMsg<T>> {
        checkNotNull(adapter, "adapter == null")
        return OnSubscribeRecyclerExt.ItemDataOnSubscribe(adapter)
    }

    @CheckResult
    fun itemTouch(adapter: BaseItemViewBuild<*, *>): Observable<OnSubscribeRecyclerExt.ItemTouchOnSubscribe.TouchMsg> {
        checkNotNull(adapter, "adapter == null")
        return OnSubscribeRecyclerExt.ItemTouchOnSubscribe(adapter)
    }

    @CheckResult
    fun canScroll(view: RecyclerView, direction: Int): Observable<RecyclerView> {
        checkNotNull(view, "view == null")
        return OnSubscribeRecyclerExt.CanScrollOnSubscribe(view, direction)
    }

    @CheckResult
    fun canScroll(view: RecyclerView): Observable<RecyclerView> {
        checkNotNull(view, "view == null")
        return OnSubscribeRecyclerExt.CanScrollOnSubscribe(view)
    }

    @CheckResult
    fun swipeRefresh(view: SwipeRecyclerView): Observable<SwipeRecyclerView> {
        checkNotNull(view, "view == null")
        return OnSubscribeRecyclerExt.SwipeRefreshOnSubscribe(view)
    }
}

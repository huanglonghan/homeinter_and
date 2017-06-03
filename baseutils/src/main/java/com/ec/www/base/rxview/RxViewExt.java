package com.ec.www.base.rxview;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ec.www.view.swiperecycler.SwipeRecyclerView;
import com.ec.www.view.swiperecycler.base.BaseAdapter;

import io.reactivex.Observable;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkNotNull;



/*
 * Created by huang on 2017/1/8.
 */

public final class RxViewExt {

    @CheckResult
    @NonNull
    public static Observable<View> clicks(@NonNull View view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeExt.ClickOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeExt.ItemClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemLongClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeExt.ItemLongClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static <T> Observable<OnSubscribeExt.ItemDataOnSubscribe.InnerMsg<T>> itemDataClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeExt.ItemDataOnSubscribe<>(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribeExt.ItemTouchOnSubscribe.TouchMsg> itemTouch(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeExt.ItemTouchOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view, int direction) {
        checkNotNull(view, "view == null");
        return new OnSubscribeExt.CanScrollOnSubscribe(view, direction);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeExt.CanScrollOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<SwipeRecyclerView> swipeRefresh(@NonNull SwipeRecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeExt.SwipeRefreshOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribeExt.MultiNestedScrollChangeOnSubscribe.ScrollInfo> multiNestedScrollChange(@NonNull NestedScrollView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeExt.MultiNestedScrollChangeOnSubscribe(view);
    }
}

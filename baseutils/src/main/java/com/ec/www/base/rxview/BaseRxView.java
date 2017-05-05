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

public final class BaseRxView {

    @CheckResult
    @NonNull
    public static Observable<View> clicks(@NonNull View view) {
        checkNotNull(view, "view == null");
        return new OnSubscribe.ClickOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribe.ItemClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemLongClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribe.ItemLongClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static <T> Observable<T> itemDataClicks(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribe.ItemDataOnSubscribe<T>(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribe.ItemTouchOnSubscribe.TouchMsg> itemTouch(@NonNull BaseAdapter adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribe.ItemTouchOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view, int direction) {
        checkNotNull(view, "view == null");
        return new OnSubscribe.CanScrollOnSubscribe(view, direction);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribe.CanScrollOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<SwipeRecyclerView> swipeRefresh(@NonNull SwipeRecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribe.SwipeRefreshOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribe.MultiNestedScrollChangeOnSubscribe.ScrollInfo> multiNestedScrollChange(@NonNull NestedScrollView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribe.MultiNestedScrollChangeOnSubscribe(view);
    }
}

package com.ec.www.view.swiperecycler.utils;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ec.www.view.swiperecycler.base.BaseItemViewBuild;
import com.ec.www.view.swiperecycler.SwipeRecyclerView;

import io.reactivex.Observable;

import static com.ec.www.base.rxview.Preconditions.checkNotNull;


/*
 * Created by huang on 2017/1/8.
 */

public final class RxViewRecyclerExt {

    @CheckResult
    @NonNull
    public static Observable<View> itemClicks(@NonNull BaseItemViewBuild adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeRecyclerExt.ItemClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<View> itemLongClicks(@NonNull BaseItemViewBuild adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeRecyclerExt.ItemLongClickOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static <T, VH extends RecyclerView.ViewHolder> Observable<OnSubscribeRecyclerExt.ItemDataOnSubscribe.InnerMsg<T>> itemDataClicks(@NonNull BaseItemViewBuild<T, VH> adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeRecyclerExt.ItemDataOnSubscribe<>(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<OnSubscribeRecyclerExt.ItemTouchOnSubscribe.TouchMsg> itemTouch(@NonNull BaseItemViewBuild adapter) {
        checkNotNull(adapter, "adapter == null");
        return new OnSubscribeRecyclerExt.ItemTouchOnSubscribe(adapter);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view, int direction) {
        checkNotNull(view, "view == null");
        return new OnSubscribeRecyclerExt.CanScrollOnSubscribe(view, direction);
    }

    @CheckResult
    @NonNull
    public static Observable<RecyclerView> canScroll(@NonNull RecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeRecyclerExt.CanScrollOnSubscribe(view);
    }

    @CheckResult
    @NonNull
    public static Observable<SwipeRecyclerView> swipeRefresh(@NonNull SwipeRecyclerView view) {
        checkNotNull(view, "view == null");
        return new OnSubscribeRecyclerExt.SwipeRefreshOnSubscribe(view);
    }
}

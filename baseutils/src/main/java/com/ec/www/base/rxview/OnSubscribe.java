package com.ec.www.base.rxview;

import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.MotionEvent;
import android.view.View;

import pw.bmyo.www.swiperecycler.OnItemClickListener;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.MainThreadDisposable;
import pw.bmyo.www.swiperecycler.BaseAdapter;

import static com.jakewharton.rxbinding2.internal.Preconditions.checkMainThread;


/**
 * Created by huang on 2017/1/8.
 */

public class OnSubscribe {

    public static final class ClickOnSubscribe extends Observable<View> {
        final View view;

        ClickOnSubscribe(View view) {
            this.view = view;
        }

        @Override
        protected void subscribeActual(Observer<? super View> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(view, observer);
            observer.onSubscribe(listener);
            view.setOnClickListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements View.OnClickListener {
            private final View view;
            private final Observer<? super View> observer;

            Listener(View view, Observer<? super View> observer) {
                this.view = view;
                this.observer = observer;
            }

            @Override
            public void onClick(View v) {
                if (!isDisposed()) {
                    observer.onNext(v);
                }
            }

            @Override
            protected void onDispose() {
                view.setOnClickListener(null);
            }
        }
    }

    public static final class ItemClickOnSubscribe extends Observable<View> {
        final BaseAdapter mAdapter;

        ItemClickOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        protected void subscribeActual(Observer<? super View> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(mAdapter, observer);
            observer.onSubscribe(listener);
            mAdapter.setOnItemClickListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements View.OnClickListener {
            private final BaseAdapter adapter;
            private final Observer<? super View> observer;

            Listener(BaseAdapter adapter, Observer<? super View> observer) {
                this.adapter = adapter;
                this.observer = observer;
            }

            @Override
            public void onClick(View v) {
                if (!isDisposed()) {
                    observer.onNext(v);
                }
            }

            @Override
            protected void onDispose() {
                adapter.setOnItemClickListener((View.OnClickListener) null);
            }
        }
    }

    public static final class ItemDataOnSubscribe<T> extends Observable<T> {
        final BaseAdapter mAdapter;

        ItemDataOnSubscribe(
                BaseAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        protected void subscribeActual(Observer<? super T> observer) {
            checkMainThread(observer);
            Listener listener = new Listener<>(mAdapter, observer);
            observer.onSubscribe(listener);
            mAdapter.setOnItemClickListener(listener);
        }

        static final class Listener<T> extends MainThreadDisposable implements OnItemClickListener<T> {
            private final BaseAdapter adapter;
            private final Observer<? super T> observer;

            Listener(BaseAdapter adapter, Observer<? super T> observer) {
                this.adapter = adapter;
                this.observer = observer;
            }

            @Override
            protected void onDispose() {
                adapter.setOnItemClickListener((OnItemClickListener) null);
            }

            @Override
            public void onItemClick(View view, T data, int position) {
                if (!isDisposed()) {
                    observer.onNext(data);
                }
            }
        }
    }

    public static final class ItemLongClickOnSubscribe extends Observable<View> {
        final BaseAdapter mAdapter;

        ItemLongClickOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        protected void subscribeActual(Observer<? super View> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(mAdapter, observer);
            observer.onSubscribe(listener);
            mAdapter.setOnItemLongClickListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements View.OnLongClickListener {
            private final BaseAdapter adapter;
            private final Observer<? super View> observer;

            Listener(BaseAdapter adapter, Observer<? super View> observer) {
                this.adapter = adapter;
                this.observer = observer;
            }

            @Override
            protected void onDispose() {
                adapter.setOnItemLongClickListener(null);
            }

            @Override
            public boolean onLongClick(View v) {
                if (!isDisposed()) {
                    observer.onNext(v);
                }
                return adapter.isLongIntercept();
            }
        }
    }

    public static final class ItemTouchOnSubscribe extends Observable<ItemTouchOnSubscribe.TouchMsg> {
        final BaseAdapter mAdapter;

        ItemTouchOnSubscribe(BaseAdapter adapter) {
            this.mAdapter = adapter;
        }

        @Override
        protected void subscribeActual(Observer<? super TouchMsg> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(mAdapter, observer);
            observer.onSubscribe(listener);
            mAdapter.setTouchListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements View.OnTouchListener {
            private final BaseAdapter adapter;
            private final Observer<? super TouchMsg> observer;

            Listener(BaseAdapter adapter, Observer<? super TouchMsg> observer) {
                this.adapter = adapter;
                this.observer = observer;
            }

            @Override
            protected void onDispose() {
                adapter.setTouchListener(null);
            }

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!isDisposed()) {
                    observer.onNext(new TouchMsg(v, event));
                }
                return adapter.isTouchIntercept();
            }
        }

        public static class TouchMsg {
            public View mView;
            public MotionEvent mEvent;

            TouchMsg(View view, MotionEvent event) {
                mEvent = event;
                mView = view;
            }

        }
    }

    public static final class CanScrollOnSubscribe extends Observable<RecyclerView> {
        public static final int CAN_UP = 1;
        public static final int CAN_DOWN = -1;

        final RecyclerView view;
        final int direction;

        CanScrollOnSubscribe(RecyclerView view, int direction) {
            this.view = view;
            this.direction = direction;
        }

        CanScrollOnSubscribe(RecyclerView view) {
            this.view = view;
            this.direction = CAN_UP;
        }

        @Override
        protected void subscribeActual(Observer<? super RecyclerView> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(view);
            OnScrollListener scrollListener = new OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!ViewCompat.canScrollVertically(recyclerView, direction)) {
                        if (!listener.isDisposed()) {
                            observer.onNext(recyclerView);
                        }
                    }
                }
            };
            view.addOnScrollListener(scrollListener);
        }

        static final class Listener extends MainThreadDisposable {
            private final RecyclerView mView;

            Listener(RecyclerView adapter) {
                this.mView = adapter;
            }

            @Override
            protected void onDispose() {
                mView.setOnScrollListener(null);
            }
        }
    }

    public static final class SwipeRefreshOnSubscribe extends Observable<SwipeRecyclerView> {
        final SwipeRecyclerView view;

        SwipeRefreshOnSubscribe(SwipeRecyclerView view) {
            this.view = view;
        }

        @Override
        protected void subscribeActual(Observer<? super SwipeRecyclerView> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(view, observer);
            observer.onSubscribe(listener);
            view.setOnRefreshListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements SwipeRecyclerView.OnRefreshListener {
            private final SwipeRecyclerView view;
            private final Observer<? super SwipeRecyclerView> observer;

            Listener(SwipeRecyclerView view, Observer<? super SwipeRecyclerView> observer) {
                this.view = view;
                this.observer = observer;
            }


            @Override
            public void onRefresh() {
                if (!isDisposed()) {
                    view.setSlideState(SwipeRecyclerView.SLIDE_DOWN);
                    observer.onNext(view);
                }
            }

            @Override
            public void onLoading() {
                if (!isDisposed()) {
                    view.setSlideState(SwipeRecyclerView.SLIDE_UP);
                    observer.onNext(view);
                }
            }

            @Override
            public void onErrorReload() {
                if (!isDisposed()) {
                    view.setSlideState(SwipeRecyclerView.LOAD_ERROR);
                    observer.onNext(view);
                }
            }

            @Override
            protected void onDispose() {
                view.setOnRefreshListener(null);
            }

        }
    }

    public static final class MultiNestedScrollChangeOnSubscribe extends Observable<MultiNestedScrollChangeOnSubscribe.ScrollInfo> {

        final NestedScrollView view;

        MultiNestedScrollChangeOnSubscribe(NestedScrollView view) {
            this.view = view;
        }

        @Override
        protected void subscribeActual(Observer<? super ScrollInfo> observer) {
            checkMainThread(observer);
            Listener listener = new Listener(view, observer);
            observer.onSubscribe(listener);
            view.setOnScrollChangeListener(listener);
        }

        static final class Listener extends MainThreadDisposable implements NestedScrollView.OnScrollChangeListener {
            private final NestedScrollView view;
            private final Observer<? super ScrollInfo> observer;
            private final ScrollInfo scrollInfo;

            Listener(NestedScrollView view, Observer<? super ScrollInfo> observer) {
                this.view = view;
                this.observer = observer;
                scrollInfo = new ScrollInfo();
            }

            @Override
            protected void onDispose() {
                view.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) null);
            }

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!isDisposed()) {
                    scrollInfo.init(v, scrollX, scrollY, oldScrollX, oldScrollY);
                    observer.onNext(scrollInfo);
                }
            }
        }

        public static class ScrollInfo {
            public NestedScrollView v;
            public int scrollX;
            public int scrollY;
            public int oldScrollX;
            public int oldScrollY;

            public void init(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                this.v = v;
                this.scrollX = scrollX;
                this.scrollY = scrollY;
                this.oldScrollX = oldScrollX;
                this.oldScrollY = oldScrollY;
            }
        }
    }

}

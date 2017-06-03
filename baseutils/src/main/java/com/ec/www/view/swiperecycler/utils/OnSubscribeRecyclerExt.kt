package com.ec.www.view.swiperecycler.utils

import android.annotation.SuppressLint
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.OnScrollListener
import android.view.MotionEvent
import android.view.View

import com.ec.www.view.swiperecycler.base.BaseItemViewBuild
import com.ec.www.view.swiperecycler.SwipeRecyclerView

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

import com.ec.www.base.rxview.Preconditions.checkMainThread

/**
 * Created by huang on 2017/1/8.
 */

class OnSubscribeRecyclerExt {

    class ItemClickOnSubscribe internal constructor(internal val mAdapter: BaseItemViewBuild<*, *>) : Observable<View>() {

        override fun subscribeActual(observer: Observer<in View>) {
            checkMainThread(observer)
            val listener = Listener(mAdapter, observer)
            observer.onSubscribe(listener)
            mAdapter.setOnItemClickListener(listener)
        }

        internal class Listener(private val adapter: BaseItemViewBuild<*, *>, private val observer: Observer<in View>) : MainThreadDisposable(), View.OnClickListener {

            override fun onClick(v: View) {
                if (!isDisposed) {
                    observer.onNext(v)
                }
            }

            override fun onDispose() {
                adapter.setOnItemClickListener(null!!)
            }
        }
    }

    class ItemDataOnSubscribe<T, VH : RecyclerView.ViewHolder> internal constructor(
            internal val mAdapter: BaseItemViewBuild<T, VH>) : Observable<ItemDataOnSubscribe.InnerMsg<T>>() {

        override fun subscribeActual(observer: Observer<in InnerMsg<T>>) {
            checkMainThread(observer)
            val listener = ListenerData(mAdapter, observer)
            observer.onSubscribe(listener)
            mAdapter.onItemDataClickListener = listener
        }

        internal class ListenerData<T, VH : RecyclerView.ViewHolder>(private val adapter: BaseItemViewBuild<T, VH>, private val observer: Observer<in InnerMsg<T>>) : MainThreadDisposable(), OnItemDataClickListener<T> {
            private val mMsg: InnerMsg<T>

            init {
                mMsg = InnerMsg<T>()
            }

            override fun onDispose() {
                adapter.onItemDataClickListener = null
            }

            override fun onItemClick(view: View, data: T) {
                if (!isDisposed) {
                    observer.onNext(mMsg.set(view, data))
                }
            }
        }

        class InnerMsg<T> {
            var view: View
            var data: T

            internal operator fun set(view: View, data: T): InnerMsg<T> {
                this.view = view
                this.data = data
                return this
            }

        }
    }

    class ItemLongClickOnSubscribe internal constructor(internal val mAdapter: BaseItemViewBuild<*, *>) : Observable<View>() {

        override fun subscribeActual(observer: Observer<in View>) {
            checkMainThread(observer)
            val listener = Listener(mAdapter, observer)
            observer.onSubscribe(listener)
            mAdapter.setOnItemLongClickListener(listener)
        }

        internal class Listener(private val adapter: BaseItemViewBuild<*, *>, private val observer: Observer<in View>) : MainThreadDisposable(), View.OnLongClickListener {

            override fun onDispose() {
                adapter.setOnItemLongClickListener(null!!)
            }

            override fun onLongClick(v: View): Boolean {
                if (!isDisposed) {
                    observer.onNext(v)
                }
                return adapter.isLongIntercept
            }
        }
    }

    class ItemTouchOnSubscribe internal constructor(internal val mAdapter: BaseItemViewBuild<*, *>) : Observable<ItemTouchOnSubscribe.TouchMsg>() {

        override fun subscribeActual(observer: Observer<in TouchMsg>) {
            checkMainThread(observer)
            val listener = Listener(mAdapter, observer)
            observer.onSubscribe(listener)
            mAdapter.setTouchListener(listener)
        }

        internal class Listener(private val adapter: BaseItemViewBuild<*, *>, private val observer: Observer<in TouchMsg>) : MainThreadDisposable(), View.OnTouchListener {
            private val mMsg: TouchMsg

            init {
                mMsg = TouchMsg()
            }

            override fun onDispose() {
                adapter.setTouchListener(null!!)
            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (!isDisposed) {
                    observer.onNext(mMsg.set(v, event))
                }
                return adapter.isTouchIntercept
            }
        }

        class TouchMsg {
            var view: View
            var event: MotionEvent

            internal operator fun set(view: View, event: MotionEvent): TouchMsg {
                this.event = event
                this.view = view
                return this
            }

        }
    }

    class CanScrollOnSubscribe : Observable<RecyclerView> {

        internal val view: RecyclerView
        internal val direction: Int

        internal constructor(view: RecyclerView, direction: Int) {
            this.view = view
            this.direction = direction
        }

        internal constructor(view: RecyclerView) {
            this.view = view
            this.direction = CAN_UP
        }

        override fun subscribeActual(observer: Observer<in RecyclerView>) {
            checkMainThread(observer)
            val listener = Listener(view)
            val scrollListener = object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (!ViewCompat.canScrollVertically(recyclerView, direction)) {
                        if (!listener.isDisposed) {
                            observer.onNext(recyclerView)
                        }
                    }
                }
            }
            view.addOnScrollListener(scrollListener)
        }

        internal class Listener(private val mView: RecyclerView) : MainThreadDisposable() {

            override fun onDispose() {
                mView.setOnScrollListener(null)
            }
        }

        companion object {
            val CAN_UP = 1
            val CAN_DOWN = -1
        }
    }

    class SwipeRefreshOnSubscribe internal constructor(internal val view: SwipeRecyclerView) : Observable<SwipeRecyclerView>() {

        override fun subscribeActual(observer: Observer<in SwipeRecyclerView>) {
            checkMainThread(observer)
            val listener = Listener(view, observer)
            observer.onSubscribe(listener)
            view.setOnRefreshListener(listener)
        }

        internal class Listener(private val view: SwipeRecyclerView, private val observer: Observer<in SwipeRecyclerView>) : MainThreadDisposable(), SwipeRecyclerView.OnRefreshListener {


            override fun onRefresh() {
                if (!isDisposed) {
                    view.setSlideState(SwipeRecyclerView.SLIDE_DOWN)
                    observer.onNext(view)
                }
            }

            override fun onLoading() {
                if (!isDisposed) {
                    view.setSlideState(SwipeRecyclerView.SLIDE_UP)
                    observer.onNext(view)
                }
            }

            override fun onErrorReload() {
                if (!isDisposed) {
                    view.setSlideState(SwipeRecyclerView.LOAD_ERROR)
                    observer.onNext(view)
                }
            }

            override fun onDispose() {
                view.setOnRefreshListener(null)
            }

        }
    }

}

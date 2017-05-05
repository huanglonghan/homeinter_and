package com.ec.www.base.http;

import android.support.v4.util.Pools;

import com.ec.www.BuildConfig;
import com.ec.www.base.http.response.IDispose;
import com.ec.www.base.http.response.ResultSupport;
import com.ec.www.utils.CommonUtils;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.DefaultObserver;


/**
 * Created by Administrator on 2016/10/18 0018.
 * http请求回调默认处理类
 */

public class HttpResponse extends DefaultObserver<Object> {

    private List<Consumer<ResultSupport>> mCompleteCallBack;
    private Consumer<Object> mCacheCallback;
    private IDispose mIDispose;


    public void recycle() {
        try {
            mIDispose.recycle();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        mCompleteCallBack.clear();
        mCacheCallback = null;
        mIDispose = null;
    }

    public void setCacheCallback(Consumer<Object> cacheCallback) {
        mCacheCallback = cacheCallback;
    }

    public final void init() {
        if (mCompleteCallBack == null)
            mCompleteCallBack = new LinkedList<>();
        mCacheCallback = null;
    }

    public HttpResponse() {
        init();
    }

    /**
     * 请求开始
     */
    @Override
    public final void onStart() {
        if (mIDispose == null) throw new RuntimeException("must first initialize mIDispose !");
        mIDispose.onPerStart();
    }

    /**
     * 请求结果
     *
     * @param o
     */
    @Override
    public final void onNext(Object o) {
        if (o == null) throw new RuntimeException("CommonResponse is null!");
        if (!mIDispose.onPrePerform(o)) {
            mIDispose.onPerform(o);
            if (mCacheCallback != null) {
                try {
                    mCacheCallback.accept(o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        mIDispose.onPerformAfter(o);
    }

    /**
     * 无异常结束
     */
    @Override
    public final void onComplete() {
        mIDispose.onPerCompleted();
        overCallback();
    }

    /**
     * 结果处理异常, 请求异常结束
     */
    @Override
    public final void onError(Throwable e) {
        mIDispose.onPerError(e);
        overCallback();
        CommonUtils.printStackTrace(e, BuildConfig.DEBUG);
    }

    /**
     * 结束请求回调
     */
    private void overCallback() {
        if (mCompleteCallBack != null && mCompleteCallBack.size() > 0) {
            for (Consumer<ResultSupport> runnable : mCompleteCallBack) {
                if (mIDispose.getResultSupport() == null) return;
                try {
                    runnable.accept(mIDispose.getResultSupport());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        recycle();
    }

    /**
     * 请求重复
     */
    public final void onRepeat(String tag) {
        mIDispose.onRepeat(tag);
        overCallback();
    }

    /**
     * 添加完成回调
     *
     * @param callback 回调
     * @return HttpResponse
     */
    public HttpResponse addCompleteCallBack(Consumer<ResultSupport> callback) {
        mCompleteCallBack.add(callback);
        return this;
    }

    /**
     * 设置默认结果处理类
     *
     * @param iDispose 结果处理接口
     * @return HttpResponse
     */
    public HttpResponse setIDispose(IDispose iDispose) {
        if (mIDispose != null) {
            try {
                mIDispose.recycle();
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
        mIDispose = iDispose;
        return this;
    }

    public IDispose getIDispose() {
        return mIDispose;
    }
}


package com.ec.www.base.http.response;

import android.widget.Toast;

import com.ec.www.BuildConfig;
import com.ec.www.base.ICallbackView;

/**
 * Created by huang on 2017/2/26.
 * http请求面向业务处理实现类
 */

public abstract class Dispose implements IDispose {

    private boolean isCheckLogin = true;
    private boolean isShowLoading = false;
    private ResultSupport mResultSupport;
    private ICallbackView mView;

    public final boolean isCheckLogin() {
        return isCheckLogin;
    }

    public final IDispose setCheckLogin(boolean checkLogin) {
        isCheckLogin = checkLogin;
        return this;
    }

    public final boolean isShowLoading() {
        return isShowLoading;
    }

    public final IDispose setShowLoading(boolean showLoading) {
        isShowLoading = showLoading;
        return this;
    }

    public final void set(ICallbackView view, int resultCode, int requestCode) {
        mView = view;
        mResultSupport = ResultSupport.obtain(resultCode, requestCode);
    }

    @Override
    public void recycle() {
        reset();
    }

    private void reset() {
        isCheckLogin = true;
        isShowLoading = false;
        mView = null;
        mResultSupport.recycle();
        mResultSupport = null;
    }

    @Override
    public boolean onPrePerform(Object o) {
        mView.hideAlert(isShowLoading);
        mResultSupport.setData(o);
        return false;
    }

    public static boolean isDefault(ResultSupport o) {
        return o != null && o.requestCode == DEFAULT;
    }

    @Override
    public void onPerformAfter(Object o) {
        if (mView.getStatus() < ICallbackView.STATE_PAUSE) {
            mView.onHttpResult(mResultSupport);
        }
    }

    @Override
    public void onPerError(Throwable e) {
        mView.hideAlert(isShowLoading);
        if (BuildConfig.DEBUG) {
            mView.showTip(e.toString(), Toast.LENGTH_LONG);
        }
        if (mView.getStatus() < ICallbackView.STATE_PAUSE) {
            if (mResultSupport.data != null) {
                mView.onHttpResult(mResultSupport);
            }
            mView.onHttpError(mResultSupport.resultCode, e);
        }
    }

    @Override
    public void onPerCompleted() {
        mView.hideAlert(isShowLoading);
    }

    @Override
    public void onPerStart() {
        mView.showAlert(isShowLoading);
    }

    @Override
    public void onRepeat(String tag) {
        if (BuildConfig.DEBUG) {
            mView.showTip("http请求重复, http方法:" + tag, Toast.LENGTH_LONG);
        }
        if (mView.getStatus() < ICallbackView.STATE_PAUSE) {
            if (mResultSupport.data != null) {
                mView.onHttpResult(mResultSupport);
            }
        }
    }

    public ResultSupport getResultSupport() {
        return mResultSupport;
    }

    public ICallbackView getView() {
        return mView;
    }

    public void setView(ICallbackView view) {
        mView = view;
    }
}

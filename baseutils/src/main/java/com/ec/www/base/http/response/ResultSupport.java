package com.ec.www.base.http.response;

import android.support.v4.util.Pools;

/**
 * Created by Administrator on 2016/10/12 0012.
 */

public class ResultSupport {

    public Object data;
    public int resultCode = IDispose.DEFAULT;
    public int requestCode = IDispose.DEFAULT;

    private static final Pools.SynchronizedPool<ResultSupport> sPool =
            new Pools.SynchronizedPool<>(10);

    public static ResultSupport obtain(Object o, int result, int request) {
        ResultSupport instance = sPool.acquire();
        if (instance == null) {
            instance = new ResultSupport(o, result, request);
        } else {
            instance.set(o, result, request);
        }
        return instance;
    }

    public static ResultSupport obtain(int result, int request) {
        ResultSupport instance = sPool.acquire();
        if (instance == null) {
            instance = new ResultSupport(result, request);
        } else {
            instance.set(result, request);
        }
        return instance;
    }


    public void recycle() {
        clear();
        try {
            sPool.release(this);
        } catch (IllegalStateException ignored) {
        }
    }

    private void clear() {
        resultCode = IDispose.DEFAULT;
        requestCode = IDispose.DEFAULT;
        data = null;
    }

    public ResultSupport(Object data) {
        this.data = data;
    }


    public ResultSupport(Object data, int result, int request) {
        this.data = data;
        resultCode = result;
        requestCode = request;
    }

    public ResultSupport(int result, int request) {
        resultCode = result;
        requestCode = request;
    }

    public ResultSupport set(Object o, int result, int request) {
        data = o;
        resultCode = result;
        requestCode = request;
        return this;
    }

    public ResultSupport set(int result, int request) {
        resultCode = result;
        requestCode = request;
        return this;
    }

    public ResultSupport setData(Object o) {
        data = o;
        return this;
    }

    public ResultSupport setRequestCode(int code) {
        requestCode = code;
        return this;
    }

    public ResultSupport setResultCode(int code) {
        resultCode = code;
        return this;
    }

    @Override
    public String toString() {
        return String.format("address: %s\nresultCode: %d\nrequestCode: %s", super.toString(), resultCode, requestCode);
    }
}

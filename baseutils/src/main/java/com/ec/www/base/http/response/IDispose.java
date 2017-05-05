package com.ec.www.base.http.response;

import com.ec.www.base.ICallbackView;

/**
 * Created by huang on 2016/12/23.
 * http请求回调接口
 */

public interface IDispose {

    int DEFAULT = -20000;

    void recycle();

    boolean onPrePerform(Object o);

    void onPerform(Object o);

    void onPerformAfter(Object o);

    void onPerError(Throwable e);

    void onPerCompleted();

    void onPerStart();

    void onRepeat(String tag);

    void set(ICallbackView view, int resultCode, int requestCode);

    ResultSupport getResultSupport();

}

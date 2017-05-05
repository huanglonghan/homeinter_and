package com.ec.www.base;

import com.ec.www.base.http.HttpResponse;
import com.ec.www.base.http.response.IDispose;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class AbstractPresenter<T extends AbstractModel<?, ?>> {

    protected ICallbackView mView;
    protected T mModel;

    public AbstractPresenter(ICallbackView view, T model) {
        mView = view;
        mModel = model;

    }

    protected final HttpResponse getResponse(int resultCode, int requestCode, IDispose dispose) {
        HttpResponse response = new HttpResponse();
        if (dispose == null) {
            defaultDecoration(response);
        } else {
            response.setIDispose(dispose);
        }
        response.getIDispose().set(mView, resultCode, requestCode);
        return response;
    }

    protected final HttpResponse getResponse(int resultCode, int requestCode) {
        return getResponse(resultCode, requestCode, null);
    }

    protected final HttpResponse getResponse(int resultCode, IDispose dispose) {
        return getResponse(resultCode, IDispose.DEFAULT, dispose);
    }

    protected final HttpResponse getResponse(int resultCode) {
        return getResponse(resultCode, IDispose.DEFAULT);
    }

    protected abstract void defaultDecoration(HttpResponse response);
}

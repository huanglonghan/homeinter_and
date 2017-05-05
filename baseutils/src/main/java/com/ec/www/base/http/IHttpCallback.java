package com.ec.www.base.http;

import com.ec.www.base.http.response.ResultSupport;

/**
 * Created by huang on 2017/4/20.
 */

public interface IHttpCallback {
    void onHttpResult(ResultSupport result);

    void onHttpError(int type, Throwable e);
}

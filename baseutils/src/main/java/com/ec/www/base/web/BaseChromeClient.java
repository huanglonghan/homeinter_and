package com.ec.www.base.web;

import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import io.reactivex.functions.BiConsumer;

/**
 * Created by huang on 2017/4/27.
 */

public class BaseChromeClient extends WebChromeClient {

    private BiConsumer<WebView, Integer> mProgressChanged;

    public BaseChromeClient setProgressChanged(BiConsumer<WebView, Integer> progressChanged) {
        mProgressChanged = progressChanged;
        return this;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        Log.d(url, message);
        return false;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if (mProgressChanged != null) {
            try {
                mProgressChanged.accept(view, newProgress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

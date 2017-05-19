package com.ec.www.base.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.ec.www.utils.CommonUtils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huang on 2017/3/21.
 */

public class BaseWebView extends WebView {

    private static String format;

    public BaseWebView(Context context) {
        super(context);
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings settings = getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setAppCacheEnabled(true);// 设置启动缓存
        settings.setSupportZoom(false);// 不支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //settings.setBlockNetworkImage(true);
        settings.setJavaScriptEnabled(true);
        setFocusable(false);

        //开启第三方cookie存储
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
    }

    public void loadData(String baseFileAssets, String data) {
        Observable.just(baseFileAssets)
                .map((str) -> {
                    if (format == null) {
                        format = CommonUtils.getAssetsFileAscii(getContext(), str);
                    }
                    if (data != null && !data.equals("null")) {
                        return String.format(format, data);
                    }
                    return null;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((str) -> {
                    if (str != null) {
                        loadDataWithBaseURL("file:///android_asset/",str, "text/html; charset=UTF-8", null, null);
                    }
                });
    }


}

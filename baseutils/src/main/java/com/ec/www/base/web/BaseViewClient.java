package com.ec.www.base.web;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ec.www.base.AbstractApplication;
import com.ec.www.base.NotProguard;

import java.util.ArrayList;
import java.util.Collections;

import io.reactivex.functions.BiConsumer;

/**
 * Created by huang on 2017/4/27.
 */

public class BaseViewClient extends WebViewClient {

    private BiConsumer<WebView, String> mPageFinished;
    private ArrayList<String> mUrls = new ArrayList<>();
    private static final String interfaceName = "DefaultInterface";

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    public BaseViewClient(WebView view) {
        view.addJavascriptInterface(this, interfaceName);
    }

    public BaseViewClient setPageFinished(BiConsumer<WebView, String> pageFinished) {
        mPageFinished = pageFinished;
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        //requestHandle(request.getUrl().toString());
        return true;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        //requestHandle(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mPageFinished != null) {
            try {
                mPageFinished.accept(view, url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        view.getSettings().setBlockNetworkImage(false);
        addDefaultListener(view);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        view.getSettings().setBlockNetworkImage(true);
    }

    /**
     * 增加图片和超链接点击监听
     *
     * @param view
     */
    public void addDefaultListener(WebView view) {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        // 如要点击一张图片在弹出的页面查看所有的图片集合,则获取的值应该是个图片数组
        view.loadUrl("javascript:(function(){" +
                "   var objs = document.getElementsByTagName(\"img\");" +
                "   if(objs.length <= 0) return;" +
                "   var arrayObj = new Array(); " +
                "   for(var i = 0; i < objs.length; i++)" +
                "   {" +
                //      "objs[i].onclick=function(){alert(this.getAttribute(\"src\"));}" +
                "       objs[i].onclick = function(){" +
                "            DefaultInterface.imageClickListener(this.getAttribute(\"src\"));" +
                "       }; " +
                "       arrayObj.push(objs[i].src);" +
                "   } " +
                "   if(arrayObj.length > 0)" +
                "       DefaultInterface.allImageUrls(arrayObj);" +
                "})()");

        // 遍历所有的a节点,将节点里的属性传递过去(属性自定义,用于页面跳转)
        view.loadUrl("javascript:(function(){" +
                "   var objs = document.getElementsByTagName(\"a\");" +
                "   if(objs.length <= 0) return;" +
                "   for(var i = 0; i < objs.length;i++)" +
                "   {" +
                "       if(objs[i].href.length <= 0 ) {" +
                "             objs[i].href = \"javascript:;\";" +
                "       }" +
                "       objs[i].onclick = function() {" +
                "             DefaultInterface.textLinkClickListener(this.getAttribute(\"href\"));" +
                "       };" +
                "   }" +
                "})()");
    }

    @NotProguard
    @JavascriptInterface
    public void allImageUrls(String[] urls) {
        if (mUrls.size() > 0) {
            mUrls.clear();
        }
        Collections.addAll(mUrls, urls);
    }

    @NotProguard
    @JavascriptInterface
    public void imageClickListener(String url) {
        if (mUrls.size() > 0)
            imageClick(mUrls, mUrls.indexOf(url));
    }

    @NotProguard
    @JavascriptInterface
    public void textLinkClickListener(String href) {
        if (href.contentEquals("javascript:;")) return;
        requestHandle(href);
    }

    private void requestHandle(String url) {
        // 优酷视频跳转浏览器播放
        if (url.startsWith("http://v.youku.com/")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.addCategory("android.intent.category.BROWSABLE");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            AbstractApplication.getContext().startActivity(intent);

            // 电话、短信、邮箱
        } else if (url.startsWith(WebView.SCHEME_TEL) || url.startsWith("sms:") || url.startsWith(WebView.SCHEME_MAILTO)) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                AbstractApplication.getContext().startActivity(intent);
            } catch (ActivityNotFoundException ignored) {
            }
        } else {
            textLinkClick(url);
        }
    }

    public void textLinkClick(String url) {
    }

    public void imageClick(ArrayList<String> urls, int index) {
    }

}

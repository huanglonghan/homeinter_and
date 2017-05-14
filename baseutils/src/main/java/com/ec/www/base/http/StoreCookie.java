package com.ec.www.base.http;

import android.util.Log;


import com.ec.www.BuildConfig;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * Created by huang on 2017/4/19.
 * cookie 持久化实现类
 */

public class StoreCookie implements CookieJar {

    private final ConcurrentHashMap<String, ArrayList<Cookie>> cookieStore;

    public StoreCookie(ConcurrentHashMap<String, ArrayList<Cookie>> cookieStore) {
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        if (!cookies.isEmpty()) {
            ArrayList<Cookie> oldCookie = cookieStore.get(url.host());
            if (oldCookie != null) {
                removeAll(cookies, oldCookie);
                oldCookie.addAll(cookies);
            } else {
                oldCookie = new ArrayList<>(cookies);
            }
            cookieStore.put(url.host(), oldCookie);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList<Cookie> cookies = cookieStore.get(url.host());
        return cookies != null ? cookies : new ArrayList<>();
    }

    private static void removeAll(List<Cookie> cookies, List<Cookie> oldCookies) {

        if (cookies == null || oldCookies == null) {
            return;
        }

        List<Cookie> delCookie = new ArrayList<>();
        if (cookies.isEmpty()) return;
        try {
            for (Cookie c : cookies) {
                if (c == null || c.name() == null) continue;
                for (Cookie oldC : oldCookies) {
                    if (oldC == null || oldC.name() == null) continue;
                    if (oldC.name().equals(c.name())) {
                        delCookie.add(oldC);
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d("", e.toString());
            }
        }

        try {
            oldCookies.removeAll(delCookie);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                Log.d("", e.toString());
            }
        }

    }
}

package com.ec.www.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ec.www.base.AbstractApplication;

/**
 * Created by huang on 2017/4/27.
 */

public class NetworkUtils {

    /**
     * 判断网络是否连通
     */
    public static boolean isNetworkConnected() {
        @SuppressWarnings("static-access")
        ConnectivityManager cm = (ConnectivityManager) AbstractApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isWifiConnected() {
        ConnectivityManager cm = (ConnectivityManager) AbstractApplication.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && (info.getType() == ConnectivityManager.TYPE_WIFI);
    }
}

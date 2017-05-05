package com.ec.www.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;


/**
 * Created by huang on 2016/12/29.
 */

public abstract class AbstractApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        CrashHandler crashHandler = setCrashHandler();
        if (crashHandler != null) Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    protected abstract CrashHandler setCrashHandler();

    public static Context getContext() {
        return AbstractApplication.mContext;
    }

}

package com.ec.www.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;


import com.ec.www.BuildConfig;
import com.ec.www.utils.CommonUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;


/**
 * 处理程序中未捕获的异常，将异常写入日志文件
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String TAG = CrashHandler.class.getSimpleName();

    private Consumer<String> mSaveExceptionCallback;

    public void setSaveExceptionCallback(Consumer<String> saveExceptionCallback) {
        mSaveExceptionCallback = saveExceptionCallback;
    }

    private static class CRASH_HANDLER {
        static {
            crashHandler = new CrashHandler();
        }

        private static final CrashHandler crashHandler;
    }

    private UncaughtExceptionHandler mDefaultHandler;

    private final Map<String, String> infos = new HashMap<>();

    private CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    public static CrashHandler getInstance() {
        return CRASH_HANDLER.crashHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        CommonUtils.printStackTrace(ex, BuildConfig.DEBUG);
        if (!handleException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e("error : ", e.getMessage());
            }
            System.exit(0);
        }
    }

    private boolean handleException(final Throwable ex) {

        // 如果是调试状态则不生成异常文件，让系统默认的异常处理器来处理
        if (BuildConfig.DEBUG || Debug.isDebuggerConnected())
            return false;
        if (ex == null)
            return false;
        // 收集设备参数信息
        collectDeviceInfo();

        //异常信息转换成字符串
        String exStr = exceptionInfo2String(ex);

        if (mSaveExceptionCallback != null) {
            try {
                mSaveExceptionCallback.accept(exStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 保存日志文件
            saveCrashInfo(exStr);
        }
        return true;
    }

    private void collectDeviceInfo() {
        Context ctx = AbstractApplication.getContext();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occurred when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                Log.e(TAG, "an error occurred when collect crash info", e);
            }
        }
    }

    private String exceptionInfo2String(final Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return sb.toString();
    }

    protected void saveCrashInfo(String exStr) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            String fileName = String.format("crash-%s.log", df.format(new Date(System.currentTimeMillis())));
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = "/sdcard/log/";
                File dir = new File(path);
                if (!dir.exists())
                    dir.mkdirs();
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(exStr.getBytes());
                fos.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
    }
}

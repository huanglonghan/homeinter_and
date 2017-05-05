package com.ec.www.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pools;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by huang on 2017/2/23.
 */

public class BroadcastUtil {

    private LocalBroadcastManager mBroadcastManager;
    private HashMap<String, MPBroadcastReceiver> mReceiverHashMap;
    private static HashMap<String, BroadcastUtil> utilsHashMap = new HashMap<>();
    private static String PackageTag = BroadcastUtil.class.getSimpleName();
    private static final Pools.SynchronizedPool<BroadcastUtil> sPool =
            new Pools.SynchronizedPool<>(15);

    private static BroadcastUtil obtain(Context context) {
        BroadcastUtil instance = sPool.acquire();
        if (instance == null) {
            instance = new BroadcastUtil();
        }
        instance.init(context);
        return instance;
    }

    private void recycle() {
        reset();
        sPool.release(this);
    }

    private void init(Context context) {
        if (PackageTag.equals(""))
            PackageTag = getClass().getName().toLowerCase().concat("_");
        mBroadcastManager = LocalBroadcastManager.getInstance(context);
        if (mReceiverHashMap == null)
            mReceiverHashMap = new HashMap<>();
    }

    private void reset() {
        mReceiverHashMap.clear();
        mBroadcastManager = null;
    }

    public static void sendBroadcast(Context context, Intent intent) {
        BroadcastUtil.obtain(context).innerRegisterSendBroadcast(intent).recycle();
    }

    public static void sendBroadcast(Context context, String action) {
        BroadcastUtil.obtain(context).innerRegisterSendBroadcast(action).recycle();
    }

    public static void sendBroadcastSync(Context context, Intent intent) {
        BroadcastUtil.obtain(context).innerRegisterSendBroadcastSync(intent).recycle();
    }

    public static void sendBroadcastSync(Context context, String action) {
        BroadcastUtil.obtain(context).innerRegisterSendBroadcastSync(action).recycle();
    }

    private BroadcastUtil innerRegisterSendBroadcast(Intent intent) {
        wrapIntent(intent);
        mBroadcastManager.sendBroadcast(intent);
        return this;
    }

    private BroadcastUtil innerRegisterSendBroadcast(String action) {
        return innerRegisterSendBroadcast(new Intent(action));
    }

    private BroadcastUtil innerRegisterSendBroadcastSync(String action) {
        return innerRegisterSendBroadcast(new Intent(action));
    }

    private BroadcastUtil innerRegisterSendBroadcastSync(Intent intent) {
        wrapIntent(intent);
        mBroadcastManager.sendBroadcastSync(intent);
        return this;
    }

    private void wrapIntent(Intent intent) {
        intent.setAction(PackageTag + intent.getAction());
    }

    public static void register(Context context, ReceiverCallback callback, String...action) {
        StringBuilder tag = new StringBuilder();
        String actions;
        for (String s: action) {
            actions = PackageTag + s;
            tag.append(actions);
        }
        utilsHashMap.put(tag.toString(), BroadcastUtil.obtain(context).innerRegister(callback, action));
    }

    public static void register(Context context, ReceiverCallback callback, String action) {
        utilsHashMap.put(action, BroadcastUtil.obtain(context).innerRegister(callback, action));
    }

    public static void unregister() {
        if (utilsHashMap.size() <= 0) return;
        for (Map.Entry<String, BroadcastUtil> entry : utilsHashMap.entrySet()) {
            entry.getValue().unRegister();
        }
        utilsHashMap.clear();
    }

    private BroadcastUtil innerRegister(ReceiverCallback callback, String action) {
        MPBroadcastReceiver receiver = MPBroadcastReceiver.obtain(callback);
        String tag = PackageTag + action;
        mBroadcastManager.registerReceiver(receiver, new IntentFilter(tag));
        mReceiverHashMap.put(action, receiver);
        return this;
    }

    private BroadcastUtil innerRegister(ReceiverCallback callback, String...action) {
        MPBroadcastReceiver receiver = MPBroadcastReceiver.obtain(callback);
        IntentFilter filter = new IntentFilter();
        StringBuilder tag = new StringBuilder();
        String actions;
        for (String s: action) {
            actions = PackageTag + s;
            tag.append(actions);
            filter.addAction(actions);
        }
        mBroadcastManager.registerReceiver(receiver, filter);
        mReceiverHashMap.put(tag.toString(), receiver);
        return this;
    }

    private void unRegister() {
        for (Map.Entry<String, MPBroadcastReceiver> entry : mReceiverHashMap.entrySet()) {
            mBroadcastManager.unregisterReceiver(entry.getValue());
            entry.getValue().recycle();
        }
        recycle();
    }

    private static class MPBroadcastReceiver extends BroadcastReceiver {

        private static final Pools.SynchronizedPool<MPBroadcastReceiver> sPool =
                new Pools.SynchronizedPool<>(15);

        private ReceiverCallback mCallback;

        public static MPBroadcastReceiver obtain(ReceiverCallback callback) {
            MPBroadcastReceiver instance = sPool.acquire();
            if (instance == null) {
                instance = new MPBroadcastReceiver(callback);
            } else {
                instance.reset(callback);
            }
            return instance;
        }

        public void recycle() {
            reset();
            sPool.release(this);
        }

        public MPBroadcastReceiver(ReceiverCallback callback) {
            mCallback = callback;
        }

        public void reset() {
            reset(null);
        }

        public void reset(ReceiverCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Observable.just(1)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe((i) -> {
                        if (mCallback != null) {
                            mCallback.onReceive(intent);
                        }
                    });
        }

    }

    public interface ReceiverCallback {
        void onReceive(Intent intent);
    }
}

package com.ec.www.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import io.reactivex.functions.Function;

/**
 * Created by huang on 2017/4/27.
 */

public class StartUtils {

    public static void startActivity(Context context, Function<Intent, Intent> callback) {
        Intent intent = genIntent(callback);
        context.startActivity(intent);
    }

    public static void startActivityHenson(Context context, Function<Context, Intent> callback) {
        Intent intent = genIntent(context, callback);
        context.startActivity(intent);
    }

    public static void startActivityForResultHenson(Fragment fragment, int requestCode, Function<Context, Intent> callback) {
        Intent intent = genIntent(fragment.getContext(), callback);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResultHenson(Activity activity, int requestCode, Function<Context, Intent> callback) {
        Intent intent = genIntent(activity, callback);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivityForResult(Fragment fragment, int requestCode, Function<Intent, Intent> callback) {
        Intent intent = genIntent(callback);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Activity activity, int requestCode, Function<Intent, Intent> callback) {
        Intent intent = genIntent(callback);
        activity.startActivityForResult(intent, requestCode);
    }

    public static Intent genIntent(Function<Intent, Intent> callback) {
        Intent intent = new Intent();
        if (callback != null) {
            try {
                intent = callback.apply(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return intent;
    }

    public static Intent genIntent(Context context, Function<Context, Intent> callback) {
        Intent intent = new Intent();
        if (callback != null) {
            try {
                intent = callback.apply(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return intent;
    }
}

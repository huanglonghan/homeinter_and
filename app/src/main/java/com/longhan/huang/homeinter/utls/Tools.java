package com.longhan.huang.homeinter.utls;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.longhan.huang.homeinter.service.LocationService;

import java.util.ArrayList;

/**
 * Created by 龙汗 on 2016/3/9.
 */
public class Tools {

    /**
     * is the once launch
     */
    public static final String ISONCELAUNCH = "isOnceLaunch";

    public static boolean isOnceLaunch(Context context) {
        SharedPreferences sharedPreferences =
                context.getSharedPreferences(LocationService.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(ISONCELAUNCH, true);
    }

    public static boolean getServerState(Context Context, String serviceName) {
        ActivityManager activityManager = (ActivityManager) Context.getSystemService(android.content.Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> serviceList = (ArrayList<ActivityManager.RunningServiceInfo>) activityManager.getRunningServices(100);
        int size = serviceList.size();
        for (int i = 0; i < size; i++) {
            String str = serviceList.get(i).service.getClassName();
            if (str.equals(serviceName)) {
                return true;
            }
        }
        return false;
    }

}

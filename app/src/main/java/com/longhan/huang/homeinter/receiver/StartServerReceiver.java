package com.longhan.huang.homeinter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.longhan.huang.homeinter.service.LocationService;
import com.longhan.huang.homeinter.service.MonitorService;
import com.longhan.huang.homeinter.utls.Tools;

public class StartServerReceiver extends BroadcastReceiver {
    public StartServerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Tools.getServerState(context, LocationService.HomeInterServicePackName)){
            LocationService.startConnectAndLocationServer(context);
        }

        if(!Tools.getServerState(context, MonitorService.MonitorServicePackName)){
            context.startService(new Intent(context, MonitorService.class));
        }
    }
}

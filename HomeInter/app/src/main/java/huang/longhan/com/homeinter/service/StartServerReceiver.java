package huang.longhan.com.homeinter.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import huang.longhan.com.homeinter.utls.Tools;

public class StartServerReceiver extends BroadcastReceiver {
    public StartServerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Tools.getServerState(context, HomeInterService.HomeInterServicePackName)){
            HomeInterService.startConnectAndLocationServer(context);
        }

        if(!Tools.getServerState(context,MonitorService.MonitorServicePackName)){
            context.startService(new Intent(context, MonitorService.class));
        }
    }
}

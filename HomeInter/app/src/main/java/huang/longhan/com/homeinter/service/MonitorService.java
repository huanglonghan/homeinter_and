package huang.longhan.com.homeinter.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import huang.longhan.com.homeinter.utls.Tools;

public class MonitorService extends Service {

    Thread mMonitorService;
    boolean mMonitorFlag;
    public static final String MonitorServicePackName = "huang.longhan.com.homeinter.service.MonitorService";


    public MonitorService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mMonitorFlag = true;
        if (mMonitorService == null) {
            mMonitorService = new Thread("MonitorService"){
                @Override
                public void run() {
                    while (mMonitorFlag){
                        try {
                            Thread.sleep(1000*10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Context context = getApplicationContext();
                        if(!Tools.getServerState(context,HomeInterService.HomeInterServicePackName)){
                            context.startService(new Intent(context, HomeInterService.class));
                        }
                    }
                }
            };
        }
        mMonitorService.start();
    }

    @Override
    public void onDestroy() {
        mMonitorFlag = false;
        super.onDestroy();
    }
}

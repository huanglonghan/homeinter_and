package com.longhan.huang.homeinter.ui.acticity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

import com.longhan.huang.homeinter.R;
import com.longhan.huang.homeinter.service.LocationService;
import com.longhan.huang.homeinter.service.MonitorService;
import com.longhan.huang.homeinter.utls.Tools;
import rx.Observable;
import rx.Subscription;

public class SplashActivity extends Activity {

    //@Bind(R.id.splash_view)
    // ImageView splashView;
    Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //ButterKnife.bind(this);
        init();
    }

    protected void init() {
//        Glide.with(this)
//                .load(R.drawable.splash_view)
//                .crossFade()
//                 .into(splashView);

        if (!Tools.getServerState(this, LocationService.HomeInterServicePackName)) {
            startService(new Intent(this, LocationService.class));
        }

        if (!Tools.getServerState(this, MonitorService.MonitorServicePackName)) {
            startService(new Intent(this, MonitorService.class));
        }

        subscription = Observable.timer(1, TimeUnit.SECONDS).subscribe(i -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        // ButterKnife.unbind(this);
     }
}

package huang.longhan.com.homeinter;

import android.Manifest;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

public class HomeInterMapActivity extends FragmentActivity implements HomeInterMapFragment.OnStopServiceListeners {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeinter_map_main);

        if (!getNetworkState()) {

        }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,new String[]{    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_LOCATION_EXTRA_COMMANDS"},123);
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.homeinter_main_fragment, new HomeInterMapFragment(), "MapFragment").commit();

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStopService(ServiceConnection serviceConnection) {
//        if (getServerState()) {
//            unbindService(serviceConnection);
//            stopService(new Intent(this, HomeInterService.class));
//        } else {
//            HomeInterService.startConnectAndLocationServer(getApplicationContext(), mTCode, mUid, mNickName);
//            bindService(new Intent(this, HomeInterService.class), serviceConnection, BIND_AUTO_CREATE);
//        }
    }

    public boolean getNetworkState() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

    }

}

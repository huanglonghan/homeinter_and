package huang.longhan.com.homeinter.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import huang.longhan.com.homeinter.HomeInterHandle;
import huang.longhan.com.homeinter.HomeInterMapActivity;
import huang.longhan.com.homeinter.R;
import huang.longhan.com.homeinter.UserMetaData;
import huang.longhan.com.homeinter.config.Config;
import huang.longhan.com.homeinter.utls.ErrorCode;
import huang.longhan.com.homeinter.utls.Tools;

public class HomeInterService extends Service implements AMapLocationListener {

    public volatile boolean isBeginService = true;
    public static final int CONNECT_SERVICE_INTERVAL = 1000;
    private Thread homeInterServiceThread;
    private HomeInterHandle handler;
    private int homeinter_foreground = 0x00af;
    public static final String HomeInterServicePackName = "huang.longhan.com.homeinter.service.HomeInterService";

    public String getLocationProviderName() {
        return locationProviderName;
    }

    private String locationProviderName = "gps";

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;


    private BaseTCPCommunication baseTCPCommunication;
    long interval = 1000 * 15;

    private final HashMap<String, UserMetaData> userMetaDatas = new HashMap<String, UserMetaData>();

    private String mUid;
    Thread mInitUserInfo;
    private String mNickName;
    private String mTCode;
    public static final String PREFERENCE_NAME = "HomeInter.xml";
    private static final String PRE_UID = "uid";
    private static final String PRE_TCODE = "tCode";
    public static final String PRE_NICKNAME = "nickName";

    private ServerStatus mServiceStatus;
    Criteria criteria;



    public ServerStatus getServiceStatus() {
        return mServiceStatus;
    }


    public String getNickName() {
        return mNickName;
    }

    public String getTCode() {
        return mTCode;
    }


    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }

    public void setTCode(String mTCode) {
        this.mTCode = mTCode;
    }

    public void setNickName(String mNickName) {
        this.mNickName = mNickName;
    }

    public class HomeInterBinder extends Binder {
        public UserMetaData getUserData(String key) {
            return getUserMetaDatas(key);
        }

        public Set<String> getUserList() {
            return HomeInterService.this.getUserList();
        }

        public ServerStatus getServiceStatus() {
            return HomeInterService.this.getServiceStatus();
        }

        public boolean sendMsg(String msg){
            if (baseTCPCommunication!=null){
                baseTCPCommunication.sendMsg(msg);
                return true;
            }
            return false;
        }

        public String getUid(){
           return HomeInterService.this.getUid();
        }
        public String getNickname(){
            return HomeInterService.this.getNickName();
        }
        public void setNickname(String nickname){
                synchronized (userMetaDatas) {
                    UserMetaData userData;
                        userData = userMetaDatas.get(mUid);
                        userData.setNickName(nickname);
                    }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    public UserMetaData getUserMetaDatas(String name) {
        UserMetaData userMetaData;
        synchronized(userMetaDatas) {
            userMetaData = userMetaDatas.get(name);
        }
        return userMetaData;
    }

    public void setUserMetaDatas(String name,String uid, Location location) {
        synchronized (userMetaDatas) {
            UserMetaData userData;
            if (!userMetaDatas.containsKey(name)) {
                userData = new UserMetaData(location);
                if (userData.getUid()==null){
                    userData.setUid(uid);
                }
                String nickName = userData.getNickName();
                if(nickName==null||nickName.equals("")||nickName.equals("正在获取昵称..")){
                    userData.setNickName(getUserNickName(uid));
                }
                userMetaDatas.put(name, userData);
            } else {
                userData = userMetaDatas.get(uid);
                userData.setLocation(location);
            }
            if (baseTCPCommunication != null && name.equals(getUid())) {
                double lon = location.getLongitude();
                double lat = location.getLatitude();
//            double alt = location.getAltitude();
//            float speed = location.getSpeed();
//            float bearing = location.getBearing();
                float accuracy = location.getAccuracy();
                baseTCPCommunication.sendMsg(BaseTCPProtocol.heartMsg(lon, lat,/* alt, speed, bearing,*/ accuracy));
                mServiceStatus = ServerStatus.connectAndLocationSuccess;
            }
        }
    }

    public void setUserMetaDatas(String name, String uid, String tCode, String nickName) {
        synchronized (userMetaDatas) {
            UserMetaData userData;
            if (!userMetaDatas.containsKey(name)) {
                userData = new UserMetaData();
                userData.setNickName(nickName);
                userData.setUid(uid);
                userData.setTCode(tCode);
                userMetaDatas.put(name, userData);
            } else {
                userData = userMetaDatas.get(uid);
                userData.setNickName(nickName);
                userData.setUid(uid);
                userData.setTCode(tCode);
            }
        }
    }


    private String getUserNickName(String uid){
        String nickName = "正在获取昵称..";
        BaseHTTPCommunication baseHTTPCommunication = BaseHTTPCommunication.createConnect(Config.httpServiceUrl);
        if (baseHTTPCommunication == null) {
            return nickName;
        }
        String msg = baseHTTPCommunication.sendMsg(BaseHTTPProtocol.getUserNickNameMsg(uid));
        if (msg == null) {
            return nickName;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return nickName;
        }
        int errorCode = jsonObject.optInt("errorCode", -1);
        if (errorCode == ErrorCode.SUCCESS) {
            JSONObject result = jsonObject.optJSONObject("result");
            if (result != null) {
                 return result.optString("nickname");
            }
        }
        return nickName;
    }

    public void removeUserMetaDatas(LinkedList<String> uIds){
        synchronized (userMetaDatas){
            Set<String> keys =userMetaDatas.keySet();
            for (String key:keys){
                if (!uIds.contains(key)&&!key.equals(getUid())){
                    userMetaDatas.remove(key);
                }
            }
        }
    }

    public boolean userDataContainsKey(String name) {
        boolean isContain;
        synchronized(userMetaDatas){
            isContain = userMetaDatas.containsKey(name);
        }
        return isContain;
    }

    public Set<String> getUserList() {
        Set<String> keys;
        synchronized(userMetaDatas) {
            keys=userMetaDatas.keySet();
        }
        return keys;
    }


    public HomeInterService() {
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startConnectAndLocationServer(Context context) {
        Intent intent = new Intent(context, HomeInterService.class);
        context.startService(intent);
    }

    public boolean isBeginService() {
        return isBeginService;
    }

    public void setIsBeginService(boolean isBeginService) {
        this.isBeginService = isBeginService;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new HomeInterBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public enum ServerStatus{
        initUserDataAfter,initUserDataBefore,initLocationAfter,initLocationBefore,initConnectAfter,
        initConnectBefore,locationSuccess,connectAndLocationSuccess
    }


    @Override
    public void onCreate() {

        Notification.Builder builder = new Notification.Builder(this);
        PendingIntent contentIntent =
                PendingIntent.getActivity(this,0,
                        new Intent(this,
                                HomeInterMapActivity.class),0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setTicker("HomeInter 正在后台运行");
        builder.setContentText("HomeInter 正在后台运行");
        builder.setContentTitle("HomeInter");
        startForeground(homeinter_foreground, builder.getNotification());

        if (mInitUserInfo == null) {
            mInitUserInfo = new Thread(new Runnable() {
                @Override
                public void run() {
                    mServiceStatus = ServerStatus.initUserDataBefore;
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    mUid = sharedPreferences.getString(PRE_UID, null);
                    if (mUid == null || mUid.equals("")) {
                        String deviceID = ((TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                        BaseHTTPCommunication baseHTTPCommunication = BaseHTTPCommunication.createConnect(Config.httpServiceUrl);
                        if (baseHTTPCommunication == null) {
                            return;
                        }
                        String msg = baseHTTPCommunication.sendMsg(BaseHTTPProtocol.connectMsg(deviceID));
                        if (msg == null) {
                            return;
                        }
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (jsonObject == null) {
                            return;
                        }
                        int errorCode = jsonObject.optInt("errorCode", -1);
                        if (errorCode == ErrorCode.SUCCESS) {
                            JSONObject result = jsonObject.optJSONObject("result");
                            if (result != null) {
                                mTCode = result.optString("tCode");
                                mUid = result.optString("uid");
                                mNickName = result.optString("nickName");
                                editor.putString(PRE_TCODE, mTCode);
                                editor.putString(PRE_UID, mUid);
                                editor.putString(PRE_NICKNAME, mNickName);
                                editor.apply();
                            }
                        }
                    } else {
                        mUid = sharedPreferences.getString(PRE_UID, null);
                        mTCode = sharedPreferences.getString(PRE_TCODE, null);
                        mNickName = sharedPreferences.getString(PRE_NICKNAME, null);
                    }
                    mServiceStatus = ServerStatus.initUserDataAfter;
                }
            }, "InitUserInfo");
        }

        mInitUserInfo.start();

        if (homeInterServiceThread == null) {
            homeInterServiceThread = new Thread("HomeInterService") {
                @Override
                public void run() {
                    Looper.prepare();
                    try {
                        mInitUserInfo.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mServiceStatus = ServerStatus.initLocationBefore;
                    //初始化定位
                    mLocationClient = new AMapLocationClient(getApplicationContext());
                    //设置定位回调监听
                    mLocationClient.setLocationListener(HomeInterService.this);

                    //初始化定位参数
                    mLocationOption = new AMapLocationClientOption();
                    //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
                    mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
                    //设置是否返回地址信息（默认返回地址信息）
                    mLocationOption.setNeedAddress(false);
                    //设置是否只定位一次,默认为false
                    mLocationOption.setOnceLocation(false);
                    //设置是否强制刷新WIFI，默认为强制刷新
                    mLocationOption.setWifiActiveScan(true);
                    //设置是否允许模拟位置,默认为false，不允许模拟位置
                    mLocationOption.setMockEnable(false);
                    //设置定位间隔,单位毫秒,默认为2000ms
                    mLocationOption.setInterval(interval);
                    //给定位客户端对象设置定位参数
                    mLocationClient.setLocationOption(mLocationOption);
                    //启动定位
                    mLocationClient.startLocation();
                    mServiceStatus = ServerStatus.initLocationAfter;

                    handler = new HomeInterHandle(HomeInterService.this);
                    mServiceStatus = ServerStatus.initConnectBefore;
                    boolean isSocketInitiated = true;
                    do {
                        if (!isSocketInitiated) {
                            try {
                                Thread.sleep(CONNECT_SERVICE_INTERVAL);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        baseTCPCommunication = BaseTCPCommunication.createConnect(handler);
                        if (baseTCPCommunication == null) {
                            isSocketInitiated = false;
                        } else {
                            handler.setTcpConnect(baseTCPCommunication);
                            isSocketInitiated = true;
                        }

                    } while (!isSocketInitiated && isBeginService);
                    mServiceStatus = ServerStatus.initConnectAfter;
                    baseTCPCommunication.sendMsg(BaseTCPProtocol.connectMsg(getTCode(), getUid()));
                    setUserMetaDatas(getUid(), getUid(), getTCode(), getNickName());
                    Looper.loop();
                }
            };
            homeInterServiceThread.start();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        Location location = new Location(aMapLocation.getProvider());
        location.setAccuracy(aMapLocation.getAccuracy());
        location.setLatitude(aMapLocation.getLatitude());
        location.setLongitude(aMapLocation.getLongitude());
        setUserMetaDatas(getUid(), getUid(), location);
    }

    @Override
    public void onDestroy() {
        isBeginService = false;
        mLocationClient.onDestroy();
        if (baseTCPCommunication != null) {
            baseTCPCommunication.closeSocket();
        }
        if (handler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                handler.getLooper().quitSafely();
            } else {
                handler.getLooper().quit();
            }
        }
        stopForeground(true);
        Context context = getApplicationContext();
        if(!Tools.getServerState(context, HomeInterService.HomeInterServicePackName)){
            context.startService(new Intent(context, HomeInterService.class));
        }

        HomeInterService.startConnectAndLocationServer(context);
        if(!Tools.getServerState(context,MonitorService.MonitorServicePackName)){
            context.startService(new Intent(context, MonitorService.class));
        }
        super.onDestroy();
    }
}

package com.longhan.huang.homeinter.ui.fragment;

import android.app.ActionBar;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.longhan.huang.homeinter.R;
import com.longhan.huang.homeinter.UserMetaData;
import com.longhan.huang.homeinter.component.UserDetailPageAdapter;
import com.longhan.huang.homeinter.ui.acticity.MainActivity;
import com.longhan.huang.homeinter.utls.connect.TCPInterface;
import com.longhan.huang.homeinter.service.LocationService;
import com.longhan.huang.homeinter.service.MonitorService;
import com.longhan.huang.homeinter.utls.Tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by 龙汗 on 2016/2/15.
 */
public class MapFragment extends BaseMapFragment implements AMap.OnMarkerClickListener, DrawerLayout.DrawerListener {

    PopupWindow mPopupWindow;
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Runnable mRefreshChangeLocation;
    Runnable mRefreshChangeTip;
    OnStopServiceListeners mStopServiceListeners;
    public ServiceConnection serviceConnection;
    @Bind(R.id.showAll)
    ImageButton showAll;
    Handler handler;

    @Bind(R.id.showStatus)
    TextView showStatus;

    @Bind(R.id.showSelf)
    ImageButton showSelf;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;


    private LocationService.HomeInterBinder mHomeInterBinder;
    public HashMap<String, LocationInfo> locationInfoMap = new HashMap<>();
    Boolean mChangeLocationFlag = true;
    Boolean mChangeTipFlag = true;

    boolean mIsInitShow;
    String mUid;
    String mNickname;
    SidebarFragment sidebarFragment;
    private LayoutInflater mInflater;


    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle bundle = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getContext());
        Context context = getContext();
        if (!Tools.getServerState(context, LocationService.HomeInterServicePackName)) {
            context.startService(new Intent(context, LocationService.class));
        }

        LocationService.startConnectAndLocationServer(context);
        if (!Tools.getServerState(context, MonitorService.MonitorServicePackName)) {
            context.startService(new Intent(context, MonitorService.class));
        }

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mHomeInterBinder = (LocationService.HomeInterBinder) service;
                if (mUid == null) {
                    mUid = mHomeInterBinder.getUid();
                    mNickname = mHomeInterBinder.getNickname();
                    sidebarFragment.setNickName(mUid, mNickname);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);
        FragmentManager childFragmentManager = getChildFragmentManager();
        FragmentTransaction transition = childFragmentManager.beginTransaction();
        sidebarFragment = new SidebarFragment();
        transition.add(R.id.map_fragment_sidebar, sidebarFragment).commit();

        mDrawerLayout.addDrawerListener(this);
        mDrawerLayout.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && MainActivity.isFirstOpen == 0) {
                mDrawerLayout.openDrawer(Gravity.LEFT);
                MainActivity.isFirstOpen++;
            }
        });
        showAll.setEnabled(false);
        showSelf.setEnabled(false);
        mMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        handler = new Handler();
        if (mRefreshChangeTip == null) {
            mRefreshChangeTip = new Runnable() {
                @Override
                public void run() {
                    final String[] statusText = {""};
                    while (mChangeTipFlag) {
                        if (mHomeInterBinder != null) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    switch (mHomeInterBinder.getServiceStatus()) {
                                        case initUserDataBefore:
                                            statusText[0] = "正在登录..";
                                            break;
                                        case initUserDataAfter:
                                            statusText[0] = "登陆完成..";
                                            break;
                                        case initLocationBefore:
                                            statusText[0] = "正在准备定位..";
                                            break;
                                        case initLocationAfter:
                                            statusText[0] = "准备定位完成..";
                                            break;
                                        case initConnectBefore:
                                            statusText[0] = "正在连接服务器..";
                                            break;
                                        case initConnectAfter:
                                            statusText[0] = "连接服务器完成..";
                                            break;
                                        case locationSuccess:
                                            statusText[0] = "定位成功..";
                                            break;
                                        case connectAndLocationSuccess:
                                            statusText[0] = "全部准备完成,即将显示..";
                                            showStatus.setText(statusText[0]);
                                            mChangeTipFlag = false;
                                            break;
                                    }
                                    showStatus.setText(statusText[0]);
                                }
                            });
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
        }

        if (mHomeInterBinder == null) {
            executorService.execute(mRefreshChangeTip);
        }


        if (mRefreshChangeLocation == null) {
            mRefreshChangeLocation = new Runnable() {
                @Override
                public void run() {

                    mIsInitShow = true;
                    while (mChangeLocationFlag) {
                        while (mHomeInterBinder == null || mHomeInterBinder.getServiceStatus() != LocationService.ServerStatus.connectAndLocationSuccess) {
                            if (mHomeInterBinder == null) {
                                getContext().bindService(new Intent(getContext(), LocationService.class), serviceConnection, Service.BIND_AUTO_CREATE);
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        final Set<String> uidList = mHomeInterBinder.getUserList();
                        final int count = uidList.size();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (showStatus != null) {
                                    showStatus.setText(String.format("当前在线人数:%d(人)", count));
                                }
                            }
                        });
                        if (count <= 0) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (showAll != null && showSelf != null) {
                                        showAll.setEnabled(false);
                                        showSelf.setEnabled(false);
                                    }
                                }
                            });
                            break;
                        } else if (count > 1) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (showAll != null && showSelf != null) {
                                        showAll.setEnabled(true);
                                        showSelf.setEnabled(true);
                                    }
                                }
                            });
                        } else if (count == 1) {
                            handler.post(() -> {
                                if (showAll != null && showSelf != null) {
                                    showAll.setEnabled(false);
                                    showSelf.setEnabled(true);
                                }
                            });
                        }
                        for (final String uid : uidList) {
                            handler.post(() -> onChangeLocationListener(mHomeInterBinder.getUserData(uid)));
                        }
                        if (mIsInitShow) {
                            try {
                                Thread.sleep(2 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    initShowSelfMarker();
                                }
                            });
                            mIsInitShow = false;
                        }
                        mHomeInterBinder.sendMsg(TCPInterface.updateMsg());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            };
        }

        executorService.execute(mRefreshChangeLocation);
    }


    @Override
    public void onPause() {
        super.onPause();
        mChangeLocationFlag = false;
//        mChangeTipFlag = false;

        if (mHomeInterBinder != null) {
            mHomeInterBinder.sendMsg(TCPInterface.clearInvalidUserMsg());
            Set<String> uidList = mHomeInterBinder.getUserList();
            Set<String> keys = locationInfoMap.keySet();
            for (String key : keys) {
                if (!uidList.contains(key) && !key.equals(mUid)) {
                    locationInfoMap.remove(key);
                }
            }


        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsInitShow = true;
//        if (!mChangeTipFlag) {
//            mChangeTipFlag = true;
//            executorService.execute(mRefreshChangeTip);
//        }

        if (!mChangeLocationFlag) {
            mChangeLocationFlag = true;
            executorService.execute(mRefreshChangeLocation);
        }

    }


    protected void initShowAllMarker() {
        if (locationInfoMap.size() <= 0) {
            return;
        }
        LatLngBounds.Builder builder = LatLngBounds.builder();
        Collection<LocationInfo> locationInfos = locationInfoMap.values();
        for (LocationInfo locationInfo : locationInfos) {
            builder.include(locationInfo.mMarker.getPosition());
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
    }

    protected void initShowSelfMarker() {
        if (locationInfoMap.size() <= 0 && mUid != null) {
            return;
        }
        showDetailWindow(locationInfoMap.get(mUid).mMarker);
    }

    protected void onChangeLocationListener(UserMetaData userMetaData) {
        String uid = userMetaData.getUid();
        Location location = userMetaData.getLocation();
        String nickName = userMetaData.getNickName();
        if (!locationInfoMap.containsKey(uid)) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.draggable(false);
            markerOptions.anchor(0.5F, 0.5F);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));
            markerOptions.title(nickName);

            CircleOptions circleOptions = new CircleOptions();
            circleOptions.fillColor(Color.argb(100, 0, 0, 180));
            circleOptions.strokeColor(Color.BLUE);
            circleOptions.strokeWidth(1.0f);
            Marker marker = mMap.addMarker(markerOptions);
            locationInfoMap.put(uid, new LocationInfo(marker, mMap.addCircle(circleOptions)));
        }
        LatLng latLng = userMetaData.getLatLon();
        float accuracy = location.getAccuracy();
        Circle circle = locationInfoMap.get(uid).mCircle;
        circle.setRadius(accuracy);
        circle.setCenter(latLng);
        Marker marker = locationInfoMap.get(uid).mMarker;
        marker.setPosition(latLng);
        if (!marker.getTitle().equals(nickName)) {
            marker.setTitle(nickName);
        }
        marker.setSnippet(Long.toString(location.getTime()));
    }

    @Override
    public void onDestroyView() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        mChangeLocationFlag = false;
        mChangeTipFlag = false;
        executorService.shutdown();
        getActivity().unbindService(serviceConnection);
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    @OnClick({R.id.showAll, R.id.showSelf})
    public void OnClick(View v) {
        int Id = v.getId();
        switch (Id) {
            case R.id.showSelf:
                initShowSelfMarker();
                break;
            case R.id.showAll:
                initShowAllMarker();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SidebarFragment.UPDATE_NICKNAME) {
            if (mHomeInterBinder != null) {
                String nickname = data.getStringExtra(LocationService.PRE_NICKNAME);
                mHomeInterBinder.setNickname(nickname);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showDetailWindow(Marker marker) {
        View view = mInflater.inflate(R.layout.user_detail_viewpager, null);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.detail_viewpager);
        TabLayout tabView = (TabLayout) view.findViewById(R.id.tab_view);
        Collection<LocationInfo> locationInfos = locationInfoMap.values();
        final LinkedList<Marker> userData = new LinkedList<>();
        for (LocationInfo locationInfo : locationInfos) {
            userData.add(locationInfo.mMarker);
        }
        UserDetailPageAdapter userDetailPageAdapter = new UserDetailPageAdapter(getContext(), R.layout.user_detail_info, userData);
        viewPager.setAdapter(userDetailPageAdapter);
        final Handler handler = new Handler();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition(userData.get(position).getPosition(), 15, 0, 0)));
                    }
                }, 400);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabView.setupWithViewPager(viewPager);
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(view);
        }
        mPopupWindow.setContentView(view);
        mPopupWindow.setWidth(ActionBar.LayoutParams.MATCH_PARENT);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mPopupWindow.setHeight(displayMetrics.heightPixels / 6);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAtLocation(getView(), Gravity.BOTTOM, 0, 0);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        showDetailWindow(marker);
        return false;
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        View content = mDrawerLayout.getChildAt(0);
        drawerView.setTranslationX(drawerView.getMeasuredWidth() * (1 - slideOffset));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawerView.setZ(0.5f);
            content.setZ(1.0f);
        }
        content.setTranslationX(drawerView.getWidth() * slideOffset);
        content.setPivotX(0);
        content.setScaleX(0.8f + 0.2F * (1 - slideOffset));
        content.setScaleY(0.8f + 0.2F * (1 - slideOffset));
    }

    @Override
    public void onDrawerOpened(View drawerView) {
    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    public interface OnStopServiceListeners {
        void onStopService(ServiceConnection serviceConnection);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStopServiceListeners) {
            mStopServiceListeners = (OnStopServiceListeners) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStopServiceListeners");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mStopServiceListeners = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

class LocationInfo {
    Marker mMarker;
    Circle mCircle;

    public LocationInfo(Marker marker, Circle circle) {
        mMarker = marker;
        mCircle = circle;
    }
}

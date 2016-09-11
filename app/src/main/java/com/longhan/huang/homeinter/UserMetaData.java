package com.longhan.huang.homeinter;

import android.location.Location;

import com.amap.api.maps2d.model.LatLng;

/**
 * Created by 龙汗 on 2016/2/2.
 */
public class UserMetaData {

    private Location mLocation = new Location("lbs");
    private boolean mIsSelf =false;
    private String mNickName;
    private String mUid;
    private String mTCode;

    public LatLng getLatLon(){
        return new LatLng(mLocation.getLatitude(),mLocation.getLongitude());
    }

    public String getUid() {
        return mUid;
    }

    public void setUid(String mUid) {
        this.mUid = mUid;
    }



    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        this.mNickName = nickName;
    }

    public String getTCode() {
        return mTCode;
    }

    public void setTCode(String tCode) {
        this.mTCode = tCode;
    }


    public boolean getIsSelf() {
        return mIsSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.mIsSelf = isSelf;
    }

    public Location getLocation() {
        return mLocation;
    }

    public void setLocation(Location location) {
        this.mLocation.set(location);
    }

    public UserMetaData(Location location) {
        this.mLocation.set(location);
    }

    public UserMetaData() {

    }

}

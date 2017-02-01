package com.longhan.huang.homeinter.component;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.longhan.huang.homeinter.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

/**
 * Created by 龙汗 on 2016/3/19.
 */
public class UserDetailPageAdapter extends PagerAdapter {

    private HashMap<Integer, View> mViews = new HashMap<>();
    private LayoutInflater mInflater;
    private LinkedList<Marker> mUserData;
    private int mResId;

    public UserDetailPageAdapter(Context context, int resId, LinkedList<Marker> userData) {
        mInflater = LayoutInflater.from(context);
        mResId = resId;
        mUserData = userData;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mUserData.get(position).getTitle();
    }

    @Override
    public int getCount() {
        return mUserData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mInflater.inflate(mResId, null);
        TextView latlon = (TextView) view.findViewById(R.id.latlon);
        TextView time = (TextView) view.findViewById(R.id.time);
        Marker marker = mUserData.get(position);
        LatLng latLng = marker.getPosition();
        latlon.setText(String.format("经度:%s | 纬度:%s", Double.toString(latLng.latitude), Double.toString(latLng.longitude)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        time.setText(String.format("最近一次位置时间:%s", simpleDateFormat.format(new Date(Long.parseLong(marker.getSnippet())))));
        container.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mViews.put(position, view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViews.get(position));
        mViews.remove(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

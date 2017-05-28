package com.ec.www.base;

import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huang on 2017/4/27.
 */

public class FragmentHelper implements IStatus {

    private AbstractFragment mFragment;
    private Unbinder mUnbinder;

    protected RequestManager mGlide;
    private int curStatus = STATE_NORMAL;

    public FragmentHelper(AbstractFragment fragment) {
        mFragment = fragment;
    }

    public void onCreate() {
        mGlide = Glide.with(mFragment);
    }

    public void bindButterKnife(View view) {
        if (mUnbinder == null) {
            mUnbinder = ButterKnife.bind(mFragment, view);
        }
    }

    protected void initPrepare() {
        curStatus = STATE_CREATE;
        if (!EventBus.getDefault().isRegistered(mFragment)) {
            EventBus.getDefault().register(mFragment);
        }
    }

    public void onDestroy() {
        curStatus = STATE_DESTROY;
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (mGlide != null) {
            mGlide.onDestroy();
        }
        if (EventBus.getDefault().isRegistered(mFragment)) {
            EventBus.getDefault().unregister(mFragment);
        }

        mFragment = null;
        mUnbinder = null;
        mGlide = null;
    }

    public void onStop() {
        curStatus = STATE_STOP;
        if (mGlide != null) {
            mGlide.onStop();
        }
    }

    public void onStart() {
        curStatus = STATE_START;
        if (mGlide != null) {
            mGlide.onStart();
        }
    }

    public void onResume() {
        curStatus = STATE_RESUME;
    }

    public void onPause() {
        curStatus = STATE_PAUSE;
    }

    public void onLowMemory() {
        if (mGlide != null) {
            mGlide.onLowMemory();
        }
    }

    public RequestManager getGlide() {
        return mGlide;
    }

    @Override
    public int getStatus() {
        return curStatus;
    }
}

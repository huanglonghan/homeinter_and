package com.ec.www.base;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.anthony.ultimateswipetool.SwipeHelper;
import com.anthony.ultimateswipetool.activity.SwipeBackLayout;
import com.anthony.ultimateswipetool.activity.interfaces.SwipeBackActivityBase;
import com.bumptech.glide.RequestManager;
import com.ec.www.R;
import com.ec.www.view.LoadingFragment;

import org.greenrobot.eventbus.Subscribe;

import io.reactivex.functions.Function;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class AbstractActivity extends AppCompatActivity implements SwipeBackActivityBase {

    private SwipeHelper mSwipeHelper;
    private ActivityHelper mActivityHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        onCreateBefore();
        super.onCreate(savedInstanceState);
        mSwipeHelper = new SwipeHelper(this);
        mSwipeHelper.onActivityCreate();
        mActivityHelper = new ActivityHelper(this);
        mActivityHelper.onCreate();
        onInflateBefore();
        initContentView();
    }

    /**
     * 初始化内容视图
     */
    private void initContentView() {
        int resId = getContentLayoutId();
        if (resId == 0) return;
        setContentView(resId);
        mActivityHelper.bindButterKnife(getWindow().getDecorView());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeHelper.onPostCreate();
        mActivityHelper.onPostCreate();
    }

    /**
     * onCreate创建之前
     */
    protected void onCreateBefore() {
        //填充iconfont
//        LayoutInflaterCompat.setFactory(getLayoutInflater(),
//                new IconFontLayoutFactory(this, getDelegate()));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//            window.setExitTransition(new Slide());
//            window.setReturnTransition(new Slide());
//            window.setEnterTransition(new Explode());
//            window.setReenterTransition(new Slide());
//        }
    }

    /**
     * 布局填充之前
     */
    protected void onInflateBefore() {
    }

    protected abstract int getContentLayoutId();

    @Override
    public final View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mSwipeHelper != null)
            return mSwipeHelper.findViewById(id);
        return v;
    }

    @Override
    public final SwipeBackLayout getSwipeBackLayout() {
        return mSwipeHelper.getSwipeBackLayout();
    }

    @Override
    public final void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public final void scrollToFinishActivity() {
        SwipeHelper.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    @Override
    public final void setScrollDirection(int edgeFlags) {
        //SwipeBackLayout.EDGE_ALL| EDGE_LEFT | EDGE_RIGHT | EDGE_BOTTOM | EDGE_TOP
        getSwipeBackLayout().setEdgeTrackingEnabled(edgeFlags);
    }

    @Override
    protected void onDestroy() {
        mActivityHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        mActivityHelper.onStop();
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mActivityHelper.onStart();
    }

    @Override
    public void onLowMemory() {
        mActivityHelper.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        mActivityHelper.onTrimMemory(level);
        super.onTrimMemory(level);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivityHelper.onResume();
    }

    @Override
    protected void onPause() {
        mActivityHelper.onPause();
        super.onPause();
    }

    @Subscribe
    public void msgHandle(String msg) {

    }

    @Subscribe
    public void msgHandle(ActivityHelper.DialogMsg msg) {
        switch (msg.type) {
            case ActivityHelper.DialogMsg.HIDE_DIALOG:
                hideAlert(msg.isShow);
                break;
            case ActivityHelper.DialogMsg.SHOW_DIALOG:
                showAlert(msg.isShow, msg.mClass);
                break;
        }
    }

    public final void changeFragment(@IdRes int id, String tag, Class<? extends AbstractFragment> tClass) {
        changeFragment(id, tag, tClass, true);
    }

    public final void changeFragment(@IdRes int id, String tag, Class<? extends AbstractFragment> tClass, boolean isAddStackBack) {
        mActivityHelper.changeFragment(id, tag, tClass, isAddStackBack);

    }

    public final void changeFragment(@IdRes int id, String tag, Function<Object, AbstractFragment> callBack) {
        changeFragment(id, tag, callBack, true);
    }

    public final void changeFragment(@IdRes int id, String tag, Function<Object, AbstractFragment> callBack, boolean isAddStackBack) {
        mActivityHelper.changeFragment(id, tag, callBack, isAddStackBack);
    }

    public final <T> void postMsg(T t) {
        mActivityHelper.postMsg(t);
    }

    public final <T> void postStickyMsg(T t) {
        mActivityHelper.postStickyMsg(t);
    }

    /**
     * 显示加载非模态等候
     */
    public final void showAlert(boolean isShow) {
        showAlert(isShow, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public final void showAlert() {
        showAlert(true, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public final <T extends DialogFragment> void showAlert(boolean isShow, Class<T> fragment) {
        mActivityHelper.showAlert(isShow, fragment);
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert(boolean isShow) {
        mActivityHelper.hideAlert(isShow);
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert() {
        hideAlert(true);
    }

    /**
     * 退出
     */
    public final void exit() {
        mActivityHelper.exit();
    }

    /**
     * 双击退出
     */
    protected final void doubleExit() {
        mActivityHelper.doubleExit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    public Fragment getCurrentFragment() {
        return mActivityHelper.getCurrentFragment();
    }

    public void setCurrentFragment(Fragment current) {
        mActivityHelper.setCurrentFragment(current);
    }

    public RequestManager getGlide() {
        return mActivityHelper.getGlide();
    }

}

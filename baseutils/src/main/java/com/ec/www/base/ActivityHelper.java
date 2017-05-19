package com.ec.www.base;

import android.os.Build;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ec.www.R;
import com.f2prateek.dart.Dart;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Function;

/**
 * Created by huang on 2017/4/27.
 */

public class ActivityHelper implements IStatus {

    public static final String FRAGMENT_TAG_DIALOG = "fragment_tag_dialog";
    private static boolean isExit = false;
    protected Unbinder mUnbinder;
    protected FragmentManager mFragmentManager;
    protected Fragment mCurrentFragment;
    protected RequestManager mGlide;
    private DialogFragment mDialogFragment;
    private AbstractActivity mActivity;
    private int curStatus = STATE_NORMAL;

    public ActivityHelper(AbstractActivity activity) {
        mActivity = activity;
    }

    public final void bindButterKnife(View view) {
        mUnbinder = ButterKnife.bind(mActivity, view);
    }

    protected void onCreate() {
        Dart.inject(mActivity);
        mFragmentManager = mActivity.getSupportFragmentManager();
        mGlide = Glide.with(mActivity);
        ActivityManager.getInstance().add(mActivity);
    }

    public void onPostCreate() {
        if (!EventBus.getDefault().isRegistered(mActivity)) {
            EventBus.getDefault().register(mActivity);
        }
        curStatus = STATE_CREATE;
    }

    public void onStart() {
        if (mGlide != null)
            mGlide.onStart();
        curStatus = STATE_START;
    }

    public void onResume() {
        curStatus = STATE_RESUME;
    }

    public void onPause() {
        curStatus = STATE_PAUSE;
    }

    public void onStop() {
        if (mGlide != null)
            mGlide.onStop();
        curStatus = STATE_STOP;
    }

    public void onDestroy() {
        ActivityManager.getInstance().remove(mActivity);
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (mGlide != null) {
            mGlide.onDestroy();
            mGlide = null;
        }
        if (EventBus.getDefault().isRegistered(mActivity)) {
            EventBus.getDefault().unregister(mActivity);
        }
        curStatus = STATE_DESTROY;
        mActivity = null;
    }

    public void onLowMemory() {
        if (mGlide != null)
            mGlide.onLowMemory();
    }

    public void onTrimMemory(int level) {
        if (mGlide != null)
            mGlide.onTrimMemory(level);
    }

    @Override
    public int getStatus() {
        return curStatus;
    }

    public final <T> void postMsg(T t) {
        EventBus.getDefault().post(t);
    }

    public final <T> void postStickyMsg(T t) {
        EventBus.getDefault().postSticky(t);
    }

    public final void changeFragment(@IdRes int id, String tag, Class<? extends AbstractFragment> tClass) {
        changeFragment(id, tag, tClass, true);
    }

    public final void changeFragment(@IdRes int id, String tag, Class<? extends AbstractFragment> tClass, boolean isAddStackBack) {
        changeFragment(id, tag, (o) -> {
            AbstractFragment t = null;
            try {
                t = tClass.newInstance();
            } catch (Exception ignored) {
                throw new RuntimeException("changeFragment(): create fragment error!");
            }
            return t;
        }, isAddStackBack);

    }

    public final void changeFragment(@IdRes int id, String tag, Function<Object, AbstractFragment> callBack) {
        changeFragment(id, tag, callBack, true);
    }

    public final void changeFragment(@IdRes int id, String tag, Function<Object, AbstractFragment> callBack, boolean isAddStackBack) {
        AbstractFragment t = (AbstractFragment) mFragmentManager.findFragmentByTag(tag);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        if (mCurrentFragment != null && mCurrentFragment != t) {
            ft.hide(mCurrentFragment);
        }
        if (isAddStackBack) {
            ft.addToBackStack(null);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (t != null) {
            if (!t.isVisible()) {
                ft.show(t);
            }
        } else {
            try {
                t = callBack.apply(1);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            ft.add(id, t, tag);
        }
        ft.commitAllowingStateLoss();
        mCurrentFragment = t;
    }

    public Fragment getCurrentFragment() {
        return mCurrentFragment;
    }

    public void setCurrentFragment(Fragment current) {
        mCurrentFragment = current;
    }

    /**
     * 退出
     */
    public final void exit() {
        ActivityManager.getInstance().remove(mActivity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.finishAfterTransition(mActivity);
        } else {
            mActivity.finish();
        }
    }

    /**
     * 双击退出
     */
    protected final void doubleExit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(mActivity, R.string.double_click_exit_tip,
                    Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> isExit = false, 2000);
        } else {
            mActivity.exit();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exit();
                return true;
            default:
                return false;
        }
    }

    public RequestManager getGlide() {
        return mGlide;
    }

    /**
     * 显示加载非模态等候
     */
    public final <T extends DialogFragment> void showAlert(boolean isShow, Class<T> fragment) {
        if (!isShow || !(curStatus == STATE_RESUME)) {
            return;
        }

        if (mDialogFragment == null || !(mDialogFragment.getClass() == fragment)) {
            try {
                mDialogFragment = fragment.newInstance();
            } catch (Exception e) {
                return;
            }
        }

        if (mFragmentManager == null) {
            return;
        }
        if (!mDialogFragment.isVisible()) {
            mDialogFragment.show(mFragmentManager, ActivityHelper.FRAGMENT_TAG_DIALOG);
        }
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert(boolean isShow) {
        if (!isShow) {
            return;
        }
        if (mDialogFragment != null) {
            mDialogFragment.dismissAllowingStateLoss();
        }
    }

    public final static class DialogMsg<T extends DialogFragment> {

        public static final int SHOW_DIALOG = 0x01;
        public static final int HIDE_DIALOG = 0x02;

        int type;
        boolean isShow;
        Class<T> mClass;

        DialogMsg(boolean isShow, int type) {
            this.isShow = isShow;
            this.type = type;
        }

        DialogMsg(boolean isShow, int type, Class<T> aClass) {
            this.isShow = isShow;
            mClass = aClass;
            this.type = type;
        }
    }
}

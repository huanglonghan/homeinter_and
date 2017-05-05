package com.ec.www.base;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.ec.www.utils.StatusBarHelper;
import com.ec.www.view.LoadingFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by huang on 2017/3/7.
 */

public abstract class AbstractDialogFragment extends DialogFragment implements ICallbackView, IStatus {

    private Unbinder mUnbinder;
    protected RequestManager mGlide;
    private onDismissListener mListener;
    private int gravity;
    private int width = WindowManager.LayoutParams.WRAP_CONTENT;
    private int height = WindowManager.LayoutParams.WRAP_CONTENT;
    private boolean isPopupInput;
    private boolean isGravity;
    private boolean isDimAmount = true;
    private BuildOnClickListener mBuildOnClickListener;
    private int curStatus = STATE_NORMAL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mGlide = Glide.with(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getContentLayoutId();
        switch (resId) {
            case 0:
                return null;
            default:
                View view = inflater.inflate(resId, container, false);
                bindButterKnife(view);
                initWindow();
                onInitViewsAndEvents(view);
                initPrepare(savedInstanceState);
                return view;
        }
    }

    private void initWindow() {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
            StatusBarHelper.setStatusBarColor(window, Color.WHITE);
            WindowManager.LayoutParams windowParams = window.getAttributes();
            if (isDimAmount) {
                windowParams.dimAmount = 0.3f;
            } else {
                windowParams.dimAmount = 0.0f;
            }
            windowParams.height = height;
            windowParams.width = width;
            if (isGravity)
                windowParams.gravity = gravity;
            if (isPopupInput)
                windowParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
            window.setAttributes(windowParams);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    protected void initPrepare(@Nullable Bundle savedInstanceState) {

    }

    protected abstract int getContentLayoutId();

    protected void onInitViewsAndEvents(View view) {
        if (mBuildOnClickListener != null)
            mBuildOnClickListener.buildOnClickListener(view);
    }

    public void setPopupSoftInput() {
        isPopupInput = true;
    }

    public void showAtLocation(FragmentManager manager, String tag, int gravity) {
        setLocation(gravity);
        show(manager, tag);
    }

    public void showAtLocation(FragmentTransaction manager, String tag, int gravity) {
        setLocation(gravity);
        show(manager, tag);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    private void setLocation(int gravity) {
        isGravity = true;
        this.gravity = gravity;
    }

    private void bindButterKnife(View view) {
        if (mUnbinder == null)
            mUnbinder = ButterKnife.bind(this, view);
    }

    public AbstractDialogFragment setBuildOnClickListener(BuildOnClickListener listener) {
        mBuildOnClickListener = listener;
        return this;
    }

    public AbstractDialogFragment setDismissListener(onDismissListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public void onDestroyView() {
        if (mListener != null) {
            mListener.onDismiss(getView());
        }
        super.onDestroyView();
    }

    @Subscribe
    public void msgHandle(String msg) {

    }

    @Override
    public void onDestroy() {
        onDestroyAnything();
        super.onDestroy();
    }

    public void onDestroyAnything() {
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if (mGlide != null) {
            mGlide.onDestroy();
            mGlide = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        curStatus = STATE_DESTROY;
    }

    @Override
    public void onStop() {
        if (mGlide != null)
            mGlide.onStop();
        curStatus =STATE_STOP;
        super.onStop();
    }

    @Override
    public void onStart() {
        if (mGlide != null)
            mGlide.onStart();
        curStatus =STATE_START;
        super.onStart();
    }

    @Override
    public void onLowMemory() {
        if (mGlide != null)
            mGlide.onLowMemory();
        super.onLowMemory();
    }

    /**
     * 显示提示
     *
     * @param msg  提示的消息
     * @param view 如果为空则自动获取activity的view
     */
    public void showTip(String msg, View view) {
        if (view != null)
            //Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).show();
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示提示
     *
     * @param msg   提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    public final void showTip(String msg, int delay) {
        View view = getView();
        if (view != null)
            //Snackbar.make(view, msg, delay).show();
            Toast.makeText(getContext(), msg, delay).show();
    }

    /**
     * 显示提示
     *
     * @param msg 提示的消息
     */
    public final void showTip(String msg) {
        View view = getView();
        if (view != null)
            showTip(msg, view);
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param view  如果为空则自动获取activity的view
     */
    public final void showTip(@StringRes int resId, View view) {
        if (view != null) {
            String msg = getContext().getResources().getString(resId);
            showTip(msg, view);
        }
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    public final void showTip(@StringRes int resId, int delay) {
        View view = getView();
        if (view != null) {
            String msg = getContext().getResources().getString(resId);
            showTip(msg, delay);
        }
    }

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     */
    public final void showTip(@StringRes int resId) {
        View view = getView();
        if (view != null)
            showTip(resId, view);
    }

    /**
     * 显示加载非模态等候
     */
    public void showAlert(boolean isShow) {
        showAlert(isShow, LoadingFragment.class);
    }

    /**
     * 显示加载非模态等候
     */
    public <T extends DialogFragment> void showAlert(boolean isShow, Class<T> fragment) {
        postMsg(new ActivityHelper.DialogMsg<>(isShow,
                ActivityHelper.DialogMsg.SHOW_DIALOG,
                fragment));
    }

    /**
     * 隐藏加载非模态等候
     */
    public final void hideAlert(boolean isShow) {
        postMsg(new ActivityHelper.DialogMsg<>(isShow, ActivityHelper.DialogMsg.HIDE_DIALOG));
    }

    /**
     * 被子类选择性实现
     */
    public void setDelaySendMsg() {
    }

    public final <T> void postMsg(T t) {
        EventBus.getDefault().post(t);
    }

    public final <T> void postStickyMsg(T t) {
        EventBus.getDefault().postSticky(t);
    }


    @Override
    public final String getResourceString(@StringRes int id) {
        return getResources().getString(id);
    }

    @Override
    public void onHttpError(int type, Throwable e) {

    }

    @Override
    public int getStatus() {
        return curStatus;
    }

    public void setDimAmount(boolean dimAmount) {
        isDimAmount = dimAmount;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    public interface BuildOnClickListener {
        void buildOnClickListener(View view);
    }

    public interface onDismissListener {
        void onDismiss(View view);
    }

}

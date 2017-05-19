package com.ec.www.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.ec.www.base.http.response.ResultSupport;
import com.ec.www.view.LoadingFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public abstract class AbstractFragment<M> extends Fragment implements ICallbackView, IStatus {

    private View mView;
    private FragmentHelper mHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mHelper = new FragmentHelper(this);
        mHelper.onCreate();
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = onBuildVIew(inflater, container, savedInstanceState);
            if (mView ==null) {
                throw new RuntimeException("must be inflater view");
            }
            onInitViewsAndEvents(mView);
            initPrepare(savedInstanceState);
        }
        return mView;
    }

    public View onBuildVIew(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int resId = getContentLayoutId();
        switch (resId) {
            case 0:
                return null;
            default:
                mView = inflater.inflate(resId, container, false);
                mHelper.bindButterKnife(mView);
                return mView;
        }
    }

    /**
     * 初始化准备 只会调用一次
     *
     * @param savedInstanceState 保存状态
     */
    protected void initPrepare(@Nullable Bundle savedInstanceState) {
        mHelper.initPrepare();
    }

    protected abstract int getContentLayoutId();

    /**
     * 初始化视图创建
     *
     * @param view
     */
    protected void onInitViewsAndEvents(View view) {

    }

    protected final void exit() {
        AbstractActivity activity = (AbstractActivity) getActivity();
        activity.exit();
    }

    @Override
    public void onDestroy() {
        mHelper.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        mHelper.onStop();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mHelper.onResume();
    }

    @Override
    public void onPause() {
        mHelper.onPause();
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        mHelper.onStart();
    }

    @Override
    public void onLowMemory() {
        mHelper.onLowMemory();
        super.onLowMemory();
    }

    @Subscribe
    public void msgHandle(String msg) {

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
    public void showAlert() {
        showAlert(true, LoadingFragment.class);
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
     * 隐藏加载非模态等候
     */
    public final void hideAlert() {
        postMsg(new ActivityHelper.DialogMsg<>(true, ActivityHelper.DialogMsg.HIDE_DIALOG));
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

    public final String getResourceString(@StringRes int id) {
        return getResources().getString(id);
    }

    @Override
    public final void onHttpResult(ResultSupport result) {
        onHttpResult(result, (M) result.data);
    }

    public abstract void onHttpResult(ResultSupport result, M data);

    @Override
    public void onHttpError(int requestCode, Throwable e) {

    }

    public RequestManager getGlide() {
        return mHelper.getGlide();
    }

    @Override
    public int getStatus() {
        return mHelper.getStatus();
    }
}

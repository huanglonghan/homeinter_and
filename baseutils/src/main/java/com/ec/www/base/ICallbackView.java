package com.ec.www.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.ec.www.base.http.IHttpCallback;

/**
 * Created by Administrator on 2016/10/13 0013.
 */

public interface ICallbackView extends IHttpCallback, IStatus {

    /**
     * 显示加载非模态等候
     */
    <T extends DialogFragment> void showAlert(boolean isShow, Class<T> dialogFragment);

    /**
     * 显示加载非模态等候
     */
    void showAlert(boolean isShow);

    /**
     * 隐藏加载非模态等候
     */
    void hideAlert(boolean isShow);

    /**
     * 被子类选择性实现
     */
    void setDelaySendMsg();

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    void showTip(@StringRes int resId, int delay);

    /**
     * 显示提示
     *
     * @param msg   提示的消息
     * @param delay 显示的时长(Snackbar内部常量)
     */
    void showTip(String msg, int delay);

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     * @param view  如果为空则自动获取activity的view
     */
    void showTip(@StringRes int resId, View view);

    /**
     * 显示提示
     *
     * @param msg  提示的消息
     * @param view 如果为空则自动获取activity的view
     */
    void showTip(String msg, View view);

    /**
     * 显示提示
     *
     * @param resId 提示的消息
     */
    void showTip(@StringRes int resId);

    /**
     * 显示提示
     *
     * @param msg 提示的消息
     */
    void showTip(String msg);

    <T> void postMsg(T t);

    <T> void postStickyMsg(T t);

    String getResourceString(@StringRes int id);

    Context getContext();

}

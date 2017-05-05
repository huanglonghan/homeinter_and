package com.ec.www.base;

import android.app.Service;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.bumptech.glide.RequestManager;
import com.ec.www.R;
import com.ec.www.base.http.response.ResultSupport;
import com.ec.www.utils.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by huang on 2016/12/22.
 */

public class BasePopupWindow<M> extends PopupWindow implements ICallbackView {

    protected AbstractFragment mFragment;
    protected RequestManager mGlide;
    private OnDismissListener mListener;
    private boolean isFullScreen;

    @Override
    public <T extends DialogFragment> void showAlert(boolean isShow, Class<T> dialogFragment) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showAlert(isShow, dialogFragment);
    }

    @Override
    public void showAlert(boolean isShow) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showAlert(isShow);
    }

    @Override
    public void hideAlert(boolean isShow) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.hideAlert(isShow);
    }

    @Override
    public void setDelaySendMsg() {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.setDelaySendMsg();
    }

    @Override
    public void showTip(@StringRes int resId, int delay) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId, delay);
    }

    @Override
    public void showTip(String msg, int delay) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg, delay);
    }

    @Override
    public void showTip(@StringRes int resId, View view) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId, view);
    }

    @Override
    public void showTip(String msg, View view) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg, view);
    }

    @Override
    public void showTip(@StringRes int resId) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(resId);
    }

    @Override
    public void showTip(String msg) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.showTip(msg);
    }

    @Override
    public <T> void postMsg(T t) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.postMsg(t);
    }

    @Override
    public <T> void postStickyMsg(T t) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        mFragment.postStickyMsg(t);
    }

    @Override
    public String getResourceString(@StringRes int id) {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        return mFragment.getResourceString(id);
    }

    @Override
    public Context getContext() {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        return mFragment.getContext();
    }

    @Override
    public int getStatus() {
        if (mFragment == null)
            throw new RuntimeException("use error getInstant!");
        return mFragment.getStatus();
    }

    @Override
    public final void onHttpResult(ResultSupport result) {
        onHttpResult(result, (M) result.data);
    }

    public void onHttpResult(ResultSupport result, M data) {
    }

    @Override
    public void onHttpError(int type, Throwable e) {

    }

    public void setPopupSoftInput(boolean popupSoftInput) {
        if (popupSoftInput)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    public BasePopupWindow(View view, RequestManager glide) {
        this(view, null, glide);
    }

    public BasePopupWindow(View view, AbstractFragment fragment, RequestManager glide) {
        super(view, MATCH_PARENT, MATCH_PARENT);
        mFragment = fragment;
        mGlide = glide;
        init(view);
    }

    private void init(View view) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        ButterKnife.bind(this, view);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(CommonUtils.getColor(getContext(), R.color.popup_background_color)));
        view.setOnClickListener(v -> {
            BasePopupWindow.this.dismiss();
        });
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            TransitionInflater inflater = TransitionInflater.from(getContentView().getContext());
//            setEnterTransition(inflater.inflateTransition(android.R.transition.slide_bottom));
//            setExitTransition(inflater.inflateTransition(android.R.transition.slide_bottom));
//        }

    }

    @Subscribe
    public void msgHandel(String msg) {

    }

    public static BasePopupWindow getInstant(AbstractFragment fragment, @LayoutRes int resId, RequestManager glide) {
        return new BasePopupWindow(getView(fragment.getContext(), resId), fragment, glide);
    }

    public static BasePopupWindow getInstant(AbstractActivity content, @LayoutRes int resId, RequestManager glide) {
        return new BasePopupWindow(getView(content, resId), glide);
    }

    protected static View getView(Context content, @LayoutRes int resId) {
        return getView(content, resId, MATCH_PARENT, WRAP_CONTENT);
    }

    protected static View getView(Context content, @LayoutRes int resId, int width, int height) {
        RelativeLayout relativeLayout = new RelativeLayout(content);
        ViewGroup.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        relativeLayout.setLayoutParams(params);
        View view = LayoutInflater.from(content).inflate(resId, null, false);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(width, height);
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        view.setLayoutParams(relativeLayoutParams);
        relativeLayout.addView(view);
        return relativeLayout;
    }

    public BasePopupWindow setBuildOnClickListener(BuildOnClickListener listener) {
        listener.buildOnClickListener(getContentView());
        return this;
    }

    public BasePopupWindow showAtLocation(View parent, int gravity) {
        showAtLocation(parent, gravity, 0, 0);
        return this;
    }

    protected void closeAnimator(View view, Runnable mEndRunnable) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_slide_out);
        //view.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mEndRunnable.run();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animation);
    }

    protected void openAnimator(View view) {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.bottom_slide_in);
        //view.setAnimation(animation);
        view.startAnimation(animation);
    }

    private boolean isDismissComplete;
    private Runnable mEndRunnable = () -> {
        isDismissComplete = false;
//        if (mFragment != null) {
//            Window window = mFragment.getActivity().getWindow();
//            if (isFullScreen) {
//                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
//        }
        BasePopupWindow.super.dismiss();
    };

    @Override
    public void dismiss() {
        if (!isDismissComplete) {
            View view = ((ViewGroup) getContentView()).getChildAt(0);
            if (view != null) {
                closeAnimator(view, mEndRunnable);
            }

            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getContentView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            isDismissComplete = true;
        }
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        getContentView().setVisibility(View.INVISIBLE);
        View view = ((ViewGroup) getContentView()).getChildAt(0);
        if (view != null) {
            view.post(() -> {
                getContentView().setVisibility(View.VISIBLE);
                openAnimator(view);
            });
        }

//        if (mFragment != null) {
//            Window window = mFragment.getActivity().getWindow();
//            isFullScreen = CommonUtils.hasFlag(window, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            if (isFullScreen) {
//                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            }
//        }
        setOnDismissListener(() -> {
            if (mListener != null) {
                mListener.onDismiss();
            }
        });

    }

    public BasePopupWindow setDismissListener(OnDismissListener listener) {
        mListener = listener;
        return this;
    }

    public interface BuildOnClickListener {
        void buildOnClickListener(View view);
    }

}

package com.ec.www.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.ec.www.R;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * 封装了4种状态的View：正在加载、加载失败、没有数据、正常界面
 *
 * @author dzl
 */
public class StateLayout extends FrameLayout {

    private View loadingView;
    private View failView;
    private View emptyView;
    private View contentView;

    private View.OnClickListener mOnClickListener;
    /**
     * 包含了3种状态(正在加载、加载失败、没有数据) 的一个容器
     */
    private FrameLayout container;

    public StateLayout(Context context) {
        super(context);
        init();
    }

    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public StateLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        if (isInEditMode()) return;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (container == null) {

            View view = getChildAt(0);
            if (view != null) {
                setContentView(view);
            }

            // 创建出来的容器已经包含了3种状态：正在加载、加载失败、没有数据
            container = (FrameLayout) View.inflate(this.getContext(), R.layout.state_layout, null);
            container.setClickable(true);

            // 查找出3种状态对应的View
            loadingView = container.findViewById(R.id.loadingView);
            failView = container.findViewById(R.id.failView);
            emptyView = container.findViewById(R.id.emptyView);

            showLoadingView();
            this.addView(container);
        }
    }

    /**
     * 显示“正在加载。。。”
     */
    public void showLoadingView() {
        showStateView(loadingView);
    }

    /**
     * 显示“加载失败”
     */
    public void showFailView() {
        showStateView(failView);
    }

    /**
     * 显示“加载为空”
     */
    public void showEmptyView() {
        showStateView(emptyView);
    }

    /**
     * 显示“正常加载到数据”的界面的View
     */
    public void showContentView() {
        hideAnimate(container);
    }

    /**
     * 设置“正常状态的View”
     */
    public void setContentView(int layoutResId) {
        View contentView = View.inflate(getContext(), layoutResId, null);
        this.setContentView(contentView);
    }

    /**
     * 设置“正常状态的View”
     */
    public void setContentView(View contentView) {
        this.contentView = contentView;
        //contentView.setVisibility(View.INVISIBLE);   // 默认不显示，默认显示的是LoadingView

    }

    public void setOnReloadListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    /**
     * 指示指定的View，并隐藏其它的View
     */
    private void showStateView(View view) {
        container.setVisibility(VISIBLE);
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            child.setVisibility(child == view ? View.VISIBLE : View.GONE);
        }
        if (view == failView) {
            container.setOnClickListener(mOnClickListener);
        }
    }

    private void hideAnimate(View view) {
        Observable.just(view)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe((panel) -> {
                    if (panel != null && panel.getVisibility() != View.INVISIBLE) {
                        final ViewPropertyAnimatorCompat animation = ViewCompat.animate(panel);
                        animation.setDuration(250)
                                .alpha(0)
                                .setInterpolator(new AccelerateInterpolator())
                                .setListener(new ViewPropertyAnimatorListener() {
                                    @Override
                                    public void onAnimationStart(View view) {

                                    }

                                    @Override
                                    public void onAnimationEnd(View view) {
                                        animation.setListener(null);
                                        ViewCompat.setAlpha(view, 0);
                                        view.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onAnimationCancel(View view) {

                                    }
                                })
                                .start();
                    }
                });
    }

    public interface StateChangeImp {
        /**
         * 显示“正在加载。。。”
         */
        void showLoadingView();

        /**
         * 显示“加载失败”
         */
        void showFailView();

        /**
         * 显示“加载为空”
         */
        void showEmptyView();

        /**
         * 显示“正常加载到数据”的界面的View
         */
        void showContentView();

    }

}
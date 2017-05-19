package com.ec.www.view.swiperecycler;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ec.www.R;


/**
 * Created by huang on 2017/1/6.
 */

public class HeaderView extends LinearLayout{

    public final static int STATE_NORMAL = 0;
    public final static int STATE_RELEASE_TO_REFRESH = 1;
    public final static int STATE_REFRESHING = 2;
    public final static int STATE_DONE = 3;

    private Context mContext;
    private AnimationDrawable animationDrawable;
    private TextView msg;
    private ImageView loading;
    private ImageView arrow;
    private int mState = STATE_NORMAL;
    private int mMeasuredHeight;
    private LinearLayout mContainer;
    private RotateAnimation mUpAnim;
    private RotateAnimation mDownAnim;
    private boolean isRelease = false;

    public HeaderView(Context context) {
        this(context, null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
        initAnimation();
    }

    private void initAnimation() {
        mUpAnim = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mUpAnim.setDuration(150);
        mUpAnim.setFillAfter(true);

        mDownAnim = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mDownAnim.setDuration(150);
        mDownAnim.setFillAfter(true);
    }


    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.header_view, this);
        loading = (ImageView) findViewById(R.id.iv_loading);
        arrow = (ImageView) findViewById(R.id.iv_arrow);
        animationDrawable = (AnimationDrawable) loading.getDrawable();
        if (animationDrawable.isRunning()) {
            animationDrawable.stop();
        }
        msg = (TextView) findViewById(R.id.tv_status);
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMeasuredHeight = getMeasuredHeight();
        setGravity(Gravity.CENTER_HORIZONTAL);
        mContainer = (LinearLayout) findViewById(R.id.container);
        mContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0));
        this.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }


    public void onMove(float delta) {
        if (getVisibleHeight() > 0 || delta > 0) {
            setVisibleHeight((int) delta + getVisibleHeight());
            if (mState <= STATE_RELEASE_TO_REFRESH) { // 未处于刷新状态，更新箭头
                if (getVisibleHeight() > mMeasuredHeight) {
                    setState(STATE_RELEASE_TO_REFRESH);
                } else {
                    setState(STATE_NORMAL);
                }
            }
        }
    }


    private void setState(int state) {
        if (state == mState) return;
        switch (state) {
            case STATE_NORMAL:
                arrow.setVisibility(View.VISIBLE);
                if (isRelease) arrow.startAnimation(mDownAnim);
                loading.setVisibility(GONE);
                msg.setText(R.string.pull_refresh);
                break;
            case STATE_RELEASE_TO_REFRESH:
                isRelease = true;
                arrow.setVisibility(View.VISIBLE);
                arrow.startAnimation(mUpAnim);
                msg.setText(R.string.release_refresh);
                break;
            case STATE_REFRESHING:
                isRelease = false;
                arrow.clearAnimation();
                arrow.setVisibility(INVISIBLE);
                loading.setVisibility(VISIBLE);
                if (!animationDrawable.isRunning()) {
                    animationDrawable.start();
                }
                msg.setText(R.string.refreshing);
                break;
            case STATE_DONE:
                isRelease = false;
                animationDrawable.stop();
                loading.setVisibility(GONE);
                arrow.clearAnimation();
                arrow.setVisibility(GONE);
                msg.setText(R.string.refresh_done);
                break;
            default:
        }
        mState = state;
    }

    public boolean releaseAction() {
        boolean isOnRefresh = false;
        int height = getVisibleHeight();
        if (height == 0) // not visible.
            isOnRefresh = false;

        if (getVisibleHeight() > mMeasuredHeight && mState < STATE_REFRESHING) {
            setState(STATE_REFRESHING);
            isOnRefresh = true;
        }
        // refreshing and header isn'data shown fully. do nothing.
        if (mState == STATE_REFRESHING && height <= mMeasuredHeight) {
            //return;
        }
        int destHeight = 0; // default: scroll back to dismiss header.
        // is refreshing, just scroll back to show all the header.
        if (mState == STATE_REFRESHING) {
            destHeight = mMeasuredHeight;
        }
        smoothScrollTo(destHeight);

        return isOnRefresh;
    }

    public void refreshComplete() {
        setState(STATE_DONE);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                reset();
            }
        }, 500);
    }

    public void reset() {
        smoothScrollTo(0);
        setState(STATE_NORMAL);
    }

    private void smoothScrollTo(int destHeight) {
        ValueAnimator animator = ValueAnimator.ofInt(getVisibleHeight(), destHeight);
        animator.setDuration(300).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setVisibleHeight((int) animation.getAnimatedValue());
            }
        });
        animator.start();
    }

    private void setVisibleHeight(int height) {
        if (height < 0)
            height = 0;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }


    public int getVisibleHeight() {
        return mContainer.getHeight();
    }

    public int getState() {
        return mState;
    }
}

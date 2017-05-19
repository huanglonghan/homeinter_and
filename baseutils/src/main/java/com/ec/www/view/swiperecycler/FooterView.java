package com.ec.www.view.swiperecycler;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ec.www.R;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.widget.ListPopupWindow.WRAP_CONTENT;


/**
 * Created by huang on 2016/12/16.
 */

class FooterView extends LinearLayout {

    public final static int STATE_LOADING = 1;
    public final static int STATE_COMPLETE = 2;
    public final static int STATE_ERROR = 3;
    public final static int STATE_MOREOVER = 4;
    private TextView mText;
    private AnimationDrawable mAnimationDrawable;
    private ImageView mIvProgress;
    public int mMeasuredHeight;
    private OnClickListener mErrorReloadListener;

    public FooterView(Context context) {
        super(context);
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.footer_view, this);
        mText = (TextView) findViewById(R.id.tv_status);
        mIvProgress = (ImageView) findViewById(R.id.iv_loading);
        mAnimationDrawable = (AnimationDrawable) mIvProgress.getDrawable();
        if (!mAnimationDrawable.isRunning()) {
            mAnimationDrawable.start();
        }
        setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        setVisibility(GONE);
    }

    public void setState(int state) {
        setState(state, null);
    }

    public void setState(int state, @Nullable String msg) {
        switch (state) {
            case STATE_LOADING:
                setVisibility(VISIBLE);
                mIvProgress.setVisibility(View.VISIBLE);
                if (!mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.start();
                }
                mText.setText(R.string.loading);
//                RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
//                lp.height = mMeasuredHeight;
//                setLayoutParams(lp);
                break;
            case STATE_COMPLETE:
                setVisibility(VISIBLE);
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                mIvProgress.setVisibility(View.GONE);
                mText.setText(R.string.loading_done);
                //setVisibility(GONE);
//                ValueAnimator animator1 = ValueAnimator.ofInt(mMeasuredHeight, 1);
//                animator1.setDuration(300).start();
//                animator1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) getLayoutParams();
//                        lp.height = (int) animation.getAnimatedValue();
//                        setLayoutParams(lp);
//                    }
//                });
//                animator1.start();
                break;
            case STATE_ERROR:
                setVisibility(VISIBLE);
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                mText.setText(R.string.loading_error);
                mIvProgress.setVisibility(View.GONE);
                if (mErrorReloadListener != null)
                    setOnClickListener(mErrorReloadListener);
                break;
            case STATE_MOREOVER:
                setVisibility(VISIBLE);
                if (mAnimationDrawable.isRunning()) {
                    mAnimationDrawable.stop();
                }
                if (msg == null) {
                    mText.setText(R.string.moreover_loading);
                } else {
                    mText.setText(msg);
                }
                mIvProgress.setVisibility(View.GONE);
                break;
        }
    }

    public OnClickListener getErrorReloadListener() {
        return mErrorReloadListener;
    }

    public void setErrorReloadListener(OnClickListener errorReloadListener) {
        mErrorReloadListener = errorReloadListener;
    }
}

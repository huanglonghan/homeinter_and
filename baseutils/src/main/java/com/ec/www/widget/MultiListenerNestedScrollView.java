package com.ec.www.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.ec.www.utils.DimenUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Action;

/**
 * Created by huang on 2017/4/13.
 */

public class MultiListenerNestedScrollView extends NestedScrollView {

    //滑动方向
    private static final int SCROLL_DIRECTORY_DOWN = 0x001;

    //滑动方向
    private static final int SCROLL_DIRECTORY_UP = 0x002;

    //继续滑动的最小加速度
    private static final int VELOCITY_MIN = 240;

    //滑动阻力
    private static final float DRAG_RATE = 1.75f;

    //计算加速度的时间
    private static final int VELOCITY_INTERVAL = 50;

    //默认相对屏幕高度百分比
    private static final float DEFAULT_HEIGHT_PERCENT = 0.35f;
    private static final float SCREEN_PERCENT = 1.0f;

    private static final int STATE_NORMAL = 0x00;
    //private static final int STATE_NORMAL = 0x00;
    //private static final int STATE_NORMAL = 0x00;

    private int mCurState;

    private ViewGroup headerView;

    private ValueAnimator mSpringBackAnimator;
    private ValueAnimator mReleaseAnimator;

    private Observable<Action> mLoadMoreObservable;

    private int velocity;
    private VelocityTracker tracker;
    private float mLastY = -1;

    //手指是否离开屏幕 快速滑动回弹使用
    private boolean isPress = true;

    private boolean isLoadMore = false;

    //图片高度
    private int sourceHeight = -1;

    //默认高度
    private int defaultHeight;

    //屏幕高度
    private int screenHeight;

    //选用高度
    private int selectHeight;

    //当前方向
    private int curDirectory;

    public MultiListenerNestedScrollView(Context context) {
        super(context);
        init();
    }

    public MultiListenerNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiListenerNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        defaultHeight = DimenUtils.getPercentSizePx(1.0f, DEFAULT_HEIGHT_PERCENT).getHeight();
        screenHeight = DimenUtils.getPercentSizePx(1.0f, SCREEN_PERCENT).getHeight();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getChildCount() == 1) {
            ViewGroup contentView = (ViewGroup) getChildAt(0);
            removeViewAt(0);
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            if (headerView == null) {
                throw new RuntimeException("must be initial headerView!");
            }
            linearLayout.addView(headerView);
            linearLayout.addView(contentView);
            addView(linearLayout);
            if (sourceHeight == -1) {
                sourceHeight = defaultHeight;
            }
            if (sourceHeight < defaultHeight) {
                selectHeight = sourceHeight;
            } else {
                selectHeight = defaultHeight;
            }
            move(selectHeight);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tracker != null)
            tracker.recycle();
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //快速滑动回弹
        if (t == 0 && oldt != 0) {
            springBack();
        }
        View view = getChildAt(0);
        if (view != null) {
            if (getScrollY() >= view.getMeasuredHeight() - screenHeight * 2.5) {
                if (mLoadMoreObservable != null) {
                    if (!isLoadMore)
                        mLoadMoreObservable.subscribe(Action::run);
                }
                isLoadMore = true;
            } else {
                isLoadMore = false;
            }
            if (ViewCompat.canScrollVertically(this, -1)) {
                if (mLoadMoreObservable != null) {
                    mLoadMoreObservable.subscribe(Action::run);
                }
            }
        }
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        super.onNestedPreScroll(target, dx, dy, consumed);
        Log.d("asd", String.format("dy:%d", dy));
        scrollBy(dx, dy);
        consumed[1] = dy;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        Log.d("asd", String.format("velocityY:%f", velocityY));
        fling((int) velocityY);
        return true;
    }

    //回弹动画
    private void springBack() {
        //必须手指离开屏幕才会回弹
        if (!isPress && velocity > VELOCITY_MIN) {
            cancelSpringBack();
            mSpringBackAnimator = smoothToDistance(50, selectHeight, selectHeight + (int) ((float) velocity * 2.5f / 10.0f));
            mSpringBackAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    cancelAction();
                    mReleaseAnimator = smoothToDistance(300, headerView.getLayoutParams().height, selectHeight);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    cancelAction();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    //取消回弹动画
    private void cancelSpringBack() {
        if (mSpringBackAnimator != null && mSpringBackAnimator.isRunning()) {
            mSpringBackAnimator.cancel();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        cancelSpringBack();
        cancelAction();

        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        if (tracker == null) {
            tracker = VelocityTracker.obtain();
        }

        tracker.addMovement(ev);
        int viewHeight = headerView.getLayoutParams().height;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                isPress = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();

                isPress = true;
                //计算速度
                tracker.computeCurrentVelocity(VELOCITY_INTERVAL);
                velocity = (int) tracker.getYVelocity();

                //记录滑动方向
                if (deltaY > 0) {  //向下
                    curDirectory = SCROLL_DIRECTORY_DOWN;
                } else if (deltaY < 0) {  //向上
                    curDirectory = SCROLL_DIRECTORY_UP;
                }

                //在顶部滑动时
                int height = (int) (deltaY / DRAG_RATE);
                if (getScrollY() == 0) {
                    //放大时
                    if (viewHeight > defaultHeight || deltaY > 0) {
                        move(viewHeight + height);
                        return true;
                    }
                    if (deltaY < 0 && viewHeight >= defaultHeight && viewHeight <= sourceHeight) {
                        move(viewHeight + height);
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isPress = false;
                mLastY = -1; // reset
                if (getScrollY() == 0) {
                    if (sourceHeight < defaultHeight * 6.0f / 4.0f) {
                        releaseAction();
                    } else {
                        if (viewHeight > sourceHeight) {
                            smoothToDistance(viewHeight, sourceHeight);
                        } else if (viewHeight > defaultHeight && viewHeight < sourceHeight) {
                            int area = 0;
                            //在默认大小和源大小之间
                            //不同方向释放向不同方向滚动
                            switch (curDirectory) {
                                case SCROLL_DIRECTORY_DOWN:
                                    area = defaultHeight + (int) ((sourceHeight - defaultHeight) * 8.0f / 20.0f);
                                    break;
                                case SCROLL_DIRECTORY_UP:
                                    area = sourceHeight - (int) ((sourceHeight - defaultHeight) * 3.0f / 10.0f);
                                    break;
                            }

                            if (viewHeight > area) {
                                smoothToDistance(viewHeight, sourceHeight);
                            } else {
                                smoothToDistance(viewHeight, defaultHeight);
                            }
                        }
                    }
                    return true;
                }
        }
        return super.onTouchEvent(ev);
    }

    //释放动画
    private void releaseAction(int duration) {
        cancelAction();
        mReleaseAnimator = smoothToDistance(duration, headerView.getLayoutParams().height, defaultHeight);
    }

    private void releaseAction() {
        releaseAction(250);
    }

    //取消释放动画
    private void cancelAction() {
        if (mReleaseAnimator != null && mReleaseAnimator.isRunning()) {
            mReleaseAnimator.cancel();
        }
    }

    private ValueAnimator smoothToDistance(int start, int end) {
        return smoothToDistance(230, start, end);
    }

    private ValueAnimator smoothToDistance(int duration, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(duration).start();
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(animation -> move((int) animation.getAnimatedValue()));
        animator.start();
        return animator;
    }

    private void move(int height) {
        ViewGroup.LayoutParams params = headerView.getLayoutParams();
        params.height = height;
        headerView.setLayoutParams(params);
    }

    public ViewGroup getHeaderView() {
        return headerView;
    }

    public int getHeaderHtight() {
        return headerView.getHeight();
    }

    public void setHeaderView(ViewGroup headerView) {
        this.headerView = headerView;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public void setSourceHeight(int sourceHeight) {
        this.sourceHeight = sourceHeight;
    }

    public void setDefaultHeight(float heightPercent) {
        this.defaultHeight = DimenUtils.getPercentSizePx(1.0f, heightPercent).getHeight();
    }

    public void setLoadMoreCallback(Action loadMoreCallback) {
        mLoadMoreObservable = Observable.just(loadMoreCallback).throttleFirst(400, TimeUnit.MILLISECONDS);
    }
}

package com.ec.www.view.swiperecycler;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.ec.www.utils.DimenUtils;

/**
 * Created by huang on 2016/1/28.
 */
public class SwipeRecyclerView extends RecyclerView {

    private OnRefreshListener mOnRefreshListener;
    private SwipeAdapter mSwipeAdapter;
    private FooterView mFootViews;
    private boolean refreshEnabled = true;
    private boolean loadEnabled = true;
    private HeaderView mRefreshHeader;
    private boolean isLoadingData = false;
    public int previousTotal;
    public boolean isMoreOver;
    private float mLastY = -1;
    private static final float DRAG_RATE = 1.75f;
    public static final int SLIDE_UP = 0x001;
    public static final int SLIDE_DOWN = 0x002;
    public static final int LOAD_ERROR = 0x003;
    private int slideDirection;
    private int screenHeight;

    public SwipeRecyclerView(Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (refreshEnabled) {
            mRefreshHeader = new HeaderView(context);
        }
        screenHeight = DimenUtils.getPercentSizePx(1.0f, 1.0f).getHeight();
        mFootViews = new FooterView(context);
    }


    private void loadComplete() {
        isLoadingData = false;
        isLoadComplete = true;
        if (previousTotal <= getLayoutManager().getItemCount()) {
            mFootViews.setState(FooterView.STATE_COMPLETE);
        } else {
            mFootViews.setState(FooterView.STATE_MOREOVER);
            isMoreOver = true;
        }
        previousTotal = getLayoutManager().getItemCount();
    }

    public void moreOver(String msg) {
        if (isLoadComplete) {
            isLoadingData = false;
            isLoadComplete = false;
            isMoreOver = true;
            mFootViews.setState(FooterView.STATE_MOREOVER, msg);
        }
    }

    private boolean isLoadComplete = false;

    public void refreshComplete() {
        if (isLoadingData) {
            loadComplete();
        } else {
            mRefreshHeader.refreshComplete();
        }
    }

    public void loadError() {
        mFootViews.setErrorReloadListener((view) -> {
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onErrorReload();
                mFootViews.setErrorReloadListener(null);
            }
        });
        mFootViews.setState(FooterView.STATE_ERROR);
    }

    public void setAdapter(SwipeAdapter adapter) {
        mSwipeAdapter = adapter;
        mSwipeAdapter.setHeaderViews(mRefreshHeader);
        mSwipeAdapter.setFootViews(mFootViews);
        mSwipeAdapter.setRefreshEnabled(refreshEnabled);
        super.setAdapter(mSwipeAdapter);
    }

//    @Override
//    public void onScrollStateChanged(int state) {
//        super.onScrollStateChanged(state);
//        switch (state) {
//            case RecyclerView.SCROLL_STATE_IDLE:
//                if (mOnRefreshListener != null && !isLoadingData && loadEnabled) {
//                    if (!ViewCompat.canScrollVertically(this, 1) &&
//                            !isMoreOver &&
//                            mRefreshHeader.getState() < HeaderView.STATE_REFRESHING) {
//                        isLoadingData = true;
//                        mFootViews.setState(FooterView.STATE_LOADING);
//                        smoothScrollToPosition(getAdapter().getItemCount() - 1);
//                        if (isNetWorkConnected(getContext())) {
//                            postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mOnRefreshListener.onLoading();
//                                }
//                            }, 300);
//                        } else {
//                            postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mOnRefreshListener.onLoading();
//                                }
//                            }, 800);
//                        }
//                    }
//                }
//                break;
//        }
//
//    }

    /**
     * 外部调用自动加载更多方法
     */
    public void onLoadMore() {
        if (mOnRefreshListener != null && !isLoadingData && loadEnabled) {
            if (!isMoreOver &&
                    mRefreshHeader.getState() < HeaderView.STATE_REFRESHING) {
                isLoadingData = true;
                mFootViews.setState(FooterView.STATE_LOADING);
                if (isNetWorkConnected(getContext())) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnRefreshListener.onLoading();
                        }
                    }, 300);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnRefreshListener.onLoading();
                        }
                    }, 800);
                }
            }
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mOnRefreshListener != null && !isLoadingData && loadEnabled) {
            if (getLayoutManager().getItemCount() > 5 &&
                    !isMoreOver &&
                    mRefreshHeader.getState() < HeaderView.STATE_REFRESHING) {
                LayoutManager manager = getLayoutManager();
                int position = -1;
                int[] firstVisibleItems = new int[0];
                if (manager instanceof LinearLayoutManager) {
                    position = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                } else if (manager instanceof StaggeredGridLayoutManager) {
                    firstVisibleItems = ((StaggeredGridLayoutManager) manager).findFirstVisibleItemPositions(null);
                }
                if (position == -1) {
                    position = firstVisibleItems[0];
                }
                if (getLayoutManager().getItemCount() - position > 25) return;
                isLoadingData = true;
                mFootViews.setState(FooterView.STATE_LOADING);
                //smoothScrollToPosition(getAdapter().getItemCount() - 1);
                if (isNetWorkConnected(getContext())) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnRefreshListener.onLoading();
                        }
                    }, 300);
                } else {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnRefreshListener.onLoading();
                        }
                    }, 800);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && refreshEnabled) {
                    mRefreshHeader.onMove(deltaY / DRAG_RATE);
                    return !(mRefreshHeader.getVisibleHeight() > 0 && mRefreshHeader.getState() < HeaderView.STATE_REFRESHING);
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && refreshEnabled) {
                    if (mRefreshHeader.releaseAction()) {
                        if (mOnRefreshListener != null) {
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mOnRefreshListener.onRefresh();
                                }
                            }, 500);
                            isMoreOver = false;
                            previousTotal = 0;
                        }
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int findMin(int[] firstPositions) {
        int min = firstPositions[0];
        for (int value : firstPositions) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    public boolean isOnTop() {
        if (mRefreshHeader == null) {
            return false;
        }

        return mRefreshHeader.getParent() != null;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshing(boolean enable) {
        if (refreshEnabled == enable) return;
        refreshEnabled = enable;
//        if (mSwipeAdapter != null)
//            mSwipeAdapter.setRefreshEnabled(enable);
    }

    public void setLoading(boolean enable) {
        this.loadEnabled = enable;
        if (!enable) {
            if (mFootViews != null) {
                mFootViews = null;
            }
        } else {
            if (mFootViews != null) {
                mFootViews = new FooterView(getContext());
            }
        }
    }

    public void removeLoad() {
        mFootViews = null;
    }

    public int getSlideDirection() {
        return slideDirection;
    }

    public void setSlideState(int slideDirection) {
        this.slideDirection = slideDirection;
    }

    public interface OnRefreshListener {

        void onRefresh();

        void onLoading();

        void onErrorReload();

    }

    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}

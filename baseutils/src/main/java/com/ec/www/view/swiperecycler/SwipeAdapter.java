package com.ec.www.view.swiperecycler;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by huang on 2016/1/28.
 */
public abstract class SwipeAdapter<T> extends BaseAdapter {

    private static final int TYPE_REFRESH_HEADER = -10001;
    private static final int TYPE_HEADER = -10002;
    private static final int TYPE_FOOTER = -10003;

    private View mHeaderViews;
    private View mFootViews;
    private LayoutInflater mLayoutInflater;
    private boolean refreshEnabled = true;

    private int headerPosition = 1;

    public SwipeAdapter() {
        super();
    }

    @Override
    public final void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isRefreshHeader(position) || isFooter(position))
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public final void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeader(holder.getLayoutPosition()) || isFooter(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    public final boolean isHeader(int position) {
        return refreshEnabled && position >= 0 && position < 1;
    }

    public final boolean isFooter(int position) {
        return position < getItemCount() && position >= getItemCount() - 1;
    }

    public final boolean isRefreshHeader(int position) {
        return refreshEnabled && position == 0;
    }

    public final int getHeadersCount() {
        return 1;
    }

    public final int getFootersCount() {
        return 1;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_REFRESH_HEADER:
                return new SimpleViewHolder(mHeaderViews);
            case TYPE_FOOTER:
                return new SimpleViewHolder(mFootViews);
            default:
                if (mLayoutInflater == null) {
                    mLayoutInflater = LayoutInflater.from(parent.getContext());
                }
                return onCreateHolder(mLayoutInflater, parent, viewType);
        }
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindHolder(holder, mContent.get(position).setPosition(position));
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        if (refreshEnabled && isRefreshHeader(position)) {
            return;
        }
        if (isFooter(position)) {
            return;
        }
        if (payloads.size() <= 0 || !onBindHolder(holder, mContent.get(position).setPosition(position), payloads)) {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return getFootersCount() + super.getItemCount();
    }

    @Override
    public int getItemViewType(int position) {
        if (refreshEnabled && isRefreshHeader(position)) {
            return TYPE_REFRESH_HEADER;
        }
        if (isFooter(position)) {
            return TYPE_FOOTER;
        }
        return super.getItemViewType(position);
    }

    public final void setHeaderViews(View headerViews) {
        mHeaderViews = headerViews;
        mContent.add(0, new Data<>(TYPE_REFRESH_HEADER));
    }

    public final void setFootViews(View footViews) {
        mFootViews = footViews;
    }

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    public void setRefreshEnabled(boolean refreshEnabled) {
        if (this.refreshEnabled == refreshEnabled) return;
        this.refreshEnabled = refreshEnabled;
        if (refreshEnabled) {
            mContent.add(0, new Data<>(TYPE_REFRESH_HEADER));
        } else {
            mContent.remove(0);
        }
        notifyItemRemoved(0);
    }

    public View getHeaderViews() {
        return mHeaderViews;
    }

    public View getFootViews() {
        return mFootViews;
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

}
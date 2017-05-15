package pw.bmyo.www.swiperecycler;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import pw.bmyo.www.swiperecycler.multitype.ItemViewBinder;

/**
 * Created by huang on 2017/5/14.
 */

public abstract class BaseItemViewBuild<T, VH extends RecyclerView.ViewHolder> extends ItemViewBinder<T, VH> {

    protected View.OnClickListener mOnClickListener;
    protected View.OnLongClickListener mOnLongClickListener;

    protected OnItemClickListener mOnItemClickListener;

    protected View.OnTouchListener mTouchListener;

    private boolean isTouchIntercept = false;

    private boolean isLongIntercept = false;

    public boolean isLongIntercept() {
        return isLongIntercept;
    }

    public void setLongIntercept(boolean longIntercept) {
        isLongIntercept = longIntercept;
    }

    public boolean isTouchIntercept() {
        return isTouchIntercept;
    }

    public void setTouchIntercept(boolean touchIntercept) {
        isTouchIntercept = touchIntercept;
    }

    public final void setOnItemClickListener(View.OnClickListener listener) {
        mOnClickListener = listener;
    }

    public final void setOnItemLongClickListener(View.OnLongClickListener listener) {
        mOnLongClickListener = listener;
    }

    public void setTouchListener(View.OnTouchListener touchListener) {
        mTouchListener = touchListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

}

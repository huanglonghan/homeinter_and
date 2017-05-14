package pw.bmyo.www.swiperecycler;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by huang on 2016/12/28.
 */

public class SpaceItem extends RecyclerView.ItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private int mOrientation;
    private int mSpace;

    public SpaceItem(Context context, int orientation, int size) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        a.recycle();
        setOrientation(orientation);
        mSpace = size;
    }

    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int index = parent.getChildAdapterPosition(view);
        if (index < 0 || index > parent.getAdapter().getItemCount()) return;
        if (parent.getAdapter().getItemViewType(index) == Adapter.TYPE_TAG_DEFAULT) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mSpace);
            } else {
                outRect.set(0, 0, mSpace, 0);
            }
        }
    }
}

package pw.bmyo.www.swiperecycler;

import android.view.View;

/**
 * Created by huang on 2017/1/21.
 */

public interface OnItemClickListener<T> {
    void onItemClick(View view, T data, int position);

}

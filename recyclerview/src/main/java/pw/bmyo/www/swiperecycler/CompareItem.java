package pw.bmyo.www.swiperecycler;

/**
 * Created by huang on 2016/12/27.
 */

public interface CompareItem<T> {
    boolean areItemsTheSame(Adapter.Data<T> oldItem,
                            Adapter.Data<T> newItem);

    boolean areContentsTheSame(Adapter.Data<T> oldItem,
                               Adapter.Data<T> newItem);

    Object getChangePayload(Adapter.Data<T> oldItem,
                            Adapter.Data<T> newItem);
}

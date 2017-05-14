package pw.bmyo.www.swiperecycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.drakeet.multitype.Linker;
import me.drakeet.multitype.MultiTypeAdapter;
import me.drakeet.multitype.TypePool;

/**
 * Created by huang on 2016/12/14.
 */

public abstract class BaseAdapter extends MultiTypeAdapter {

    private boolean mDetectMove = true;

    private CompareItem<?> mCompare;

    private int indexInTypesOf(@NonNull Class<?> clazz) {
        TypePool pool = getTypePool();
        int index = pool.firstIndexOf(clazz);
        if (index != -1) {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) pool.getLinkers().get(index);
            try {
                return index + linker.index(clazz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    private int indexInTypesOf(@NonNull Object item) {
        TypePool pool = getTypePool();
        int index = pool.firstIndexOf(item.getClass());
        if (index != -1) {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) pool.getLinkers().get(index);
            return index + linker.index(item);
        }
        return -1;
    }

    public final int getFirstPosition(Class<?> clazz) {
        return getFirstPosition(clazz, 0);
    }

    public final int getFirstPosition(Class<?> clazz, int defaultVal) {
        List<?> items = getItems();
        int type = indexInTypesOf(clazz);
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            if (indexInTypesOf(items.get(i)) == type) {
                return i;
            }
        }
        return defaultVal;
    }

    public final int getLastPosition(Class<?> clazz) {
        return getLastPosition(clazz, getItemCount());
    }

    public final int getLastPosition(Class<?> clazz, int defaultVal) {
        List<?> items = getItems();
        int type = indexInTypesOf(clazz);
        int size = getItemCount();
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (indexInTypesOf(items.get(i)) == type) {
                index = i + 1;
            }
        }
        if (index == -1) {
            index = defaultVal;
        }
        return index;
    }

    public final int getTypeCount(Class<?> clazz) {
        int count = 0;
        List<?> items = getItems();
        int type = indexInTypesOf(clazz);
        int size = getItemCount();
        for (int i = 0; i < size; i++) {
            if (indexInTypesOf(items.get(i)) == type) {
                count++;
            }
        }
        return count;
    }

    public final List<?> getChildList(Class<?> clazz) {
        int first = getFirstPosition(clazz, -1);
        if (first == -1) {
            return null;
        }
        int last = getLastPosition(clazz, -1);
        if (last == -1) {
            return null;
        }
        return getItems().subList(first, last);
    }

    public final List<?> getTypeData(Class<?> clazz) {
        int count = getTypeCount(clazz);
        LinkedList<Object> ts = new LinkedList<>();
        if (count <= 0) {
            return ts;
        }
        List<?> items = getItems();
        int start = getFirstPosition(clazz);
        if (overRange(start) || overRange(start + count)) {
            return null;
        }
        for (int i = start; i < start + count; i++) {
            ts.add(items.get(i));
        }
        return ts;
    }

    public final <T> void add(T item) {
        int index = getLastPosition(item.getClass());
        List<T> list = new LinkedList<>();
        list.add(item);
        add(list, index);
    }

    public final <T> void add(T item, int position) {
        if (overRange(position)) {
            return;
        }
        List<T> list = new LinkedList<>();
        list.add(item);
        add(list, position);
    }


    public final void add(List<?> items) {
        int index = getItemCount();
        if (items.size() > 0) {
            index = getLastPosition(items.get(0).getClass());
        }
        add(items, index);
    }


    public final void add(List<?> items, int position) {
        if (overRange(position)) {
            return;
        }
        if (getItems() == Collections.emptyList()) {
            setItems(new LinkedList<>(items));
        } else {
            List<Object> listOld = new LinkedList<>(getItems());
            listOld.addAll(position, items);
            setItems(listOld);
        }
        notifyItemRangeInserted(position, getItemCount());
    }


    public void setCompareItem(CompareItem<?> compare) {
        mCompare = compare;
    }


    public final void remove(int position) {
        if (overRange(position)) {
            return;
        }
        getItems().remove(position);
        notifyItemRemoved(position);
    }

    private boolean overRange(int position) {
        return position < 0 && position >= getItemCount();
    }

    //删除连续类型
    public final void removeType(Class<?> clazz) {
        int start = getFirstPosition(clazz);
        int count = getTypeCount(clazz);
        if (overRange(start) || overRange(start + count)) {
            return;
        }
        List<?> items = getItems();
        for (int i = 0; i < count; i++) {
            items.remove(start);
        }
        notifyItemRangeRemoved(start, count);
    }


//    public final void update(List<?> items, int position) {
//        List<Object> list = new LinkedList<>();
//        Set<Map.Entry<T, Integer>> set = data.entrySet();
//        for (Map.Entry<T, Integer> t : set) {
//            list.add(new Data<>(t.getValue(), t.getKey()));
//        }
//        updateData(list, position, list.size());
//    }
//
//    public final void update(List<T> data, int type, int size) {
//        int position = getFirstPosition(type);
//        List<Data<T>> list = new LinkedList<>();
//        for (T t : data) {
//            list.add(new Data<>(type, t));
//        }
//        updateData(list, position, size);
//    }
//
//    public final void update(List<T> data, int type) {
//        int size = getTypeCount(type);
//        update(data, type, size);
//    }
//
//    public final void update(List<T> data) {
//        int size = getTypeCount(TYPE_TAG_DEFAULT);
//        update(data, TYPE_TAG_DEFAULT, size);
//    }
//
//    public final void update(T data) {
//        List<Data<T>> list = new LinkedList<>();
//        list.add(new Data<>(type, data));
//        updateData(list, position, 1);
//    }
//
//    public final void update(T data, int position) {
//        update(data, TYPE_TAG_DEFAULT, position);
//    }

    public final <T> void update(int position, UpdateTypeData<T> data) {
        data.update((T) getItems().get(position));
        notifyItemChanged(position, getItems().get(position));
    }

    public final <T> void updateTypeData(Class<?> clazz, UpdateTypeData<T> data) {
        int start = getFirstPosition(clazz);
        int end = getLastPosition(clazz);
        if (overRange(start) || overRange(start + end)) {
            return;
        }
        List<?> items = getItems();
        if (end == items.size()) {
            return;
        }
        for (int i = start; i < end; i++) {
            data.update((T) items.get(i));
        }
        notifyItemRangeChanged(start, end - start);
    }


    public final void update(List<?> data, int position) {
        updateData(new LinkedList<>(data), position, 1);
    }

    protected void updateData(List<?> data, int index, int size) {
        updateData(data, index, size, true);
    }

    protected void updateData(List<?> newData, int index, int size, boolean isNotify) {

        if (mCompare == null) {
            throw new RuntimeException("mCompare is null");
        }
        List<?> oldData;

        oldData = getRange(index, size);
        replaceRange(newData, index, size);

        if (!isNotify) return;
        Observable.just(new CallBack<>(oldData, newData, mCompare))
                .map(callback -> DiffUtil.calculateDiff(callback, mDetectMove))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(diffUtil -> {
                    diffUtil.dispatchUpdatesTo(new ListUpdateCallback() {
                        @Override
                        public void onInserted(int position, int count) {
                            notifyItemRangeInserted(index + position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            notifyItemRangeRemoved(index + position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            notifyItemMoved(fromPosition, toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count, Object payload) {
                            notifyItemRangeChanged(index + position, count, payload);
                        }
                    });
                });
    }

    private List<?> getRange(int index, int size) {
        List<?> items = getItems();
        LinkedList<Object> list = new LinkedList<>();
        for (int i = index; i < index + size; i++) {
            list.add(items.get(i));
        }
        return list;
    }

    private void replaceRange(List<?> data, int index, int size) {
        List<?> items = getItems();
        for (int i = 0; i < size; i++) {
            items.remove(index);
        }
        LinkedList<Object> list = new LinkedList<>(items);
        list.addAll(index, data);
    }

    public final void delete(Class<?> clazz) {
        int start = getFirstPosition(clazz);
        int end = getLastPosition(clazz);
        if (overRange(start) || overRange(start + end)) {
            return;
        }
        List<?> items = getItems();
        if (end == items.size()) {
            return;
        }
        for (int i = start; i < end; i++) {
            items.remove(start);
        }
        notifyItemRangeRemoved(start, end - start);
    }

    public void setDetectMove(boolean detectMove) {
        mDetectMove = detectMove;
    }


    public final void clear() {
        getItems().clear();
        notifyDataSetChanged();
    }


//
//    @Override
//    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
//        if (mLayoutInflater == null) {
//            mLayoutInflater = LayoutInflater.from(parent.getContext());
//        }
//        return onCreateHolder(mLayoutInflater, parent, viewType);
//    }
//
//    public abstract VH onCreateHolder(LayoutInflater inflater, ViewGroup parent, int viewType);
//
//    @Override
//    public void onBindViewHolder(VH holder, int position) {
//        onBindHolder(holder, mContent.get(position).setPosition(position));
//    }
//
//    public abstract void onBindHolder(VH holder, Data<T> data);
//
//    @Override
//    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
//        if (payloads.size() <= 0 || !onBindHolder(holder, mContent.get(position).setPosition(position), payloads)) {
//            super.onBindViewHolder(holder, position, payloads);
//        }
//    }
//
//    public boolean onBindHolder(VH holder, Data<T> data, List<Object> payloads) {
//        return false;
//    }
//


    private static class CallBack<T> extends DiffUtil.Callback {

        private List<?> mOldList;
        private List<?> mNewList;
        private CompareItem<?> mCompareItem;

        CallBack(List<?> oldList, List<?> newList, CompareItem<T> compare) {
            mOldList = oldList;
            mNewList = newList;
            mCompareItem = compare;
        }

        @Override
        public int getOldListSize() {
            return mOldList.size();
        }

        @Override
        public int getNewListSize() {
            return mNewList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            //return mCompareItem.areItemsTheSame(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
            return false;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            //return mCompareItem.areContentsTheSame(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
            return false;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            //return mCompareItem.getChangePayload(mOldList.get(oldItemPosition), mNewList.get(newItemPosition));
            return false;
        }
    }

    public interface UpdateTypeData<T> {
        void update(T data);
    }

}

package com.ec.www.view.swiperecycler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.util.ListUpdateCallback;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import pw.bmyo.www.swiperecycler.multitype.CompareItem;
import pw.bmyo.www.swiperecycler.multitype.Linker;
import pw.bmyo.www.swiperecycler.multitype.MultiTypeAdapter;
import pw.bmyo.www.swiperecycler.multitype.TypePool;

/**
 * Created by huang on 2017/5/15.
 */

public class DataOperateHelper {

    private MultiTypeAdapter adapter;

    public DataOperateHelper(MultiTypeAdapter adapter) {
        this.adapter = adapter;
    }

    private boolean mDetectMove = true;

    private CompareItem<Object, ?> mCompare;

    /**
     * 检查是否越界
     *
     * @param position 位置
     * @return 越界为真
     */
    private boolean overRange(int position) {
        return position < 0 && position >= adapter.getItemCount();
    }

    /**
     * 根据Class计算数据类型
     *
     * @param clazz CLass
     * @return int 数值
     */
    private int indexInTypesOf(@NonNull Class<?> clazz) {
        TypePool pool = adapter.getTypePool();
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

    /**
     * 根据Class计算数据类型的重载版本
     *
     * @param item CLass
     * @return int 数值
     */
    private int indexInTypesOf(@NonNull Object item) {
        TypePool pool = adapter.getTypePool();
        int index = pool.firstIndexOf(item.getClass());
        if (index != -1) {
            @SuppressWarnings("unchecked")
            Linker<Object> linker = (Linker<Object>) pool.getLinkers().get(index);
            return index + linker.index(item);
        }
        return -1;
    }

    /**
     * 取类型第一次出现的位置 取不到返回-1
     *
     * @param clazz 类型
     * @return 位置
     */
    public final int getFirstPosition(Class<?> clazz) {
        return getFirstPosition(clazz, -1);
    }

    /**
     * 取类型第一次出现的位置
     *
     * @param clazz      类型
     * @param defaultVal 取不到时的默认返回值
     * @return 位置
     */
    public final int getFirstPosition(Class<?> clazz, int defaultVal) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return defaultVal;
        }
        int type = indexInTypesOf(clazz);
        int size = adapter.getItemCount();
        for (int i = 0; i < size; i++) {
            if (indexInTypesOf(items.get(i)) == type) {
                return i;
            }
        }
        return defaultVal;
    }

    /**
     * 取类型最后一次出现的位置 取不到返回当前列表大小
     *
     * @param clazz 类型
     * @return 位置
     */
    public final int getLastPosition(Class<?> clazz) {
        return getLastPosition(clazz, adapter.getItemCount());
    }

    /**
     * 取类型最后一次出现的位置
     *
     * @param clazz      类型
     * @param defaultVal 取不到时的默认返回值
     * @return 位置
     */
    public final int getLastPosition(Class<?> clazz, int defaultVal) {
        int index = -1;
        List<?> items = adapter.getItems();
        if (items == null) {
            return defaultVal;
        }
        int type = indexInTypesOf(clazz);
        int size = adapter.getItemCount();
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

    /**
     * 取连续的某类型数据数量 默认查找开始位置为该数据类型第一次出现的位置
     *
     * @param clazz 类型
     * @return 数量
     */
    public final int getTypeCount(Class<?> clazz) {
        int start = getFirstPosition(clazz);
        return getTypeCount(clazz, start);
    }

    /**
     * 取连续的某类型数据数量
     *
     * @param clazz 类型
     * @param start 查找开始位置
     * @return 数量
     */
    public final int getTypeCount(Class<?> clazz, int start) {
        int count = 0;
        List<?> items = adapter.getItems();
        if (items == null) {
            return count;
        }
        if (overRange(start)) {
            return count;
        }
        boolean isChange = false;
        int type = indexInTypesOf(clazz);
        int size = adapter.getItemCount();
        for (int i = start; i < size; i++) {
            if (indexInTypesOf(items.get(i)) == type) {
                count++;
                if (isChange) {
                    return count;
                }
            } else {
                isChange = true;
            }
        }
        return count;
    }

    /**
     * 根据类型取子列表 取不到则返回null
     *
     * @param clazz 类型
     * @return 子集
     */
    public final List<?> getChildList(Class<?> clazz) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return null;
        }
        int start = getFirstPosition(clazz);
        int count = getTypeCount(clazz, start);
        int end = start + count;
        if (overRange(start) || overRange(end)) {
            return null;
        }
        return items.subList(start, end);
    }


    public final List<?> getChildList(Class<?> clazz, int position) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return null;
        }
        int count = getTypeCount(clazz, position);
        int end = position + count;
        if (overRange(position) || overRange(end)) {
            return null;
        }
        return items.subList(position, end);
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
        int index = adapter.getItemCount();
        if (items.size() > 0) {
            index = getLastPosition(items.get(0).getClass());
        }
        add(items, index);
    }

    public final void add(List<?> items, int position) {
        if (overRange(position)) {
            return;
        }
        List<?> itemsOld = adapter.getItems();
        int count;
        if (itemsOld == null) {
            adapter.setItems(new LinkedList<>(items));
            count = adapter.getItemCount();
        } else {
            List<Object> listOld = new LinkedList<>(itemsOld);
            listOld.addAll(position, items);
            adapter.setItems(listOld);
            count = items.size();
        }
        adapter.notifyItemRangeInserted(position, count);
    }

    public final void remove(int position) {
        remove(position, 1);
    }

    public final void remove(int position, int count) {
        if (overRange(position)) {
            return;
        }
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        for (int i = 0; i < count; i++) {
            items.remove(position);
        }
        adapter.notifyItemRangeRemoved(position, count);
    }

    //删除连续类型
    public final void removeType(Class<?> clazz) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        int start = getFirstPosition(clazz);
        int count = getTypeCount(clazz, start);
        if (overRange(start) || overRange(start + count)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            items.remove(start);
        }
        adapter.notifyItemRangeRemoved(start, count);
    }

    public final <T> void update(T item) {
        updateSize(item, 1);
    }

    public final <T> void update(T item, Class<?> clazz) {
        int index = getFirstPosition(clazz, 0);
        updateSize(item, index);
    }

    public final <T> void update(T item, int position) {
        updateSize(item, position, 1);
    }

    public final <T> void updateSize(T item, int size) {
        int index = getFirstPosition(item.getClass(), 0);
        updateSize(item, index, size);
    }

    public final <T> void updateSize(T item, Class<?> clazz, int size) {
        int index = getFirstPosition(clazz, 0);
        updateSize(item, index, size);
    }

    public final <T> void updateSize(T item, int position, int size) {
        List<T> list = new LinkedList<>();
        list.add(item);
        updateData(list, position, size);
    }

    public final void update(List<?> items) {
        List<?> itemsOld = adapter.getItems();
        if (itemsOld != null && itemsOld.size() > 0) {
            update(items, itemsOld.get(0).getClass());
        }
    }

    public final void update(List<?> items, Class<?> clazz) {
        List<?> itemsOld = adapter.getItems();
        int index = 0;
        if (itemsOld != null && itemsOld.size() > 0) {
            index = getFirstPosition(clazz, 0);
        }
        updateSize(items, index, items.size());
    }

    public final void update(List<?> items, int position) {
        updateSize(items, position, items.size());
    }

    public final void updateSize(List<?> items, int size) {
        List<?> itemsOld = adapter.getItems();
        if (itemsOld != null && itemsOld.size() > 0) {
            updateSize(items, itemsOld.get(0).getClass(), size);
        }
    }

    public final void updateSize(List<?> items, Class<?> clazz, int size) {
        List<?> itemsOld = adapter.getItems();
        int index = 0;
        if (itemsOld != null && itemsOld.size() > 0) {
            index = getFirstPosition(clazz, 0);
        }
        updateData(items, index, size);
    }

    public final void updateSize(List<?> items, int position, int size) {
        updateData(items, position, size);
    }


    public final <T> void update(int position, Consumer<T> data) {
        if (overRange(position)) {
            return;
        }
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        try {
            data.accept((T) items.get(position));
            adapter.notifyItemChanged(position, items.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public final <T> void updateFirst(Class<?> clazz, Consumer<T> data) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        int index = getFirstPosition(clazz, 0);
        update(index, data);
    }

    public final <T> void update(Class<?> clazz, Consumer<T> data) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        int start = getFirstPosition(clazz);
        int count = getTypeCount(clazz, start);
        int end = start + count;
        if (overRange(start) || overRange(end)) {
            return;
        }
        for (int i = start; i < end; i++) {
            try {
                data.accept((T) items.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter.notifyItemRangeChanged(start, count);
    }

    /**
     * 更新数据并计算移动(默认通知数据改变)
     *
     * @param newData 新数据
     * @param index   老数据的开始位置
     * @param size    老数据的大小
     */
    protected void updateData(List<?> newData, int index, int size) {
        updateData(newData, index, size, true);
    }

    /**
     * 更新数据并计算移动
     *
     * @param newData  新数据
     * @param index    老数据的开始位置
     * @param size     老数据的大小
     * @param isNotify 是否通知更新
     */
    protected void updateData(List<?> newData, int index, int size, boolean isNotify) {
        if (overRange(index) || overRange(index + size)) {
            return;
        }
        List<?> items = adapter.getItems();
        if (items == null) {
            add(newData);
            return;
        }

        if (mCompare == null) {
            throw new RuntimeException("mCompare is null");
        }

        List<?> oldData;

        //更新数据
        oldData = getRange(index, size);
        replaceRange(newData, index, size);

        if (!isNotify) return;

        //去比较器
        TypePool pool = adapter.getTypePool();
        int position = pool.firstIndexOf(newData.get(0).getClass());
        CompareItem<Object, ?> compare = pool.getItemViewBinders().get(position);

        //比较并通知更新
        Observable.just(new CallBack(oldData, newData, compare))
                .map(callback -> DiffUtil.calculateDiff(callback, mDetectMove))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(diffUtil -> {
                    diffUtil.dispatchUpdatesTo(new ListUpdateCallback() {
                        @Override
                        public void onInserted(int position, int count) {
                            adapter.notifyItemRangeInserted(index + position, count);
                        }

                        @Override
                        public void onRemoved(int position, int count) {
                            adapter.notifyItemRangeRemoved(index + position, count);
                        }

                        @Override
                        public void onMoved(int fromPosition, int toPosition) {
                            adapter.notifyItemMoved(fromPosition, toPosition);
                        }

                        @Override
                        public void onChanged(int position, int count, Object payload) {
                            adapter.notifyItemRangeChanged(index + position, count, payload);
                        }
                    });
                });
    }

    /**
     * 获取范围数据
     *
     * @param index 开始位置
     * @param size  数量
     * @return 数据集
     */
    private List<?> getRange(int index, int size) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return null;
        }
        return items.subList(index, index + size);
    }

    /**
     * 替换范围数据
     *
     * @param data  新数据
     * @param index 替换开始位置
     * @param size  替换的大小
     */
    private void replaceRange(List<?> data, int index, int size) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        for (int i = 0; i < size; i++) {
            items.remove(index);
        }
        LinkedList<Object> list = new LinkedList<>(items);
        list.addAll(index, data);
    }

    /**
     * 删除类型数据
     *
     * @param clazz 类型
     */
    public final void delete(Class<?> clazz) {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        int start = getFirstPosition(clazz);
        int count = getTypeCount(clazz, start);
        int end = start + count;
        if (overRange(start) || overRange(end)) {
            return;
        }
        for (int i = start; i < end; i++) {
            items.remove(start);
        }
        adapter.notifyItemRangeRemoved(start, count);
    }

    public final void clear() {
        List<?> items = adapter.getItems();
        if (items == null) {
            return;
        }
        adapter.getItems().clear();
        adapter.notifyDataSetChanged();
    }

    public void setDetectMove(boolean detectMove) {
        mDetectMove = detectMove;
    }

    public void setCompareItem(CompareItem<Object, ?> compare) {
        mCompare = compare;
    }

    /**
     * 比较器回调类
     *
     * @param <T>
     * @param <E>
     */
    private static class CallBack<T, E> extends DiffUtil.Callback {

        /**
         * 老数据集
         */
        private List<T> oldList;

        /**
         * 新数据集
         */
        private List<E> newList;

        /**
         * 比较器接口
         */
        private CompareItem<T, E> compareItem;

        CallBack(List<T> oldList, List<E> newList, CompareItem<T, E> compare) {
            this.oldList = oldList;
            this.newList = newList;
            compareItem = compare;
        }

        @Override
        public int getOldListSize() {
            return oldList != null ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return newList != null ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            T oldItem = oldList.get(oldItemPosition);
            E newItem = newList.get(newItemPosition);
            return newItem.getClass().isInstance(oldItem) && compareItem.areItemsTheSame(oldItem, newItem);
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            T oldItem = oldList.get(oldItemPosition);
            E newItem = newList.get(newItemPosition);
            return newItem.getClass().isInstance(oldItem) && compareItem.areContentsTheSame(oldItem, newItem);
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return compareItem.getChangePayload(oldList.get(oldItemPosition), newList.get(newItemPosition));
        }
    }

}

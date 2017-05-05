package com.ec.www.base;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.ArrayList;

/**
 * Created by huang on 2017/2/22.
 */

public abstract class AbstractFragmentPagerAdapter<T> extends FragmentPagerAdapter {

    protected ArrayList<T> mDatas = new ArrayList<>();
    protected SparseArray<AbstractFragment> mList = new SparseArray<>();

    public AbstractFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public void add(ArrayList<T> datas) {
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void update(ArrayList<T> map) {
//        Observable.just(new DiffUtil.Callback() {
//
//            @Override
//            public int getOldListSize() {
//                return mDatas.size();
//            }
//
//            @Override
//            public int getNewListSize() {
//                return map.size();
//            }
//
//            @Override
//            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                return mDatas.get(oldItemPosition).interestId == map.get(newItemPosition).interestId;
//            }
//
//            @Override
//            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                return true;
//            }
//        }).map(callback -> DiffUtil.calculateDiff(callback, true))
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe((diffUtil) -> {
//                    diffUtil.dispatchUpdatesTo(new ListUpdateCallback() {
//                        @Override
//                        public void onInserted(int position, int count) {
//                            for (int i = position; i < position + count; i++) {
//                                changeQueue.add(i);
//                            }
//                        }
//
//                        @Override
//                        public void onRemoved(int position, int count) {
//                            for (int i = position; i < position + count; i++) {
//                                changeQueue.add(i);
//                            }
//                        }
//
//                        @Override
//                        public void onMoved(int fromPosition, int toPosition) {
//                            changeQueue.add(fromPosition);
//                            changeQueue.add(toPosition);
//                        }
//
//                        @Override
//                        public void onChanged(int position, int count, Object payload) {
//                            for (int i = position; i < position + count; i++) {
//                                changeQueue.add(i);
//                            }
//                        }
//                    });
//                });
        mDatas = map;
        notifyDataSetChanged();
    }

    public void clear() {
        mDatas.clear();
        //notifyDataSetChanged();
    }

    public int findContentPosition(int id) {
        int size = mDatas.size();
        for (int i = 0; i < size; i++) {
            if (getId(mDatas.get(i)) == id) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getTitle(mDatas.get(position));
    }

    @Override
    public Fragment getItem(int position) {
        int index = (int) getItemId(position);
        AbstractFragment fragment = mList.get(index, null);
        if (fragment == null) {
            fragment = createFragment(mDatas.get(position));
            int oldIndex = mList.indexOfValue(fragment);
            if (index != -1) {
                mList.put(oldIndex, fragment);
            } else {
                mList.put(index, fragment);
            }
        }
        return fragment;
    }

    public abstract String getTitle(T data);

    public abstract AbstractFragment createFragment(T data);

    public abstract int getId(T data);

    @Override
    public long getItemId(int position) {
        return getId(mDatas.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

}

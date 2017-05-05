package com.ec.www.base;

/**
 * Created by huang on 2017/1/23.
 */

public abstract class HasStackFragment<T> extends AbstractFragment<T> {
    @Override
    public void onResume() {
        if (isVisible()) {
            ((AbstractActivity) getActivity()).setCurrentFragment(this);
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isVisible()) {
            ((AbstractActivity) getActivity()).setCurrentFragment(this);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((AbstractActivity) getActivity()).setCurrentFragment(this);
        }
    }
}

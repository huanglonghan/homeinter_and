package com.ec.www.base;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

/**
 * Created by huang on 2017/5/17.
 */

public class ActivityManager {

    private LinkedList<WeakReference<AbstractActivity>> mActivities;

    private ActivityManager() {
        mActivities = new LinkedList<>();
    }

    private static class ACTIVITY_MANAGER {
        static {
            MANAGER = new ActivityManager();
        }

        private static final ActivityManager MANAGER;
    }

    public static ActivityManager getInstance() {
        return ACTIVITY_MANAGER.MANAGER;
    }

    public final LinkedList<WeakReference<AbstractActivity>> get() {
        return mActivities;
    }

    public final void add(AbstractActivity activity) {
        if (!contains(activity)) {
            mActivities.add(new WeakReference<>(activity));
        }
    }

    public final void remove(AbstractActivity activity) {
        int position = getActivityPosition(activity);
        if (position != -1) {
            mActivities.remove(position);
        }
    }

    public final void remove(Class<? extends AbstractActivity> clazz) {
        int position = getActivityPosition(clazz);
        if (position != -1) {
            mActivities.remove(position);
        }
    }

    public final boolean contains(AbstractActivity activity) {
        int position = getActivityPosition(activity);
        return position != -1;
    }

    public final boolean contains(Class<? extends AbstractActivity> clazz) {
        int position = getActivityPosition(clazz);
        return position != -1;
    }

    public final void finishActivity(AbstractActivity activity) {
        int position = getActivityPosition(activity);
        if (position != -1) {
            AbstractActivity abstractActivity = mActivities.get(position).get();
            if (abstractActivity != null) {
                abstractActivity.exit();
            }
        }
    }

    public final void finishActivity(Class<? extends AbstractActivity> clazz) {
        int position = getActivityPosition(clazz);
        if (position != -1) {
            AbstractActivity activity = mActivities.get(position).get();
            if (activity != null) {
                activity.exit();
            }
        }
    }

    private int getActivityPosition(AbstractActivity activity) {
        return getActivityPosition(activity, -1);
    }

    private int getActivityPosition(AbstractActivity activity, int defaultVal) {
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            if (mActivities.get(i).get().equals(activity)) {
                return i;
            }
        }
        return defaultVal;
    }

    private int getActivityPosition(Class<? extends AbstractActivity> clazz) {
        return getActivityPosition(clazz, -1);
    }

    private int getActivityPosition(Class<? extends AbstractActivity> clazz, int defaultVal) {
        int size = mActivities.size();
        for (int i = 0; i < size; i++) {
            if (clazz.isInstance(mActivities.get(i).get())) {
                return i;
            }
        }
        return defaultVal;
    }

}

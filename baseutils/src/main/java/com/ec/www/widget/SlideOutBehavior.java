package com.ec.www.widget;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;

/**
 * Created by huang on 2016/12/25.
 */

public class SlideOutBehavior extends CoordinatorLayout.Behavior<LinearLayout> {
    private boolean scrolling = true;

    public SlideOutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View directTargetChild, View target, int nestedScrollAxes) {
        return ViewCompat.SCROLL_AXIS_VERTICAL == nestedScrollAxes;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, LinearLayout child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {

        if (dyConsumed < 0) {
            if (scrolling) {
                tranX(child, 0);
            }
        } else if (dyConsumed > 0) {
            if (scrolling) {
                tranX(child, coordinatorLayout.getWidth() - child.getLeft());
            }
        }
    }

    private void tranX(View view, int x) {

        ViewPropertyAnimator anim = view.animate();
        anim.setDuration(250).translationX(x);
        anim.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                scrolling = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                scrolling = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim.start();
    }
}

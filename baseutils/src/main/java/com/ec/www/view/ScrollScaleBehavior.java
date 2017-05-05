package com.ec.www.view;

import android.animation.Animator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageButton;

/**
 * Created by huang on 2017/3/14.
 */
public class ScrollScaleBehavior extends CoordinatorLayout.Behavior<ImageButton> {

    private boolean scrolling = true;
    int slop;

    public ScrollScaleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        slop = 2;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, ImageButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return ViewCompat.SCROLL_AXIS_VERTICAL == nestedScrollAxes;
    }



    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, ImageButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (Math.abs(dyConsumed) > slop) {
            if (dyConsumed < 0) {
                if (scrolling) {
                    show(child);
                }
            } else if (dyConsumed > 0) {
                if (scrolling) {
                    hide(child);
                }
            }
        }
    }

    private void show(View view) {

        ViewPropertyAnimator anim = view.animate();
        anim.setDuration(250).scaleX(1).scaleY(1);
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

    private void hide(View view) {

        ViewPropertyAnimator anim = view.animate();
        anim.setDuration(250).scaleX(0).scaleY(0);
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

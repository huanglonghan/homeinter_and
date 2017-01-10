package com.longhan.huang.homeinter.component;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps2d.model.Marker;

import java.util.LinkedList;

import com.longhan.huang.homeinter.R;

/**
 * Created by 龙汗 on 2016/3/18.
 */
public class ViewPagerIndicator extends LinearLayout {

    private Paint mPaint;
    private int mTop;
    private int mLeft;
    private int mWidth;
    private int mHeight;
    private int mSelectedTitleColor;
    private int mChildCount;
    private int mPerPageMaxTabCount;
    private int mIndicatorColor;
    private Rect mRect = new Rect();


    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(Color.TRANSPARENT);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mIndicatorColor = typedArray.getColor(R.styleable.ViewPagerIndicator_indicator_color, Color.RED);
        mSelectedTitleColor = typedArray.getColor(R.styleable.ViewPagerIndicator_indicator_selected_title_color, Color.BLACK);
        mPerPageMaxTabCount = typedArray.getInt(R.styleable.ViewPagerIndicator_max_pager_count, 4);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mTop = getMeasuredHeight();
        mHeight = mTop / 6;
        Resources resources = getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        mWidth = (displayMetrics.widthPixels / mPerPageMaxTabCount);
        setMeasuredDimension(mWidth * mChildCount, mTop + mHeight);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRect.set(0, mTop, mChildCount * mWidth, mTop + mHeight);
        mPaint.setColor(Color.rgb(0xee, 0xee, 0xee));
        canvas.drawRect(mRect, mPaint);
        mRect.set(mLeft, mTop, mLeft + mWidth, mTop + mHeight);
        mPaint.setColor(mIndicatorColor);
        canvas.drawRect(mRect, mPaint);
    }

    public void scroll(int position, float offset) {

        TextView textView;

        mLeft = (int) ((position + offset) * mWidth);

        if (position > mPerPageMaxTabCount - 2) {
            int off = (int) ((position - mPerPageMaxTabCount + 1 + offset) * mWidth);
            scrollTo(off, 0);
        }
        if (position + 1 < mChildCount) {
            textView = (TextView) getChildAt(position + 1);
            textView.setTextColor(Color.GRAY);
        }
        if (position - 1 >= 0) {
            textView = (TextView) getChildAt(position - 1);
            textView.setTextColor(Color.GRAY);
        }

        textView = (TextView) getChildAt(position);
        textView.setTextColor(mSelectedTitleColor);

        invalidate();
    }

    public void updateTab(final LinkedList<Marker> titles, final ViewPager viewpager) {
        removeAllViews();
        mChildCount = titles.size();
        for (final Marker title : titles) {
            TextView textView = new TextView(getContext());
            textView.setText(title.getTitle());
            LayoutParams layoutParams = new LayoutParams(getMeasuredWidth() / mPerPageMaxTabCount, LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.weight = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setTextAlignment(TEXT_ALIGNMENT_CENTER);
            }
            textView.setTextSize(14.5F);
            textView.setBackgroundResource(R.drawable.tab_viewpager);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewpager != null) {
                        viewpager.setCurrentItem(titles.indexOf(title));
                    }
                }
            });
            addView(textView, layoutParams);
        }
        invalidate();
    }
}

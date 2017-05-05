package com.ec.www.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ec.www.base.AbstractApplication;

/**
 * Created by huang on 2017/4/26.
 */

public class DimenUtils {

    public static int sp2px(float spValue) {
        float fontScale = AbstractApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp( float pxValue) {
        float fontScale = AbstractApplication.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int dp2px(int dipValue) {
        final float scale = AbstractApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        final float scale = AbstractApplication.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //根据百分比获取像素大小
    public static Size getPercentSizePx(float percentW, float percentH) {
        DisplayMetrics metrics = AbstractApplication.getContext()
                .getResources()
                .getDisplayMetrics();
        return new Size((int) (metrics.widthPixels * percentW),
                (int) (metrics.heightPixels * percentH));
    }

    //根据原大小设置视图大小
    public static Size setScaleViewPx(View view, int sourceWidth, int sourceHeight) {
        return setScaleViewPx(view, measureSize(view).getWidth(), sourceWidth, sourceHeight);
    }

    public static Size measureSize(View view) {
        view.measure(0, 0);
        int measureWidth = view.getMeasuredWidth();
        int measureHeight = view.getMeasuredHeight();
        return new Size(measureWidth, measureHeight);
    }

    public static Size calcSize(int targetWidth, int sourceWidth, int sourceHeight) {
        float sum = sourceWidth + sourceHeight;
        float heightScale = (sourceHeight / sum);
        float widthScale = (sourceWidth / sum);
        int calcHeight = (int) (((float) targetWidth / widthScale) * heightScale);
        return new Size(targetWidth, calcHeight);
    }

    //根据原大小设置视图大小
    public static Size setScaleViewPx(View view, int targetWidth, int sourceWidth, int sourceHeight) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        Size size = calcSize(targetWidth, sourceWidth, sourceHeight);
        if (params.height != size.getHeight()) {
            params.height = size.getHeight();
            view.setLayoutParams(params);
        }
        return size;
    }

    public static void setViewHeight(View view, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.height != height) {
            params.height = height;
        }
        view.setLayoutParams(params);
    }

    public static void setViewWidth(View view, int width) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params.width != width) {
            params.width = width;
        }
        view.setLayoutParams(params);
    }

    public static void setViewSize(View view, Size size) {
        setViewHeight(view, size.getHeight());
        setViewWidth(view, size.getWidth());
    }

    //根据大小转换Drawable
    public static Drawable setDrawableOfResource(Context context, @DrawableRes int resId, int width, int height) {
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(resId);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(width, height));
        return imageView.getDrawable();
    }

}

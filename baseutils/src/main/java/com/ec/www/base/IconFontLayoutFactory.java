package com.ec.www.base;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by huang on 2016/12/7.
 */

public class IconFontLayoutFactory implements LayoutInflaterFactory {

    private static Typeface mTypeFace;
    private AppCompatDelegate mAppCompatDelegate;

    public IconFontLayoutFactory(Context context,
                                 AppCompatDelegate appCompatDelegate) {
        if (mTypeFace == null) {
            mTypeFace = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
            mAppCompatDelegate = appCompatDelegate;
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View view = mAppCompatDelegate.createView(parent, name, context, attrs);
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(mTypeFace);
            }
        }
        return view;
    }
}

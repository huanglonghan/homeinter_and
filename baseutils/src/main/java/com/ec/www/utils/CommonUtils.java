package com.ec.www.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ec.www.BuildConfig;
import com.ec.www.R;
import com.ec.www.base.AbstractApplication;
import com.ec.www.base.http.HttpUtil;
import com.github.mzule.activityrouter.router.Routers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DefaultObserver;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * Created by huang on 2016/10/13 0013.
 */

public class CommonUtils {

    static {
        if (BuildConfig.DEBUG) {
            SEND_MSG_INTERVAL = 5;
        } else {
            SEND_MSG_INTERVAL = 60;
        }
    }

    private static final int SEND_MSG_INTERVAL;

    /**
     * 延时函数
     *
     * @param time 要延迟的时间
     * @return
     */
    public static Observable<Integer> setDelayCallback(int time) {
        if (time < 0) time = 0;
        final int countTime = time;
        return Observable.interval(0, 1, TimeUnit.SECONDS)
                .map(increaseTime -> countTime - increaseTime.intValue())
                .take(countTime + 1)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 重复发送短信延时函数
     *
     * @param view
     * @param resource
     */
    public static void setDelaySendMsg(TextView view, Fragment resource) {
        String strFormat = resource.getString(R.string.msg_tip);
        CommonUtils.setDelayCallback(SEND_MSG_INTERVAL)
                .subscribe(new DefaultObserver<Integer>() {

                    @Override
                    public void onError(Throwable e) {
                        if (resource.isVisible()) {
                            view.setText(resource.getString(R.string.send_verify_code));
                            view.setEnabled(true);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if (resource.isVisible()) {
                            view.setText(resource.getString(R.string.send_verify_code));
                            view.setEnabled(true);
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        if (resource.isVisible()) {
                            view.setEnabled(false);
                        }
                    }

                    @Override
                    public void onNext(Integer integer) {
                        if (resource.isVisible()) {
                            view.setText(String.format(strFormat, integer.toString()));
                        }
                    }
                });
    }

    /**
     * 给图片添加圆形遮罩
     *
     * @param context
     * @param view
     * @param id
     */
    public static void setCirclePortrait(Context context, ImageView view, @DrawableRes int id) {
        view.setScaleType(ImageView.ScaleType.CENTER);
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), id);
        Bitmap mask = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mask);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(source.getWidth() / 2, source.getHeight() / 2, source.getHeight() * 3 / 6, paint);

        Bitmap result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        canvas.drawBitmap(mask, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(source, 0, 0, paint);
        paint.setXfermode(null);
        view.setImageBitmap(result);
    }

    // 上传文件
    public static <T> void uploadImg(String host, Class<T> tClass, File file, BiConsumer<T, RequestBody> response,
                                     @Nullable Function<OkHttpClient.Builder, OkHttpClient> clientCallback) {
        Retrofit retrofit = HttpUtil.createRetrofit(host, null, clientCallback);
        T service = retrofit.create(tClass);
        RequestBody body = RequestBody.create(MediaType.parse("image/png"), file);
        try {
            response.accept(service, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 上传文件
    public static <T> void uploadImg(String host, Class<T> tClass, File file, BiConsumer<T, RequestBody> response) {
        uploadImg(host, tClass, file, response, null);
    }

    public enum PositionType {
        LEFT,
        TOP,
        BOTTOM,
        RIGHT
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           @DrawableRes int redRes,
                                           @ColorRes int colorRes,
                                           PositionType position) {
        setTextViewDrawable(view, redRes, colorRes, null, position);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           @DrawableRes int redRes,
                                           @Nullable Size size,
                                           PositionType position) {
        setTextViewDrawable(view, getDrawable(view.getContext(), redRes), size, position);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           Drawable drawable,
                                           @Nullable Size size,
                                           PositionType position) {
        Size sizeBounds = size;
        if (drawable == null) {
            return;
        }

        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        float density = view.getContext().getResources().getDisplayMetrics().density;
        if (sizeBounds == null) {
            sizeBounds = new Size(drawable.getMinimumWidth(), drawable.getMinimumHeight());
        } else {
            sizeBounds = new Size((int) (size.getWidth() * density), (int) (size.getHeight() * density));
        }
        drawable.setBounds(0, 0, sizeBounds.getWidth(), sizeBounds.getHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           Drawable drawable,
                                           PositionType position) {
        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }
        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           Drawable drawable,
                                           @ColorRes int colorRes,
                                           @Nullable Size size,
                                           PositionType position) {
        Size sizeBounds = size;
        int color = getColor(view.getContext(), colorRes);

        if (drawable == null) {
            return;
        }

        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        float density = view.getContext().getResources().getDisplayMetrics().density;
        if (sizeBounds == null) {
            sizeBounds = new Size(drawable.getMinimumWidth(), drawable.getMinimumHeight());
        } else {
            sizeBounds = new Size((int) (size.getWidth() * density), (int) (size.getHeight() * density));
        }
        drawable.setBounds(0, 0, sizeBounds.getWidth(), sizeBounds.getHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        view.setTextColor(color);
    }

    //设置TextView 的 drawable
    public static void setTextViewDrawable(TextView view,
                                           @DrawableRes int redRes,
                                           @ColorRes int colorRes,
                                           @Nullable Size size,
                                           PositionType position) {
        Size sizeBounds = size;
        Drawable drawable = getDrawable(view.getContext(), redRes);
        int color = getColor(view.getContext(), colorRes);

        if (drawable == null) {
            return;
        }

        Drawable drawableLeft = null, drawableTop = null, drawableBottom = null, drawableRight = null;
        float density = view.getContext().getResources().getDisplayMetrics().density;
        if (sizeBounds == null) {
            sizeBounds = new Size(drawable.getMinimumWidth(), drawable.getMinimumHeight());
        } else {
            sizeBounds = new Size((int) (size.getWidth() * density), (int) (size.getHeight() * density));
        }
        drawable.setBounds(0, 0, sizeBounds.getWidth(), sizeBounds.getHeight());
        switch (position) {
            case LEFT:
                drawableLeft = drawable;
                break;
            case TOP:
                drawableTop = drawable;
                break;
            case BOTTOM:
                drawableBottom = drawable;
                break;
            case RIGHT:
                drawableRight = drawable;
                break;
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);
        view.setTextColor(color);
    }

    public static void setImageVector(ImageView view, @DrawableRes int resId) {
        view.setImageDrawable(VectorDrawableCompat.create(view.getResources(), resId, view.getContext().getTheme()));
    }

    public static Drawable getVectorDrawable(Context context, @DrawableRes int resId) {
        return VectorDrawableCompat.create(context.getResources(), resId, context.getTheme());
    }

    public static Drawable getVectorDrawable(@DrawableRes int resId) {
        return VectorDrawableCompat.create(AbstractApplication.getContext().getResources(), resId, AbstractApplication.getContext().getTheme());
    }

    public static boolean isCanScroll(RecyclerView v, int scrollY) {
        return scrollY + v.getMeasuredHeight()
                >= ((v.getChildAt(0).getMeasuredHeight() * 0.9));
    }

    public static int getStatusBarHeight() {
        Resources resources = AbstractApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static int getNavigationBarHeight() {
        Resources resources = AbstractApplication.getContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    public static String getParam(Intent intent, String param) {
        Uri uri = intent.getData();
        if (uri == null) {
            uri = Uri.parse(intent.getStringExtra(Routers.KEY_RAW_URL));
        }
        return uri.getQueryParameter(param);
    }

    public static String getAssetsFileAscii(Context context, String fileName) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(fileName);
            int size = stream.available();
            byte[] buff = new byte[size];
            stream.read(buff);
            stream.close();
            return new String(buff, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean compareList(List<?> listOne, List<?> listTwo) {
        if (listOne == null) {
            if (listTwo != null) {
                return false;
            }
        } else {
            if (listTwo == null) return false;
            if (listOne == listTwo) return true;
            int size = listTwo.size();
            if (size != listOne.size()) return false;
            for (int i = 0; i < size; i++) {
                if (!listOne.get(i).equals(listTwo.get(i))) return false;
            }
        }
        return true;
    }

    public static <T> boolean compareT(T listOne, T listTwo) {
        if (listOne == null) {
            if (listTwo != null) {
                return false;
            }
        } else {
            if (listTwo == null) return false;
            if (!listOne.equals(listTwo)) return false;
        }
        return true;
    }

    public static void move(List<?> list, int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            //向下拖动
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            //向上拖动
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
    }

    public static int getColor(Context context, @ColorRes int resId) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(resId, context.getTheme());
        } else {
            color = context.getResources().getColor(resId);
        }
        return color;
    }

    public static String getString(@StringRes int resId) {
        return AbstractApplication.getContext().getResources().getString(resId);
    }

    public static String getString(Context context, @StringRes int resId) {
        return context.getResources().getString(resId);
    }

    //资源id获取drawable
    public static Drawable getDrawable(Context context, @DrawableRes int resId) {
        Drawable drawable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable = context.getResources().getDrawable(resId, context.getTheme());
        } else {
            drawable = context.getResources().getDrawable(resId);
        }
        return drawable;
    }


    public static void fitSystem(ViewGroup group) {
        ViewGroup.LayoutParams params = group.getLayoutParams();
        int marginHeight = getStatusBarHeight();
        if (params != null)
            params.height += marginHeight;
        group.setPadding(group.getPaddingLeft(), marginHeight, group.getPaddingRight(), group.getPaddingBottom());
    }

    public static void fitSystem(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        int marginHeight = getStatusBarHeight();
        if (params != null)
            params.height += marginHeight;
        view.setPadding(view.getPaddingLeft(), marginHeight, view.getPaddingRight(), view.getPaddingBottom());
    }

    public static boolean hasFlag(Window window, int flags) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        return (attrs.flags & flags) == flags;
    }

    public static void setSpan(TextView view, String format, int textSize) {
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(textSize, true);
        SpannableString spanVal = new SpannableString(format);
        int littleIndex = format.indexOf("\n");
        spanVal.setSpan(span, littleIndex, format.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        view.setText(spanVal);
    }

    public static Uri path2Uri(String absolutePath) {
        return Uri.parse("file://" + absolutePath);
    }

    public static boolean isFirstOpen() {
        String IS_FIRST_OPEN = "is_first_open";
        return SPUtil.init().getBoolean(IS_FIRST_OPEN, true);
    }

    public static void setFirstOpen(boolean isFirst) {
        String IS_FIRST_OPEN = "is_first_open";
        SPUtil.init().putBoolean(IS_FIRST_OPEN, isFirst);
    }

    public static boolean isLogin() {
        String IS_FIRST_OPEN = "is_login";
        return SPUtil.init().getBoolean(IS_FIRST_OPEN, false);
    }

    public static void setLogin(boolean isLogin) {
        String IS_LOGIN = "is_login";
        SPUtil.init().putBoolean(IS_LOGIN, isLogin);
    }

    public static String getApplicationName(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        return (String) packageManager.getApplicationLabel(applicationInfo);
    }

    public static String mergePath(String... dirs) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < dirs.length; i++) {
            if (i != 0) {
                builder.append(File.separatorChar);
            }
            builder.append(dirs[i]);
        }
        return builder.toString();
    }

    public static void printStackTrace(Throwable e, boolean isDebug) {
        if (isDebug || Debug.isDebuggerConnected()) {
            e.printStackTrace();
        }
    }

    public static void loadImage(ImageView view, String url,
                                 Function<DrawableRequestBuilder<String>, DrawableRequestBuilder<String>> customCallback) {
        DrawableRequestBuilder<String> builder = Glide.with(view.getContext()).load(url).diskCacheStrategy(DiskCacheStrategy.ALL);
        if (customCallback != null) {
            try {
                builder = customCallback.apply(builder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        builder.into(view);
    }

    public static void loadImage(ImageView view, String url) {
        loadImage(view, url, null);
    }

    public static int getAdapterPosition(RecyclerView view, View tagView) {
        View holder = view.findContainingItemView(tagView);
        return view.getChildAdapterPosition(holder);
    }
}

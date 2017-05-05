package com.ec.www.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.ec.www.R;
import com.ec.www.base.AbstractApplication;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by huang on 2017/3/21.
 */

public class ShareUtil {

    private Tencent tencent;
    private IWXAPI IWXApi;
    private IWeiboShareAPI weiBoShareAPI;

    private IUiListener mUiListener;

    //QQ
    private static String QQ_ID;

    //WeChet
    private static String WECHAT_KEY;

    //WeiBo
    private static String WEIBO_KEY;
    private static String WEIBO_SCOP;

    private static class SHARE_UTILS {
        static {
            SHARE_UTILS = new ShareUtil();
        }

        private static final ShareUtil SHARE_UTILS;
    }

    private ShareUtil() {
        //QQ回调
        mUiListener = new IUiListener() {
            @Override
            public void onComplete(Object o) {

            }

            @Override
            public void onError(UiError uiError) {

            }

            @Override
            public void onCancel() {

            }
        };
    }

    public static ShareUtil getInstance() {
        return SHARE_UTILS.SHARE_UTILS;
    }

    public static ShareUtil initQQ(String QQId) {
        ShareUtil utils = ShareUtil.getInstance();
        QQ_ID = QQId;
        utils.init();
        return utils;
    }

    public static ShareUtil initWeChat(String weChatKey) {
        ShareUtil utils = ShareUtil.getInstance();
        WECHAT_KEY = weChatKey;
        utils.init();
        return utils;
    }

    public static ShareUtil initWeiBo(String weiBoKey) {
        ShareUtil utils = ShareUtil.getInstance();
        WEIBO_KEY = weiBoKey;
        utils.init();
        return utils;
    }

    private void init() {
        //QQ
        if (tencent == null && QQ_ID != null) {
            tencent = Tencent.createInstance(QQ_ID, AbstractApplication.getContext());
        }

        //微信
        if (IWXApi == null && WECHAT_KEY != null) {
            IWXApi = WXAPIFactory.createWXAPI(AbstractApplication.getContext(), WECHAT_KEY, true);
            IWXApi.registerApp(WECHAT_KEY);
        }

        //微博
        if (weiBoShareAPI == null && WEIBO_KEY != null) {
            weiBoShareAPI = WeiboShareSDK.createWeiboAPI(AbstractApplication.getContext(), WEIBO_KEY);
            weiBoShareAPI.registerApp();
        }
    }

    //QQ回调需调用
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mUiListener != null)
            Tencent.onActivityResultData(requestCode, resultCode, data, mUiListener);
    }

    public void QQShareWeb(Activity activity,
                           String title,
                           String digest,
                           String imageUrl,
                           String targetUrl,
                           String appName) {
        QQShare(activity, params -> {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, digest);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
            return params;
        });
    }

    public void QQShareImage(Activity activity,
                             String imageUrl,
                             String appName) {
        QQShare(activity, params -> {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            return params;
        });
    }

    public void QQShareMusic(Activity activity,
                             String title,
                             String digest,
                             String imageUrl,
                             String targetUrl,
                             String audioUrl,
                             String appName) {
        QQShare(activity, params -> {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_AUDIO);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, digest);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
            params.putString(QQShare.SHARE_TO_QQ_AUDIO_URL, audioUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            return params;
        });
    }

    public void QQShareApp(Activity activity,
                           String title,
                           String digest,
                           String imageUrl,
                           String appName) {
        QQShare(activity, params -> {
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_APP);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QQShare.SHARE_TO_QQ_SUMMARY, digest);
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, imageUrl);
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, appName);
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
            return params;
        });
    }

    public void QQShareQzoneWeb(Activity activity,
                                String title,
                                String digest,
                                String targetUrl,
                                ArrayList<String> imageUrls) {
        QQShareQzone(activity, (params -> {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, digest);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);
            return params;
        }));
    }

    public void QQShareQzoneImage(Activity activity,
                                  String title,
                                  String digest,
                                  String targetUrl,
                                  ArrayList<String> imageUrls) {
        QQShareQzone(activity, (params -> {
            params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, title);
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, digest);
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, targetUrl);
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, imageUrls);
            return params;
        }));
    }

    public void QQShare(Activity activity, Function<Bundle, Bundle> callback) {
        Bundle params = new Bundle();
        try {
            params = callback.apply(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tencent != null && mUiListener != null)
            tencent.shareToQQ(activity, params, mUiListener);
    }

    public void QQShareQzone(Activity activity, Function<Bundle, Bundle> callback) {
        Bundle params = new Bundle();
        try {
            params = callback.apply(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (tencent != null && mUiListener != null)
            tencent.shareToQzone(activity, params, mUiListener);
    }

    public void WeiXinShare() {

    }

    public static void getImagePath(RequestManager glide,
                                    String imageUrl,
                                    Action startCallback,
                                    Consumer<Throwable> errorCallback,
                                    Consumer<String> completeCallback) {
        Observable.just(imageUrl)
                .observeOn(Schedulers.io())
                .doOnComplete(startCallback)
                .map((url) -> {
                    FutureTarget<File> future = glide.load(url)
                            .downloadOnly(500, 500);
                    try {
                        File cacheFile = future.get();
                        return cacheFile.getAbsolutePath();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .timeout(3, TimeUnit.SECONDS)
                .doOnError(errorCallback)
                .subscribe(completeCallback);
    }

    public void WeiBoShare(Activity activity, String title, String targetUrl, String imageUrl, String description) {
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = title;
        mediaObject.description = description;
        Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), R.drawable.a1);
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = targetUrl;

        TextObject textObject = new TextObject();
        textObject.text = description;

        ImageObject imageObject = new ImageObject();
        bitmap = BitmapFactory.decodeFile(imageUrl);
        imageObject.setImageObject(bitmap);

        weiboMessage.textObject = textObject;
        weiboMessage.imageObject = imageObject;
        weiboMessage.mediaObject = mediaObject;

        sendMultiMessage(activity, weiboMessage, targetUrl);
    }

    private void sendMultiMessage(Activity activity, WeiboMultiMessage weiboMessage, String targetUrl) {
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;

        if (WEIBO_KEY == null || WEIBO_SCOP == null) return;
        AuthInfo authInfo = new AuthInfo(activity, WEIBO_KEY, targetUrl, WEIBO_SCOP);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(activity);
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }

        if (weiBoShareAPI != null) {
            weiBoShareAPI.sendRequest(activity, request, authInfo, token, new WeiboAuthListener() {

                @Override
                public void onWeiboException(WeiboException arg0) {
                }

                @Override
                public void onComplete(Bundle bundle) {
                    Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                    AccessTokenKeeper.writeAccessToken(activity, newToken);
                }

                @Override
                public void onCancel() {
                }
            });
        }
    }


    public static void otherShare(Context context, int stringRes) {
        otherShare(context, context.getString(stringRes));
    }

    public static void otherShareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(shareIntent, title));
    }

    public static void otherShare(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.action_share));
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.action_share)));
    }

    public static void copyLink(String clipText) {
        ClipboardManager cm = (ClipboardManager) AbstractApplication.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setPrimaryClip(ClipData.newPlainText("", clipText));
        Toast.makeText(AbstractApplication.getContext(), "复制成功", Toast.LENGTH_SHORT).show();
    }

    private static void saveView(Observable<Bitmap> createBitmap, File saveParent, String saveChild, Consumer<Uri> completeCallback) {
        createBitmap.observeOn(Schedulers.io())
                .subscribe((picture) -> {
                    File appDir = new File(saveParent, saveChild);
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = System.currentTimeMillis() + ".jpg";
                    File file = new File(appDir, fileName);
                    try {
                        FileOutputStream fos = new FileOutputStream(file);
                        picture.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                        fos.flush();
                        fos.close();
                        completeCallback.accept(Uri.parse("file://" + file.getAbsoluteFile()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(picture != null && !picture.isRecycled()){
                            // 回收并且置为null
                            picture.recycle();
                            picture = null;
                        }
                        System.gc();
                    }
                });
    }

    public static void saveViewAsPicture(View captureView, Action captureBeforeCallback, Consumer<Uri> completeCallback, String saveDirectory) {
        saveViewAsPicture(Observable.just(captureView), captureBeforeCallback, completeCallback, saveDirectory);
    }

    public static void saveViewAsPicture(View captureView, Action captureBeforeCallback, Consumer<Uri> completeCallback,
                                         File savePath, String saveDirectory) {
        saveViewAsPicture(Observable.just(captureView), captureBeforeCallback, completeCallback, savePath, saveDirectory);
    }

    public static void saveViewAsPicture(Observable<View> viewObservable,
                                         Action captureAfterCallback,
                                         Consumer<Uri> completeCallback,
                                         File savePath,
                                         String saveDirectory) {
        saveView(viewObservable.observeOn(Schedulers.newThread())
                        .map((v) -> {
                            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
                            Canvas canvas = new Canvas(bitmap);
                            v.draw(canvas);
                            return bitmap;
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnComplete(captureAfterCallback)
                , savePath, saveDirectory, completeCallback);
    }

    public static void saveViewAsPicture(Observable<View> viewObservable,
                                         Action captureAfterCallback,
                                         Consumer<Uri> completeCallback,
                                         String saveDirectory) {
        saveViewAsPicture(viewObservable, captureAfterCallback, completeCallback, Environment.getExternalStorageDirectory(), saveDirectory);
    }

    public static void saveBitmapAsPicture(Observable<Bitmap> createBitmap, Consumer<Uri> completeCallback, String saveDirectory) {
        saveView(createBitmap, Environment.getExternalStorageDirectory(), saveDirectory, completeCallback);
    }
}

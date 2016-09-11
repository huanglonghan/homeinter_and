package com.longhan.huang.homeinter.utls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.bumptech.glide.manager.RequestManagerTreeNode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.longhan.huang.homeinter.config.Config;
import com.longhan.huang.homeinter.module.QQAuthorizeInfo;
import com.longhan.huang.homeinter.module.QQUserInfo;
import com.tencent.connect.UserInfo;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;

import java.util.Objects;

/**
 * Created by 龙汗 on 2016/4/19.
 */
public class QQLogin  implements IUiListener{
    Tencent mTencent;
    final static String SCOPE = "get_user_info,get_simple_userinfo";
    Context context;
    QQLoginCallback callback;

    public QQLogin(Context context,Activity activity,QQLoginCallback callback) {
        this.context = context;
        this.callback=callback;
        doLogin(activity);
    }

    public void resultCallback(int var0, int var1, Intent var2){
        Tencent.onActivityResultData(var0, var1, var2,this);
    }

    private void doLogin(Activity activity) {
        mTencent = Tencent.createInstance(Config.TENCENT_APP_ID, context);
        if (!mTencent.isSessionValid()){
            mTencent.login(activity, SCOPE,this);
        }
    }

    private void doLogout() {
        if (mTencent != null) {
            mTencent.logout(context);
        }
    }

    @Override
    public void onComplete(Object o) {
        String openID = ((JSONObject)o).optString("openid");
        String accessToken = ((JSONObject)o).optString("access_token");
        String expires = ((JSONObject)o).optString("expires_in");
        mTencent.setOpenId(openID);
        mTencent.setAccessToken(accessToken, expires);
        if (!openID.equals("") && !accessToken.equals("")){
            doGetUserInfo();
        }else{
            callback.onError(null);
            doLogout();
        }
    }

    @Override
    public void onError(UiError uiError) {
        callback.onError(uiError);
    }

    @Override
    public void onCancel() {
        callback.onError(null);
    }

    private void doGetUserInfo(){
        UserInfo userInfo = new UserInfo(context,mTencent.getQQToken());
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Gson gson = new Gson();
                QQUserInfo qqUserInfo = gson.fromJson(o.toString(), QQUserInfo.class);
                callback.onSuccess(qqUserInfo,mTencent.getOpenId());
                doLogout();
            }

            @Override
            public void onError(UiError uiError) {
                callback.onError(uiError);
                doLogout();
            }

            @Override
            public void onCancel() {
                callback.onError(null);
                doLogout();
            }
        });
    }

    public interface QQLoginCallback{
        void onSuccess(QQUserInfo qqUserInfo,String openId);
        void onError(UiError uiError);
    }

}

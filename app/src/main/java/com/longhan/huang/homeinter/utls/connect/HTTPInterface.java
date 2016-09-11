package com.longhan.huang.homeinter.utls.connect;

/**
 * Created by 龙汗 on 2016/3/7.
 */
public class HTTPInterface {
    private static final String connectKV = "opt=init&deviceId=%s&time=%d";

    private static final String getUserNickNameKV = "opt=getNickname&uid=%s&time=%d";

    private static final String setUserNickNameKV = "opt=setNickname&uid=%s&nickname=%s&time=%d";

    public static String connectMsg(String deviceId){
        return String.format(connectKV,deviceId,System.currentTimeMillis());
    }

    public static String getUserNickNameMsg(String uid){
        return String.format(getUserNickNameKV,uid,System.currentTimeMillis());
    }

    public static String setUserNickNameMsg(String uid,String nickname){
        return String.format(setUserNickNameKV,uid,nickname,System.currentTimeMillis());
    }
}

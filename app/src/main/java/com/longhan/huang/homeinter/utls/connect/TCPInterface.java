package com.longhan.huang.homeinter.utls.connect;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by 龙汗 on 2016/2/3.
 */
public class TCPInterface {

    private static final String heartJson = "{" +
            "\"opt\":\"heart\"," +
            "\"lon\":%f," +
            "\"lat\":%f," +
            /*
            "\"alt\":%f," +
            "\"speed\":%f," +
            "\"bearing\":%f," +*/
            "\"accuracy\":%f," +
            "\"time\":%d" +
            "}";

    private static final String connectJson = "{" +
            "\"opt\":\"connect\"," +
            "\"tCode\":\"%s\"," +
            "\"uid\":\"%s\"," +
            "\"time\":%d" +
            "}";

    private static final String updateJson = "{" +
            "\"opt\":\"update\"," +
            "\"time\":%d" +
            "}";

    private static final String clearJson = "{" +
            "\"opt\":\"clear\"," +
            "\"time\":%d" +
            "}";

    /**
     * 产生主动更新json
     *
     * @return updateJson String
     */
    public static String updateMsg() {
        return String.format(updateJson, System.currentTimeMillis());
    }

    /**
     * 产生连接json
     *
     * @param tCode    String
     * @param uid      String
     * @return connectJson String
     */
    public static String connectMsg(String tCode, String uid) {
        return String.format(connectJson, tCode, uid, System.currentTimeMillis());
    }

    /**
     * 产生心跳消息
     *
     * @param lon double
     * @param lat double
     * @return heartJson String
     */
    public static String heartMsg(double lon, double lat,/* double alt, float speed, float bearing,*/ float accuracy) {
        return String.format(heartJson, lon, lat,/* alt, speed, bearing, */accuracy, System.currentTimeMillis());
    }

    public static String clearInvalidUserMsg() {
        return String.format(clearJson, System.currentTimeMillis());
    }


    //检查协议
    public static int checkProtocol(byte[] buffer, int dataLength) {
        int height =  ((((int) buffer[0]) << 8));
        int low  = (((int) buffer[1]) & 0xff);
        int len = height|low ;
        return  len - (dataLength - 2);
    }

    //解析协议
    public static String parseProtocol(StringBuilder buffer) {
        return buffer.substring(2,buffer.length());
    }

    //构建协议
    public static byte[] buildProtocol(String buffer) {
        int len = buffer.length();
        buffer = "我" + buffer;
        byte[] buf = new byte[0];
        try {
            buf = buffer.getBytes("gbk");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        buf[1] = (byte) ((len) & 0xff);
        buf[0] = (byte) ((len >> 8) & 0xff);
        return buf;
    }


}

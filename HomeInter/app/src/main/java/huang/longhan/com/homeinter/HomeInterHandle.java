package huang.longhan.com.homeinter;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.ArraySet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import huang.longhan.com.homeinter.service.BaseTCPCommunication;
import huang.longhan.com.homeinter.service.BaseTCPProtocol;
import huang.longhan.com.homeinter.service.HomeInterService;
import huang.longhan.com.homeinter.utls.ErrorCode;

/**
 * Created by 龙汗 on 2016/3/3.
 */
public class HomeInterHandle extends Handler {

    private BaseTCPCommunication mBaseTCPCommunication;
    private HomeInterService mHomeInterService;

    public HomeInterHandle(HomeInterService homeInterService) {
        super();
        this.mHomeInterService = homeInterService;
    }

    public void setTcpConnect(BaseTCPCommunication baseTCPCommunication) {
        this.mBaseTCPCommunication = baseTCPCommunication;
    }

    @Override
    public void handleMessage(Message msg) {
        if (mBaseTCPCommunication == null || mHomeInterService == null) {
            return;
        }
        switch (msg.what) {
            case BaseTCPCommunication.RECEIVE_DATA_MSG:
                Bundle bundle = msg.getData();
                String retMsg = bundle.getString(BaseTCPCommunication.RECEIVE_DATA);
                receiveDataHandle(retMsg);
                break;
        }
    }

    private void receiveDataHandle(String retMsg) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(retMsg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jsonObject == null) {
            return;
        }
        int errorCode = jsonObject.optInt("errorCode", -1);
        switch (errorCode) {
            case -1:
                return;
            case ErrorCode.NOT_ONLINE:
                mBaseTCPCommunication.sendMsg(BaseTCPProtocol.connectMsg(mHomeInterService.getTCode(), mHomeInterService.getUid()));
                break;
            case ErrorCode.SUCCESS:
                JSONObject result = jsonObject.optJSONObject("result");
                if (result == null) {
                    return;
                }
                onMessage(result);
                break;
        }

    }

    private void onMessage(JSONObject msg) {
        switch (msg.optString("opt")) {
            case "updateLocation":
                onUpdateLocation(msg);
                break;
            case "clearInvalidUser":
                onClearInvalidUser(msg);
                break;
        }
    }

    private void onUpdateLocation(JSONObject msg) {
        JSONArray jsonArray = msg.optJSONArray("content");
        if (jsonArray == null) {
            return;
        }

        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            JSONObject jsonObject = jsonArray.optJSONObject(i);
            String uid = jsonObject.optString("uid");
            double lat = jsonObject.optDouble("lat");
            double lon = jsonObject.optDouble("lon");
//          double alt = jsonObject.optDouble("alt");
//          float speed = (float)jsonObject.optDouble("speed");
//          float bearing = (float)jsonObject.optDouble("bearing");
            float accuracy = (float) jsonObject.optDouble("accuracy");
            long time = jsonObject.optLong("time");
            Location location = new Location(mHomeInterService.getLocationProviderName());
            location.setLatitude(lat);
            location.setLongitude(lon);
            location.setAccuracy(accuracy);
            location.setTime(time);
            mHomeInterService.setUserMetaDatas(uid, uid,location);
        }
    }

    public void onClearInvalidUser(JSONObject msg){
        JSONArray jsonArray = msg.optJSONArray("content");
        if (jsonArray == null) {
            return;
        }

        LinkedList<String> uIds = new LinkedList<>();
        int count = jsonArray.length();
        for (int i = 0; i < count; i++) {
            String uid = jsonArray.optString(i);
            uIds.add(uid);
        }
        mHomeInterService.removeUserMetaDatas(uIds);
    }

}
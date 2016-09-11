package huang.longhan.com.homeinter.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 龙汗 on 2016/2/12.
 */
public class BaseHTTPCommunication {

    private HttpURLConnection httpURLConnection;
    public static final int port = 7272;

    private BaseHTTPCommunication(){
    }

    public static BaseHTTPCommunication createConnect(String url){
        BaseHTTPCommunication baseHTTPCommunication = new BaseHTTPCommunication();
        if(!baseHTTPCommunication.initConnect(url)){
            return null;
        }
        return baseHTTPCommunication;
    }

    private boolean initConnect(String url){
        try {
            httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestMethod("POST");
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
            return false;
        }
        httpURLConnection.setDefaultUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        return true;
    }

    public String sendMsg(String msg){
        BufferedWriter bufferedWriter = null;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        char[] buffer = new char[1024];
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            bufferedWriter.write(msg.toCharArray());
            bufferedWriter.flush();
            if (httpURLConnection.getResponseCode() != 200) {
                return null;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            int len;
            do {
                len = bufferedReader.read(buffer);
                stringBuilder.append(buffer, 0, len);
            } while (len == 1024);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try{
                if (bufferedReader!=null){
                    bufferedReader.close();
                }
                if (bufferedWriter!=null){
                    bufferedWriter.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            disconnect();
        }

        return stringBuilder.toString();

    }


    public void disconnect() {
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        disconnect();
        super.finalize();
    }
}

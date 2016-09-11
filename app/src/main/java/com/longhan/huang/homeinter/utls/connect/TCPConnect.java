package com.longhan.huang.homeinter.utls.connect;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.longhan.huang.homeinter.config.Config;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


/**
 * Created by 龙汗 on 2016/2/2.
 */
public class TCPConnect {
    private Socket socket;
    private volatile Boolean receiveFlag = true;
    private Thread receiveThread;
    private BufferedInputStream bufferedInputStream;
    private BufferedOutputStream bufferedOutputStream;
    public static final String RECEIVE_DATA = "receiveData";
    public static final int RECEIVE_DATA_MSG = 0x12;
    public static final int RECEIVE_RETRY_TIME = 1000;

    private TCPConnect() {
    }

    public static TCPConnect createConnect(Handler handler){
        TCPConnect TCPCommunication = new TCPConnect();
        if(!TCPCommunication.initSocket(handler)){
            return null;
        }
        TCPCommunication.receiveMsg();
        return TCPCommunication;
    }

    //初始化
    public boolean initSocket(final Handler handler) {
        try {
            socket = new Socket(Config.locationServerIP, Config.locationServerPort);
            socket.setTcpNoDelay(true);
            bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
            bufferedInputStream = new BufferedInputStream((socket.getInputStream()));
        } catch (IOException e) {
            closeSocket();
            return false;
        }
        if (receiveThread == null) {
            receiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1024];
                    StringBuilder strBuffer = new StringBuilder();
                    int dataLength = 1;
                    int checkLen=0;
                    while (receiveFlag) {
                        if (dataLength == -1) {
                            closeSocket();
                            try {
                                Thread.sleep(RECEIVE_RETRY_TIME);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (initSocket(handler)) {
                                dataLength = 1;
                            }
                            receiveFlag = true;
                        } else {
                            try {
                                while ((dataLength = bufferedInputStream.read(buffer)) != -1) {
                                    if (dataLength == 1024) {
                                        checkLen = TCPInterface.checkProtocol(buffer, dataLength);
                                        if (checkLen==0){
                                            strBuffer.append(new String(buffer));
                                            Message message = handler.obtainMessage(RECEIVE_DATA_MSG);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(RECEIVE_DATA, TCPInterface.parseProtocol(strBuffer));
                                            message.setData(bundle);
                                            message.sendToTarget();
                                            strBuffer.delete(0, strBuffer.length());
                                            break;
                                        }else if(checkLen<0) {
                                            break;
                                        }
                                        strBuffer.append(new String(buffer));
                                    } else {
                                        if (strBuffer.length()<1024){
                                            if (TCPInterface.checkProtocol(buffer, dataLength)!=0){
                                                break;
                                            }
                                        }else if(strBuffer.length()==1024){
                                            if (checkLen!=dataLength){
                                                break;
                                            }
                                        }
                                        strBuffer.append(new String(buffer));
                                        Message message = handler.obtainMessage(RECEIVE_DATA_MSG);
                                        Bundle bundle = new Bundle();
                                        bundle.putString(RECEIVE_DATA, TCPInterface.parseProtocol(strBuffer));
                                        message.setData(bundle);
                                        message.sendToTarget();
                                        strBuffer.delete(0, strBuffer.length());
                                    }
                                }

                            } catch (IOException e) {
                                   e.printStackTrace();
                            }
                        }
                    }
                }
            }, "receiveMsg");
        }
        return true;
    }

    //发送消息
    public boolean sendMsg(String str) {
        if (socket == null) {
            return false;
        }
        try {
            bufferedOutputStream.write(TCPInterface.buildProtocol(str));
            bufferedOutputStream.flush();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    //接收消息
    private boolean receiveMsg() {
        if (socket == null || receiveThread == null) {
            receiveFlag = false;
            return false;
        }
        receiveFlag = true;
        receiveThread.start();
        return true;
    }

    public void stopReceiveMsg() {
        receiveFlag = false;
    }

    public boolean isRunReceviceMsg() {
        return receiveThread.isAlive();
    }

    public boolean closeSocket() {
        receiveFlag = false;
        try {
            if (socket != null) {
                socket.close();
            }
            if (bufferedInputStream != null) {
                bufferedInputStream.close();
            }
            if (bufferedOutputStream != null) {
                bufferedOutputStream.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    @Override
    protected void finalize() throws Throwable {
        closeSocket();
        super.finalize();
    }
}


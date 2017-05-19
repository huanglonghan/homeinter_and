package com.ec.www.base;

/**
 * Created by huang on 2017/4/27.
 */

public interface IStatus {
    int STATE_NORMAL = 0x01;
    int STATE_CREATE = 0x02;
    int STATE_START = 0x03;
    int STATE_RESUME = 0x04;
    int STATE_PAUSE = 0x05;
    int STATE_STOP = 0x06;
    int STATE_DESTROY = 0x07;

    int getStatus();
}

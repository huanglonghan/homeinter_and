package com.ec.www.base;

/**
 * Created by huang on 2017/4/27.
 */

public interface IStatus {
    int STATE_NORMAL = 0x001;
    int STATE_CREATE = 0x002;
    int STATE_START = 0x003;
    int STATE_RESUME = 0x004;
    int STATE_PAUSE = 0x005;
    int STATE_STOP = 0x006;
    int STATE_DESTROY = 0x007;

    int getStatus();
}

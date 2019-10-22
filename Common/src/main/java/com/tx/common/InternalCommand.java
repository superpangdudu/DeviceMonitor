package com.tx.common;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/9/27.
 */
public class InternalCommand implements Serializable {
    public static final int ACTION_CONNECT = 0;
    public static final int ACTION_START_ENGINE = 1;
    public static final int ACTION_SHUTDOWN_ENGINE = 2;
    public static final int ACTION_GET_GENERATOR_STATUS = 3;
    public static final int ACTION_GET_DEVICE_STATUS = 4;

    private String deviceId;
    private String host;
    private int port;
    private int action;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}

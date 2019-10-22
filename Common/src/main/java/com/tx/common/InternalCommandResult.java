package com.tx.common;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/10/8.
 */
public class InternalCommandResult implements Serializable {
    private String deviceId;
    private int result;
    private Object data;

    public InternalCommandResult() {

    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

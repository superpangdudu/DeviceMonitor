package com.tx.monitor.cmd;

import com.tx.monitor.cmd.DeviceCmd;
import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/27.
 */
public class GetBasicAlarm extends DeviceCmd {


    public GetBasicAlarm(Device device, byte[] initialData) {
        super(device, initialData);
    }

    @Override
    protected byte[] getCmdData() {
        return new byte[0];
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        return 0;
    }
}

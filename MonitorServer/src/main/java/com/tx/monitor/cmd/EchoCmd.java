package com.tx.monitor.cmd;

import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/20.
 */
public class EchoCmd extends DeviceCmd {
    protected EchoCmd(Device device, byte[] inputData) {
        super(device, inputData);
    }

    @Override
    protected byte[] getCmdData() {
        return new byte[] {0x31, 0x32, 0x33};
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        device.write(data);
        return 0;
    }
}

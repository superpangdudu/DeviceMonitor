package com.tx.monitor.cmd;

import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/20.
 */
public class StopEngineCmd extends DeviceCmd {
    protected StopEngineCmd(Device device, byte[] inputData) {
        super(device, inputData);
    }

    @Override
    protected byte[] getCmdData() {
        return new byte[] {0x00, 0x05, 0x00, 0x06, (byte) 0x00, 0x00};
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        // TODO check response
        return 0;
    }
}

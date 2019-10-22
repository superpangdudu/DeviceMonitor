package com.tx.monitor.cmd;

import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/20.
 */
public class StartEngineCmd extends DeviceCmd {

    public StartEngineCmd(Device device, byte[] data) {
        super(device, data);
    }

    @Override
    protected byte[] getCmdData() {
        return new byte[] {0x00, 0x05, 0x00, 0x06, (byte) 0xFF, 0x00};
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        // TODO check response
        return 0;
    }
}

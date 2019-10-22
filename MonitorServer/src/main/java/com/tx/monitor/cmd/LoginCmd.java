package com.tx.monitor.cmd;

import com.tx.monitor.device.Device;

/**
 * LoginCmd
 */
public class LoginCmd extends DeviceCmd {
    public LoginCmd(Device device, byte[] inputData) {
        super(device, inputData);
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

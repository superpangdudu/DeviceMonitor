package com.tx.monitor.cmd;

import com.tx.common.GeneratorStatus;
import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/27.
 */
public class GetGeneratorStatusCmd extends DeviceCmd {

    private GeneratorStatus generatorStatus;

    public GetGeneratorStatusCmd(Device device, byte[] initialData) {
        super(device, initialData);
    }

    public GeneratorStatus getGeneratorStatus() {
        return generatorStatus;
    }

    @Override
    protected byte[] getCmdData() {
        return new byte[] { 0x00, 0x03, 0x00, (byte) 0xBD, 0x00, 0x01 };
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        if (data.length != 11)
            return -1;

        byte high = data[9];
        byte low = data[10];

        generatorStatus = new GeneratorStatus(high, low);

        return 0;
    }
}

package com.tx.monitor.cmd;

import com.tx.common.DeviceStatus;
import com.tx.common.Utils;
import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/10/8.
 */
public class GetDeviceStatusCmd extends DeviceCmd {

    private DeviceStatus deviceStatus;
    private int fromRegister = 0; // default to get from the 1st register
    private int registerCount = 100; // default to read 100 registers

    public GetDeviceStatusCmd(Device device, byte[] initialData) {
        super(device, initialData);
    }

    public GetDeviceStatusCmd(Device device, byte[] initialData,
                              DeviceStatus deviceStatus, int fromRegister, int registerCount) {
        super(device, initialData);

        this.deviceStatus = deviceStatus;
        this.fromRegister = fromRegister;
        this.registerCount = registerCount;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    @Override
    protected byte[] getCmdData() {
        byte[] from = Utils.intToByte(fromRegister);
        byte[] count = Utils.intToByte(registerCount);

        return new byte[] { 0x00, 0x03, from[2], from[3], count[2], count[3] };
    }

    @Override
    protected int doHandleResponseImpl(byte[] data) {
        int size = registerCount * 2; // each register uses 2 bytes
        if (data.length < size)
            return -1;

        byte[] statusData = new byte[size];
        System.arraycopy(data, data.length - size, statusData, 0, size);

        if (deviceStatus == null) {
            deviceStatus = new DeviceStatus();
        }

        int ret = deviceStatus.update(fromRegister, registerCount, statusData);
        if (ret != 0) {
            deviceStatus = null;
            return ret;
        }

        return 0;
    }

    //===================================================================================
    public static void main(String[] args) {
        byte[] from = Utils.intToByte(260);
        int n = 0;
    }
}

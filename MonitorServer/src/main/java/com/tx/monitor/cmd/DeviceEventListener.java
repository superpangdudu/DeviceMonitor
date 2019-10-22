package com.tx.monitor.cmd;

import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.monitor.device.Device;

/**
 * Created by Administrator on 2019/9/20.
 */
public interface DeviceEventListener {
    void onStartEngine(Device device, int result);
    void onShutDownEngine(Device device, int result);
    void onUpdateDeviceStatus(Device device, DeviceStatus deviceStatus);
    void onUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus);
}

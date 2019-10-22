package com.tx.monitor;

import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.monitor.client.DeviceClient;
import com.tx.monitor.device.Device;
import com.tx.monitor.device.DeviceManagement;

import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2019/10/11.
 */
public class Reconnector implements DeviceManagement.DeviceManagementListener {

    @Override
    public void onDeviceAdded(Device device) {

    }

    @Override
    public void onDeviceRemoved(Device device) {
        InetSocketAddress socketAddress = (InetSocketAddress) device.getChannel().remoteAddress();
        String ip = socketAddress.getHostName();
        int port = socketAddress.getPort();

        DeviceClient client = new DeviceClient(device.getId(),
                ip, port,
                device.getChannel().eventLoop().parent());
        client.start();

    }

    @Override
    public void onDeviceEngineStarted(Device device, int result) {

    }

    @Override
    public void onDeviceEngineShutdown(Device device, int result) {

    }

    @Override
    public void onUpdateDeviceStatus(Device device, DeviceStatus deviceStatus) {

    }

    @Override
    public void onUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus) {

    }
}

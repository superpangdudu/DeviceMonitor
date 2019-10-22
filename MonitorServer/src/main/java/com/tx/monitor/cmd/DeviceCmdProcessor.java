package com.tx.monitor.cmd;

import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.monitor.device.Device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * DeviceCmdProcessor
 */
public class DeviceCmdProcessor {

    private static DeviceCmdProcessor INSTANCE = null;

    private DeviceCmdProcessor() {
    }

    public static DeviceCmdProcessor getInstance() {
        if (INSTANCE == null) {
            synchronized (DeviceCmdProcessor.class) {
                if (INSTANCE == null)
                    INSTANCE = new DeviceCmdProcessor();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    private ExecutorService executorService = Executors.newCachedThreadPool();
    // only one command on device at the same time
    private Map<Device, DeviceCmd> deviceCmdMap = new ConcurrentHashMap<>();

    //===================================================================================
    public void echo(Device device) {
        EchoCmd cmd = new EchoCmd(device, new byte[] {0x31, 0x32, 0x33, 0x34});
        doDeviceCmd(device, cmd);
    }

    public int startEngine(Device device) {
        DeviceCmd deviceCmd = new StartEngineCmd(device, null);
        return doDeviceCmd(device, deviceCmd);
    }

    public int shutdownEngine(Device device) {
        DeviceCmd deviceCmd = new StopEngineCmd(device, null);
        return doDeviceCmd(device, deviceCmd);
    }

    public DeviceStatus getDeviceStatus(Device device) {
        // get 100 registers starts from 1st register
        GetDeviceStatusCmd getDeviceStatusCmd = new GetDeviceStatusCmd(device, null);
        int ret = doDeviceCmd(device, getDeviceStatusCmd);
        if (ret != 0)
            return null;

        DeviceStatus deviceStatus = getDeviceStatusCmd.getDeviceStatus();

        // to read registers from 100 to 199
        getDeviceStatusCmd = new GetDeviceStatusCmd(device, null, deviceStatus, 100, 100);
        ret = doDeviceCmd(device, getDeviceStatusCmd);
        if (ret != 0)
            return null;

        deviceStatus = getDeviceStatusCmd.getDeviceStatus();

        // to read registers from 200 to 284
        getDeviceStatusCmd = new GetDeviceStatusCmd(device, null, deviceStatus, 200, 85);
        ret = doDeviceCmd(device, getDeviceStatusCmd);
        if (ret != 0)
            return null;

        return getDeviceStatusCmd.getDeviceStatus();
    }

    public GeneratorStatus getGeneratorStatus(Device device) {
        GetGeneratorStatusCmd getGeneratorStatusCmd = new GetGeneratorStatusCmd(device, null);
        doDeviceCmd(device, getGeneratorStatusCmd);

        return getGeneratorStatusCmd.getGeneratorStatus();
    }

    private int doDeviceCmd(Device device, DeviceCmd cmd) {
        // only one command allowed for one device at the same time
        // It's the same to synchronize inside Device
        synchronized (device) {
            if (hasOngoingCmd(device))
                return -100;

            deviceCmdMap.put(device, cmd);
        }

        int result = cmd.doCmd();
        return result;
    }

    //===================================================================================
    private boolean hasOngoingCmd(Device device) {
        if (deviceCmdMap.containsKey(device))
            return true;
        return false;
    }

    public void processData(final Device device, final byte[] data) {
        // if the data is a response of one of the waiting commands
        DeviceCmd cmd = deviceCmdMap.remove(device);
        if (cmd == null)
            return;

        final DeviceCmd tmp = cmd;
        executorService.execute(() -> {
            tmp.handleReceivedData(data);
        });
    }
}

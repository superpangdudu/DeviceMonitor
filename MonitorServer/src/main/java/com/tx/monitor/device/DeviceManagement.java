package com.tx.monitor.device;

import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.monitor.cmd.DeviceEventListener;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DeviceManagement
 */
public class DeviceManagement
        implements DeviceEventListener {

    public interface DeviceManagementListener {
        void onDeviceAdded(Device device);
        void onDeviceRemoved(Device device);
        void onDeviceEngineStarted(Device device, int result);
        void onDeviceEngineShutdown(Device device, int result);
        void onUpdateDeviceStatus(Device device, DeviceStatus deviceStatus);
        void onUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus);
    }

    private List<DeviceManagementListener> listeners = new ArrayList<>();

    public void addListener(DeviceManagementListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(DeviceManagementListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void notifyDeviceAdded(Device device) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onDeviceAdded(device);
        }
    }

    private void notifyDeviceRemoved(Device device) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onDeviceRemoved(device);
        }
    }

    private void notifyDeviceEngineStarted(Device device, int result) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onDeviceEngineStarted(device, result);
        }
    }

    private void notifyDeviceEngineShutdown(Device device, int result) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onDeviceEngineShutdown(device, result);
        }
    }

    private void notifyUpdateGeneratorStatus(Device device, DeviceStatus deviceStatus) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onUpdateDeviceStatus(device, deviceStatus);
        }
    }

    private void notifyUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus) {
        synchronized (listeners) {
            for (DeviceManagementListener listener : listeners)
                listener.onUpdateGeneratorStatus(device, generatorStatus);
        }
    }

    //===================================================================================
    private static DeviceManagement INSTANCE = null;

    private DeviceManagement() {
    }

    public static DeviceManagement getInstance() {
        if (INSTANCE == null) {
            synchronized (DeviceManagement.class) {
                if (INSTANCE == null)
                    INSTANCE = new DeviceManagement();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    private Map<String, Device> deviceMap = new ConcurrentHashMap<>();
    // FIXME
    private Map<Channel, Device> deviceConnectionMap = new ConcurrentHashMap<>();
    private Map<String, Object> connectingDeviceMap = new ConcurrentHashMap<>();

    //===================================================================================
    public Device getById(String id) {
        return deviceMap.get(id);
    }

    public Device getByChannel(Channel channel) {
        return deviceConnectionMap.get(channel);
    }

    public void handleDeviceData(Channel channel, byte[] data) {
        Device device = DeviceManagement.getInstance().getByChannel(channel);
        if (device == null)
            return;
        device.processData(data);
    }

    public void onDeviceConnecting(String id) {
        connectingDeviceMap.put(id, new Object());
    }

    public void onDeviceConnectionStatus(String id, boolean isConnected) {
        connectingDeviceMap.remove(id);
    }

    public boolean isDeviceConnecting(String id) {
        return connectingDeviceMap.containsKey(id);
    }

    //===================================================================================
    public Device newDevice(String id, Channel channel) {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) channel.remoteAddress();
        String address = inetSocketAddress.getHostName();
        int port = inetSocketAddress.getPort();

        Device device = new Device();
        device.setChannel(channel);

        device.setId(id);

        device.setListener(this);

        return device;
    }

    public void addDevice(Device device) {
        if (device.getId() != null && !device.getId().equals(""))
            deviceMap.put(device.getId(), device);

        deviceConnectionMap.put(device.getChannel(), device);

        //
        notifyDeviceAdded(device);
    }

    public void removeDevice(Device device) {
        if (device == null)
            return;

        deviceMap.remove(device.getId());
        deviceConnectionMap.remove(device.getChannel());

        //
        notifyDeviceRemoved(device);
    }

    //===================================================================================
    // DeviceEventListener implementation
    @Override
    public void onStartEngine(Device device, int result) {
        notifyDeviceEngineStarted(device, result);
    }

    @Override
    public void onShutDownEngine(Device device, int result) {
        notifyDeviceEngineShutdown(device, result);
    }

    @Override
    public void onUpdateDeviceStatus(Device device, DeviceStatus deviceStatus) {
        notifyUpdateGeneratorStatus(device, deviceStatus);
    }

    @Override
    public void onUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus) {
        notifyUpdateGeneratorStatus(device, generatorStatus);
    }
}

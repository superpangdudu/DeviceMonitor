package com.tx.monitor.device;


import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.monitor.cmd.DeviceCmdProcessor;
import com.tx.monitor.cmd.DeviceEventListener;
import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * Device
 */
public class Device implements Serializable {
    public static final int STATUS_IDLE = 0;
    public static final int STATUS_WORKING = 1;

    /**
     * device id
     */
    private String id;
    /**
     *
     */
    private int status;
    /**
     * FIXME
     * I don't think we should use the concrete connection directly
     */
    private transient Channel channel;
    /**
     *
     */
    private transient DeviceEventListener listener;
    /**
     *
     */
    private transient DeviceWriter deviceWriter;

    //===================================================================================
    protected Device() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    private void setStatus(int status) {
        this.status = status;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public DeviceEventListener getListener() {
        return listener;
    }

    public void setListener(DeviceEventListener listener) {
        this.listener = listener;
    }

    public DeviceWriter getDeviceWriter() {
        return deviceWriter;
    }

    public void setDeviceWriter(DeviceWriter deviceWriter) {
        this.deviceWriter = deviceWriter;
    }

    //===================================================================================
    public void write(byte[] data) {
        deviceWriter.write(data);
    }

    public void processData(byte[] data) {
        DeviceCmdProcessor.getInstance().processData(this, data);
    }

    //===================================================================================
    public void echo() {
        long start = System.currentTimeMillis();

        System.out.println("echo start");
        DeviceCmdProcessor.getInstance().echo(this);
        System.out.println("echo end after: " + (System.currentTimeMillis() - start));
    }

    public int startEngine() {
        int ret = DeviceCmdProcessor.getInstance().startEngine(this);
        if (ret == 0)
            setStatus(STATUS_WORKING);

        listener.onStartEngine(this, ret);
        return ret;
    }

    public int shutdownEngine() {
        int ret = DeviceCmdProcessor.getInstance().shutdownEngine(this);
        if (ret == 0)
            setStatus(STATUS_IDLE);

        listener.onShutDownEngine(this, ret);
        return ret;
    }

    public DeviceStatus getDeviceStatus() {
        DeviceStatus deviceStatus = DeviceCmdProcessor.getInstance().getDeviceStatus(this);
        if (deviceStatus != null) {
            listener.onUpdateDeviceStatus(this, deviceStatus);
        }

        return deviceStatus;
    }

    public GeneratorStatus getGeneratorStatus() {
        GeneratorStatus generatorStatus = DeviceCmdProcessor.getInstance().getGeneratorStatus(this);
        if (generatorStatus != null) {
            listener.onUpdateGeneratorStatus(this, generatorStatus);
        }

        return generatorStatus;
    }
}

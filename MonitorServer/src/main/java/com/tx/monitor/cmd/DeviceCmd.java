package com.tx.monitor.cmd;

import com.tx.monitor.device.Device;

/**
 * DeviceCmd
 * A base class of all device commands
 *
 * A valid ModBus protocol package should be:
 * 1. Device identifier, 1 byte
 * 2. Function code, 1 byte
 * 3.
 */
public abstract class DeviceCmd {

    /**
     * command synchronization object
     */
    private final Object object = new Object();

    protected Device device;
    protected byte[] initialData;

    private int result = -1;

    //===================================================================================
    protected DeviceCmd(Device device, byte[] initialData) {
        this.device = device;
        this.initialData = initialData;
    }

    public int getResult() {
        return result;
    }

    public int doCmd() {
        // get command data
        byte[] data = getCmdData();
        int length = data.length;

        // append {0x00, 0x00, 0x00, 0x00, length (2 bytes)}
        byte[] toSend = new byte[6 + length];
        toSend[4] = (byte) ((length & 0xFF00) >> 8);
        toSend[5] = (byte) (length & 0xFF);

        System.arraycopy(data, 0, toSend, 6, length);

        //
        device.write(toSend);

        // waiting for response
        synchronized (object) {
            try {
                result = -2; // for timeout

                object.wait(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public void handleReceivedData(byte[] data) {
        synchronized (object) {
            result = doHandleResponseImpl(data);

            object.notify();
        }
    }

    protected abstract byte[] getCmdData();
    protected abstract int doHandleResponseImpl(byte[] data);

    //===================================================================================
    public static void main(String[] args) {
        int tmp = 0b01;
        byte b = (byte) 0xFF;

        byte[] data = new byte[2];
        int length = 254;

        data[0] = (byte) ((length & 0xFF00) >> 8);
        data[1] = (byte) (length & 0xFF);

        tmp = (int) (data[1] & 0xFF);

        int n = 0;
    }
}

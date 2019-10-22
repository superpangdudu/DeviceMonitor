package com.tx.monitor.client;

import com.tx.common.InternalCommand;
import com.tx.common.Utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * Created by Administrator on 2019/10/8.
 */
public class UdpClient {

    private static void connectCmd(String id, String host, int port) throws Exception {
        InternalCommand cmd = new InternalCommand();
        cmd.setAction(InternalCommand.ACTION_CONNECT);
        cmd.setDeviceId(id);
        cmd.setHost(host);
        cmd.setPort(port);

        byte[] data = Utils.serialize(cmd);
        send(data);
    }

    private static void startEngine(String id) throws Exception {
        InternalCommand cmd = new InternalCommand();
        cmd.setAction(InternalCommand.ACTION_START_ENGINE);
        cmd.setDeviceId(id);

        byte[] data = Utils.serialize(cmd);
        send(data);
    }

    private static void getDeviceStatus(String id) throws Exception {
        InternalCommand cmd = new InternalCommand();
        cmd.setAction(InternalCommand.ACTION_GET_DEVICE_STATUS);
        cmd.setDeviceId(id);

        byte[] data = Utils.serialize(cmd);
        send(data);
    }

    private static void send(byte[] data) throws Exception {
        DatagramPacket packet = new DatagramPacket(data, data.length,
                new InetSocketAddress("192.168.1.55", 44414));

        DatagramSocket client = new DatagramSocket();
        client.send(packet);
    }

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("input test cmd id: ");
            String cmd = scanner.nextLine();

            String deviceId = "1";

            if (cmd.equals("1"))
                connectCmd(deviceId, "192.168.1.102", 502);
            else if (cmd.equals("2"))
                startEngine(deviceId);
            else if (cmd.equals("3"))
                getDeviceStatus(deviceId);
        }
    }
}

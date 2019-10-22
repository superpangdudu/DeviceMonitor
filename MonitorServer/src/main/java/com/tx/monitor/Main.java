package com.tx.monitor;


import com.tx.monitor.device.DeviceManagement;
import com.tx.monitor.server.InternalCmdHandler;
import com.tx.monitor.server.InternalCmdTransitServer;
import io.netty.util.ResourceLeakDetector;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;

import java.util.Scanner;

/**
 * Main
 */
public class Main {

    public static void main(String[] args) throws Exception {

//        Scanner scanner = new Scanner(System.in);
//
//        System.out.print("Please set local IP address: ");
//        String ip = scanner.nextLine();
//
//        System.out.print("Please set local port: ");
//        String port = scanner.nextLine();
//
//        System.out.print("Please set redis IP address: ");
//        String redisIp = scanner.nextLine();
//
//        System.out.print("Please set redis port: ");
//        String redisPort = scanner.nextLine();

        //
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);

        //
        String redisHost = "127.0.0.1";
        int redisPort = 6379;
        String redisPassword = null;

        String host = "192.168.1.55";
        //String host = "172.16.0.160";
        int udpServerPort = 44414;
        int deviceServerPort = 13414;

        // config redis client
        RemoteDeviceCache cache = new RemoteDeviceCache(redisHost, redisPort, redisPassword);

        //
        DeviceManagement.getInstance().addListener(cache);
        DeviceManagement.getInstance().addListener(new Reconnector());

        // config UDP server
        InternalCmdTransitServer internalCmdTransitServer
                = new InternalCmdTransitServer(host, udpServerPort);

        InternalCmdHandler internalCmdHandler = new InternalCmdHandler();
        internalCmdTransitServer.addListener(internalCmdHandler);
        internalCmdTransitServer.start(false);

        // config TCP server
//        DeviceServer deviceServer = new DeviceServer(host, deviceServerPort);
//        deviceServer.addListener(DeviceManagement.getInstance());
//        deviceServer.addListener(DeviceCmdProcessor.getInstance());
//
//        deviceServer.start(true);
    }
}

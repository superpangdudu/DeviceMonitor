package com.tx.monitor.server;

import com.tx.common.*;
import com.tx.monitor.client.DeviceClient;
import com.tx.monitor.device.Device;
import com.tx.monitor.device.DeviceManagement;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * InternalCmdHandler
 */
public class InternalCmdHandler
        implements InternalCmdTransitServer.LocalCmdTransitServerListener {

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    @Override
    public void onInternalDataReceived(Channel channel, byte[] data, InetSocketAddress senderAddr) {

        // for test
        if (data.length == 1 && data[0] == 0x01) {
            executorService.execute(() -> {
//                Device device = DeviceManagement.getInstance().getById("123");
//                device.echo();

                ByteBuf buf = Unpooled.buffer(16);
                buf.writeBytes(new byte[] {0x01, 0x02, 0x03});

                channel.writeAndFlush(new DatagramPacket(buf, senderAddr));
            });
            return;
        }

        //
        InternalCommand command = Utils.deserialize(data);
        if (command == null)
            return;

        if (command.getAction() == InternalCommand.ACTION_CONNECT)
            doConnect(channel, senderAddr, command.getDeviceId(), command.getHost(), command.getPort());
        else if (command.getAction() == InternalCommand.ACTION_START_ENGINE)
            doStartEngine(channel, senderAddr, command.getDeviceId());
        else if (command.getAction() == InternalCommand.ACTION_SHUTDOWN_ENGINE)
            doShutdownEngine(channel, senderAddr, command.getDeviceId());
        else if (command.getAction() == InternalCommand.ACTION_GET_GENERATOR_STATUS)
            doGetGeneratorStatus(channel, senderAddr, command.getDeviceId());
        else if (command.getAction() == InternalCommand.ACTION_GET_DEVICE_STATUS)
            doGetDeviceStatus(channel, senderAddr, command.getDeviceId());

//        // FIXME
//        TurnDeviceOn turnDeviceOn = Utils.deserialize(data);
//
//        executorService.execute(() -> {
//            Device device = DeviceManagement.getInstance().getById(turnDeviceOn.getDeviceId());
//            if (device == null)
//                return;
//
//            int ret = device.startEngine();
//
//            // TODO
//            //channel.writeAndFlush()
//        });
    }

    //===================================================================================
    public void doConnect(Channel channel, InetSocketAddress senderAddr,
                          String id, String host, int port) {
        executorService.execute(() -> {
            boolean isConnecting = DeviceManagement.getInstance().isDeviceConnecting(id);
            if (isConnecting)
                return;

            DeviceClient client = new DeviceClient(id, host, port, eventLoopGroup);
            ChannelFuture future = client.start();

            int ret = -1;
            try {
                future.sync();
                if (future.isSuccess())
                    ret = 0;

            } catch (Exception e) {
                e.printStackTrace();
            }

            // TODO response to caller
            InternalCommandResult result = new InternalCommandResult();
            result.setResult(ret);
            result.setDeviceId(id);

            sendResult(channel, senderAddr, result);
        });
    }

    public void doStartEngine(Channel channel, InetSocketAddress senderAddr, String id) {
        executorService.execute(() -> {
            int ret = -101;

            Device device = DeviceManagement.getInstance().getById(id);
            if (device != null) {
                ret = device.startEngine();
            }

            // TODO response to caller
            InternalCommandResult result = new InternalCommandResult();
            result.setResult(ret);
            result.setDeviceId(id);

            sendResult(channel, senderAddr, result);
        });
    }

    public void doShutdownEngine(Channel channel, InetSocketAddress senderAddr, String id) {
        executorService.execute(() -> {
            int ret = -101;

            Device device = DeviceManagement.getInstance().getById(id);
            if (device != null) {
                ret = device.shutdownEngine();
            }

            // TODO response to caller
            InternalCommandResult result = new InternalCommandResult();
            result.setResult(ret);
            result.setDeviceId(id);

            sendResult(channel, senderAddr, result);
        });
    }

    public void doGetDeviceStatus(Channel channel, InetSocketAddress senderAddr, String id) {
        executorService.execute(() -> {
            int ret = -101;

            DeviceStatus deviceStatus = null;
            Device device = DeviceManagement.getInstance().getById(id);
            if (device != null) {
                ret = 0;
                deviceStatus = device.getDeviceStatus();
            }

            // TODO response to caller

            InternalCommandResult result = new InternalCommandResult();
            result.setResult(ret);
            result.setDeviceId(id);

            if (deviceStatus != null) {
                Map<String, DeviceStatus.Status> map = deviceStatus.getStatusMap();
                for (String key : map.keySet()) {
                    System.out.println(key + " : " + map.get(key).getValue());
                }

                for (int register : DeviceStatus.statusSetterMap.keySet()) {

                }
            }

            // FIXME
            // for the too big result makes the deserialize failure since the UDP packet not sent completely
//            if (deviceStatus != null) {
//                result.setData(deviceStatus);
//            }

            //
            sendResult(channel, senderAddr, result);
        });
    }

    public void doGetGeneratorStatus(Channel channel, InetSocketAddress senderAddr, String id) {
        executorService.execute(() -> {
            int ret = -101;

            GeneratorStatus generatorStatus = null;
            Device device = DeviceManagement.getInstance().getById(id);
            if (device != null) {
                ret = 0;
                generatorStatus = device.getGeneratorStatus();
            }

            // TODO response to caller
            InternalCommandResult result = new InternalCommandResult();
            result.setResult(ret);
            result.setDeviceId(id);

            // for the too big result makes the deserialize failure since the UDP packet not sent completely
//            if (generatorStatus != null) {
//                result.setData(generatorStatus);
//            }

            //
            sendResult(channel, senderAddr, result);
        });
    }

    //===================================================================================
    private void sendResult(Channel channel,
                            InetSocketAddress senderAddr,
                            InternalCommandResult result) {
        byte[] data = Utils.serialize(result);
        ByteBuf buf = Unpooled.buffer(data.length);
        buf.writeBytes(data);

        channel.writeAndFlush(new DatagramPacket(buf, senderAddr));
    }
}

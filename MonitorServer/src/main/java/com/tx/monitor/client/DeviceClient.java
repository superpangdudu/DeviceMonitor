package com.tx.monitor.client;

import com.tx.common.Utils;
import com.tx.monitor.device.Device;
import com.tx.monitor.device.DeviceManagement;
import com.tx.monitor.device.DeviceWriter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;

/**
 * DeviceClient
 * Make a TCP connection with the remote device.
 * The device works as server.
 */
public class DeviceClient {

    private String deviceId;
    private String host;
    private int port = 502; // ModBus TCP default port
    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap = new Bootstrap();

    public DeviceClient(String deviceId,
                        String host, int port, EventLoopGroup eventLoopGroup) {
        this.deviceId = deviceId;
        this.host = host;
        this.port = port;
        this.eventLoopGroup = eventLoopGroup;

        //
        init();
    }

    public String getDeviceId() {
        return deviceId;
    }

    private void init() {
        bootstrap.group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host, port))
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {

                        ch.pipeline().addLast(
                                /**
                                 * ModBus TCP
                                 * TID: Transaction Id, 2 bytes
                                 * PID: Protocol Id, 2 bytes
                                 * Length: 2 bytes, excluding TID, PID and Length fields
                                 *
                                 * |TID|PID|Length|Content|
                                 */
                                new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                                        4, 2, 0, 6));

                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("client channelActive : " + System.currentTimeMillis());

                                // for heart beat
                                //ctx.executor().scheduleAtFixedRate();

                                // when the connection with device is established,
                                // a Device instance will be created to stand for the remote device
                                Device device = DeviceManagement.getInstance().newDevice(getDeviceId(), ctx.channel());
                                device.setDeviceWriter(new DeviceWriter() {
                                    @Override
                                    public int write(byte[] data) {
                                        ByteBuf buf = Unpooled.buffer(data.length);
                                        buf.writeBytes(data);

                                        ctx.channel().writeAndFlush(buf);
                                        //buf.release();

                                        return 0;
                                    }
                                });
                                DeviceManagement.getInstance().addDevice(device);
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("client channelInactive : " + System.currentTimeMillis());

                                // TODO
                                // just remove the Device instance for simplicity
                                Device device = DeviceManagement.getInstance().getByChannel(ctx.channel());
                                if (device == null)
                                    return;
                                DeviceManagement.getInstance().removeDevice(device);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                byte[] data = new byte[msg.readableBytes()];
                                msg.readBytes(data);

                                System.out.println(Utils.byteArrayToHexString(data));
                                System.out.println(new String(data));

                                //
                                DeviceManagement.getInstance().handleDeviceData(ctx.channel(), data);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                                    throws Exception {
                                cause.printStackTrace();

                                ctx.fireExceptionCaught(cause);
                            }
                        });

                    }
                });
    }

    //===================================================================================

    /**
     * Connecting to remote device
     * FIXME in multi-thread environment, needs a lock to avoid the status conflict
     */
    public ChannelFuture start() {
        System.out.println("connecting");
        DeviceManagement.getInstance().onDeviceConnecting(getDeviceId());

        try {
            ChannelFuture future = bootstrap.connect();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    DeviceManagement.getInstance().onDeviceConnectionStatus(getDeviceId(), future.isSuccess());

                    // try again after per 5 seconds if failed
//                    if (future.isSuccess())
//                        return;
//
//                    future.channel().eventLoop().schedule(() -> {
//                        start();
//                    }, 5, TimeUnit.SECONDS);
                }
            });

            return future;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //===================================================================================
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup();

        DeviceClient client = new DeviceClient("1", "192.168.214.1", 60000, group);
        client.start();
    }
}

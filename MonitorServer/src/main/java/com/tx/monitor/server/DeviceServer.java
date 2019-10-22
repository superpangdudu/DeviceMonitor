package com.tx.monitor.server;

import com.tx.monitor.device.Device;
import com.tx.monitor.device.DeviceManagement;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DeviceServer
 */
public class DeviceServer {
    private static final Logger logger = LoggerFactory.getLogger(DeviceServer.class);

    //===================================================================================
    /**
     * local address to bind to serve incoming
     */
    private String host;
    /**
     * local port
     */
    private int port;

    public DeviceServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start(boolean sync) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //.channel(EpollServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        //ch.pipeline().addLast(new CmdDecoder());
                        //ch.pipeline().addLast(new CmdEncoder());

                        // for idle state
                        //ch.pipeline().addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));

                        ch.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                            @Override
                            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("channelActive");

                                // TODO send login request to get device id
                                // for a incoming connection, we need to get its basic information
                                // before marking it as usable
                            }

                            @Override
                            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                System.out.println("channelInactive");

                                // just simply remove the corresponding Device instance
                                Device device = DeviceManagement.getInstance().getByChannel(ctx.channel());
                                if (device == null)
                                    return;
                                DeviceManagement.getInstance().removeDevice(device);
                            }

                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                                byte[] data = new byte[msg.readableBytes()];
                                msg.readBytes(data);

                                // TODO
                                // if msg is response of Login command, add new device
                                // else let Device to handle it
                                DeviceManagement.getInstance().handleDeviceData(ctx.channel(), data);
                            }

                            // for test
//                            @Override
//                            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
//                                ctx.fireUserEventTriggered(evt);
//                            }
                        });
                    }
                });

        try {
            if (sync) {
                ChannelFuture future = bootstrap.bind(host, port).sync();
                future.channel().closeFuture().sync();

            } else {
                bootstrap.bind(host, port);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //===================================================================================
    public static void main(String[] args) {
        DeviceServer server = new DeviceServer("127.0.0.1", 13414);
        server.start(true);
    }
}

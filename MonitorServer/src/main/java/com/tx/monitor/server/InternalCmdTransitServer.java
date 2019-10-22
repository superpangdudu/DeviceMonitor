package com.tx.monitor.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * InternalCmdTransitServer
 *
 * A UDP server used to perform a simple IPC
 */
public class InternalCmdTransitServer {
    private static Logger logger = LoggerFactory.getLogger(InternalCmdTransitServer.class);

    public interface LocalCmdTransitServerListener {
        void onInternalDataReceived(Channel channel, byte[] data, InetSocketAddress senderAddr);
    }

    //===================================================================================
    private String host;
    private int port;

    private List<LocalCmdTransitServerListener> listeners = new ArrayList<>();

    private void doOnInternalDataReceived(Channel channel, byte[] data, InetSocketAddress senderAddr) {
        for (LocalCmdTransitServerListener listener : listeners)
            listener.onInternalDataReceived(channel, data, senderAddr);
    }

    //===================================================================================
    public InternalCmdTransitServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public synchronized void addListener(LocalCmdTransitServerListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(LocalCmdTransitServerListener listener) {
        listeners.remove(listener);
    }

    public void start(boolean sync) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .channel(NioDatagramChannel.class)
                .handler(new SimpleChannelInboundHandler<DatagramPacket>() {

                    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
                        byte[] data = new byte[msg.content().readableBytes()];
                        msg.content().readBytes(data);

                        try {
                            System.out.println(new String(data));

                            // notify listeners

                            doOnInternalDataReceived(ctx.channel(), data, msg.sender());

                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.toString());
                        }
                    }
                });

        // start server
        if (sync) {
            try {
                ChannelFuture future = bootstrap.bind(host, port).sync();
                future.channel().closeFuture().sync();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            bootstrap.bind(host, port);
        }
    }

    //===================================================================================
    public static void main(String[] args) {
        InternalCmdTransitServer server = new InternalCmdTransitServer("127.0.0.1", 44414);
        server.start(true);
    }

}

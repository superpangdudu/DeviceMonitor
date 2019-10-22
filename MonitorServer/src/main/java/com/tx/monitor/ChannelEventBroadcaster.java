package com.tx.monitor;

import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * ChannelEventBroadcaster
 */
public class ChannelEventBroadcaster {
    public interface ChannelEventBroadcasterListener {
        void onChannelConnected(Channel channel);
        void onChannelDisconnected(Channel channel);
        void onChannelReceivedData(Channel channel, byte[] data);
    }

    private List<ChannelEventBroadcasterListener> listeners = new ArrayList<>();

    public synchronized void addListener(ChannelEventBroadcasterListener listener) {
        listeners.add(listener);
    }

    public synchronized void removeListener(ChannelEventBroadcasterListener listener) {
        listeners.remove(listener);
    }

    //===================================================================================
    private static ChannelEventBroadcaster INSTANCE = null;

    public static ChannelEventBroadcaster getInstance() {
        if (INSTANCE == null) {
            synchronized (ChannelEventBroadcaster.class) {
                if (INSTANCE == null)
                    INSTANCE = new ChannelEventBroadcaster();
            }
        }
        return INSTANCE;
    }

    //===================================================================================
    public void channelConnected(Channel channel) {
        for (ChannelEventBroadcasterListener listener : listeners)
            listener.onChannelConnected(channel);
    }

    public void channelDisconnected(Channel channel) {
        for (ChannelEventBroadcasterListener listener : listeners)
            listener.onChannelDisconnected(channel);
    }

    public void channelReceivedData(Channel channel, byte[] data) {
        for (ChannelEventBroadcasterListener listener : listeners)
            listener.onChannelReceivedData(channel, data);
    }
}

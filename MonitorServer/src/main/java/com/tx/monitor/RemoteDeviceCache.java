package com.tx.monitor;

import com.tx.common.DeviceStatus;
import com.tx.common.GeneratorStatus;
import com.tx.common.Utils;
import com.tx.monitor.device.Device;
import com.tx.monitor.device.DeviceManagement;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Created by Administrator on 2019/9/20.
 */
public class RemoteDeviceCache implements DeviceManagement.DeviceManagementListener {
    private JedisPool jedisPool;
    private static final byte[] KEY_DEVICE_INFO = "key_device_info".getBytes();
    private static final byte[] KEY_DEVICE_STATUS = "key_device_status".getBytes();
    private static final byte[] KEY_GENERATOR_STATUS = "key_generator_status".getBytes();

    public RemoteDeviceCache(String host, int port, String password) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(5);
        config.setMaxTotal(20);
        config.setMaxWaitMillis(30000);

        if (password == null || password.equals(""))
            jedisPool = new JedisPool(config, host, port);

        jedisPool = new JedisPool(config, host, port, 10, password);
    }

    //===================================================================================
    @Override
    public void onDeviceAdded(Device device) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(device.getId().getBytes(),
                    KEY_DEVICE_INFO,
                    Utils.serialize(device));
        }
    }

    @Override
    public void onDeviceRemoved(Device device) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hdel(device.getId().getBytes(), KEY_DEVICE_INFO);
        }
    }

    @Override
    public void onDeviceEngineStarted(Device device, int result) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.hset(device.getId().getBytes(),
//                    KEY_DEVICE_INFO,
//                    Utils.intToByte(result));
//        }
    }

    @Override
    public void onDeviceEngineShutdown(Device device, int result) {
//        try (Jedis jedis = jedisPool.getResource()) {
//            jedis.hdel(device.getId().getBytes(), KEY_DEVICE_INFO);
//        }
    }

    @Override
    public void onUpdateDeviceStatus(Device device, DeviceStatus deviceStatus) {
        byte[] data = Utils.serialize(deviceStatus);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(device.getId().getBytes(),
                    KEY_DEVICE_STATUS,
                    Utils.serialize(deviceStatus));
        }
    }

    @Override
    public void onUpdateGeneratorStatus(Device device, GeneratorStatus generatorStatus) {
        byte[] data = Utils.serialize(generatorStatus);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(device.getId().getBytes(),
                    KEY_GENERATOR_STATUS,
                    Utils.serialize(generatorStatus));
        }
    }
}

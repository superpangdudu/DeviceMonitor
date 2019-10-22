package com.tx.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2019/10/9.
 */
public class DeviceStatus2 implements Serializable {
    public static class Status<T> implements Serializable {
        private T value;

        public Status(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    private Map<String, Status> statusMap = new HashMap<>();

    private void setStatus(String name, Status status) {
        statusMap.put(name, status);
    }

    public int init(byte[] data) {
        // for 100 registers, each has 2 bytes value
        int count = 100;
        if (data.length != count * 2)
            return -1;

        for (int n = 0; n < count; n++) {
            byte low = data[n * 2 + 1];
            byte high = data[n * 2];

            StatusSetter statusSetter = statusSetterMap.get(n);
            if (statusSetter == null)
                continue;

            statusSetter.setStatus(this, high, low);
        }

        return 0;
    }

    public Status getStatus(String name) {
        return statusMap.get(name);
    }

    //===================================================================================
    private interface StatusSetter {
        void setStatus(DeviceStatus2 deviceStatus, byte high, byte low);
    }

    private static Map<Integer, StatusSetter> statusSetterMap = new HashMap<>();

    /**
     *
     * @param content
     * @param pos from 0 to 7
     * @return
     */
    private static byte getBit(byte content, int pos) {
        return (byte) ((content & (1 << pos)) >> pos);
    }

    private static short getShort(byte high, byte low) {
        short value = 0;
        value |= low & 0xFF;
        value |= (high << 8) & 0xFF00;

        return value;
    }

    static {
        {
            // 0000
            statusSetterMap.put(0, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("公共报警", new Status<>(getBit(low, 0))); // l0 公共报警 low
                deviceStatus.setStatus("公共停机报警", new Status<>(getBit(low, 1))); // l1 公共停机报警
                deviceStatus.setStatus("公共警告报警", new Status<>(getBit(low, 2))); // l2 公共警告报警
                deviceStatus.setStatus("公共跳闸停机报警", new Status<>(getBit(low, 3))); // l3 公共跳闸停机报警
                deviceStatus.setStatus("公共分闸不停机", new Status<>(getBit(low, 4))); // l4 公共分闸不停机
                deviceStatus.setStatus("公共跳闸停机报警和公共停机报警", new Status<>(getBit(low, 5))); // l5 公共跳闸停机报警和公共停机报警
                // l6 保留
                // l7 保留
                // h0 保留
                deviceStatus.setStatus("系统在自动模式", new Status<>(getBit(high, 1))); // h1 系统在自动模式
                deviceStatus.setStatus("系统在手动模式", new Status<>(getBit(high, 2))); // h2 系统在手动模式
                deviceStatus.setStatus("系统在停机模式", new Status<>(getBit(high, 3))); // h3 系统在停机模式
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留 high

            });

            // 0001
            statusSetterMap.put(1, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("紧急停机报警", new Status<>(getBit(low, 0))); // l0 紧急停机报警
                deviceStatus.setStatus("超速报警停机", new Status<>(getBit(low, 1))); // l1 超速报警停机
                deviceStatus.setStatus("欠速报警停机", new Status<>(getBit(low, 2))); // l2 欠速报警停机
                deviceStatus.setStatus("速度信号丢失报警", new Status<>(getBit(low, 3))); // l3 速度信号丢失报警
                deviceStatus.setStatus("发电过频报警停机", new Status<>(getBit(low, 4))); // l4 发电过频报警停机
                deviceStatus.setStatus("发电欠频停机", new Status<>(getBit(low, 5))); // l5 发电欠频停机
                deviceStatus.setStatus("发电过压停机", new Status<>(getBit(low, 6))); // l6 发电过压停机
                deviceStatus.setStatus("发电欠压停机", new Status<>(getBit(low, 7))); // l7 发电欠压停机
                deviceStatus.setStatus("起动失败报警", new Status<>(getBit(high, 0))); // h0 起动失败报警
                deviceStatus.setStatus("发电过流停机", new Status<>(getBit(high, 1))); // h1 发电过流停机
                deviceStatus.setStatus("维护时间到报警停机", new Status<>(getBit(high, 2))); // h2 维护时间到报警停机
                // h3 保留
                deviceStatus.setStatus("逆功率报警停机", new Status<>(getBit(high, 4))); // h4 逆功率报警停机
                deviceStatus.setStatus("过功率报警停机", new Status<>(getBit(high, 5))); // h5 过功率报警停机
                deviceStatus.setStatus("温度高输入报警停机", new Status<>(getBit(high, 6))); // h6 温度高输入报警停机
                deviceStatus.setStatus("油压低输入报警停机", new Status<>(getBit(high, 7))); // h7 油压低输入报警停机

            });

            // 0002
            statusSetterMap.put(2, (deviceStatus, high, low) -> {
                // l0 保留
                deviceStatus.setStatus("液位低输入报警停机", new Status<>(getBit(low, 1))); // l1 液位低输入报警停机
                // l2 保留
                // l3 保留
                // l4 保留
                // l5 保留
                // l6 保留
                // l7 保留
                deviceStatus.setStatus("温度传感器开路", new Status<>(getBit(high, 0))); // h0 温度传感器开路
                deviceStatus.setStatus("温度高报警停机", new Status<>(getBit(high, 1))); // h1 温度高报警停机
                // h2 保留
                // h3 保留
                deviceStatus.setStatus("油压传感器开路", new Status<>(getBit(high, 4))); // h4 油压传感器开路
                // h5 保留
                deviceStatus.setStatus("油压低报警停机", new Status<>(getBit(high, 6))); // h6 油压低报警停机
                // h7 保留
            });

            // 0003
            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

            // 0008
            statusSetterMap.put(8, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("输入口1停机", new Status<>(getBit(low, 0))); // l0 输入口1停机
                deviceStatus.setStatus("输入口2停机", new Status<>(getBit(low, 1))); // l1 输入口2停机
                deviceStatus.setStatus("输入口3停机", new Status<>(getBit(low, 2))); // l2 输入口3停机
                deviceStatus.setStatus("输入口4停机", new Status<>(getBit(low, 3))); // l3 输入口4停机
                deviceStatus.setStatus("输入口5停机", new Status<>(getBit(low, 4))); // l4 输入口5停机
                deviceStatus.setStatus("输入口6停机", new Status<>(getBit(low, 5))); // l5 输入口6停机
                deviceStatus.setStatus("输入口7停机", new Status<>(getBit(low, 6))); // l6 输入口7停机
                // l7 保留
                // h0 保留
                // h1 保留
                // h2 保留
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0012
            statusSetterMap.put(12, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("过流跳闸停机", new Status<>(getBit(low, 0))); // l0 过流跳闸停机
                deviceStatus.setStatus("维护时间到跳闸停机", new Status<>(getBit(low, 1))); // l1 维护时间到跳闸停机
                deviceStatus.setStatus("逆功率跳闸停机", new Status<>(getBit(low, 2))); // l2 逆功率跳闸停机
                deviceStatus.setStatus("过功率跳闸停机", new Status<>(getBit(low, 3))); // l3 过功率跳闸停机
                deviceStatus.setStatus("输入口1跳闸停机", new Status<>(getBit(low, 4))); // l4 输入口1跳闸停机
                deviceStatus.setStatus("输入口2跳闸停机", new Status<>(getBit(low, 5))); // l5 输入口2跳闸停机
                deviceStatus.setStatus("输入口3跳闸停机", new Status<>(getBit(low, 6))); // l6 输入口3跳闸停机
                deviceStatus.setStatus("输入口4跳闸停机", new Status<>(getBit(low, 7))); // l7 输入口4跳闸停机
                deviceStatus.setStatus("输入口5跳闸停机", new Status<>(getBit(high, 0))); // h0 输入口5跳闸停机
                deviceStatus.setStatus("输入口6跳闸停机", new Status<>(getBit(high, 1))); // h1 输入口6跳闸停机
                deviceStatus.setStatus("输入口7跳闸停机", new Status<>(getBit(high, 2))); // h2 输入口7跳闸停机
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0016
            statusSetterMap.put(16, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("过流跳闸不停机", new Status<>(getBit(low, 0))); // l0 过流跳闸不停机
                deviceStatus.setStatus("维护时间到跳闸不停机", new Status<>(getBit(low, 1))); // l1 维护时间到跳闸不停机
                deviceStatus.setStatus("逆功率跳闸不停机", new Status<>(getBit(low, 2))); // l2 逆功率跳闸不停机
                deviceStatus.setStatus("过功率跳闸不停机", new Status<>(getBit(low, 3))); // l3 过功率跳闸不停机
                deviceStatus.setStatus("输入口1跳闸不停机", new Status<>(getBit(low, 4))); // l4 输入口1跳闸不停机
                deviceStatus.setStatus("输入口2跳闸不停机", new Status<>(getBit(low, 5))); // l5 输入口2跳闸不停机
                deviceStatus.setStatus("输入口3跳闸不停机", new Status<>(getBit(low, 6))); // l6 输入口3跳闸不停机
                deviceStatus.setStatus("输入口4跳闸不停机", new Status<>(getBit(low, 7))); // l7 输入口4跳闸不停机
                deviceStatus.setStatus("输入口5跳闸不停机", new Status<>(getBit(high, 0))); // h0 输入口5跳闸不停机
                deviceStatus.setStatus("输入口6跳闸不停机", new Status<>(getBit(high, 1))); // h1 输入口6跳闸不停机
                deviceStatus.setStatus("输入口7跳闸不停机", new Status<>(getBit(high, 2))); // h2 输入口7跳闸不停机
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0020
            statusSetterMap.put(20, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("超速警告", new Status<>(getBit(low, 0))); // l0 超速警告
                deviceStatus.setStatus("欠速警告", new Status<>(getBit(low, 1))); // l1 欠速警告
                deviceStatus.setStatus("速度信号丢失警告", new Status<>(getBit(low, 2))); // l2 速度信号丢失警告
                deviceStatus.setStatus("发电过频警告", new Status<>(getBit(low, 3))); // l3 发电过频警告
                deviceStatus.setStatus("发电欠频警告", new Status<>(getBit(low, 4))); // l4 发电欠频警告
                deviceStatus.setStatus("发电过压警告", new Status<>(getBit(low, 5))); // l5 发电过压警告
                deviceStatus.setStatus("发电欠压警告", new Status<>(getBit(low, 6))); // l6 发电欠压警告
                deviceStatus.setStatus("发电过流警告", new Status<>(getBit(low, 7))); // l7 发电过流警告
                deviceStatus.setStatus("停机失败警告", new Status<>(getBit(high, 0))); // h0 停机失败警告
                deviceStatus.setStatus("充电失败警告", new Status<>(getBit(high, 1))); // h1 充电失败警告
                deviceStatus.setStatus("电池过压警告", new Status<>(getBit(high, 2))); // h2 电池过压警告
                deviceStatus.setStatus("电池欠压警告", new Status<>(getBit(high, 3))); // h3 电池欠压警告
                deviceStatus.setStatus("维护时间到警告", new Status<>(getBit(high, 4))); // h4 维护时间到警告
                deviceStatus.setStatus("逆功率警告", new Status<>(getBit(high, 5))); // h5 逆功率警告
                deviceStatus.setStatus("过功率警告", new Status<>(getBit(high, 6))); // h6 过功率警告
                // h7 保留

            });

            // 0021
            statusSetterMap.put(21, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("发电缺相警告", new Status<>(getBit(low, 0))); // l0 发电缺相警告
                deviceStatus.setStatus("发电逆相序警告", new Status<>(getBit(low, 1))); // l1 发电逆相序警告
                // l2 保留
                // l3 保留
                // l4 保留
                // l5 保留
                // l6 保留
                deviceStatus.setStatus("开关转换失败警告", new Status<>(getBit(low, 7))); // l7 开关转换失败警告
                deviceStatus.setStatus("温度传感器开路", new Status<>(getBit(high, 0))); // h0 温度传感器开路
                deviceStatus.setStatus("温度高警告", new Status<>(getBit(high, 1))); // h1 温度高警告
                deviceStatus.setStatus("温度低警告", new Status<>(getBit(high, 2))); // h2 温度低警告
                // h3 保留
                deviceStatus.setStatus("油压传感器开路", new Status<>(getBit(high, 4))); // h4 油压传感器开路
                // h5 保留
                deviceStatus.setStatus("油压低警告", new Status<>(getBit(high, 6))); // h6 油压低警告
                // h7 保留

            });

            // 0022
            statusSetterMap.put(22, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("液位传感器开路", new Status<>(getBit(low, 0))); // l0 液位传感器开路
                // l1 保留
                deviceStatus.setStatus("液位低警告", new Status<>(getBit(low, 2))); // l2 液位低警告
                // l3 保留
                deviceStatus.setStatus("可编程传感器1开路", new Status<>(getBit(low, 4))); // l4 可编程传感器1开路
                deviceStatus.setStatus("可编程传感器1高", new Status<>(getBit(low, 5))); // l5 可编程传感器1高
                deviceStatus.setStatus("可编程传感器1低", new Status<>(getBit(low, 6))); // l6 可编程传感器1低
                // l7 保留
                deviceStatus.setStatus("可编程传感器2开路", new Status<>(getBit(high, 0))); // h0 可编程传感器2开路
                deviceStatus.setStatus("可编程传感器2高", new Status<>(getBit(high, 1))); // h1 可编程传感器2高
                deviceStatus.setStatus("可编程传感器2低", new Status<>(getBit(high, 2))); // h2 可编程传感器2低
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0034
            statusSetterMap.put(34, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("市电正常指示", new Status<>(getBit(low, 0))); // l0 市电正常指示
                deviceStatus.setStatus("市电异常指示", new Status<>(getBit(low, 1))); // l1 市电异常指示
                deviceStatus.setStatus("发电机正常运行指示", new Status<>(getBit(low, 2))); // l2 发电机正常运行指示
                deviceStatus.setStatus("发电机停机指示", new Status<>(getBit(low, 3))); // l3 发电机停机指示
                deviceStatus.setStatus("市电带载指示", new Status<>(getBit(low, 4))); // l4 市电带载指示
                deviceStatus.setStatus("发电带载指示", new Status<>(getBit(low, 5))); // l5 发电带载指示
                deviceStatus.setStatus("系统在自动模式指示", new Status<>(getBit(low, 6))); // l6 系统在自动模式指示
                deviceStatus.setStatus("系统不在自动模式指示", new Status<>(getBit(low, 7))); // l7 系统不在自动模式指示
                // h0 保留
                // h1 保留
                // h2 保留
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0043
            statusSetterMap.put(43, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("市电正常", new Status<>(getBit(low, 0))); // l0 市电正常
                deviceStatus.setStatus("市电合闸", new Status<>(getBit(low, 1))); // l1 市电合闸
                deviceStatus.setStatus("发电正常", new Status<>(getBit(low, 2))); // l2 发电正常
                deviceStatus.setStatus("发电合闸", new Status<>(getBit(low, 3))); // l3 发电合闸
                deviceStatus.setStatus("报警灯状态", new Status<>(getBit(low, 4))); // l4 报警灯状态
                deviceStatus.setStatus("运行灯状态", new Status<>(getBit(low, 5))); // l5 运行灯状态
                // l6 保留
                // l7 保留
                // h0 保留
                // h1 保留
                // h2 保留
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0044
            statusSetterMap.put(44, (deviceStatus, high, low) -> {
                deviceStatus.setStatus("市电异常", new Status<>(getBit(low, 0))); // l0 市电异常
                deviceStatus.setStatus("市电过压", new Status<>(getBit(low, 1))); // l1 市电过压
                deviceStatus.setStatus("市电欠压", new Status<>(getBit(low, 2))); // l2 市电欠压
                deviceStatus.setStatus("市电过频", new Status<>(getBit(low, 3))); // l3 市电过频
                deviceStatus.setStatus("市电欠频", new Status<>(getBit(low, 4))); // l4 市电欠频
                deviceStatus.setStatus("市电缺相", new Status<>(getBit(low, 5))); // l5 市电缺相
                deviceStatus.setStatus("市电逆相序", new Status<>(getBit(low, 6))); // l6 市电逆相序
                deviceStatus.setStatus("市电无", new Status<>(getBit(low, 7))); // l7 市电无
                // h0 保留
                // h1 保留
                // h2 保留
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留
            });

            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

            statusSetterMap.put(3, (deviceStatus, high, low) -> {

            });

        }
    }

    //===================================================================================
    public static void main(String[] args) {
        byte content = (byte) 0b10000011;
        byte data = getBit(content, 3);
        data = getBit(content, 7);
        data = getBit(content, 1);
        data = getBit(content, 0);
        data = getBit(content, 2);

        String hexStr = "0405000000000000000000000000000000000000000000000000000000000000000000000000000008001100000100000000000000000000000000000000000000000000008A000000000080000000000000000000000020008500000000000000000000000000000000000000000000000000000000000000007FFE7FFE7FFE000000000000000000000000000000000000000000000000000000000000000000007FFE7FFE7FFE0000000000000000000000000000000000000000000000000000000000000000";
        byte[] value = Utils.hexString2Bytes(hexStr);

        DeviceStatus2 deviceStatus = new DeviceStatus2();
        deviceStatus.init(value);

        int n = 0;
    }
}

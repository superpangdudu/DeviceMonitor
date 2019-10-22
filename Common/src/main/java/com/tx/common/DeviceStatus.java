package com.tx.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.tx.common.Utils.*;

/**
 * DeviceStatus
 */
public class DeviceStatus implements Serializable {
    public static class Status<T> implements Serializable {
        private T value;

        public Status(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    // status map
    private Map<String, Status> statusMap = new HashMap<>();

    private void setStatus(String name, Status status) {
        statusMap.put(name, status);
    }

    public int update(int fromRegister, int count, byte[] data) {
        // for registers, each has a 2 bytes value
        if (data.length != count * 2)
            return -1;

        int register = fromRegister;
        int readPos = 0;
        while (readPos < data.length - 1) {
            StatusSetter statusSetter = statusSetterMap.get(register);
            if (statusSetter != null)
                statusSetter.setStatus(this, data, readPos);

            // each register uses 2 bytes
            register += 1;
            readPos += 2;
        }

        return 0;
    }

    public Status getStatus(String name) {
        return statusMap.get(name);
    }

    public Map<String, Status> getStatusMap() {
        return statusMap;
    }

    //===================================================================================
    private interface StatusSetter {
        void setStatus(DeviceStatus deviceStatus, byte[] data, int readPos);
    }

    public static Map<Integer, StatusSetter> statusSetterMap = new HashMap<>();

    private static short getShort(byte[] data, int from) {
        byte high = data[from];
        byte low = data[from + 1];

        return Utils.bytesToShort(high, low);
    }

    private static int getInt(byte[] data, int from) {
        byte b1 = data[from];
        byte b2 = data[from + 1];
        byte b3 = data[from + 2];
        byte b4 = data[from + 3];

        return bytesToInt(b1, b2, b3, b4);
    }

    static {
        {
            // 0000
            statusSetterMap.put(0, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(1, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(2, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(3, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("液位传感器开路", new Status<>(getBit(low, 0))); // l0 液位传感器开路
                // l1 保留
                deviceStatus.setStatus("液位低报警停机", new Status<>(getBit(low, 2))); // l2 液位低报警停机
                // l3 保留
                deviceStatus.setStatus("可编程传感器1开路", new Status<>(getBit(low, 4))); // l4 可编程传感器1开路
                deviceStatus.setStatus("可编程1高报警停机", new Status<>(getBit(low, 5))); // l5 可编程1高报警停机
                deviceStatus.setStatus("可编程1低报警停机", new Status<>(getBit(low, 6))); // l6 可编程1低报警停机
                // l7 保留
                deviceStatus.setStatus("可编程传感器2开路", new Status<>(getBit(high, 0))); // h0 可编程传感器2开路
                deviceStatus.setStatus("可编程2高报警停机", new Status<>(getBit(high, 1))); // h1 可编程2高报警停机
                deviceStatus.setStatus("可编程2低报警停机", new Status<>(getBit(high, 2))); // h2 可编程2低报警停机
                // h3 保留
                // h4 保留
                // h5 保留
                // h6 保留
                // h7 保留

            });

            // 0008
            statusSetterMap.put(8, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(12, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(16, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(20, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(21, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("发电缺相警告", new Status<>(getBit(low, 0))); // l0 发电缺相警告
                deviceStatus.setStatus("发电逆相序警告", new Status<>(getBit(low, 1))); // l1 发电逆相序警告
                // l2 保留
                // l3 保留
                // l4 保留
                // l5 保留
                // l6 保留
                deviceStatus.setStatus("开关转换失败警告", new Status<>(getBit(low, 7))); // l7 开关转换失败警告
                deviceStatus.setStatus("温度传感器开路", new Status<>(getBit(high, 0))); // h0 温度传感器开路 - reduplicated
                deviceStatus.setStatus("温度高警告", new Status<>(getBit(high, 1))); // h1 温度高警告
                deviceStatus.setStatus("温度低警告", new Status<>(getBit(high, 2))); // h2 温度低警告
                // h3 保留
                deviceStatus.setStatus("油压传感器开路", new Status<>(getBit(high, 4))); // h4 油压传感器开路 - reduplicated
                // h5 保留
                deviceStatus.setStatus("油压低警告", new Status<>(getBit(high, 6))); // h6 油压低警告
                // h7 保留

            });

            // 0022
            statusSetterMap.put(22, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(34, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(43, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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
            statusSetterMap.put(44, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

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

            // 0055
            statusSetterMap.put(55, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UAB = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UAB", new Status<>(UAB));

            });

            // 0056
            statusSetterMap.put(56, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UBC = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UBC", new Status<>(UBC));

            });

            // 0057
            statusSetterMap.put(57, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UCA = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UCA", new Status<>(UCA));

            });

            // 0058
            statusSetterMap.put(58, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UA = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UA", new Status<>(UA));

            });

            // 0059
            statusSetterMap.put(59, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UB = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UB", new Status<>(UB));

            });

            // 0060
            statusSetterMap.put(60, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UC = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("市电UC", new Status<>(UC));

            });

            // 0061
            statusSetterMap.put(61, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("市电UA相位", new Status<>(value));

            });

            // 0062
            statusSetterMap.put(62, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("市电UB相位", new Status<>(value));

            });

            // 0063
            statusSetterMap.put(63, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("市电UC相位", new Status<>(value));

            });

            // 0064
            statusSetterMap.put(64, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos); // (*100) ???
                deviceStatus.setStatus("市电频率", new Status<>(value));

            });

            // 0075
            statusSetterMap.put(75, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UAB = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UAB", new Status<>(UAB));

            });

            // 0076
            statusSetterMap.put(76, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UBC = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UBC", new Status<>(UBC));

            });

            // 0077
            statusSetterMap.put(77, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UCA = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UCA", new Status<>(UCA));

            });

            // 0078
            statusSetterMap.put(78, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UA = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UA", new Status<>(UA));

            });

            // 0079
            statusSetterMap.put(79, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UB = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UB", new Status<>(UB));

            });

            // 0080
            statusSetterMap.put(80, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int UC = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("发电UC", new Status<>(UC));

            });

            // 0081
            statusSetterMap.put(81, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                short UAPhase = bytesToShort(high, low);
                deviceStatus.setStatus("发电UA相位", new Status<>(UAPhase));

            });

            // 0082
            statusSetterMap.put(82, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("发电UB相位", new Status<>(value));

            });

            // 0083
            statusSetterMap.put(83, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("发电UC相位", new Status<>(value));

            });

            // 0095
            statusSetterMap.put(95, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("A相电流", new Status<>(value));

            });

            // 0096
            statusSetterMap.put(96, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("B相电流", new Status<>(value));

            });

            // 0097
            statusSetterMap.put(97, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("C相电流", new Status<>(value));

            });

            // 0103 & 0104
            statusSetterMap.put(103, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("A相有功功率", new Status<>(value));

            });

            // 0105 & 0106
            statusSetterMap.put(105, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("B相有功功率", new Status<>(value));

            });

            // 0107 & 0108
            statusSetterMap.put(107, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("C相有功功率", new Status<>(value));

            });

            // 0109 & 0110
            statusSetterMap.put(109, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("总有功功率", new Status<>(value));

            });

            // 0111 & 0112
            statusSetterMap.put(111, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("A相无功功率", new Status<>(value));

            });

            // 0113 & 0114
            statusSetterMap.put(113, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("B相无功功率", new Status<>(value));

            });

            // 0115 & 0116
            statusSetterMap.put(115, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("C相无功功率", new Status<>(value));

            });

            // 0117 & 0118
            statusSetterMap.put(117, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("总无功功率", new Status<>(value));

            });

            // 0119 & 0120
            statusSetterMap.put(119, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("A相视在功率", new Status<>(value));

            });

            // 0121 & 0122
            statusSetterMap.put(121, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("B相视在功率", new Status<>(value));

            });

            // 0123 & 0124
            statusSetterMap.put(123, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("C相视在功率", new Status<>(value));

            });

            // 0125 & 0126
            statusSetterMap.put(125, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("总视在功率", new Status<>(value));

            });

            // 0127
            statusSetterMap.put(127, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos); // (*100) ???
                deviceStatus.setStatus("A相功率因数", new Status<>(value));

            });

            // 0128
            statusSetterMap.put(128, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos); // (*100) ???
                deviceStatus.setStatus("B相功率因数", new Status<>(value));

            });

            // 0129
            statusSetterMap.put(129, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos); // (*100) ???
                deviceStatus.setStatus("C相功率因数", new Status<>(value));

            });

            // 0130
            statusSetterMap.put(130, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos); // (*100) ???
                deviceStatus.setStatus("平均功率因数", new Status<>(value));

            });

            // 0131
            statusSetterMap.put(131, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("本次运行时间（时）", new Status<>(value));

            });

            // 0132
            statusSetterMap.put(132, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("本次运行时间（分）", new Status<>(value));

            });

            // 0133
            statusSetterMap.put(133, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("本次运行时间（秒）", new Status<>(value));

            });

            // 0134 & 0135
            statusSetterMap.put(134, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("本次电能", new Status<>(value));

            });

            // 0136 & 0137
            statusSetterMap.put(136, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("平均功率", new Status<>(value));

            });

            // 0138 & 0139
            statusSetterMap.put(138, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos); // (*10) ???
                deviceStatus.setStatus("历史功率", new Status<>(value));

            });

            // 0140
            statusSetterMap.put(140, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("负载百分比", new Status<>(value));

            });

            // 0141
            statusSetterMap.put(141, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("发动机转速", new Status<>(value));

            });

            // 0142
            statusSetterMap.put(142, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("电池电压", new Status<>(value));

            });

            // 0143
            statusSetterMap.put(143, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("充电机电压", new Status<>(value));

            });

            // 0148
            statusSetterMap.put(148, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("温度传感器电阻值", new Status<>(value));

            });

            // 0149
            statusSetterMap.put(149, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("温度传感器数值", new Status<>(value));

            });

            // 0150
            statusSetterMap.put(150, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("压力传感器电阻值", new Status<>(value));

            });

            // 0151
            statusSetterMap.put(151, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("压力传感器数值", new Status<>(value));

            });

            // 0152
            statusSetterMap.put(152, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("液位传感器电阻值", new Status<>(value));

            });

            // 0153
            statusSetterMap.put(153, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("液位传感器数值", new Status<>(value));

            });

            // 0189
            statusSetterMap.put(189, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("发电机状态-待机", new Status<>(getBit(low, 0))); // l0 发电机状态-待机
                deviceStatus.setStatus("发电机状态-预热", new Status<>(getBit(low, 1))); // l1 发电机状态-预热
                deviceStatus.setStatus("发电机状态-燃油输出", new Status<>(getBit(low, 2))); // l2 发电机状态-燃油输出
                deviceStatus.setStatus("发电机状态-起动", new Status<>(getBit(low, 3))); // l3 发电机状态-起动
                deviceStatus.setStatus("发电机状态-起动间隔", new Status<>(getBit(low, 4))); // l4 发电机状态-起动间隔
                deviceStatus.setStatus("发电机状态-安全延时", new Status<>(getBit(low, 5))); // l5 发电机状态-安全延时
                deviceStatus.setStatus("发电机状态-开机怠速", new Status<>(getBit(low, 6))); // l6 发电机状态-开机怠速
                deviceStatus.setStatus("发电机状态-高速暖机", new Status<>(getBit(low, 7))); // l7 发电机状态-高速暖机
                deviceStatus.setStatus("发电机状态-等待帯载", new Status<>(getBit(high, 0))); // h0 发电机状态-等待帯载
                deviceStatus.setStatus("发电机状态-正常运行", new Status<>(getBit(high, 1))); // h1 发电机状态-正常运行
                deviceStatus.setStatus("发电机状态-高速散热", new Status<>(getBit(high, 2))); // h2 发电机状态-高速散热
                deviceStatus.setStatus("发电机状态-停机怠速", new Status<>(getBit(high, 3))); // h3 发电机状态-停机怠速
                deviceStatus.setStatus("发电机状态-得电停机", new Status<>(getBit(high, 4))); // h4 发电机状态-得电停机
                deviceStatus.setStatus("发电机状态-等待停稳", new Status<>(getBit(high, 5))); // h5 发电机状态-等待停稳
                deviceStatus.setStatus("发电机状态-停机失败", new Status<>(getBit(high, 6))); // h6 发电机状态-停机失败
                deviceStatus.setStatus("发电机状态-过停稳", new Status<>(getBit(high, 7))); // h7 发电机状态-过停稳

            });

            // 0190
            statusSetterMap.put(190, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("发电延时值", new Status<>(value));

            });

            // 0191
            statusSetterMap.put(191, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("远程开机状态-无延时", new Status<>(getBit(low, 0))); // l0 远程开机状态-无延时
                deviceStatus.setStatus("远程开机状态-开机延时", new Status<>(getBit(low, 1))); // l1 远程开机状态-开机延时
                deviceStatus.setStatus("远程开机状态-停机延时", new Status<>(getBit(low, 2))); // l2 远程开机状态-停机延时

            });

            // 0192
            statusSetterMap.put(192, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("远程开机延时值", new Status<>(value));

            });

            // 0193
            statusSetterMap.put(193, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("开关状态-负载断开", new Status<>(getBit(low, 0))); // l0 开关状态-负载断开
                deviceStatus.setStatus("开关状态-市电带载", new Status<>(getBit(low, 1))); // l1 开关状态-市电带载
                deviceStatus.setStatus("开关状态-发电带载", new Status<>(getBit(low, 2))); // l2 开关状态-发电带载
                deviceStatus.setStatus("开关状态-分闸延时", new Status<>(getBit(low, 3))); // l3 开关状态-分闸延时
                deviceStatus.setStatus("开关状态-开关转换延时", new Status<>(getBit(low, 4))); // l4 开关状态-开关转换延时
                deviceStatus.setStatus("开关状态-市电合闸延时", new Status<>(getBit(low, 5))); // l5 开关状态-市电合闸延时
                deviceStatus.setStatus("开关状态-发电合闸延时", new Status<>(getBit(low, 6))); // l6 开关状态-发电合闸延时
                deviceStatus.setStatus("开关状态-等待分闸", new Status<>(getBit(low, 7))); // l7 开关状态-等待分闸
                deviceStatus.setStatus("开关状态-等待发电合闸", new Status<>(getBit(high, 0))); // h0 开关状态-等待发电合闸
                deviceStatus.setStatus("开关状态-等待市电合闸", new Status<>(getBit(high, 1))); // h1 开关状态-等待市电合闸
                deviceStatus.setStatus("开关状态-正常", new Status<>(getBit(high, 2))); // h2 开关状态-正常

            });

            // 0194
            statusSetterMap.put(194, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("ATS转换延时值", new Status<>(value));

            });

            // 0195
            statusSetterMap.put(195, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                deviceStatus.setStatus("市电状态-市电正常", new Status<>(getBit(low, 0))); // l0 市电状态-市电正常
                deviceStatus.setStatus("市电状态-市电正常延时", new Status<>(getBit(low, 1))); // l1 市电状态-市电正常延时
                deviceStatus.setStatus("市电状态-市电异常", new Status<>(getBit(low, 2))); // l2 市电状态-市电异常
                deviceStatus.setStatus("市电状态-市电异常延时", new Status<>(getBit(low, 3))); // l3 市电状态-市电异常延时

            });

            // 0196
            statusSetterMap.put(196, (deviceStatus, data, readPos) -> {
                short value = getShort(data, readPos);
                deviceStatus.setStatus("市电延时值", new Status<>(value));

            });

            // 0199
            statusSetterMap.put(199, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("累计运行小时", new Status<>(value));

            });

            // 0200
            statusSetterMap.put(200, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("累计运行分钟", new Status<>(value));

            });

            // 0201
            statusSetterMap.put(201, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("累计运行秒种", new Status<>(value));

            });

            // 0202
            statusSetterMap.put(202, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("累计开机次数", new Status<>(value));

            });

            // 0203 & 0204
            statusSetterMap.put(203, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos);
                deviceStatus.setStatus("累计电能kWh", new Status<>(value));

            });

            // 0205 & 0206
            statusSetterMap.put(205, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos);
                deviceStatus.setStatus("累计电能kVarh", new Status<>(value));

            });

            // 0207 & 0208
            statusSetterMap.put(207, (deviceStatus, data, readPos) -> {
                int value = getInt(data, readPos);
                deviceStatus.setStatus("累计电能kVAh", new Status<>(value));

            });

            // 0211
            statusSetterMap.put(211, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("维护剩余时间小时", new Status<>(value));

            });

            // 0212
            statusSetterMap.put(212, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("维护剩余时间分钟", new Status<>(value));

            });

            // 0213
            statusSetterMap.put(213, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("维护剩余时间秒钟", new Status<>(value));

            });

            // 0217
            statusSetterMap.put(217, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low);
                deviceStatus.setStatus("控制器型号", new Status<>(value));

            });

            // 0218
            statusSetterMap.put(218, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("控制器软件版本", new Status<>(value));

            });

            // 0219
            statusSetterMap.put(219, (deviceStatus, data, readPos) -> {
                byte high = data[readPos];
                byte low = data[readPos + 1];

                int value = bytesToInt((byte) 0, (byte) 0, high, low); // (*10) ???
                deviceStatus.setStatus("控制器硬件版本", new Status<>(value));

            });


            statusSetterMap.put(4, (deviceStatus, data, readPos) -> {
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

        DeviceStatus deviceStatus = new DeviceStatus();
        deviceStatus.update(0, 100, value);

        int n = 0;
    }
}

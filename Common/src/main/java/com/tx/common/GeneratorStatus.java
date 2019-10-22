package com.tx.common;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/9/27.
 */
public class GeneratorStatus implements Serializable {
    private boolean isStandby; // 待机
    private boolean isPreheating; // 预热
    private boolean isOilOutputting; // 燃油输出
    private boolean isStarted; // 起动
    private boolean isStartDelayed; // 起动间隔
    private boolean isDelayedForSafety; // 安全延时
    private boolean isIdling; // 开机怠速
    private boolean isWarmingUp; // 高速暖机
    private boolean isWaiting; // 等待帯载
    private boolean isRunning; // 正常运行
    // 高速散热
    // 停机怠速
    // 得电停机
    // 等待停稳
    // 停机失败
    // 过停稳

    public GeneratorStatus(int status) {
        isStandby = ((status & 0b1) > 0);
        isPreheating = ((status & 0b10) > 0);
        isOilOutputting = ((status & 0b100) > 0);
        isStarted = ((status & 0b1000) > 0);
        isStartDelayed = ((status & 0b10000) > 0);
        isDelayedForSafety = ((status & 0b100000) > 0);
        isIdling = ((status & 0b1000000) > 0);
        isWarmingUp = ((status & 0b10000000) > 0);
        isWaiting = ((status & 0b100000000) > 0);
        isRunning = ((status & 0b1000000000) > 0);
    }

    public GeneratorStatus(byte high, byte low) {
        isStandby = ((low & 0b1) > 0);
        isPreheating = ((low & 0b10) > 0);
        isOilOutputting = ((low & 0b100) > 0);
        isStarted = ((low & 0b1000) > 0);
        isStartDelayed = ((low & 0b10000) > 0);
        isDelayedForSafety = ((low & 0b100000) > 0);
        isIdling = ((low & 0b1000000) > 0);
        isWarmingUp = ((low & 0b10000000) > 0);

        isWaiting = ((high & 0b1) > 0);
        isRunning = ((high & 0b10) > 0);
        // TODO
    }

    public boolean isStandby() {
        return isStandby;
    }

    public boolean isPreheating() {
        return isPreheating;
    }

    public boolean isOilOutputting() {
        return isOilOutputting;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isStartDelayed() {
        return isStartDelayed;
    }

    public boolean isDelayedForSafety() {
        return isDelayedForSafety;
    }

    public boolean isIdling() {
        return isIdling;
    }

    public boolean isWarmingUp() {
        return isWarmingUp;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    public boolean isRunning() {
        return isRunning;
    }
}

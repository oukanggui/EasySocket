package com.okg.easysocket.bean;

import com.okg.easysocket.proguard.IProguard;

import java.io.Serializable;

/**
 * @author Baymax
 * @date 2019-09-22
 * 描述：设备信息实体
 */
public class DeviceInfo implements Serializable, IProguard {
    /**
     * 用户id
     */
    private int userId;
    /**
     * 设备id
     */
    private int deviceId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备序列号
     */
    private String deviceSn;
    /**
     * 设备状态
     * 1：在线
     * 2：不在线
     */
    private int status;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isDeviceOnline() {
        return this.status == 1;
    }

    public String getDeviceSn() {
        return deviceSn;
    }

    public void setDeviceSn(String deviceSn) {
        this.deviceSn = deviceSn;
    }
}

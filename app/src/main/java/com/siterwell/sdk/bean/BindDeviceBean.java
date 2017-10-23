package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class BindDeviceBean implements Serializable {

    private static final long serialVersionUID = 951927742845205428L;
    private String devTid;
    private String deviceName;
    private String bindKey;
    private String desc;

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBindKey() {
        return bindKey;
    }

    public void setBindKey(String bindKey) {
        this.bindKey = bindKey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @param devTid     设备ID
     * @param deviceName 绑定码
     * @param bindKey    长度[1,128] 设备名称(可选)
     * @param desc       长度[1,128] 设备描述 (可选)
     */
    public BindDeviceBean(String devTid, String bindKey, String deviceName, String desc) {
        this.desc = desc;
        this.devTid = devTid;
        this.deviceName = deviceName;
        this.bindKey = bindKey;
    }

    @Override
    public String toString() {
        return "BindDeviceBean{" +
                "devTid='" + devTid + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", bindKey='" + bindKey + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

}

package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DefaultDeviceBean implements Serializable {
    private static final long serialVersionUID = 3412188515948714837L;
    /**
     * pid : 00000000000
     * deviceName : 智能空气净化器
     * logo : http://7xsk3x.com2.z0.glb.qiniucdn.com/dev_static_kqjhq.png
     * desc : 默认体验设备
     * androidH5Page : http://app.hekr.me/android/124/
     * iosH5Page : http://app.hekr.me/android/124/
     */

    private String pid;
    private String deviceName;
    private String logo;
    private String desc;
    private String androidH5Page;
    private String iosH5Page;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAndroidH5Page() {
        return androidH5Page;
    }

    public void setAndroidH5Page(String androidH5Page) {
        this.androidH5Page = androidH5Page;
    }

    public String getIosH5Page() {
        return iosH5Page;
    }

    public void setIosH5Page(String iosH5Page) {
        this.iosH5Page = iosH5Page;
    }
}

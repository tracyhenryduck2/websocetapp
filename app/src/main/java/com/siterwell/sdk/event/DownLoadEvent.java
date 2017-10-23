package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.DeviceBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DownLoadEvent {
    private DeviceBean deviceBean;

    public DownLoadEvent(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    public DeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setDeviceBean(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    @Override
    public String toString() {
        return "DownLoadEvent{" +
                "deviceBean=" + deviceBean +
                '}';
    }
}

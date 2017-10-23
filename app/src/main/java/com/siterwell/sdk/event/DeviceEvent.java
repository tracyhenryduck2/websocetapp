package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.DeviceBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DeviceEvent {
    private DeviceBean deviceBean;

    public DeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setDeviceBean(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    @Override
    public String toString() {
        return "DeviceEvent{" +
                "deviceBean=" + deviceBean +
                '}';
    }

    public DeviceEvent(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }
}

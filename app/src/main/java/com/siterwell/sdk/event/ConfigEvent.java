package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.DeviceBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ConfigEvent {
    private DeviceBean deviceBean;

    public ConfigEvent(DeviceBean deviceBean) {
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
        return "ConfigEvent{" +
                "deviceBean=" + deviceBean +
                '}';
    }
}

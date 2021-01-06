package com.siterwell.demo.BusEvents;

import me.siter.sdk.http.bean.DeviceBean;

import java.util.List;


/**
 * Created by Administrator on 2017/7/27.
 */

public class GetDeviceStatusEvent {


    private List<DeviceBean> deviceBeans;


    public List<DeviceBean> getDeviceBeans() {
        return deviceBeans;
    }

    public void setDeviceBeans(List<DeviceBean> deviceBeans) {
        this.deviceBeans = deviceBeans;
    }

}

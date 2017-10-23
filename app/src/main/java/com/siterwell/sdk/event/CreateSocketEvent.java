package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.FindDeviceBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class CreateSocketEvent {
    private FindDeviceBean findDeviceBean;

    public CreateSocketEvent(FindDeviceBean findDeviceBean) {
        this.findDeviceBean = findDeviceBean;
    }

    public FindDeviceBean getFindDeviceBean() {
        return findDeviceBean;
    }

    public void setFindDeviceBean(FindDeviceBean findDeviceBean) {
        this.findDeviceBean = findDeviceBean;
    }

    @Override
    public String toString() {
        return "CreateSocketEvent{" +
                "findDeviceBean=" + findDeviceBean +
                '}';
    }
}

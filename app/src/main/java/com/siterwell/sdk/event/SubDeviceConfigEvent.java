package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.SubDeviceConfigBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class SubDeviceConfigEvent {

    private SubDeviceConfigBean subDeviceConfigBean;

    public SubDeviceConfigEvent(SubDeviceConfigBean subDeviceConfigBean) {
        this.subDeviceConfigBean = subDeviceConfigBean;
    }

    public SubDeviceConfigBean getSubDeviceConfigBean() {
        return subDeviceConfigBean;
    }

    public void setSubDeviceConfigBean(SubDeviceConfigBean subDeviceConfigBean) {
        this.subDeviceConfigBean = subDeviceConfigBean;
    }

    @Override
    public String toString() {
        return "SubDeviceConfigEvent{" +
                "subDeviceConfigBean=" + subDeviceConfigBean +
                '}';
    }

}

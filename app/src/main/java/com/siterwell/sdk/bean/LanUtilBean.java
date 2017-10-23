package com.siterwell.sdk.bean;

import com.siterwell.sdk.util.LANUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class LanUtilBean implements Serializable {

    private static final long serialVersionUID = 2725961374820031290L;
    private FindDeviceBean findDeviceBean;
    private LANUtil lanUtil;

    public LanUtilBean(FindDeviceBean findDeviceBean, LANUtil lanUtil) {
        this.findDeviceBean = findDeviceBean;
        this.lanUtil = lanUtil;
    }

    public FindDeviceBean getFindDeviceBean() {
        return findDeviceBean;
    }

    public void setFindDeviceBean(FindDeviceBean findDeviceBean) {
        this.findDeviceBean = findDeviceBean;
    }

    public LANUtil getLanUtil() {
        return lanUtil;
    }

    public void setLanUtil(LANUtil lanUtil) {
        this.lanUtil = lanUtil;
    }

    @Override
    public String toString() {
        return "{findDeviceBean=" + findDeviceBean+ '}';
    }

}

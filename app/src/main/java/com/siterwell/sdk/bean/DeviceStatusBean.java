package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DeviceStatusBean implements Serializable {

    private static final long serialVersionUID = -3597417482742442584L;
    /**
     * devTid : test-2016-4-18-1
     * bindToUser : true
     * forceBind : false
     * deviceName :
     * logo : http://app.hekr.me/res/img/icon/icon_33@3x.png
     * cid : fc1345a42510
     * cidName : 厨房电器/电饭煲
     * productBand : HEKR
     */

    private String devTid;
    private boolean bindToUser;
    private boolean forceBind;
    private String deviceName;
    private String logo;
    private String cid;
    private String cidName;
    private String productBand;

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public boolean isBindToUser() {
        return bindToUser;
    }

    public void setBindToUser(boolean bindToUser) {
        this.bindToUser = bindToUser;
    }

    public boolean isForceBind() {
        return forceBind;
    }

    public void setForceBind(boolean forceBind) {
        this.forceBind = forceBind;
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCidName() {
        return cidName;
    }

    public void setCidName(String cidName) {
        this.cidName = cidName;
    }

    public String getProductBand() {
        return productBand;
    }

    public void setProductBand(String productBand) {
        this.productBand = productBand;
    }

    @Override
    public String toString() {
        return "DeviceStatusBean{" +
                "devTid='" + devTid + '\'' +
                ", bindToUser=" + bindToUser +
                ", forceBind=" + forceBind +
                ", deviceName='" + deviceName + '\'' +
                ", logo='" + logo + '\'' +
                ", cid='" + cid + '\'' +
                ", cidName='" + cidName + '\'' +
                ", productBand='" + productBand + '\'' +
                '}';
    }

}

package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FindDeviceBean implements Serializable {
    private static final long serialVersionUID = 9073575216741671782L;
    private String MAC;
    private String SDKVer;
    private String SSID;
    private String binType;
    private String binVer;
    private String bindKey;
    private String devTid;
    private String mid;
    private String serviceHost;
    private String serviceIp;
    private int servicePort;
    private int tokenType=2;
    private int workMode=0;

    public FindDeviceBean() {
    }

    public FindDeviceBean(String binVer, String MAC, String SDKVer, String SSID, String binType, String bindKey, String devTid, String mid, String serviceHost, String serviceIp, int servicePort, int tokenType, int workMode) {
        this.binVer = binVer;
        this.MAC = MAC;
        this.SDKVer = SDKVer;
        this.SSID = SSID;
        this.binType = binType;
        this.bindKey = bindKey;
        this.devTid = devTid;
        this.mid = mid;
        this.serviceHost = serviceHost;
        this.serviceIp = serviceIp;
        this.servicePort = servicePort;
        this.tokenType = tokenType;
        this.workMode = workMode;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getSDKVer() {
        return SDKVer;
    }

    public void setSDKVer(String SDKVer) {
        this.SDKVer = SDKVer;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getBinType() {
        return binType;
    }

    public void setBinType(String binType) {
        this.binType = binType;
    }

    public String getBinVer() {
        return binVer;
    }

    public void setBinVer(String binVer) {
        this.binVer = binVer;
    }

    public String getBindKey() {
        return bindKey;
    }

    public void setBindKey(String bindKey) {
        this.bindKey = bindKey;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }

    public int getTokenType() {
        return tokenType;
    }

    public void setTokenType(int tokenType) {
        this.tokenType = tokenType;
    }

    public int getWorkMode() {
        return workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    @Override
    public String toString() {
        return "{" +
                "devTid='" + devTid + '\'' +
                ",serviceIp='" + serviceIp + '\'' +
                ",servicePort=" + servicePort +
                '}';
    }
}

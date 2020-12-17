package me.siter.sdk.entity;

import java.util.HashMap;
import java.util.Map;

import me.siter.sdk.DeviceType;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 提供给Web使用的Bean
 */

public class SiterWebBean {

    private String devTid;
    private String subDevTid;
    private String ctrlKey;
    private String ppk;
    private String h5Zip;
    private String h5Url;
    private DeviceType type;
    private String protocol;
    private String deviceInfo;
    private HashMap<String, String> params;
    private Map<String, Map<String, String>> extras;
    private String domain;
    private String host;

    public SiterWebBean() {

    }

    public SiterWebBean(String devTid, String subDevTid, String ctrlKey, String ppk, String h5Zip,
                        String h5Url, DeviceType type, String deviceInfo, String protocol) {
        this.devTid = devTid;
        this.subDevTid = subDevTid;
        this.ctrlKey = ctrlKey;
        this.ppk = ppk;
        this.h5Zip = h5Zip;
        this.h5Url = h5Url;
        this.type = type;
        this.deviceInfo = deviceInfo;
        this.protocol = protocol;
    }

    public SiterWebBean(String devTid, String subDevTid, String ctrlKey, String ppk, String h5Zip,
                        String h5Url, DeviceType type, String deviceInfo, String protocol,
                        HashMap<String, String> params) {
        this.devTid = devTid;
        this.subDevTid = subDevTid;
        this.ctrlKey = ctrlKey;
        this.ppk = ppk;
        this.h5Zip = h5Zip;
        this.h5Url = h5Url;
        this.type = type;
        this.deviceInfo = deviceInfo;
        this.protocol = protocol;
        this.params = params;
    }

    public SiterWebBean(String devTid, String subDevTid, String ctrlKey, String ppk, String h5Zip,
                        String h5Url, DeviceType type, String protocol, String deviceInfo,
                        HashMap<String, String> params, Map<String, Map<String, String>> extras) {
        this.devTid = devTid;
        this.subDevTid = subDevTid;
        this.ctrlKey = ctrlKey;
        this.ppk = ppk;
        this.h5Zip = h5Zip;
        this.h5Url = h5Url;
        this.type = type;
        this.protocol = protocol;
        this.deviceInfo = deviceInfo;
        this.params = params;
        this.extras = extras;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getSubDevTid() {
        return subDevTid;
    }

    public void setSubDevTid(String subDevTid) {
        this.subDevTid = subDevTid;
    }

    public String getCtrlKey() {
        return ctrlKey;
    }

    public void setCtrlKey(String ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public String getPpk() {
        return ppk;
    }

    public void setPpk(String ppk) {
        this.ppk = ppk;
    }

    public String getH5Zip() {
        return h5Zip;
    }

    public void setH5Zip(String h5Zip) {
        this.h5Zip = h5Zip;
    }

    public String getH5Url() {
        return h5Url;
    }

    public void setH5Url(String h5Url) {
        this.h5Url = h5Url;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public Map<String, Map<String, String>> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Map<String, String>> extras) {
        this.extras = extras;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SiterWebBean that = (SiterWebBean) o;

        if (devTid != null ? !devTid.equals(that.devTid) : that.devTid != null) return false;
        if (subDevTid != null ? !subDevTid.equals(that.subDevTid) : that.subDevTid != null)
            return false;
        if (ctrlKey != null ? !ctrlKey.equals(that.ctrlKey) : that.ctrlKey != null) return false;
        if (ppk != null ? !ppk.equals(that.ppk) : that.ppk != null) return false;
        if (h5Zip != null ? !h5Zip.equals(that.h5Zip) : that.h5Zip != null) return false;
        if (h5Url != null ? !h5Url.equals(that.h5Url) : that.h5Url != null) return false;
        if (type != that.type) return false;
        if (protocol != null ? !protocol.equals(that.protocol) : that.protocol != null)
            return false;
        if (deviceInfo != null ? !deviceInfo.equals(that.deviceInfo) : that.deviceInfo != null)
            return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        if (extras != null ? !extras.equals(that.extras) : that.extras != null) return false;
        if (domain != null ? !domain.equals(that.domain) : that.domain != null) return false;
        return host != null ? host.equals(that.host) : that.host == null;
    }

    @Override
    public int hashCode() {
        int result = devTid != null ? devTid.hashCode() : 0;
        result = 31 * result + (subDevTid != null ? subDevTid.hashCode() : 0);
        result = 31 * result + (ctrlKey != null ? ctrlKey.hashCode() : 0);
        result = 31 * result + (ppk != null ? ppk.hashCode() : 0);
        result = 31 * result + (h5Zip != null ? h5Zip.hashCode() : 0);
        result = 31 * result + (h5Url != null ? h5Url.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (deviceInfo != null ? deviceInfo.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (extras != null ? extras.hashCode() : 0);
        result = 31 * result + (domain != null ? domain.hashCode() : 0);
        result = 31 * result + (host != null ? host.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SiterWebBean{" +
                "devTid='" + devTid + '\'' +
                ", subDevTid='" + subDevTid + '\'' +
                ", ctrlKey='" + ctrlKey + '\'' +
                ", ppk='" + ppk + '\'' +
                ", h5Zip='" + h5Zip + '\'' +
                ", h5Url='" + h5Url + '\'' +
                ", type=" + type +
                ", protocol='" + protocol + '\'' +
                ", deviceInfo='" + deviceInfo + '\'' +
                ", params=" + params +
                ", extras=" + extras +
                ", domain='" + domain + '\'' +
                ", host='" + host + '\'' +
                '}';
    }
}

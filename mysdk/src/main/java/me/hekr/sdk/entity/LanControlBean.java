package me.hekr.sdk.entity;

/**
 * Created by hucn on 2017/4/13.
 * Author: hucn
 * Description: 外界传入的控制局域网设备的bean
 */

public class LanControlBean {

    private String devTid;
    private String ctrlKey;
    private String SSID;
    private String deviceIP;
    private int devicePort;

    public LanControlBean(String devTid, String ctrlKey, String SSID, String deviceIP, int devicePort) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.SSID = SSID;
        this.deviceIP = deviceIP;
        this.devicePort = devicePort;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getCtrlKey() {
        return ctrlKey;
    }

    public void setCtrlKey(String ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    public int getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(int devicePort) {
        this.devicePort = devicePort;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LanControlBean that = (LanControlBean) o;

        if (devicePort != that.devicePort) return false;
        if (devTid != null ? !devTid.equals(that.devTid) : that.devTid != null) return false;
        if (ctrlKey != null ? !ctrlKey.equals(that.ctrlKey) : that.ctrlKey != null) return false;
        if (SSID != null ? !SSID.equals(that.SSID) : that.SSID != null) return false;
        return deviceIP != null ? deviceIP.equals(that.deviceIP) : that.deviceIP == null;

    }

    @Override
    public int hashCode() {
        int result = devTid != null ? devTid.hashCode() : 0;
        result = 31 * result + (ctrlKey != null ? ctrlKey.hashCode() : 0);
        result = 31 * result + (SSID != null ? SSID.hashCode() : 0);
        result = 31 * result + (deviceIP != null ? deviceIP.hashCode() : 0);
        result = 31 * result + devicePort;
        return result;
    }

    @Override
    public String toString() {
        return "LanControlBean{" +
                "devTid='" + devTid + '\'' +
                ", ctrlKey='" + ctrlKey + '\'' +
                ", SSID='" + SSID + '\'' +
                ", deviceIP='" + deviceIP + '\'' +
                ", devicePort=" + devicePort +
                '}';
    }
}

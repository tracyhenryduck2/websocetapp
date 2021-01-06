package me.siter.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class NewDeviceBean implements Serializable {
    private static final long serialVersionUID = -3600401164444167479L;
    private int bindResultCode;
    private String bindResultMsg;
    private String folderName;
    private boolean online;
    private TranslateBean productName;
    private TranslateBean categoryName;
    private String cidName;
    private String model;
    private String productBand;
    private String androidPageZipURL;
    private String androidH5Page;
    private String logo;
    private String productPublicKey;
    private String deviceName;
    private String devTid;
    private String cid;
    private String ctrlKey;
    private String bindKey;
    private String workModeType;
    private String ssid;
    private String mac;
    private String wanIp;
    private String binVersion;
    private String sdkVer;
    private String binType;
    private String serviceHost;
    private int servicePort;
    private String finger;
    private long firstLoginTime;
    private long lastLoginTime;
    private long currentLoginTime;
    private long lastHeartbeat;
    private String ownerUid;
    private String status;
    private boolean granted;
    private boolean setSchedulerTask;
    private boolean forceBind;
    private int devShareNum;
    private int maxDevShareNum;
    private DeviceBindingStatusBean deviceBindingStatus;

    public NewDeviceBean() {
    }

    public NewDeviceBean(String androidH5Page, int bindResultCode, String bindResultMsg, String folderName, boolean online, TranslateBean productName, TranslateBean categoryName, String cidName, String modle, String productBand, String androidPageZipURL, String logo, String productPublicKey, String deviceName, String devTid, String cid, String ctrlKey, String bindKey, String workModeType, String ssid, String mac, String wanIp, String binVersion, String sdkVer, String binType, String serviceHost, int servicePort, String finger, long firstLoginTime, long lastLoginTime, long currentLoginTime, long lastHeartbeat, String ownerUid, String status, boolean granted, boolean setSchedulerTask, boolean forceBind, int devShareNum, int maxDevShareNum, DeviceBindingStatusBean deviceBindingStatus) {
        this.androidH5Page = androidH5Page;
        this.bindResultCode = bindResultCode;
        this.bindResultMsg = bindResultMsg;
        this.folderName = folderName;
        this.online = online;
        this.productName = productName;
        this.categoryName = categoryName;
        this.cidName = cidName;
        this.model = model;
        this.productBand = productBand;
        this.androidPageZipURL = androidPageZipURL;
        this.logo = logo;
        this.productPublicKey = productPublicKey;
        this.deviceName = deviceName;
        this.devTid = devTid;
        this.cid = cid;
        this.ctrlKey = ctrlKey;
        this.bindKey = bindKey;
        this.workModeType = workModeType;
        this.ssid = ssid;
        this.mac = mac;
        this.wanIp = wanIp;
        this.binVersion = binVersion;
        this.sdkVer = sdkVer;
        this.binType = binType;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
        this.finger = finger;
        this.firstLoginTime = firstLoginTime;
        this.lastLoginTime = lastLoginTime;
        this.currentLoginTime = currentLoginTime;
        this.lastHeartbeat = lastHeartbeat;
        this.ownerUid = ownerUid;
        this.status = status;
        this.granted = granted;
        this.setSchedulerTask = setSchedulerTask;
        this.forceBind = forceBind;
        this.devShareNum = devShareNum;
        this.maxDevShareNum = maxDevShareNum;
        this.deviceBindingStatus = deviceBindingStatus;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String modle) {
        this.model = model;
    }

    public int getBindResultCode() {
        return bindResultCode;
    }

    public void setBindResultCode(int bindResultCode) {
        this.bindResultCode = bindResultCode;
    }

    public String getBindResultMsg() {
        return bindResultMsg;
    }

    public void setBindResultMsg(String bindResultMsg) {
        this.bindResultMsg = bindResultMsg;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public TranslateBean getProductName() {
        return productName;
    }

    public void setProductName(TranslateBean productName) {
        this.productName = productName;
    }

    public TranslateBean getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(TranslateBean categoryName) {
        this.categoryName = categoryName;
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

    public String getAndroidPageZipURL() {
        return androidPageZipURL;
    }

    public void setAndroidPageZipURL(String androidPageZipURL) {
        this.androidPageZipURL = androidPageZipURL;
    }

    public String getAndroidH5Page() {
        return androidH5Page;
    }

    public void setAndroidH5Page(String androidH5Page) {
        this.androidH5Page = androidH5Page;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getProductPublicKey() {
        return productPublicKey;
    }

    public void setProductPublicKey(String productPublicKey) {
        this.productPublicKey = productPublicKey;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCtrlKey() {
        return ctrlKey;
    }

    public void setCtrlKey(String ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public String getBindKey() {
        return bindKey;
    }

    public void setBindKey(String bindKey) {
        this.bindKey = bindKey;
    }

    public String getWorkModeType() {
        return workModeType;
    }

    public void setWorkModeType(String workModeType) {
        this.workModeType = workModeType;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getWanIp() {
        return wanIp;
    }

    public void setWanIp(String wanIp) {
        this.wanIp = wanIp;
    }

    public String getBinVersion() {
        return binVersion;
    }

    public void setBinVersion(String binVersion) {
        this.binVersion = binVersion;
    }

    public String getSdkVer() {
        return sdkVer;
    }

    public void setSdkVer(String sdkVer) {
        this.sdkVer = sdkVer;
    }

    public String getBinType() {
        return binType;
    }

    public void setBinType(String binType) {
        this.binType = binType;
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

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public long getFirstLoginTime() {
        return firstLoginTime;
    }

    public void setFirstLoginTime(long firstLoginTime) {
        this.firstLoginTime = firstLoginTime;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public long getCurrentLoginTime() {
        return currentLoginTime;
    }

    public void setCurrentLoginTime(long currentLoginTime) {
        this.currentLoginTime = currentLoginTime;
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(long lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public boolean isSetSchedulerTask() {
        return setSchedulerTask;
    }

    public void setSetSchedulerTask(boolean setSchedulerTask) {
        this.setSchedulerTask = setSchedulerTask;
    }

    public boolean isForceBind() {
        return forceBind;
    }

    public void setForceBind(boolean forceBind) {
        this.forceBind = forceBind;
    }

    public int getDevShareNum() {
        return devShareNum;
    }

    public void setDevShareNum(int devShareNum) {
        this.devShareNum = devShareNum;
    }

    public int getMaxDevShareNum() {
        return maxDevShareNum;
    }

    public void setMaxDevShareNum(int maxDevShareNum) {
        this.maxDevShareNum = maxDevShareNum;
    }

    public DeviceBindingStatusBean getDeviceBindingStatus() {
        return deviceBindingStatus;
    }

    public void setDeviceBindingStatus(DeviceBindingStatusBean deviceBindingStatus) {
        this.deviceBindingStatus = deviceBindingStatus;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof NewDeviceBean)) return false;

        NewDeviceBean that = (NewDeviceBean) object;

        return getDevTid().equals(that.getDevTid());

    }

    @Override
    public int hashCode() {
        return getDevTid().hashCode();
    }

    @Override
    public String toString() {
        return "NewDeviceBean{" +
                "bindResultCode=" + bindResultCode +
                ", bindResultMsg='" + bindResultMsg + '\'' +
                ", folderName='" + folderName + '\'' +
                ", online=" + online +
                ", productName=" + productName +
                ", categoryName=" + categoryName +
                ", cidName='" + cidName + '\'' +
                ", modle='" + model + '\'' +
                ", productBand='" + productBand + '\'' +
                ", androidPageZipURL='" + androidPageZipURL + '\'' +
                ", androidH5Page='" + androidH5Page + '\'' +
                ", logo='" + logo + '\'' +
                ", productPublicKey='" + productPublicKey + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", devTid='" + devTid + '\'' +
                ", cid='" + cid + '\'' +
                ", ctrlKey='" + ctrlKey + '\'' +
                ", bindKey='" + bindKey + '\'' +
                ", workModeType='" + workModeType + '\'' +
                ", ssid='" + ssid + '\'' +
                ", mac='" + mac + '\'' +
                ", wanIp='" + wanIp + '\'' +
                ", binVersion='" + binVersion + '\'' +
                ", sdkVer='" + sdkVer + '\'' +
                ", binType='" + binType + '\'' +
                ", serviceHost='" + serviceHost + '\'' +
                ", servicePort=" + servicePort +
                ", finger='" + finger + '\'' +
                ", firstLoginTime=" + firstLoginTime +
                ", lastLoginTime=" + lastLoginTime +
                ", currentLoginTime=" + currentLoginTime +
                ", lastHeartbeat=" + lastHeartbeat +
                ", ownerUid='" + ownerUid + '\'' +
                ", status='" + status + '\'' +
                ", granted=" + granted +
                ", setSchedulerTask=" + setSchedulerTask +
                ", forceBind=" + forceBind +
                ", devShareNum=" + devShareNum +
                ", maxDevShareNum=" + maxDevShareNum +
                ", deviceBindingStatus=" + deviceBindingStatus +
                '}';
    }
}

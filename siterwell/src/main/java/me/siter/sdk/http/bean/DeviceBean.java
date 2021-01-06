package me.siter.sdk.http.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DeviceBean implements Serializable {


    private static final long serialVersionUID = 1513216762027178445L;
    /**
     * devTid : MiCO_C89346546428
     * ctrlKey : 3a42b8becad84c63b195a0ce3209a02d
     * bindKey : 4bb4459ef4a445b994b9bb499be53a25
     * wanIp : null
     * cid : a49fb62ce330
     * workModeType : JSON_TRANSPARENT
     * tokenType : 2
     * binVersion : 1.0.7.0
     * sdkVer : 1.0.0.0
     * binType : A
     * serviceHost : null
     * servicePort : 0
     * ssid :
     * mac : C89346546428
     * finger : 9624c128-4a2e-4f1e-8fa7-4b7001332b29
     * currentLoginTime : 1460545154134
     * firstLoginTime : 1460337108457
     * lastLoginTime : 1460540606056
     * lastUpdateTime : null
     * lastReportDeviceInfoTime : 1460337677876
     * lastHeartbeat : 1460547294668
     * ownerUid : 17316800801
     * devShareNum : 0
     * timerMap : null
     * eventRuleMap : null
     * deviceName : devname
     * desc : devdesc
     * folderId : 0
     * productPublicKey : k0wfHypbzGlO9HFLdv8MEZdsNbLFgrIaR1HlbDpW1Eynx6x0nPeX6DR8QDzQprOdoM
     * androidH5Page :
     * iosH5Page :
     * granted : false
     * setSchedulerTask : true
     * online : true
     */

    private String devTid;
    private String ctrlKey;
    private String bindKey;
    private Object wanIp;
    private String cid;
    private String workModeType;
    private int tokenType;
    private String binVersion;
    private String sdkVer;
    private String binType;
    private Object serviceHost;
    private int servicePort;
    private String ssid;
    private String mac;
    private String finger;
    private long currentLoginTime;
    private long firstLoginTime;
    private long lastLoginTime;
    private Object lastUpdateTime;
    private long lastReportDeviceInfoTime;
    private long lastHeartbeat;
    private String ownerUid;
    private int devShareNum;
    private Object timerMap;
    private Object eventRuleMap;
    private String deviceName;
    private String desc;
    private String folderId;
    private String productPublicKey;
    private String logo;
    private boolean granted;
    private boolean setSchedulerTask;
    private boolean online;
    private List<GroupInfoBean> groupInfo;
    private String productBand;
    private String model;
    private String cidName;
    private String folderName;
    private TranslateBean categoryName;
    private TranslateBean productName;
    private String devType;
    private List<GateWaySubDeviceBean> subDevices;
    private GateWaySubDeviceBean gateWaySubDeviceBean;

    /**
     * forceBind : false
     * maxDevShareNum : 0
     */

    private boolean forceBind;
    private int maxDevShareNum;
    private String androidH5Page;
    private String iosH5Page;

    private DeviceAndFolderBean deviceAndFolderBean;

    private GroupBean groupBean;

    public DeviceBean() {
    }

    public DeviceBean(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    public List<GateWaySubDeviceBean> getSubDevices() {
        return subDevices;
    }

    public void setSubDevices(List<GateWaySubDeviceBean> subDevices) {
        this.subDevices = subDevices;
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

    public String getBindKey() {
        return bindKey;
    }

    public void setBindKey(String bindKey) {
        this.bindKey = bindKey;
    }

    public Object getWanIp() {
        return wanIp;
    }

    public void setWanIp(Object wanIp) {
        this.wanIp = wanIp;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getWorkModeType() {
        return workModeType;
    }

    public void setWorkModeType(String workModeType) {
        this.workModeType = workModeType;
    }

    public int getTokenType() {
        return tokenType;
    }

    public void setTokenType(int tokenType) {
        this.tokenType = tokenType;
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

    public Object getServiceHost() {
        return serviceHost;
    }

    public void setServiceHost(Object serviceHost) {
        this.serviceHost = serviceHost;
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
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

    public String getFinger() {
        return finger;
    }

    public void setFinger(String finger) {
        this.finger = finger;
    }

    public long getCurrentLoginTime() {
        return currentLoginTime;
    }

    public void setCurrentLoginTime(long currentLoginTime) {
        this.currentLoginTime = currentLoginTime;
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

    public Object getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Object lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public long getLastReportDeviceInfoTime() {
        return lastReportDeviceInfoTime;
    }

    public void setLastReportDeviceInfoTime(long lastReportDeviceInfoTime) {
        this.lastReportDeviceInfoTime = lastReportDeviceInfoTime;
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

    public int getDevShareNum() {
        return devShareNum;
    }

    public void setDevShareNum(int devShareNum) {
        this.devShareNum = devShareNum;
    }

    public Object getTimerMap() {
        return timerMap;
    }

    public void setTimerMap(Object timerMap) {
        this.timerMap = timerMap;
    }

    public Object getEventRuleMap() {
        return eventRuleMap;
    }

    public void setEventRuleMap(Object eventRuleMap) {
        this.eventRuleMap = eventRuleMap;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getProductPublicKey() {
        return productPublicKey;
    }

    public void setProductPublicKey(String productPublicKey) {
        this.productPublicKey = productPublicKey;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public List<GroupInfoBean> getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(List<GroupInfoBean> groupInfo) {
        this.groupInfo = groupInfo;
    }

    public String getProductBand() {
        return productBand;
    }

    public void setProductBand(String productBand) {
        this.productBand = productBand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCidName() {
        return cidName;
    }

    public void setCidName(String cidName) {
        this.cidName = cidName;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public boolean isForceBind() {
        return forceBind;
    }

    public void setForceBind(boolean forceBind) {
        this.forceBind = forceBind;
    }

    public int getMaxDevShareNum() {
        return maxDevShareNum;
    }

    public void setMaxDevShareNum(int maxDevShareNum) {
        this.maxDevShareNum = maxDevShareNum;
    }

    public String getAndroidH5Page() {
        return androidH5Page;
    }

    public void setAndroidH5Page(String androidH5Page) {
        this.androidH5Page = androidH5Page;
    }

    public String getIosH5Page() {
        return iosH5Page;
    }

    public void setIosH5Page(String iosH5Page) {
        this.iosH5Page = iosH5Page;
    }

    public DeviceAndFolderBean getDeviceAndFolderBean() {
        return deviceAndFolderBean;
    }

    public void setDeviceAndFolderBean(DeviceAndFolderBean deviceAndFolderBean) {
        this.deviceAndFolderBean = deviceAndFolderBean;
    }

    public GroupBean getGroupBean() {
        return groupBean;
    }

    public void setGroupBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }


    public TranslateBean getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(TranslateBean categoryName) {
        this.categoryName = categoryName;
    }

    public TranslateBean getProductName() {
        return productName;
    }

    public void setProductName(TranslateBean productName) {
        this.productName = productName;
    }

    public GateWaySubDeviceBean getGateWaySubDeviceBean() {
        return gateWaySubDeviceBean;
    }

    public void setGateWaySubDeviceBean(GateWaySubDeviceBean gateWaySubDeviceBean) {
        this.gateWaySubDeviceBean = gateWaySubDeviceBean;
    }

    public DeviceBean(String devTid, String ctrlKey, String bindKey, Object wanIp, String cid, String workModeType, int tokenType, String binVersion, String sdkVer, String binType, Object serviceHost, int servicePort, String ssid, String mac, String finger, long currentLoginTime, long firstLoginTime, long lastLoginTime, Object lastUpdateTime, long lastReportDeviceInfoTime, long lastHeartbeat, String ownerUid, int devShareNum, Object timerMap, Object eventRuleMap, String deviceName, String desc, String folderId, String productPublicKey, String logo, String androidPageZipURL, String iosPageZipURL, boolean granted, boolean setSchedulerTask, boolean online, List<GroupInfoBean> groupInfo, String productBand, String model, String cidName, String folderName, TranslateBean categoryName, TranslateBean productName, boolean forceBind, int maxDevShareNum, String androidH5Page, String iosH5Page, DeviceAndFolderBean deviceAndFolderBean, GroupBean groupBean) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.bindKey = bindKey;
        this.wanIp = wanIp;
        this.cid = cid;
        this.workModeType = workModeType;
        this.tokenType = tokenType;
        this.binVersion = binVersion;
        this.sdkVer = sdkVer;
        this.binType = binType;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
        this.ssid = ssid;
        this.mac = mac;
        this.finger = finger;
        this.currentLoginTime = currentLoginTime;
        this.firstLoginTime = firstLoginTime;
        this.lastLoginTime = lastLoginTime;
        this.lastUpdateTime = lastUpdateTime;
        this.lastReportDeviceInfoTime = lastReportDeviceInfoTime;
        this.lastHeartbeat = lastHeartbeat;
        this.ownerUid = ownerUid;
        this.devShareNum = devShareNum;
        this.timerMap = timerMap;
        this.eventRuleMap = eventRuleMap;
        this.deviceName = deviceName;
        this.desc = desc;
        this.folderId = folderId;
        this.productPublicKey = productPublicKey;
        this.logo = logo;
        this.granted = granted;
        this.setSchedulerTask = setSchedulerTask;
        this.online = online;
        this.groupInfo = groupInfo;
        this.productBand = productBand;
        this.model = model;
        this.cidName = cidName;
        this.folderName = folderName;
        this.categoryName = categoryName;
        this.productName = productName;
        this.forceBind = forceBind;
        this.maxDevShareNum = maxDevShareNum;
        this.androidH5Page = androidH5Page;
        this.iosH5Page = iosH5Page;
        this.deviceAndFolderBean = deviceAndFolderBean;
        this.groupBean = groupBean;
    }

    public DeviceBean(String logo, String androidH5Page, String iosH5Page, String deviceName, String desc) {
        this.logo = logo;
        this.androidH5Page = androidH5Page;
        this.iosH5Page = iosH5Page;
        this.deviceName = deviceName;
        this.desc = desc;
    }

    public DeviceBean(String workModeType, String devTid, String ctrlKey, String bindKey, Object wanIp, String cid, int tokenType, String binVersion, String sdkVer, String binType, Object serviceHost, int servicePort, String ssid, String mac, String finger, long currentLoginTime, long firstLoginTime, long lastLoginTime, Object lastUpdateTime, long lastReportDeviceInfoTime, long lastHeartbeat, String ownerUid, int devShareNum, Object timerMap, Object eventRuleMap, String deviceName, String desc, String folderId, String productPublicKey, String logo, String androidPageZipURL, String iosPageZipURL, boolean granted, boolean setSchedulerTask, boolean online, List<GroupInfoBean> groupInfo, String productBand, String model, String cidName, String folderName, TranslateBean categoryName, TranslateBean productName, String devType, List<GateWaySubDeviceBean> subDeviceInfo, boolean forceBind, int maxDevShareNum, String androidH5Page, String iosH5Page, DeviceAndFolderBean deviceAndFolderBean, GroupBean groupBean) {
        this.workModeType = workModeType;
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.bindKey = bindKey;
        this.wanIp = wanIp;
        this.cid = cid;
        this.tokenType = tokenType;
        this.binVersion = binVersion;
        this.sdkVer = sdkVer;
        this.binType = binType;
        this.serviceHost = serviceHost;
        this.servicePort = servicePort;
        this.ssid = ssid;
        this.mac = mac;
        this.finger = finger;
        this.currentLoginTime = currentLoginTime;
        this.firstLoginTime = firstLoginTime;
        this.lastLoginTime = lastLoginTime;
        this.lastUpdateTime = lastUpdateTime;
        this.lastReportDeviceInfoTime = lastReportDeviceInfoTime;
        this.lastHeartbeat = lastHeartbeat;
        this.ownerUid = ownerUid;
        this.devShareNum = devShareNum;
        this.timerMap = timerMap;
        this.eventRuleMap = eventRuleMap;
        this.deviceName = deviceName;
        this.desc = desc;
        this.folderId = folderId;
        this.productPublicKey = productPublicKey;
        this.logo = logo;
        this.granted = granted;
        this.setSchedulerTask = setSchedulerTask;
        this.online = online;
        this.groupInfo = groupInfo;
        this.productBand = productBand;
        this.model = model;
        this.cidName = cidName;
        this.folderName = folderName;
        this.categoryName = categoryName;
        this.productName = productName;
        this.devType = devType;
        this.subDevices = subDeviceInfo;
        this.forceBind = forceBind;
        this.maxDevShareNum = maxDevShareNum;
        this.androidH5Page = androidH5Page;
        this.iosH5Page = iosH5Page;
        this.deviceAndFolderBean = deviceAndFolderBean;
        this.groupBean = groupBean;
    }

    public DeviceBean(DeviceAndFolderBean deviceAndFolderBean) {
        this.deviceAndFolderBean = deviceAndFolderBean;
    }

    public DeviceBean(GroupBean groupBean) {
        this.groupBean = groupBean;
    }


    public DeviceBean(GateWaySubDeviceBean gateWaySubDeviceBean) {
        this.gateWaySubDeviceBean = gateWaySubDeviceBean;
    }

    public String getIdentity (int postion) {
        boolean isSub="SUB".equals(getDevType());
        return String.format("%s-%s%s", isSub?"APPSUBSEND":"APPSEND", isSub?getGateWaySubDeviceBean().getParentDevTid():getDevTid(), isSub?"-"+getGateWaySubDeviceBean().getDevTid():"");
    }

    @Override
    public String toString() {
        return "DeviceBean{" +
                "devTid='" + devTid + '\'' +
                ", ctrlKey='" + ctrlKey + '\'' +
                ", bindKey='" + bindKey + '\'' +
                ", wanIp=" + wanIp +
                ", cid='" + cid + '\'' +
                ", workModeType='" + workModeType + '\'' +
                ", tokenType=" + tokenType +
                ", binVersion='" + binVersion + '\'' +
                ", sdkVer='" + sdkVer + '\'' +
                ", binType='" + binType + '\'' +
                ", serviceHost=" + serviceHost +
                ", servicePort=" + servicePort +
                ", ssid='" + ssid + '\'' +
                ", mac='" + mac + '\'' +
                ", finger='" + finger + '\'' +
                ", currentLoginTime=" + currentLoginTime +
                ", firstLoginTime=" + firstLoginTime +
                ", lastLoginTime=" + lastLoginTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", lastReportDeviceInfoTime=" + lastReportDeviceInfoTime +
                ", lastHeartbeat=" + lastHeartbeat +
                ", ownerUid='" + ownerUid + '\'' +
                ", devShareNum=" + devShareNum +
                ", timerMap=" + timerMap +
                ", eventRuleMap=" + eventRuleMap +
                ", deviceName='" + deviceName + '\'' +
                ", desc='" + desc + '\'' +
                ", folderId='" + folderId + '\'' +
                ", productPublicKey='" + productPublicKey + '\'' +
                ", logo='" + logo + '\'' +
                ", granted=" + granted +
                ", setSchedulerTask=" + setSchedulerTask +
                ", online=" + online +
                ", groupInfo=" + groupInfo +
                ", productBand='" + productBand + '\'' +
                ", model='" + model + '\'' +
                ", cidName='" + cidName + '\'' +
                ", folderName='" + folderName + '\'' +
                ", categoryName=" + categoryName +
                ", productName=" + productName +
                ", devType='" + devType + '\'' +
                ", subDevices=" + subDevices +
                ", gateWaySubDeviceBean=" + gateWaySubDeviceBean +
                ", forceBind=" + forceBind +
                ", maxDevShareNum=" + maxDevShareNum +
                ", androidH5Page='" + androidH5Page + '\'' +
                ", iosH5Page='" + iosH5Page + '\'' +
                ", deviceAndFolderBean=" + deviceAndFolderBean +
                ", groupBean=" + groupBean +

                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeviceBean)) return false;

        DeviceBean that = (DeviceBean) o;

        if (getTokenType() != that.getTokenType()) return false;
        if (getServicePort() != that.getServicePort()) return false;
        if (getCurrentLoginTime() != that.getCurrentLoginTime()) return false;
        if (getFirstLoginTime() != that.getFirstLoginTime()) return false;
        if (getLastLoginTime() != that.getLastLoginTime()) return false;
        if (getLastReportDeviceInfoTime() != that.getLastReportDeviceInfoTime()) return false;
        if (getLastHeartbeat() != that.getLastHeartbeat()) return false;
        if (getDevShareNum() != that.getDevShareNum()) return false;
        if (isGranted() != that.isGranted()) return false;
        if (isSetSchedulerTask() != that.isSetSchedulerTask()) return false;
        if (isOnline() != that.isOnline()) return false;
        if (isForceBind() != that.isForceBind()) return false;
        if (getMaxDevShareNum() != that.getMaxDevShareNum()) return false;
        if (!getDevTid().equals(that.getDevTid())) return false;
        if (!getCtrlKey().equals(that.getCtrlKey())) return false;
        if (!getCid().equals(that.getCid())) return false;
        if (!getWorkModeType().equals(that.getWorkModeType())) return false;
        if (!getBinVersion().equals(that.getBinVersion())) return false;
        if (!getSdkVer().equals(that.getSdkVer())) return false;
        if (!getBinType().equals(that.getBinType())) return false;
        if (!getSsid().equals(that.getSsid())) return false;
        if (!getMac().equals(that.getMac())) return false;
        if (!getFinger().equals(that.getFinger())) return false;
        if (!getOwnerUid().equals(that.getOwnerUid())) return false;
        if (!getDeviceName().equals(that.getDeviceName())) return false;
        if (!getDesc().equals(that.getDesc())) return false;
        if (!getFolderId().equals(that.getFolderId())) return false;
        if (!getProductPublicKey().equals(that.getProductPublicKey())) return false;
        if (!getLogo().equals(that.getLogo())) return false;
        if (!getGroupInfo().equals(that.getGroupInfo())) return false;
        if (!getProductBand().equals(that.getProductBand())) return false;
        if (!getModel().equals(that.getModel())) return false;
        if (!getCidName().equals(that.getCidName())) return false;
        if (!getFolderName().equals(that.getFolderName())) return false;
        if (!getCategoryName().equals(that.getCategoryName())) return false;
        if (!getProductName().equals(that.getProductName())) return false;
        if (!getDevType().equals(that.getDevType())) return false;
        if (!getSubDevices().equals(that.getSubDevices())) return false;
        if (!getGateWaySubDeviceBean().equals(that.getGateWaySubDeviceBean())) return false;
        if (!getAndroidH5Page().equals(that.getAndroidH5Page())) return false;
        if (!getIosH5Page().equals(that.getIosH5Page())) return false;
        if (!getDeviceAndFolderBean().equals(that.getDeviceAndFolderBean())) return false;
        return getGroupBean().equals(that.getGroupBean());

    }

    @Override
    public int hashCode() {
        int result = getDevTid().hashCode();
        result = 31 * result + getCtrlKey().hashCode();
        result = 31 * result + getBindKey().hashCode();
        result = 31 * result + getCid().hashCode();
        result = 31 * result + getWorkModeType().hashCode();
        result = 31 * result + getTokenType();
        result = 31 * result + getBinVersion().hashCode();
        result = 31 * result + getSdkVer().hashCode();
        result = 31 * result + getBinType().hashCode();
        result = 31 * result + getServicePort();
        result = 31 * result + getSsid().hashCode();
        result = 31 * result + getMac().hashCode();
        result = 31 * result + getFinger().hashCode();
        result = 31 * result + (int) (getCurrentLoginTime() ^ (getCurrentLoginTime() >>> 32));
        result = 31 * result + (int) (getFirstLoginTime() ^ (getFirstLoginTime() >>> 32));
        result = 31 * result + (int) (getLastLoginTime() ^ (getLastLoginTime() >>> 32));
        result = 31 * result + (int) (getLastReportDeviceInfoTime() ^ (getLastReportDeviceInfoTime() >>> 32));
        result = 31 * result + (int) (getLastHeartbeat() ^ (getLastHeartbeat() >>> 32));
        result = 31 * result + getOwnerUid().hashCode();
        result = 31 * result + getDevShareNum();
        result = 31 * result + getDeviceName().hashCode();
        result = 31 * result + getDesc().hashCode();
        result = 31 * result + getFolderId().hashCode();
        result = 31 * result + getProductPublicKey().hashCode();
        result = 31 * result + getLogo().hashCode();
        result = 31 * result + (isGranted() ? 1 : 0);
        result = 31 * result + (isSetSchedulerTask() ? 1 : 0);
        result = 31 * result + (isOnline() ? 1 : 0);
        result = 31 * result + getGroupInfo().hashCode();
        result = 31 * result + getProductBand().hashCode();
        result = 31 * result + getModel().hashCode();
        result = 31 * result + getCidName().hashCode();
        result = 31 * result + getFolderName().hashCode();
        result = 31 * result + getCategoryName().hashCode();
        result = 31 * result + getProductName().hashCode();
        result = 31 * result + getDevType().hashCode();
        result = 31 * result + getSubDevices().hashCode();
        result = 31 * result + getGateWaySubDeviceBean().hashCode();
        result = 31 * result + (isForceBind() ? 1 : 0);
        result = 31 * result + getMaxDevShareNum();
        result = 31 * result + getAndroidH5Page().hashCode();
        result = 31 * result + getIosH5Page().hashCode();
        result = 31 * result + getDeviceAndFolderBean().hashCode();
        result = 31 * result + getGroupBean().hashCode();
        return result;
    }

}

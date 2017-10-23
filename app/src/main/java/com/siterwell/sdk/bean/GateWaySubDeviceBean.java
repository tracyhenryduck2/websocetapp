package com.siterwell.sdk.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class GateWaySubDeviceBean implements Serializable {

    private static final long serialVersionUID = -3900676727672741256L;
    /**
     * devTid : 00124b0009d52246
     * mid : 01376a96e3c9
     * connectType : zigbee_zha
     * online : false
     * binVer : 3.0.10
     * devName :
     * devModel : 2
     * manufacturerName : Shuncom
     * description : kg-02
     * cidName : 生活电器/测试增加小品类
     * logo : http://c-img-s.hekr.me/598139d2ba894218b3353d8041f163e2/1473406666891.png
     * androidH5Page : https://h5page.hekr.me/android/8d5777b567f542a581a6065e0fb9046c/index.html
     * iosH5Page : https://h5page.hekr.me/android/8d5777b567f542a581a6065e0fb9046c/index.html
     * iosPageZipURL : https://h5package.hekr.me/android/8d5777b567f542a581a6065e0fb9046c/eUiFFzIO1474276827842.zip
     * androidPageZipURL : https://h5package.hekr.me/android/8d5777b567f542a581a6065e0fb9046c/eUiFFzIO1474276827842.zip
     */

    private String devTid;
    private String mid;
    private String connectType;
    private boolean online;
    private String binVer;
    private String devName;
    private String devModel;
    private String manufacturerName;
    private String description;
    private String cidName;
    private String logo;
    private String androidH5Page;
    private String iosH5Page;
    private String iosPageZipURL;
    private String androidPageZipURL;
    private String productPublicKey;
    private TranslateBean categoryName;
    private TranslateBean productName;
    private String folderId;
    private String folderName;


    private String parentDevTid;
    private String parentCtrlKey;
    @JSONField(serialize = false)
    private Boolean getWayOnline;
    private String devType;


    public GateWaySubDeviceBean() {
    }

    public Boolean getGetWayOnline() {
        return getWayOnline == null ? false : getWayOnline;
    }

    public void setGetWayOnline(Boolean getWayOnline) {
        this.getWayOnline = getWayOnline;
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

    public String getConnectType() {
        return connectType;
    }

    public void setConnectType(String connectType) {
        this.connectType = connectType;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getBinVer() {
        return binVer;
    }

    public void setBinVer(String binVer) {
        this.binVer = binVer;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public String getDevModel() {
        return devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCidName() {
        return cidName;
    }

    public void setCidName(String cidName) {
        this.cidName = cidName;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public String getIosPageZipURL() {
        return iosPageZipURL;
    }

    public void setIosPageZipURL(String iosPageZipURL) {
        this.iosPageZipURL = iosPageZipURL;
    }

    public String getAndroidPageZipURL() {
        return androidPageZipURL;
    }

    public void setAndroidPageZipURL(String androidPageZipURL) {
        this.androidPageZipURL = androidPageZipURL;
    }

    public String getProductPublicKey() {
        return productPublicKey;
    }

    public void setProductPublicKey(String productPublicKey) {
        this.productPublicKey = productPublicKey;
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

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getParentDevTid() {
        return parentDevTid;
    }

    public void setParentDevTid(String parentDevTid) {
        this.parentDevTid = parentDevTid;
    }

    public String getParentCtrlKey() {
        return parentCtrlKey;
    }

    public void setParentCtrlKey(String parentCtrlKey) {
        this.parentCtrlKey = parentCtrlKey;
    }

    public String getDevType() {
        return devType;
    }

    public void setDevType(String devType) {
        this.devType = devType;
    }

    @Override
    public String toString() {
        return "GateWaySubDeviceBean{" +
                "devTid='" + devTid + '\'' +
                ", mid='" + mid + '\'' +
                ", connectType='" + connectType + '\'' +
                ", online=" + online +
                ", binVer='" + binVer + '\'' +
                ", devName='" + devName + '\'' +
                ", devModel='" + devModel + '\'' +
                ", manufacturerName='" + manufacturerName + '\'' +
                ", description='" + description + '\'' +
                ", cidName='" + cidName + '\'' +
                ", logo='" + logo + '\'' +
                ", androidH5Page='" + androidH5Page + '\'' +
                ", iosH5Page='" + iosH5Page + '\'' +
                ", iosPageZipURL='" + iosPageZipURL + '\'' +
                ", androidPageZipURL='" + androidPageZipURL + '\'' +
                ", productPublicKey='" + productPublicKey + '\'' +
                ", categoryName=" + categoryName +
                ", productName=" + productName +
                ", folderId='" + folderId + '\'' +
                ", folderName='" + folderName + '\'' +
                ", parentDevTid='" + parentDevTid + '\'' +
                ", parentCtrlKey='" + parentCtrlKey + '\'' +
                ", devType='" + devType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GateWaySubDeviceBean)) return false;

        GateWaySubDeviceBean that = (GateWaySubDeviceBean) o;

        if (isOnline() != that.isOnline()) return false;
        if (!getDevTid().equals(that.getDevTid())) return false;
        if (!getMid().equals(that.getMid())) return false;
        if (!getConnectType().equals(that.getConnectType())) return false;
        if (!getBinVer().equals(that.getBinVer())) return false;
        if (!getDevName().equals(that.getDevName())) return false;
        if (!getDevModel().equals(that.getDevModel())) return false;
        if (!getManufacturerName().equals(that.getManufacturerName())) return false;
        if (!getDescription().equals(that.getDescription())) return false;
        if (!getCidName().equals(that.getCidName())) return false;
        if (!getLogo().equals(that.getLogo())) return false;
        if (!getAndroidH5Page().equals(that.getAndroidH5Page())) return false;
        if (!getIosH5Page().equals(that.getIosH5Page())) return false;
        if (!getIosPageZipURL().equals(that.getIosPageZipURL())) return false;
        if (!getAndroidPageZipURL().equals(that.getAndroidPageZipURL())) return false;
        if (!getProductPublicKey().equals(that.getProductPublicKey())) return false;
        if (!getCategoryName().equals(that.getCategoryName())) return false;
        if (!getProductName().equals(that.getProductName())) return false;
        if (!getFolderId().equals(that.getFolderId())) return false;
        if (!getFolderName().equals(that.getFolderName())) return false;
        if (!getParentDevTid().equals(that.getParentDevTid())) return false;
        if (!getParentCtrlKey().equals(that.getParentCtrlKey())) return false;
        return getDevType().equals(that.getDevType());

    }

    @Override
    public int hashCode() {
        int result = getDevTid().hashCode();
        result = 31 * result + getMid().hashCode();
        result = 31 * result + getConnectType().hashCode();
        result = 31 * result + (isOnline() ? 1 : 0);
        result = 31 * result + getBinVer().hashCode();
        result = 31 * result + getDevName().hashCode();
        result = 31 * result + getDevModel().hashCode();
        result = 31 * result + getManufacturerName().hashCode();
        result = 31 * result + getDescription().hashCode();
        result = 31 * result + getCidName().hashCode();
        result = 31 * result + getLogo().hashCode();
        result = 31 * result + getAndroidH5Page().hashCode();
        result = 31 * result + getIosH5Page().hashCode();
        result = 31 * result + getIosPageZipURL().hashCode();
        result = 31 * result + getAndroidPageZipURL().hashCode();
        result = 31 * result + getProductPublicKey().hashCode();
        result = 31 * result + getCategoryName().hashCode();
        result = 31 * result + getProductName().hashCode();
        result = 31 * result + getFolderId().hashCode();
        result = 31 * result + getFolderName().hashCode();
        result = 31 * result + getParentDevTid().hashCode();
        result = 31 * result + getParentCtrlKey().hashCode();
        result = 31 * result + getDevType().hashCode();
        return result;
    }

}

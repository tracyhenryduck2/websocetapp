package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class FirmwareBean implements Serializable {
    private static final long serialVersionUID = -6013091826096823484L;
    /**
     * binUrl : http://fs.hekr.me/dev/fw/ota/xxx.bin
     * md5 : 5d41402abc4b2a76b9719d911017c592
     * latestBinType : B
     * latestBinVer : 1.2.3.4
     * size : 77
     */

    private String binUrl;
    private String md5;
    private String latestBinType;
    private String latestBinVer;
    private int size;

    public String getBinUrl() {
        return binUrl;
    }

    public void setBinUrl(String binUrl) {
        this.binUrl = binUrl;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getLatestBinType() {
        return latestBinType;
    }

    public void setLatestBinType(String latestBinType) {
        this.latestBinType = latestBinType;
    }

    public String getLatestBinVer() {
        return latestBinVer;
    }

    public void setLatestBinVer(String latestBinVer) {
        this.latestBinVer = latestBinVer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "FirmwareBean{" +
                "binUrl='" + binUrl + '\'' +
                ", md5='" + md5 + '\'' +
                ", latestBinType='" + latestBinType + '\'' +
                ", latestBinVer='" + latestBinVer + '\'' +
                ", size=" + size +
                '}';
    }
}

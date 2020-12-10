package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class OAuthRequestBean implements Serializable {

    private static final long serialVersionUID = -6479075487967651435L;
    /**
     * registerId : 9c1f50147d8244a09fa7e7f5e60a2355
     * grantor : 17316800801
     * grantee : 14169321290
     * devTid : ESP_2M_5CCF7F05D670
     * ctrlKey : e9c8a2b3df7d4dc6b6f3578e308c6cbc
     * expire : 30000000000
     * desc : hekr
     * instructionExpire : null
     * mode : ALL
     * enableIFTTT : false
     * enableScheduler : false
     * deviceName :
     * granteeName : Joyful
     * granteeAvater : {"small":"http://hekr-images.ufile.ucloud.com.cn/ufile-1416932129000000000000-788e1a7d62ccb6eafd91a84fecb8d701.jpg"}
     */

    private String registerId;
    private String grantor;
    private String grantee;
    private String devTid;
    private String ctrlKey;
    private long expire;
    private String desc;
    private Object instructionExpire;
    private String mode;
    private boolean enableIFTTT;
    private boolean enableScheduler;
    private String deviceName;
    private String granteeName;
    /**
     * small : http://hekr-images.ufile.ucloud.com.cn/ufile-1416932129000000000000-788e1a7d62ccb6eafd91a84fecb8d701.jpg
     */

    private GranteeAvater granteeAvater;

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public String getGrantor() {
        return grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
    }

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
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

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Object getInstructionExpire() {
        return instructionExpire;
    }

    public void setInstructionExpire(Object instructionExpire) {
        this.instructionExpire = instructionExpire;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public boolean isEnableIFTTT() {
        return enableIFTTT;
    }

    public void setEnableIFTTT(boolean enableIFTTT) {
        this.enableIFTTT = enableIFTTT;
    }

    public boolean isEnableScheduler() {
        return enableScheduler;
    }

    public void setEnableScheduler(boolean enableScheduler) {
        this.enableScheduler = enableScheduler;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getGranteeName() {
        return granteeName;
    }

    public void setGranteeName(String granteeName) {
        this.granteeName = granteeName;
    }

    public GranteeAvater getGranteeAvater() {
        return granteeAvater;
    }

    public void setGranteeAvater(GranteeAvater granteeAvater) {
        this.granteeAvater = granteeAvater;
    }

    public static class GranteeAvater {
        private String small;

        public String getSmall() {
            return small;
        }

        public void setSmall(String small) {
            this.small = small;
        }

        @Override
        public String toString() {
            return "GranteeAvater{" +
                    "small='" + small + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OAuthRequestBean{" +
                "registerId='" + registerId + '\'' +
                ", grantor='" + grantor + '\'' +
                ", grantee='" + grantee + '\'' +
                ", devTid='" + devTid + '\'' +
                ", ctrlKey='" + ctrlKey + '\'' +
                ", expire=" + expire +
                ", desc='" + desc + '\'' +
                ", instructionExpire=" + instructionExpire +
                ", mode='" + mode + '\'' +
                ", enableIFTTT=" + enableIFTTT +
                ", enableScheduler=" + enableScheduler +
                ", deviceName='" + deviceName + '\'' +
                ", granteeName='" + granteeName + '\'' +
                ", granteeAvater=" + granteeAvater +
                '}';
    }

}

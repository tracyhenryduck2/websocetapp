package com.siterwell.sdk.http.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class OAuthBean implements Serializable {

    private static final long serialVersionUID = 8882474428634728398L;
    /**
     * grantor : uid1
     * devTid : ESP_xxx
     * ctrlKey : xxxxxx
     * expire : 300
     * mode : ALL
     * desc : xxxx
     * enableScheduler : true
     * enableIFTTT : true
     */

    private String grantor;
    private String devTid;
    private String ctrlKey;
    private long expire;
    private String mode;
    private String desc;
    private boolean enableScheduler = true;
    private boolean enableIFTTT = true;

    public OAuthBean() {
    }

    public String getGrantor() {
        return grantor;
    }

    public void setGrantor(String grantor) {
        this.grantor = grantor;
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

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEnableScheduler() {
        return enableScheduler;
    }

    public void setEnableScheduler(boolean enableScheduler) {
        this.enableScheduler = enableScheduler;
    }

    public boolean isEnableIFTTT() {
        return enableIFTTT;
    }

    public void setEnableIFTTT(boolean enableIFTTT) {
        this.enableIFTTT = enableIFTTT;
    }

    public OAuthBean(String grantor, String devTid, String ctrlKey, long expire, String mode, String desc) {
        this.grantor = grantor;
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.expire = expire;
        this.mode = mode;
        this.desc = desc;
    }

    public OAuthBean(String grantor, String devTid, String ctrlKey, long expire, String mode, String desc, boolean enableScheduler, boolean enableIFTTT) {
        this.grantor = grantor;
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.expire = expire;
        this.mode = mode;
        this.desc = desc;
        this.enableScheduler = enableScheduler;
        this.enableIFTTT = enableIFTTT;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

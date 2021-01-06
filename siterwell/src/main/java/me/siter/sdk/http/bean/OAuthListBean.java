package me.siter.sdk.http.bean;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class OAuthListBean implements Serializable {

    private static final long serialVersionUID = 6860804387031227757L;
    /**
     * id : 5757edb4e4b08b9760884ae5
     * devTid : ESP_2M_5CCF7F8B2A99
     * grantee : 36282401231
     * grantTime : 1465380276208
     * updateTime : null
     * expire : 30000000000
     * instructionExpire : null
     * mode : ALL
     * enableIFTTT : true
     * enableScheduler : true
     * desc :
     * grantorName :
     * granteeName : 123ghhh
     */

    private String id;
    private String devTid;
    private String grantee;
    private long grantTime;
    private Object updateTime;
    private long expire;
    private Object instructionExpire;
    private String mode;
    private boolean enableIFTTT;
    private boolean enableScheduler;
    private String desc;
    private String grantorName;
    private String granteeName;

    public OAuthListBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }


    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(String grantee) {
        this.grantee = grantee;
    }

    public long getGrantTime() {
        return grantTime;
    }

    public void setGrantTime(long grantTime) {
        this.grantTime = grantTime;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getGrantorName() {
        return grantorName;
    }

    public void setGrantorName(String grantorName) {
        this.grantorName = grantorName;
    }

    public String getGranteeName() {
        return granteeName;
    }

    public void setGranteeName(String granteeName) {
        this.granteeName = granteeName;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

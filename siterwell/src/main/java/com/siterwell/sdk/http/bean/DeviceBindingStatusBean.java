package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DeviceBindingStatusBean implements Serializable {

    private static final long serialVersionUID = -1210498941479694661L;
    /**
     * uid : 23511279032
     * bindTime : 1470707431384
     * expire : -1
     * desc :
     */

    private String uid;
    private long bindTime;
    private long expire;
    private String desc;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
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

    @Override
    public String toString() {
        return "DeviceBindingStatusBean{" +
                "uid='" + uid + '\'' +
                ", bindTime=" + bindTime +
                ", expire=" + expire +
                ", desc='" + desc + '\'' +
                '}';
    }
}

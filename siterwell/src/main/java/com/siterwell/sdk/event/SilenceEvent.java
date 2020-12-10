package com.siterwell.sdk.event;

/**
 * Created by TracyHenry on 2018/3/23.
 */

public class SilenceEvent {
    private String devTid;
    private int Success;
    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public int getSuccess() {
        return Success;
    }

    public void setSuccess(int success) {
        Success = success;
    }
}

package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/6/24.
 */

public class SetSmokeTypeEvent {

    private int event;

    private String devTid;

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }
}

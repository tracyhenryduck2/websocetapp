package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class WsSwitchEvent {

    //1、用户退出当前账号
    private int status;

    public WsSwitchEvent(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WsSwitchEvent{" +
                "status=" + status +
                '}';
    }
}

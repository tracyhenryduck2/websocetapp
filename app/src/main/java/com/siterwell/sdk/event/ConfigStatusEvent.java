package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ConfigStatusEvent {
    private String msg;

    public ConfigStatusEvent(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "ConfigStatusEvent{" +
                "msg='" + msg + '\'' +
                '}';
    }
}

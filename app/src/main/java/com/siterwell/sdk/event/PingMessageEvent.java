package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class PingMessageEvent {
    private String str;

    public PingMessageEvent(String str) {
        this.str = str;
    }

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

}

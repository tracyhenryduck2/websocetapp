package com.siterwell.demo.BusEvents;

/**
 * Created by Administrator on 2017/11/30.
 */

public class BindPushEvent {
    private int type; //1为个推，2为FCM,3为小米,4为华为;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

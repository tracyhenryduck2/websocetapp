package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by gc-0001 on 2017/6/13.
 */

public class WifiTimerBean implements Serializable {
    private int notice;
    private String week;
    private int hour;
    private int min;
    private String deviceid;
    private int enable;
    private String timerid;
    private int tostatus;


    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public String getTimerid() {
        return timerid;
    }

    public void setTimerid(String timerid) {
        this.timerid = timerid;
    }

    public int getTostatus() {
        return tostatus;
    }

    public void setTostatus(int tostatus) {
        this.tostatus = tostatus;
    }

    @Override
    public String toString() {
        return "WifiTimerBean{" +
                "deviceid='" + deviceid + '\'' +
                ", notice=" + notice +
                ", week='" + week + '\'' +
                ", hour=" + hour +
                ", min=" + min +
                ", enable=" + enable +
                ", timerid='" + timerid + '\'' +
                ", tostatus=" + tostatus +
                '}';
    }
}

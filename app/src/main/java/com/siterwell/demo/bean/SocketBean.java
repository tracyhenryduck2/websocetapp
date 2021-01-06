package com.siterwell.demo.bean;

import java.util.List;

import me.siter.sdk.http.bean.DeviceBean;

/**
 * Created by TracyHenry on 2018/2/6.
 */

public class SocketBean extends DeviceBean {
    /**
     * 信号强度
     */
    protected static final int SIGNAL_BAD = 1;//差
    protected static final int SIGNAL_FINE = 2;//一般
    protected static final int SIGNAL_GOOD = 3;//好
    protected static final int SIGNAL_EXCELLENT = 4;//很好

    private int socketmodel;
    private int socketstatus;
    private String circleon;
    private String circleoff;
    private int circlenumber;
    private String countdowntime;
    private int action;
    private int notice;
    private int countdownenable;
    private int signal;
    private List<WifiTimerBean> wifiTimerBeans;

    public int getSocketmodel() {
        return socketmodel;
    }

    public void setSocketmodel(int socketmodel) {
        this.socketmodel = socketmodel;
    }

    public int getSocketstatus() {
        return socketstatus;
    }

    public void setSocketstatus(int socketstatus) {
        this.socketstatus = socketstatus;
    }

    public String getCircleon() {
        return circleon;
    }

    public void setCircleon(String circleon) {
        this.circleon = circleon;
    }

    public String getCircleoff() {
        return circleoff;
    }

    public void setCircleoff(String circleoff) {
        this.circleoff = circleoff;
    }

    public int getCirclenumber() {
        return circlenumber;
    }

    public void setCirclenumber(int circlenumber) {
        this.circlenumber = circlenumber;
    }

    public String getCountdowntime() {
        return countdowntime;
    }

    public void setCountdowntime(String countdowntime) {
        this.countdowntime = countdowntime;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getNotice() {
        return notice;
    }

    public void setNotice(int notice) {
        this.notice = notice;
    }

    public int getCountdownenable() {
        return countdownenable;
    }

    public void setCountdownenable(int countdownenable) {
        this.countdownenable = countdownenable;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }


    public List<WifiTimerBean> getWifiTimerBeans() {
        return wifiTimerBeans;
    }

    public void setWifiTimerBeans(List<WifiTimerBean> wifiTimerBeans) {
        this.wifiTimerBeans = wifiTimerBeans;
    }

    @Override
    public String toString() {
        return "SocketBean{" +
                "socketmodel=" + socketmodel +
                ", socketstatus=" + socketstatus +
                ", circleon='" + circleon + '\'' +
                ", circleoff='" + circleoff + '\'' +
                ", circlenumber=" + circlenumber +
                ", countdowntime='" + countdowntime + '\'' +
                ", action=" + action +
                ", notice=" + notice +
                ", countdownenable=" + countdownenable +
                ", signal=" + signal +
                ", wifiTimerBeans=" + wifiTimerBeans +
                '}';
    }
}

package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class NetworkEvent {
    private int netStatus;

    public NetworkEvent(int netStatus) {
        this.netStatus = netStatus;
    }

    public int getNetStatus() {
        return netStatus;
    }

    public void setNetStatus(int netStatus) {
        this.netStatus = netStatus;
    }

    @Override
    public String toString() {
        return "NetworkEvent{" +
                "netStatus=" + netStatus +
                '}';
    }
}

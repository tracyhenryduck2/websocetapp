package com.siterwell.sdk.event;

/**
 * Created by TracyHenry on 2018/3/23.
 */

public class UdpShakeHandsEvent {

    private int type; //type=1发起请求，2为握手成功，3为握手失败

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

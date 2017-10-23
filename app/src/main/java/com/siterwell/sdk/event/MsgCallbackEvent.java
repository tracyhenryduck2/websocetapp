package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.FilterBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class MsgCallbackEvent {
    //0、超时回调 1、正常回调
    public static final int TIMEOUTCALLBACK=0;
    public static final int NORMALCALLBACK=1;
    private int callBackType;
    private FilterBean filterBean;
    private String msg;

    public MsgCallbackEvent(int callBackType, FilterBean filterBean) {
        this.callBackType = callBackType;
        this.filterBean = filterBean;
    }

    public MsgCallbackEvent(int callBackType, FilterBean filterBean, String msg) {
        this.callBackType = callBackType;
        this.filterBean = filterBean;
        this.msg = msg;
    }

    public int getCallBackType() {
        return callBackType;
    }

    public void setCallBackType(int callBackType) {
        this.callBackType = callBackType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public FilterBean getFilterBean() {
        return filterBean;
    }

    public void setFilterBean(FilterBean filterBean) {
        this.filterBean = filterBean;
    }

    @Override
    public String toString() {
        return "MsgCallbackEvent{" +
                ", filterBean=" + filterBean +
                '}';
    }
}

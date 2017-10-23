package com.siterwell.sdk.bean;

import com.siterwell.sdk.listener.DataReceiverListener;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/10/16.
 */

public class CtrlBean implements Serializable {

    private static final long serialVersionUID = 5966000922161573239L;
    private Object object;
    private String devTid;
    private JSONObject data;
    private DataReceiverListener dataReceiverListener;

    public CtrlBean(Object object, String devTid, JSONObject data, DataReceiverListener dataReceiverListener) {
        this.object = object;
        this.devTid = devTid;
        this.data = data;
        this.dataReceiverListener = dataReceiverListener;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public DataReceiverListener getDataReceiverListener() {
        return dataReceiverListener;
    }

    public void setDataReceiverListener(DataReceiverListener dataReceiverListener) {
        this.dataReceiverListener = dataReceiverListener;
    }

    @Override
    public String toString() {
        return "CtrlBean{" +
                "object=" + object +
                ", devTid='" + devTid + '\'' +
                ", data=" + data +
                ", dataReceiverListener=" + dataReceiverListener +
                '}';
    }
}

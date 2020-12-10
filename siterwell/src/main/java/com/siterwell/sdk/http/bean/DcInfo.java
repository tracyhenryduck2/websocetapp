package com.siterwell.sdk.http.bean;

import java.io.Serializable;

/**
 * Created by TracyHenry on 2018/3/2.
 */

public class DcInfo implements Serializable {

    private long opTimestamp;

    private String fromDC ;
    private String dc ;
    private String area;
    private String fromArea;
    private String connectHost;

    public long getOpTimestamp() {
        return opTimestamp;
    }

    public void setOpTimestamp(long opTimestamp) {
        this.opTimestamp = opTimestamp;
    }

    public String getFromDC() {
        return fromDC;
    }

    public void setFromDC(String fromDC) {
        this.fromDC = fromDC;
    }

    public String getDc() {
        return dc;
    }

    public void setDc(String dc) {
        this.dc = dc;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFromArea() {
        return fromArea;
    }

    public void setFromArea(String fromArea) {
        this.fromArea = fromArea;
    }

    public String getConnectHost() {
        return connectHost;
    }

    public void setConnectHost(String connectHost) {
        this.connectHost = connectHost;
    }

    @Override
    public String toString() {
        return "DcInfo{" +
                "opTimestamp=" + opTimestamp +
                ", fromDC='" + fromDC + '\'' +
                ", dc='" + dc + '\'' +
                ", area='" + area + '\'' +
                ", fromArea='" + fromArea + '\'' +
                ", connectHost='" + connectHost + '\'' +
                '}';
    }
}

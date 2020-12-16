package me.siter.sdk.service;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 创建连接时的选择项，可以为WebSocket/TCP/UDP等
 */

public class ConnOptions {

    public static final int TYPE_CONN_HTTP = 1;
    public static final int TYPE_CONN_WEBSOCKET = 2;
    public static final int TYPE_CONN_TCP_NORMAL = 3;
    public static final int TYPE_CONN_TCP_NIO = 4;
    public static final int TYPE_CONN_UDP_NORMAL = 5;
    public static final int TYPE_CONN_UDP_NIO = 6;

    private String mPrefix;
    private int mConnType;
    private String mIpOrUrl;
    private int mPort;

    public ConnOptions(int connType, String ipOrUrl, int port) {
        this.mConnType = connType;
        this.mIpOrUrl = ipOrUrl;
        this.mPort = port;
    }

    public ConnOptions(String prefix, int connType, String ipOrUrl, int port) {
        this.mPrefix = prefix;
        this.mConnType = connType;
        this.mIpOrUrl = ipOrUrl;
        this.mPort = port;
    }

    public int getconnType() {
        return mConnType;
    }

    public void setconnType(int mConnType) {
        this.mConnType = mConnType;
    }

    public String getIpOrUrl() {
        return mIpOrUrl;
    }

    public void setIpOrUrl(String mIpOrUrl) {
        this.mIpOrUrl = mIpOrUrl;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int mPort) {
        this.mPort = mPort;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void setPrefix(String prefix) {
        this.mPrefix = prefix;
    }

    @Override
    public String toString() {
        return "ConnOptions{" +
                "prefix='" + mPrefix + '\'' +
                ", connType=" + mConnType +
                ", ipOrUrl='" + mIpOrUrl + '\'' +
                ", port=" + mPort +
                '}';
    }
}


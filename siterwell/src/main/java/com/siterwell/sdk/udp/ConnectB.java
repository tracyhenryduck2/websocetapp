package com.siterwell.sdk.udp;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by gc-0001 on 2017/6/7.
 */

public  class ConnectB {

    public InetAddress targetip;
    public String devTid; //搜索到的140设备id;
    public DatagramSocket ds;

    private static ConnectB instance = null;
    private ConnectB(){

    }
    public static ConnectB getInstance(){
        if (instance == null) {
//            synchronized (ConnectionPojo.class) {
//                if (instance == null) {
            return instance = new ConnectB();
//                }
//            }
        }
        return instance;
    }
}

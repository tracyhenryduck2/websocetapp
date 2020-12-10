package com.siterwell.sdk.udp;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class UDPSendData implements Runnable {
    private static final String TAG = "UDPSendData";
    private static final int PORT = 10000;
    private InetAddress localip,hostip;
    private DatagramSocket ds;
    private DatagramPacket dp;
    private byte[] bytes;
    public UDPSendData(DatagramSocket ds, InetAddress hostip, String code){
        this.ds = ds;
        this.hostip = hostip;
        bytes = new byte[code.length()];
        bytes = code.getBytes();
    }

    @Override
    public void run() {
        dp = new DatagramPacket(bytes,bytes.length,hostip,PORT);
        try {
            Log.i(TAG," send data start");
            ds.send(dp);
            Log.i(TAG," send data "+ hostip.toString() +"==="+new String(bytes));
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG," send data failed");
            ConnectB.getInstance().targetip = null;
        }catch (NullPointerException e){
            e.printStackTrace();
            Log.i(TAG," send data failed");
            ConnectB.getInstance().targetip = null;
        }
    }
}

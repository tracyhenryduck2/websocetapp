package com.siterwell.sdk.udp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class UDPRecData implements Runnable {
    private static final String TAG = "UDPRecData";
    private static final int PORT = 10000;
    private InetAddress hostip;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private byte[] bytes;
    private InetAddress hostAdd;
    private Context context;
    private boolean enudp = true;
    public static String UDPMessage = "com.siterwell.sdk.UDPMessage";
    public static String UDPUpload = "com.siterwell.sdk.Upload";
    public UDPRecData(DatagramSocket ds, InetAddress hostAdd, Context context){
        this.datagramSocket = ds;
        this.hostAdd = hostAdd;
        this.context = context;
        this.enudp = true;
    }

    @Override
    public void run() {
        while (enudp){
            bytes = new byte[512];
            datagramPacket = new DatagramPacket(bytes,bytes.length,hostAdd,PORT);
            try {
                Log.i(TAG," start to receive");
                datagramSocket.receive(datagramPacket);
                String msg = new String(datagramPacket.getData());
                Log.i(TAG,"get udp message:"+msg);
                hostip = datagramPacket.getAddress();
                resolveData(msg);
            } catch (IOException e) {
                Log.i(TAG," receive failed  Socket closed");
                break;
            }catch (NullPointerException e){
                e.printStackTrace();
                Log.i(TAG," receive failed NullPointerException");
            }
        }
    }

    private void resolveData(String msg){
        Log.i(TAG,msg);

        if(msg.startsWith("ESP_") ) {
                ConnectB.getInstance().targetip = hostip;
                ConnectB.getInstance().devTid = msg;
                Log.i(TAG, " targetip=" + ConnectB.getInstance().targetip.toString());

        }
        sendBroadcast(msg);
    }

    private void sendBroadcast(String msg){
        Intent intent = new Intent(UDPMessage);
        intent.putExtra(UDPUpload,msg);
        context.sendBroadcast(intent);
    }

    public void  close(){
        try {
            enudp = false;
            datagramSocket.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}

package com.siterwell.sdk.common;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.siterwell.sdk.event.UdpShakeHandsEvent;
import com.siterwell.sdk.http.SiterConstantsUtil;
import com.siterwell.sdk.udp.ConnectB;
import com.siterwell.sdk.udp.NetWorkUtils;
import com.siterwell.sdk.udp.SeartchWifiData;
import com.siterwell.sdk.udp.UDPRecData;
import com.siterwell.sdk.udp.UDPSendData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.Nullable;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.hekr.sdk.Hekr;
import me.hekr.sdk.dispatcher.IMessageFilter;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by TracyHenry on 2018/4/25.
 */

public class SiterCoreService extends Service {
    private final static String TAG = "SiterCoreService";
    private IMessageFilter filter;
    private static final int PORT = 10000;
    private UDPRecData udpRecData;
    private ExecutorService sendService,receiveservice;
    private SeartchWifiData seartchWifiData;
    private SeartchWifiData.MyTaskCallback taskCallback;
    private SiterReceiver siterReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        sendService = Executors.newSingleThreadExecutor();
        receiveservice = Executors.newSingleThreadExecutor();
        taskCallback = new SeartchWifiData.MyTaskCallback() {
            @Override
            public void operationFailed() {
                Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ failed");
                udpRecData.close();
                UdpShakeHandsEvent udpShakeHandsEvent = new UdpShakeHandsEvent();
                udpShakeHandsEvent.setType(3);
                EventBus.getDefault().post(udpShakeHandsEvent);
                seartchWifiData = null;
            }

            @Override
            public void operationSuccess() {
                Log.i(TAG, "+++++++++++++++++++++++++++++++++++++++++++++++ success");
                initreceiveUdp();
                UdpShakeHandsEvent udpShakeHandsEvent = new UdpShakeHandsEvent();
                udpShakeHandsEvent.setType(2);
                EventBus.getDefault().post(udpShakeHandsEvent);
                seartchWifiData = null;
            }

            @Override
            public void doReSendAction() {
                startShakeHandsGs140();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveAllMessage();
        siterReceiver = new SiterReceiver();
        registerReceiver(siterReceiver, new IntentFilter(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        unregisterReceiver(siterReceiver);
    }


    /**
     * 主动接收消息
     */
    private void receiveAllMessage() {
        if(filter == null){
            filter = new IMessageFilter() {
                @Override
                public boolean doFilter(String in) {
                    return true;
                }
            };
            Hekr.getHekrClient().receiveMessage(filter, new HekrMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    // 收到消息
                    Intent intent = new Intent(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(SiterConstantsUtil.HEKR_WS_PAYLOAD,msg);
                    sendBroadcast(intent);
                }

                @Override
                public void onTimeout() {
                    // 主动接受不会有这个回调
                    if(SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()!=null){
                        for(TimeOutListener timeOutListener:SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()){
                            timeOutListener.timeout();
                        }
                    }
                }

                @Override
                public void onError(int errorCode, String message) {
                    // 接收错误
                    Intent intent = new Intent(SiterConstantsUtil.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(SiterConstantsUtil.HEKR_WS_PAYLOAD,message);
                    sendBroadcast(intent);
                }
            });
        }

    }


    private void startShakeHandsGs140(){
        try {
            String localAddress = NetWorkUtils.getLocalIpAddress(this);
            InetAddress target = null;
            String targetip = localAddress.substring(0,localAddress.lastIndexOf(".")+1)+255;
            try {
                target = InetAddress.getByName(targetip);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            Log.i(TAG," 发送搜索udp广播地址 ===" + target.toString());
            UDPSendData udpSendData = new UDPSendData(ConnectB.getInstance().ds,target,"IOT_KEY?");
            sendService.execute(udpSendData);
            sendService.awaitTermination(50, TimeUnit.MICROSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            Log.i(TAG," targetip is null" );
        }
    }

    private void initreceiveUdp() {

        if(udpRecData!=null){
            udpRecData.close();
        }
        if(receiveservice!=null){
            receiveservice.shutdown();
        }

        Log.i(TAG,"initreceiveUdp");
        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setReuseAddress(true);
            datagramSocket.connect(ConnectB.getInstance().targetip,PORT);
            ConnectB.getInstance().ds = datagramSocket;
        } catch (SocketException e) {
            e.printStackTrace();
        }
        Log.i(TAG," 接收udp地址 ===" + ConnectB.getInstance().targetip.toString());
        receiveservice = Executors.newSingleThreadExecutor();
        udpRecData = new UDPRecData(ConnectB.getInstance().ds, ConnectB.getInstance().targetip,this);
        receiveservice.execute(udpRecData);


    }

    private void initBroadcastreceiveUdp() {
        Log.i(TAG,"initBroadcastreceiveUdp");
        if(udpRecData!=null){
            udpRecData.close();
        }
        if(receiveservice!=null){
            receiveservice.shutdown();
        }


        String localAddress = NetWorkUtils.getLocalIpAddress(this);
        InetAddress ip = null;
        try {
            ip = InetAddress.getByName(localAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.i(TAG, " send create ip failed");
        }



        InetAddress target = null;
        String targetip = localAddress.substring(0, localAddress.lastIndexOf(".") + 1) + 255;
        Log.i(TAG," 广播接收udp地址 ===" + targetip);
        try {
            target = InetAddress.getByName(targetip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        DatagramSocket datagramSocket = null;
        try {

            datagramSocket = new DatagramSocket(PORT, ip);
            ConnectB.getInstance().ds = datagramSocket;
        } catch (SocketException e) {
            e.printStackTrace();
        }


        receiveservice = Executors.newSingleThreadExecutor();
        udpRecData = new UDPRecData(datagramSocket,target,this);
        receiveservice.execute(udpRecData);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅内网握手事件
    public  void onEventMainThread(UdpShakeHandsEvent event){
        //1代表发起请求
        if(event.getType()==1 && seartchWifiData==null){
            initBroadcastreceiveUdp();

            seartchWifiData = new SeartchWifiData(taskCallback);
            seartchWifiData.startReSend();
        }


    }

}

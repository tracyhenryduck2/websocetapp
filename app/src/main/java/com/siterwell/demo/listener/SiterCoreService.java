package com.siterwell.demo.listener;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import me.siter.sdk.Constants;
import me.siter.sdk.Siter;
import me.siter.sdk.dispatcher.IMessageFilter;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2018/4/25.
 */

public class SiterCoreService extends Service {
    private final static String TAG = "SiterCoreService";
    private IMessageFilter filter;
    private SiterReceiver siterReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveAllMessage();
        if(siterReceiver==null){
            siterReceiver = new SiterReceiver();
            registerReceiver(siterReceiver, new IntentFilter(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
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
            Siter.getSiterClient().receiveMessage(filter, new SiterMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    // 收到消息
                    Intent intent = new Intent(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(Constants.WS_PAYLOAD,msg);
                    sendBroadcast(intent);
                }

                @Override
                public void onTimeout() {
                    // 主动接受不会有这个回调
                    if(SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()!=null){
                        for(TimeOutListener timeOutListener: SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()){
                            timeOutListener.timeout();
                        }
                    }
                }

                @Override
                public void onError(int errorCode, String message) {
                    // 接收错误
                    Intent intent = new Intent(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(Constants.WS_PAYLOAD,message);
                    sendBroadcast(intent);
                }
            });
        }

    }






}

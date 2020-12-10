package com.siterwell.sdk.udp;

import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by jishu0001 on 2016/12/24.
 */
public class SiterwellUtil {
    private static final String TAG = "SiterwellUtil";
    private Context context;
    private static ExecutorService executorService;

    public SiterwellUtil(Context context){
        this.context = context;

        synchronized (context){
            if(executorService == null)
                executorService = Executors.newFixedThreadPool(7);
        }
    }

    public void sendData(final String code){
            UDPSendData udpSendData = new UDPSendData(ConnectB.getInstance().ds,ConnectB.getInstance().targetip,code);
            executorService.execute(udpSendData);

    }
}

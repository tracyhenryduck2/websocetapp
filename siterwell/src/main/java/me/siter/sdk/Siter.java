package me.siter.sdk;

import android.text.TextUtils;

import me.siter.sdk.dispatcher.Dispatcher;
import me.siter.sdk.httpCore.SiterDefautHttpClient;
import me.siter.sdk.httpCore.SiterHttpClient;
import me.siter.sdk.httpCore.IHttpClient;
import me.siter.sdk.monitor.AppStatusMonitor;
import me.siter.sdk.monitor.NetworkMonitor;
import me.siter.sdk.service.ServiceBinder;
import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: SDK 的入口
 */

public class Siter {

    private static String TAG = Siter.class.getSimpleName();

    private static ISiterUser User;
    private static ISIterClient Client;


    private static IHttpClient httpClient;

    static void init() {
        // 绑定服务
        ServiceBinder binder = ServiceBinder.getInstance();
        binder.setContext(SiterSDK.getContext());
        binder.addListener(new ServiceBinder.ConnectServiceListener() {
            @Override
            public void onServiceConnected() {
                LogUtil.i(TAG, "bind service success");
            }

            @Override
            public void onServiceDisconnected() {
                LogUtil.i(TAG, "unbind service success");
            }
        });

        if (!binder.connect()) {
            throw new IllegalStateException("Can not bind the service");
        }

        // 初始化Dispacther
        Dispatcher.getInstance().start();
        // 初始化网络监听
        NetworkMonitor.getInstance().startMonitor();
        // 监听App的状态
        AppStatusMonitor.getInstance().startMonitor();
    }


    public static ISiterUser getSiterUser() {
        if (User == null) {
            synchronized (Siter.class) {
                if (User == null) {
                    User = new SiterUser();
                }
            }
        }
        return User;
    }

    public static ISIterClient getSiterClient() {
        if (Client == null) {
            synchronized (Siter.class) {
                if (Client == null) {
                    Client = new SiterClient();
                    // 连接到云端
                    if (!TextUtils.isEmpty(Siter.getSiterUser().getToken())) {
                        LogUtil.d(TAG, "SiterCloudClient connect to the cloud");
                        Client.connect();
                    } else {
                        LogUtil.d(TAG, "SiterCloudClient can not connect to the cloud because the token is null");
                    }
                }
            }
        }
        return Client;
    }


    public static IHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (Siter.class) {
                if (httpClient == null) {
                    httpClient = new SiterHttpClient(new SiterDefautHttpClient(SiterSDK.getContext()));
                    httpClient.start();
                }
            }
        }
        return httpClient;
    }

    public static Dispatcher getDispatcher() {
        return Dispatcher.getInstance();
    }

    public static ISiterDeviceScanner createSiterLANScanner() {
        return new SiterDeviceScanner();
    }

}

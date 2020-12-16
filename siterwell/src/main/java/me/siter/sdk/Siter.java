package me.siter.sdk;

import android.text.TextUtils;

import me.siter.sdk.dispatcher.Dispatcher;
import me.siter.sdk.http.SiterDefautHttpClient;
import me.siter.sdk.http.SiterHttpClient;
import me.siter.sdk.http.IHttpClient;
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

    private static ISiterUser hekrUser;
    private static ISIterClient hekrClient;


    private static IHttpClient httpClient;

    static void init() {
        // 绑定服务
        ServiceBinder binder = ServiceBinder.getInstance();
        binder.setContext(SiterSDK.getContext());
        binder.addListener(new ServiceBinder.ConnectServiceListener() {
            @Override
            public void onServiceConnected() {
                LogUtil.i(TAG, "Hekr bind service success");
            }

            @Override
            public void onServiceDisconnected() {
                LogUtil.i(TAG, "Hekr unbind service success");
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
        if (hekrUser == null) {
            synchronized (Siter.class) {
                if (hekrUser == null) {
                    hekrUser = new SiterUser();
                }
            }
        }
        return hekrUser;
    }

    public static ISIterClient getSiterClient() {
        if (hekrClient == null) {
            synchronized (Siter.class) {
                if (hekrClient == null) {
                    hekrClient = new SiterClient();
                    // 连接到云端
                    if (!TextUtils.isEmpty(Siter.getSiterUser().getToken())) {
                        LogUtil.d(TAG, "HekrCloudClient connect to the cloud");
                        hekrClient.connect();
                    } else {
                        LogUtil.d(TAG, "HekrCloudClient can not connect to the cloud because the token is null");
                    }
                }
            }
        }
        return hekrClient;
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

    public static ISiterDeviceScanner createHekrLANScanner() {
        return new SiterDeviceScanner();
    }

    private static Class getHekrConfigClass() throws ClassNotFoundException {
        return Class.forName("me.hekr.hekrconfig.HekrConfig");
    }

    private static Class getHekrWebClass() throws ClassNotFoundException {
        return Class.forName("me.hekr.hekrweb.HekrWeb");
    }
}

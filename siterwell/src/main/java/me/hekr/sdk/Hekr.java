package me.hekr.sdk;

import android.text.TextUtils;

import me.hekr.sdk.dispatcher.Dispatcher;
import me.hekr.sdk.http.HekrDefautHttpClient;
import me.hekr.sdk.http.HekrHttpClient;
import me.hekr.sdk.http.IHttpClient;
import me.hekr.sdk.monitor.AppStatusMonitor;
import me.hekr.sdk.monitor.NetworkMonitor;
import me.hekr.sdk.service.ServiceBinder;
import me.hekr.sdk.utils.LogUtil;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: SDK 的入口
 */

public class Hekr {

    private static String TAG = Hekr.class.getSimpleName();

    private static IHekrUser hekrUser;
    private static IHekrClient hekrClient;
    private static IHekrConfig hekrConfig;
    private static IHekrLAN hekrLAN;
    private static IHekrLANControl hekrLANControl;
    private static IHekrWeb hekrWeb;


    private static IHttpClient httpClient;

    static void init() {
        // 绑定服务
        ServiceBinder binder = ServiceBinder.getInstance();
        binder.setContext(HekrSDK.getContext());
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


    public static IHekrUser getHekrUser() {
        if (hekrUser == null) {
            synchronized (Hekr.class) {
                if (hekrUser == null) {
                    hekrUser = new HekrUser();
                }
            }
        }
        return hekrUser;
    }

    public static IHekrClient getHekrClient() {
        if (hekrClient == null) {
            synchronized (Hekr.class) {
                if (hekrClient == null) {
                    hekrClient = new HekrClient();
                    // 连接到云端
                    if (!TextUtils.isEmpty(Hekr.getHekrUser().getToken())) {
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

    public static IHekrLAN getHekrLAN() {
        if (hekrLAN == null) {
            synchronized (Hekr.class) {
                if (hekrLAN == null) {
                    hekrLAN = new HekrLAN();
                }
            }
        }
        return hekrLAN;
    }

    public static IHekrLANControl getHekrLANControl() {
        if (hekrLANControl == null) {
            synchronized (Hekr.class) {
                if (hekrLANControl == null) {
                    hekrLANControl = new HekrLANControl();
                }
            }
        }
        return hekrLANControl;
    }

    public static IHekrConfig getHekrConfig() {
        // 初始化HekrConfig
        // TODO: 2017/3/24 由于HekrConfig是另外一个Jar包，所以必须先检查是否存在
        if (hekrConfig == null) {
            synchronized (Hekr.class) {
                if (hekrConfig == null) {
                    try {
                        Class clz = getHekrConfigClass();
                        hekrConfig = (IHekrConfig) clz.newInstance();
                        hekrConfig.setHttp(Hekr.getHttpClient());
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("Please add the HekrConfig library");
                    } catch (InstantiationException e) {
                        throw new IllegalStateException("Fail to instantiate HekrConfig");
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Fail to access HekrConfig");
                    }
                }
            }
        }
        return hekrConfig;
    }

    public static IHekrWeb getHekrWeb() {
        if (hekrWeb == null) {
            synchronized (Hekr.class) {
                if (hekrWeb == null) {
                    try {
                        Class clz = getHekrWebClass();
                        hekrWeb = (IHekrWeb) clz.newInstance();
                    } catch (ClassNotFoundException e) {
                        throw new IllegalStateException("Please add the HekrWeb library");
                    } catch (InstantiationException e) {
                        throw new IllegalStateException("Fail to instantiate HekrWeb");
                    } catch (IllegalAccessException e) {
                        throw new IllegalStateException("Fail to access HekrWeb");
                    }
                }
            }
        }
        return hekrWeb;
    }

    public static IHttpClient getHttpClient() {
        if (httpClient == null) {
            synchronized (Hekr.class) {
                if (httpClient == null) {
                    httpClient = new HekrHttpClient(new HekrDefautHttpClient(HekrSDK.getContext()));
                    httpClient.start();
                }
            }
        }
        return httpClient;
    }

    public static Dispatcher getDispatcher() {
        return Dispatcher.getInstance();
    }

    public static IHekrDeviceScanner createHekrLANScanner() {
        return new HekrDeviceScanner();
    }

    private static Class getHekrConfigClass() throws ClassNotFoundException {
        return Class.forName("me.hekr.hekrconfig.HekrConfig");
    }

    private static Class getHekrWebClass() throws ClassNotFoundException {
        return Class.forName("me.hekr.hekrweb.HekrWeb");
    }
}

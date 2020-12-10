package me.hekr.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.hekr.sdk.utils.LogUtil;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 服务,用于保存和创建连接池
 */

public class HekrConnectionService extends Service {

    private static final String TAG = HekrConnectionService.class.getSimpleName();

    private ConnectionBinder mConnectionBinder;
    private Map<String, IAsyncConn> mCloudConns = new ConcurrentHashMap<>();
    private Map<String, IAsyncConn> mDeviceConns = new ConcurrentHashMap<>();
    private ConnFactory mConnFactory;

    @Override
    public IBinder onBind(Intent intent) {
        return mConnectionBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConnectionBinder = new ConnectionBinder(this);
        mConnFactory = new ConnFactory();
        CommonUdpConn.getCommon().start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUdpConn.getCommon().stop();
    }

    public String createCloudConn(ConnOptions options) {
        String handler;
        if (!TextUtils.isEmpty(options.getPrefix())) {
            handler = options.getPrefix() + "/" + options.getIpOrUrl();
        } else {
            handler = options.getIpOrUrl();
        }
        if (!mCloudConns.containsKey(handler)) {
            IAsyncConn cloudConn = mConnFactory.getConn(options, handler);
            mCloudConns.put(handler, cloudConn);
        } else {
            mCloudConns.get(handler).reset(options);
        }
        return handler;
    }

    public String createDeviceConn(ConnOptions options) {
        String handler;
        if (!TextUtils.isEmpty(options.getPrefix())) {
            handler = options.getPrefix() + "/" + options.getIpOrUrl() + "/" + String.valueOf(options.getPort());
        } else {
            handler = options.getIpOrUrl() + "/" + String.valueOf(options.getPort());
        }
        if (!mDeviceConns.containsKey(handler)) {
            IAsyncConn deviceConn = mConnFactory.getConn(options, handler);
            mDeviceConns.put(handler, deviceConn);
        } else {
            mDeviceConns.get(handler).reset(options);
        }
        return handler;
    }

    private IAsyncConn getCloudConn(String handler) {
        IAsyncConn conn = mCloudConns.get(handler);
        if (conn == null) {
            throw new IllegalArgumentException("Can not find the specified conn, please check the handler");
        }
        return conn;
    }

    private IAsyncConn getDeviceConn(String handler) {
        IAsyncConn conn = mDeviceConns.get(handler);
        if (conn == null) {
            throw new IllegalArgumentException("Can not find the specified conn, please check the handler");
        }
        return conn;
    }

    public void connectCloud(String handler) {
        IAsyncConn conn = getCloudConn(handler);
        conn.start();
    }

    public void connectDevice(String handler) {
        IAsyncConn conn = getDeviceConn(handler);
        conn.start();
    }

    public void disconnectCloud(String handler) {
        IAsyncConn conn = getCloudConn(handler);
        conn.stop();
    }

    public void disconnectDevice(String handler) {
        IAsyncConn conn = getDeviceConn(handler);
        conn.stop();
    }

    public void destroyCloudConn(String handler) {
        disconnectCloud(handler);
        mCloudConns.remove(handler);
    }

    public void destroyDeviceConn(String handler) {
        disconnectDevice(handler);
        mDeviceConns.remove(handler);
    }

    public void sendToCloud(String handler, String message) {
        IAsyncConn conn = getCloudConn(handler);
        conn.send(message);
    }

    public void sendToDevice(String handler, String message) {
        IAsyncConn conn = getDeviceConn(handler);
        conn.send(message);
    }

    public boolean cloudConnExist(String handler) {
        return handler != null && mCloudConns.get(handler) != null;
    }

    public boolean deviceConnExist(String handler) {
        return handler != null && mDeviceConns.get(handler) != null;
    }

    public void enableMulticast(boolean enable) {
        if (enable) {
            if (!MulticastUdpConn.getMulticast().isActive()) {
                MulticastUdpConn.getMulticast().start(getApplicationContext());
            }
        } else {
            MulticastUdpConn.getMulticast().stop();
        }
    }

    public void enableBroadcast(boolean enable) {
        if (enable) {
            if (!BroadcastUdpConn.getBroadcast().isActive()) {
                BroadcastUdpConn.getBroadcast().start();
            }
        } else {
            BroadcastUdpConn.getBroadcast().stop();
        }
    }

    public void sendCommonUdp(String message, String ip, int port) {
        if (CommonUdpConn.getCommon().isActive()) {
            CommonUdpConn.getCommon().send(message, ip, port);
        } else {
            LogUtil.e(TAG, "Common UDP is not available");
        }
    }

    public int getCommonUdpPort() {
        return CommonUdpConn.getCommon().getPort();
    }
}

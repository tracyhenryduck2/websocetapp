package me.siter.sdk.monitor;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Build;

import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.SiterSDK;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 监听网络状态的变化
 */

public class NetworkMonitor {

    private static final String TAG = NetworkMonitor.class.getSimpleName();

    private CopyOnWriteArrayList<NetObservable> mObservables;

    private NetworkReceiver mReceiver;
    private boolean isConnected = false;

    private static NetworkMonitor instance;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    public static NetworkMonitor getInstance() {
        if (instance == null) {
            synchronized (NetworkMonitor.class) {
                if (instance == null) {
                    instance = new NetworkMonitor();
                }
            }
        }
        return instance;
    }

    private NetworkMonitor() {
        mReceiver = new NetworkReceiver();
        mObservables = new CopyOnWriteArrayList<>();
    }

    public void startMonitor() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) HekrSDK.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            mNetworkCallback = new NetCallback();
//            NetworkRequest request = new NetworkRequest.Builder()
//                    .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
//                    .addCapability(NetworkCapabilities.TRANSPORT_WIFI)
//                    .build();
//            connectivityManager.registerNetworkCallback(request, mNetworkCallback);
//        } else {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        SiterSDK.getContext().registerReceiver(mReceiver, filter);
//        }
    }

    public void stopMonitor() {
        SiterSDK.getContext().unregisterReceiver(mReceiver);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) SiterSDK.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private class NetCallback extends ConnectivityManager.NetworkCallback {

        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            LogUtil.i(TAG, "Callback network connected");
            if (!isConnected) {
                notifyConnected();
                isConnected = true;
            }
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            LogUtil.i(TAG, "Callback network disconnected");
            if (isConnected) {
                notifyDisconnected();
                isConnected = false;
            }
        }
    }

    private class NetworkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 监听网络状态的变化
            if (NetworkUtil.isConnected(context)) {
                LogUtil.i(TAG, "Receive network connected");
                if (!isConnected) {
                    notifyConnected();
                    isConnected = true;
                }
            } else {
                LogUtil.i(TAG, "Receive network disconnected");
                if (isConnected) {
                    notifyDisconnected();
                    isConnected = false;
                }
            }
        }
    }

    private void notifyConnected() {
        for (NetObservable connectable : mObservables) {
            connectable.onNetOn();
        }
    }

    private void notifyDisconnected() {
        for (NetObservable connectable : mObservables) {
            connectable.onNetOff();
        }
    }

    public void add(NetObservable observable) {
        if (!mObservables.contains(observable)) {
            mObservables.add(observable);
        }
    }

    public void remove(NetObservable observable) {
        mObservables.remove(observable);
    }
}

package me.siter.sdk.service;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接服务(Android Service)的工具类
 */

public class ServiceBinder {

    private static final String TAG = ServiceBinder.class.getCanonicalName();

    private HekrConnectionService mService;
    private CopyOnWriteArrayList<ConnectServiceListener> mListeners;
    private Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static ServiceBinder instance; // 目前无法避免单例对象持有Context的引用，可能有其他办法

    public static ServiceBinder getInstance() {
        if (instance == null) {
            synchronized (ServiceBinder.class) {
                if (instance == null) {
                    instance = new ServiceBinder();
                }
            }
        }
        return instance;
    }

    private ServiceBinder() {
        mListeners = new CopyOnWriteArrayList<>();
    }

    public HekrConnectionService getService() {
        return mService;
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d(TAG, "Connection service disconnected");
            LogUtil.d(TAG, "Notify service listeners");
            for (ConnectServiceListener listener : mListeners) {
                listener.onServiceDisconnected();
            }
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d(TAG, "Connection service connected");
            if (mService == null) {
                if (service instanceof ConnectionBinder) {
                    ConnectionBinder binder = (ConnectionBinder) service;
                    mService = binder.getService();
                    if (mService == null) {
                        throw new IllegalStateException("Service is null");
                    }
                } else {
                    throw new IllegalStateException("IBinder is not an instance of ConnectionBinder");
                }
            }
            LogUtil.d(TAG, "Notify service listeners");
            for (ConnectServiceListener listener : mListeners) {
                listener.onServiceConnected();
            }
        }
    };

    public boolean connect() {
        return bindService(mContext);
    }

    public void disconnect() {
        if (mContext != null) {
            unbindService(mContext);
            mContext = null;
        }
        for (ConnectServiceListener listener : mListeners) {
            listener.onServiceDisconnected();
        }
    }

    private boolean bindService(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, HekrConnectionService.class);
        return context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService(Context context) {
        try {
            context.unbindService(mServiceConnection);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void addListener(ConnectServiceListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void removeListener(ConnectServiceListener listener) {
        mListeners.remove(listener);
    }

    public void setContext(Context context) {
        this.mContext = context;
    }

    public interface ConnectServiceListener {

        void onServiceConnected();

        void onServiceDisconnected();
    }
}

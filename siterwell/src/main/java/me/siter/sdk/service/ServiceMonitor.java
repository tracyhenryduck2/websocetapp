package me.siter.sdk.service;

import android.os.Handler;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.SiterSDK;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 由于我们把保存连接的Service和Logic layer完全分开，所有Service想要传达的信息都会通过这个类来
 * 实现，目前有收到的消息和连接状态的监听
 */

public class ServiceMonitor {

    private static ServiceMonitor instance;

    private CopyOnWriteArrayList<IMsgObserver> mMsgObservers;
    private ConcurrentHashMap<String, IConnObserver> mConnObservers;
    private Handler mHandler;


    public static ServiceMonitor getInstance() {
        if (instance == null) {
            synchronized (ServiceMonitor.class) {
                if (instance == null) {
                    instance = new ServiceMonitor();
                }
            }
        }
        return instance;
    }

    private ServiceMonitor() {
        mMsgObservers = new CopyOnWriteArrayList<>();
        mConnObservers = new ConcurrentHashMap<>();
        mHandler = new Handler(SiterSDK.getContext().getMainLooper());
    }

    public void registerMsgObserver(IMsgObserver observer) {
        mMsgObservers.add(observer);
    }

    public void unrighsterMsgObserver(IMsgObserver observer) {
        mMsgObservers.remove(observer);
    }


    public void registerConnObserver(String handler, IConnObserver observer) {
        if (handler != null) {
            mConnObservers.put(handler, observer);
        }
    }

    public void unregisterConnObserver(String handler) {
        if (handler != null) {
            mConnObservers.remove(handler);
        }
    }

    void notifyConnChanged(final String handler, final ConnStatusType status) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                IConnObserver observer = mConnObservers.get(handler);
                if (observer != null) {
                    observer.onConnChanged(status);
                }
            }
        });
    }

    void notifyConnError(final String handler, final ConnStatusType status, final Throwable throwable) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                IConnObserver observer = mConnObservers.get(handler);
                if (observer != null) {
                    observer.onError(status, throwable);
                }
            }
        });
    }

    void notifyMessageArrived(final String message, final String from) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Iterator<IMsgObserver> iterator = mMsgObservers.iterator();
                while (iterator.hasNext()) {
                    IMsgObserver observer = iterator.next();
                    observer.onReceived(message, from);
                }
            }
        });
    }
}

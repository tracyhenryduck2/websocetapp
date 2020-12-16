package me.siter.sdk;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ConcurrentHashMap;

import me.siter.sdk.monitor.NetType;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 先集中管理重连
 */

class ConnectManager {

    private static final String TAG = ConnectManager.class.getSimpleName();

    private static final long MAX_DELAY_TIME = 60 * 1000;
    private static final long MIN_DELAY_TIME = 2 * 1000;
    private static final float EXP_GROWTH_WIFI = 2;
    private static final float EXP_GROWTH_NOT_WIFI = 2;

    private final int HANDLER_CHECK_NETWORK = 1;
    private final int HANDLER_CONNECT = 2;

    private Handler mHandler;
    private ConcurrentHashMap<String, Object> mTags;
    private ConcurrentHashMap<String, Connectable> mReconnectables;

    private static ConnectManager instance;

    static ConnectManager getInstance() {
        if (instance == null) {
            synchronized (ConnectManager.class) {
                if (instance == null) {
                    instance = new ConnectManager();
                }
            }
        }
        return instance;
    }

    private ConnectManager() {
        mReconnectables = new ConcurrentHashMap<>();
        mTags = new ConcurrentHashMap<>();
        mHandler = new ReconnHandler(SiterSDK.getContext().getMainLooper());
    }

    void start(String tag) {
        start(tag, 0);
    }

    void start(String tag, long time) {
        LogUtil.d(TAG, "Connectable: " + tag + ", start connecting, delayed time is: " + time + "ms");
        if (!mReconnectables.containsKey(tag)) {
            LogUtil.d(TAG, "Connectable: " + tag + " is not in the list");
            return;
        }
        Object object = mTags.get(tag);
        if(object!=null){
            mHandler.removeCallbacksAndMessages(object);
        }
        beginReconnDelayed(tag, time);
    }

    void pause(String tag) {
        // 移除所有的重连
        Object object = mTags.get(tag);
        if(object!=null){
            mHandler.removeCallbacksAndMessages(object);
        }
    }

    private void sendMessageToHandler(int what, String tag, long delay) {
        Message message = mHandler.obtainMessage();
        message.obj = mTags.get(tag);
        Bundle bundle = new Bundle();
        bundle.putLong("delay", delay);
        bundle.putString("tag", tag);
        message.setData(bundle);
        message.what = what;
        mHandler.sendMessageDelayed(message, delay);
    }

    @SuppressLint("HandlerLeak")
    private class ReconnHandler extends Handler {

        ReconnHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            handle(msg);
        }
    }

    private synchronized void handle(Message msg) {
        Bundle bundle = msg.getData();
        String tag = bundle.getString("tag");
        long delay = bundle.getLong("delay");
        switch (msg.what) {
            case HANDLER_CONNECT:
                Connectable connectable = mReconnectables.get(tag);
                /*
                 * 如果满足如下条件，那么就不继续重连
                 * 1.Connectable 被销毁
                 * 2.已经连接上云端和设备
                 */
                if (connectable == null || connectable.isOnline()) {
                    if (connectable == null) {
                        LogUtil.d(TAG, "Connectable: " + tag + " is null");
                    }
                    if (connectable != null && connectable.isOnline()) {
                        LogUtil.d(TAG, "Connectable: " + tag + " is online");
                    }
                    return;
                }
                /*
                 * 如果满足如下条件，需要延时判断
                 * 1.手机没有可用的连接
                 */
                if (!NetworkUtil.isConnected(SiterSDK.getContext())) {
                    LogUtil.d(TAG, "Connectable: " + tag + ", Network is off, new delay:" + MIN_DELAY_TIME + "ms");
                    sendMessageToHandler(HANDLER_CHECK_NETWORK, tag, MIN_DELAY_TIME);
                    return;
                }
                // 获取连接类型，是和设备连接还是和云端连接
                ConnType type = connectable.getConnType();
                if (type == ConnType.CONN_CLOUD) {
                    // 如果正在重连，那么就跳过
                    if (!connectable.isConnecting()) {
                        // 无论在什么网络环境下都进行重连
                        LogUtil.d(TAG, "Connectable: " + tag + ", let the connectable try connect");
                        connectable.communicate();
                        // 计算延时时间
                        long newDelay = getNextDelay(delay);
                        LogUtil.d(TAG, "Connectable: " + tag + ", check the connection status after " + newDelay + "ms");
                        // 然后重新在未来某一时间重连
                        sendMessageToHandler(HANDLER_CONNECT, tag, newDelay);
                    } else {
                        if (delay == 0) {
                            delay = MIN_DELAY_TIME;
                        }
                        LogUtil.d(TAG, "Connectable: " + tag + " is connecting, new delay: " + delay + "ms");
                        sendMessageToHandler(HANDLER_CONNECT, tag, delay);
                    }
                } else {
                    // 如果不是在wifi环境下还是继续重连(因为如下的判断不准确)
                    if (NetworkUtil.getConnectedType(SiterSDK.getContext()) == NetType.WIFI) {
                        LogUtil.d(TAG, "Connectable: " + tag + ", Device connection is on wifi status");
                    } else {
                        LogUtil.d(TAG, "Connectable: " + tag + ", Device connection is not on wifi status");
                    }
                    if (!connectable.isConnecting()) {
                        LogUtil.d(TAG, "Connectable: " + tag + ", let the connectable try connect");
                        connectable.communicate();
                        long newDelay = getNextDelay(delay);
                        // 然后重新在未来某一时间重连
                        LogUtil.d(TAG, "Connectable: " + tag + ", check the connection status after " + newDelay + "ms");
                        sendMessageToHandler(HANDLER_CONNECT, tag, newDelay);
                    } else {
                        if (delay == 0) {
                            delay = MIN_DELAY_TIME;
                        }
                        LogUtil.d(TAG, "Connectable: " + tag + " is connecting, new delay: " + delay + "ms");
                        sendMessageToHandler(HANDLER_CONNECT, tag, delay);
                    }
                }
                break;
            case HANDLER_CHECK_NETWORK:
                if (!NetworkUtil.isConnected(SiterSDK.getContext())) {
                    LogUtil.d(TAG, "Connectable: " + tag + ", After 2s' waiting, Network is off still, then do nothing");
                } else {
                    LogUtil.d(TAG, "Connectable: " + tag + ", After 2s' waiting, Network is on, then reconnect");
                    sendMessageToHandler(HANDLER_CONNECT, tag, 0);
                }
                break;
            default:
                break;
        }
    }

    void add(final Connectable connectable, final String tag, final ConnType connType) {
        if (!mReconnectables.containsKey(tag)) {
            mReconnectables.put(tag, connectable);
            NetType type = NetworkUtil.getConnectedType(SiterSDK.getContext());
            if ((connType == ConnType.CONN_CLOUD && (type == NetType.WIFI || type == NetType.MOBILE))
                    || (connType == ConnType.CONN_DEVICE && type == NetType.WIFI)) {
                if (!mTags.containsKey(tag)) {
                    mTags.put(tag, new Object());
                    LogUtil.d(TAG, "Connectable: " + tag + " is added");
                }
                beginReconnDelayed(tag, 0);
            }
        }
    }

    void remove(final String tag) {
        Object object = mTags.get(tag);
        if(object!=null){
            mHandler.removeCallbacksAndMessages(object);
        }
        mReconnectables.remove(tag);
        if (mTags.containsKey(tag)) {
            mTags.remove(tag);
            LogUtil.d(TAG, "Connectable: " + tag + " is removed");
        }
    }

    private void beginReconnDelayed(String tag, long time) {
        sendMessageToHandler(HANDLER_CONNECT, tag, time);
    }

    private long getNextDelay(long delay) {
        if (delay == 0) {
            return MIN_DELAY_TIME;
        }
        long next;
        if (NetworkUtil.getConnectedType(SiterSDK.getContext()) == NetType.WIFI) {
            next = (long) (delay * EXP_GROWTH_WIFI);
        } else {
            next = (long) (delay * EXP_GROWTH_NOT_WIFI);
        }
        if (next > MAX_DELAY_TIME) {
            next = MAX_DELAY_TIME;
        }
        return next;
    }
}

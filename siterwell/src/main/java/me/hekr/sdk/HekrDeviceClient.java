package me.hekr.sdk;

import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import me.hekr.sdk.connection.ConnectionStatusListener;
import me.hekr.sdk.connection.DeviceConnection;
import me.hekr.sdk.connection.IConnection;
import me.hekr.sdk.dispatcher.MessageFilter;
import me.hekr.sdk.inter.HekrLANDeviceListener;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sdk.monitor.NetObservable;
import me.hekr.sdk.monitor.NetworkMonitor;
import me.hekr.sdk.service.ConnOptions;
import me.hekr.sdk.utils.AndroidErrorMap;
import me.hekr.sdk.utils.AppIdUtil;
import me.hekr.sdk.utils.JSONObjectUtil;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.MessageCounter;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by hucn on 2017/3/21.
 * Author: hucn
 * Description: 设备连接的Client
 */

class HekrDeviceClient implements IHekrDeviceClient, NetObservable, Connectable {

    private static final String TAG = HekrDeviceClient.class.getSimpleName();
    // 心跳包发送间隔时间
    private static final int HEART_BEAT_RATE = 60 * 1000;

    private IConnection mDeviceConnection;
    private String mTag;
    private String mCtrlKey;
    private String mAppId;
    private boolean isOnline;
    private boolean isConnecting;
    private volatile boolean isNetOffBefore;
    private Timer mBeatTimer;
    private NetworkMonitor mNetMonitor;
    private ConnectManager mConnectManager;
    private CopyOnWriteArrayList<HekrLANDeviceListener> mDeviceListeners;
    private ConnOptions mOptions;
    private Handler mHandler;

    private volatile int mCurrentBeatMessageId = -1;
    private volatile int mCurrentAuthMessageId = -1;

    HekrDeviceClient(String tag, String ctrlKey) {
        if (TextUtils.isEmpty(tag)) {
            throw new IllegalArgumentException("DevTid can not be empty");
        }
        LogUtil.d(TAG, "Create new HekrDeviceClient: " + tag);
        this.mTag = tag;
        this.mCtrlKey = ctrlKey;
        this.mAppId = AppIdUtil.getAppId(HekrSDK.getContext());
        this.mBeatTimer = new Timer();
        this.mNetMonitor = NetworkMonitor.getInstance();
        this.mConnectManager = ConnectManager.getInstance();
        this.mDeviceListeners = new CopyOnWriteArrayList<>();
        this.mHandler = new Handler(HekrSDK.getContext().getMainLooper());
        this.isNetOffBefore = !NetworkUtil.isWifiConnected(HekrSDK.getContext());
    }

    @Override
    public synchronized void sendMessage(final JSONObject message, final HekrMsgCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tryToSend(message, new HekrMsgCallbackWrapper(callback));
            }
        });

    }

    @Override
    public synchronized void connect(final String ip, final int port) {
        disconnect();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mOptions = new ConnOptions(mTag, ConnOptions.TYPE_CONN_UDP_NORMAL, ip, port);
                LogUtil.d(TAG, mTag + " connect, options is: " + mOptions);
                mDeviceConnection = new DeviceConnection();
                mDeviceConnection.bind(mOptions);
                mDeviceConnection.setConnectionStatusListener(new StatusListener());
                mNetMonitor.add(HekrDeviceClient.this);
                mConnectManager.add(HekrDeviceClient.this, getTag(), getConnType());
            }
        });
    }

    @Override
    public synchronized String getIP() {
        if (mOptions != null) {
            return mOptions.getIpOrUrl();
        }
        return null;
    }

    @Override
    public synchronized int getPort() {
        if (mOptions != null) {
            return mOptions.getPort();
        }
        return 0;
    }

    @Override
    public synchronized void disconnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                LogUtil.d(TAG, mTag + ", disconnect: " + mOptions);
                isOnline = false;
                isConnecting = false;
                mNetMonitor.remove(HekrDeviceClient.this);
                mConnectManager.remove(getTag());
                if (mDeviceConnection != null) {
                    if (!mDeviceConnection.isClosed()) {
                        mDeviceConnection.close();
                    }
                    mDeviceConnection = null;
                }
                for (HekrLANDeviceListener listener : mDeviceListeners) {
                    listener.onDisconnected();
                }

                resetMessageInfo();

                endBeat();
            }
        });
    }

    @Override
    public synchronized boolean isOnline() {
        return isOnline;
    }

    @Override
    public synchronized void addLANDeviceListener(HekrLANDeviceListener listener) {
        mDeviceListeners.add(listener);
    }

    @Override
    public synchronized void removeLANDeviceListener(HekrLANDeviceListener listener) {
        mDeviceListeners.remove(listener);
    }

    @Override
    public synchronized boolean isConnecting() {
        return isConnecting;
    }

    @Override
    public synchronized ConnType getConnType() {
        return ConnType.CONN_DEVICE;
    }

    @Override
    public synchronized void communicate() {
        if (mDeviceConnection != null) {
            if (mDeviceConnection.isConnected()) {
                tryToAuth();
                return;
            }
            isConnecting = true;
            mDeviceConnection.connect();
        }
    }

    @Override
    public synchronized String getTag() {
        return mTag;
    }

    private void tryToSend(JSONObject message, HekrMsgCallback callback) {
        if (!isOnline || mDeviceConnection == null || !mDeviceConnection.isConnected()) {
            LogUtil.e(TAG, "No connection found when send message");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_NO_CONNECTION,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_NO_CONNECTION));
            isOnline = false;
            isConnecting = false;
            if (mDeviceConnection != null) {
                mDeviceConnection.disconnect();
                mConnectManager.start(getTag());
            }
            return;
        }
        if (message == null) {
            LogUtil.e(TAG, "Message is null");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_MESSAGE_NULL,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_MESSAGE_NULL));
            return;
        }
        if (TextUtils.isEmpty(mAppId)) {
            LogUtil.e(TAG, "AppId is null");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_APP_ID_NULL,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_APP_ID_NULL));
            return;
        }
        if (!message.has("params")) {
            LogUtil.e(TAG, "No params found in the message");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_NO_PARAM,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_NO_PARAM));

            return;
        }
        try {
            int count;
            if (!message.has("msgId")) {
                count = MessageCounter.increaseCount();
                message.put("msgId", count);
            } else {
                count = message.getInt("msgId");
            }
            message.getJSONObject("params").put("appTid", mAppId);
            //协议过滤器
            JSONObject filterObject = new JSONObject();
            filterObject.put("msgId", count);
            filterObject.put("action", TextUtils.concat(message.getString("action"), "Resp"));
            // TODO: 2017/7/14 暂且去掉这个字段的过滤
//            if (!TextUtils.isEmpty(mTag)) {
//                JSONObject params = new JSONObject();
//                params.put("devTid", mTag);
//                filterObject.put("params", params);
//            }
            MessageFilter filter = new MessageFilter(filterObject);
            MessageRequest request = new MessageRequest(addSpace(message.toString()), filter, callback);
            if (isOnline && mDeviceConnection != null && mDeviceConnection.isConnected()) {
                mDeviceConnection.send(request);
            } else {
                LogUtil.d(TAG, "The connection is not available");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void tryToBeat() {
        if (isOnline && mDeviceConnection != null && mDeviceConnection.isConnected()) {
            try {
                JSONObject message = new JSONObject();

                final int count = MessageCounter.increaseCount();

                message.put("msgId", count);
                message.put("action", "heartbeat");
                JSONObject rule = new JSONObject();
                rule.putOpt("action", "heartbeatResp");
                rule.putOpt("msgId", count);

                mCurrentBeatMessageId = count;

                LogUtil.e(TAG, "Beat...");
                MessageFilter filter = new MessageFilter(rule);
                final MessageRequest request = new MessageRequest(addSpace(message.toString()), filter, new HekrMsgCallback() {
                    @Override
                    public void onReceived(String msg) {
                        onBeatReceived(count);
                    }

                    @Override
                    public void onTimeout() {
                        onBeatTimeout(count);
                    }

                    @Override
                    public void onError(int errorCode, String message) {
                        onBeatError(errorCode, message, count);
                    }
                });
                if (mDeviceConnection != null && mDeviceConnection.isConnected()) {
                    mDeviceConnection.send(request);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void onBeatReceived(int count) {
        // TODO: 2017/3/29 处理心跳的逻辑
        if (count == mCurrentBeatMessageId) {
            LogUtil.i(TAG, "Device beat success");
        } else {
            LogUtil.d(TAG, "Beat message Id is not matched");
        }
    }

    private synchronized void onBeatTimeout(int count) {
        if (count == mCurrentBeatMessageId) {
            LogUtil.e(TAG, "Device beat timeout");
            isOnline = false;
            isConnecting = false;
            resetMessageInfo();
            if (mDeviceConnection != null) {
                mDeviceConnection.disconnect();
                for (HekrLANDeviceListener listener : mDeviceListeners) {
                    listener.onDisconnected();
                }
                mConnectManager.start(getTag());
            }
        } else {
            LogUtil.d(TAG, "Beat message Id is not matched");
        }
    }

    private synchronized void onBeatError(int errorCode, String message, int count) {
        if (count == mCurrentBeatMessageId) {
            isConnecting = false;
            if (2000119 == errorCode) {
                LogUtil.e(TAG, "Unauthorized user: " + message);
                retryAuth();
            } else {
                LogUtil.e(TAG, "Get error code from device, code: " + errorCode + ", message: " + message);
            }
        } else {
            LogUtil.d(TAG, "Beat message Id is not matched");
        }
    }

    private synchronized void tryToAuth() {
        LogUtil.d(TAG, mTag + ", try to authorize");
        if (mDeviceConnection == null || !mDeviceConnection.isConnected()) {
            return;
        }
        try {
            JSONObject message = new JSONObject();
            JSONObject params = new JSONObject();

            final int count = MessageCounter.increaseCount();

            params.put("devTid", mTag);
            params.put("ctrlKey", mCtrlKey);

            message.put("msgId", count);
            message.put("action", "appDevAuth");
            message.put("params", params);

            JSONObject rule = new JSONObject();
            rule.putOpt("action", "appDevAuthResp");
            rule.putOpt("msgId", count);

            mCurrentAuthMessageId = count;

            LogUtil.d(TAG, "Current authorize message id is: " + count);

            MessageFilter filter = new MessageFilter(rule);
            MessageRequest request = new MessageRequest(addSpace(message.toString()), filter, new HekrMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    onAuthReceived(msg, count);
                }

                @Override
                public void onTimeout() {
                    onAuthTimeout(count);
                }

                @Override
                public void onError(int errorCode, String message) {
                    onAuthError(errorCode, message, count);
                }
            });
            if (mDeviceConnection != null && mDeviceConnection.isConnected()) {
                mDeviceConnection.send(request);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void onAuthReceived(String msg, int count) {
        // TODO: 2017/3/29 处理心跳的逻辑
        if (count == mCurrentAuthMessageId) {
            LogUtil.d(TAG, "Device auth message received");
            isConnecting = false;
            checkAuth(msg);
        } else {
            LogUtil.d(TAG, "Auth message Id is not matched: " + count + "=" + mCurrentAuthMessageId);
        }
    }

    private synchronized void onAuthTimeout(int count) {
        if (count == mCurrentAuthMessageId) {
            LogUtil.e(TAG, "Device auth timeout");
            isConnecting = false;
            for (HekrLANDeviceListener listener : mDeviceListeners) {
                listener.onError(AndroidErrorMap.ERROR_CLIENT_LAN_AUTH_TIMEOUT
                        , AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_CLIENT_LAN_AUTH_TIMEOUT));
            }
        } else {
            LogUtil.d(TAG, "Auth message Id is not matched: " + count + "=" + mCurrentAuthMessageId);
        }
    }

    private synchronized void onAuthError(int errorCode, String message, int count) {
        if (count == mCurrentAuthMessageId) {
            if (2000122 == errorCode) {
                LogUtil.e(TAG, "Max limited control user: " + message);
                for (HekrLANDeviceListener listener : mDeviceListeners) {
                    listener.onError(errorCode, message);
                }
            } else {
                LogUtil.e(TAG, "Get error code from device, code: " + errorCode + ", message: " + message);
                for (HekrLANDeviceListener listener : mDeviceListeners) {
                    listener.onError(errorCode, message);
                }
            }
            isConnecting = false;
        } else {
            LogUtil.d(TAG, "Auth message Id is not matched: " + count + "=" + mCurrentAuthMessageId);
        }
    }

    private synchronized void retryAuth() {
        LogUtil.d(TAG, mTag + ", reauthorize");
        isOnline = false;
        resetMessageInfo();
        mConnectManager.start(getTag());
    }

    private synchronized void checkAuth(String msg) {
        JSONObject json = JSONObjectUtil.getJSONObject(msg);
        if (json != null) {
            int code = json.optInt("code");
            if (200 == code) {
                LogUtil.d(TAG, "Success to login the device");
                isOnline = true;
                for (HekrLANDeviceListener listener : mDeviceListeners) {
                    listener.onConnected();
                }
                startBeat();
            }
        } else {
            LogUtil.e(TAG, "Get incorrect format message from cloud: " + msg);
        }
    }

    private String addSpace(String string) {
        return string + "\n";
    }

    private void startBeat() {
        if (mBeatTimer != null) {
            mBeatTimer.cancel();
            mBeatTimer.purge();
            mBeatTimer = null;
        }
        mBeatTimer = new Timer();
        mBeatTimer.schedule(new BeatTask(), HEART_BEAT_RATE, HEART_BEAT_RATE);
    }

    private void endBeat() {
        if (mBeatTimer != null) {
            mBeatTimer.cancel();
            mBeatTimer.purge();
            mBeatTimer = null;
        }
    }

    @Override
    public synchronized void onNetOn() {
        LogUtil.i(TAG, "On net on");
        if (isNetOffBefore) {
            LogUtil.i(TAG, "Net is off before");
            if (mDeviceConnection != null) {
                mDeviceConnection.disconnect();
                LogUtil.i(TAG, "try to connect the device");
                mConnectManager.start(getTag());
            }
        }
        isNetOffBefore = false;
    }

    @Override
    public synchronized void onNetOff() {
        isOnline = false;
        isNetOffBefore = true;
        resetMessageInfo();
        if (mDeviceConnection != null) {
            if (mDeviceConnection.isConnected()) {
                mDeviceConnection.disconnect();
            }
            for (HekrLANDeviceListener listener : mDeviceListeners) {
                listener.onDisconnected();
            }
        }
        mConnectManager.pause(getTag());
        endBeat();
    }

    private synchronized void resetMessageInfo() {
        mCurrentAuthMessageId = -1;
        mCurrentBeatMessageId = -1;
    }

    private class BeatTask extends java.util.TimerTask {

        @Override
        public void run() {
            tryToBeat();
        }
    }

    private class StatusListener implements ConnectionStatusListener {

        @Override
        public void onSuccess() {
            HekrDeviceClient.this.onSuccess();
        }

        @Override
        public void onFail() {
            HekrDeviceClient.this.onFail();
        }

        @Override
        public void onConnected() {
            HekrDeviceClient.this.onConnected();
        }

        @Override
        public void onDisconnected() {
            HekrDeviceClient.this.onDisconnected();
        }

        @Override
        public void onError() {
            HekrDeviceClient.this.onError();
        }
    }

    private synchronized void onSuccess() {
        LogUtil.i(TAG, "Success to connect device");
        tryToAuth();
    }

    private synchronized void onFail() {
        LogUtil.e(TAG, "Fail to connect device");
        isOnline = false;
        isConnecting = false;
        for (HekrLANDeviceListener listener : mDeviceListeners) {
            listener.onError(AndroidErrorMap.ERROR_CLIENT_LAN_CONNECT_FAIL
                    , AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_CLIENT_LAN_CONNECT_FAIL));
        }
    }

    private synchronized void onConnected() {
        LogUtil.i(TAG, "Connected");
    }

    private synchronized void onDisconnected() {
        LogUtil.i(TAG, "Disconnected");
        isOnline = false;
        isConnecting = false;
        resetMessageInfo();
        for (HekrLANDeviceListener listener : mDeviceListeners) {
            listener.onDisconnected();
        }
        if (mDeviceConnection != null) {
            mDeviceConnection.disconnect();
            mConnectManager.start(getTag(), 2000);
        }
    }

    private synchronized void onError() {
        LogUtil.i(TAG, "onError");
        boolean currentConnecting = isConnecting;
        isOnline = false;
        isConnecting = false;
        resetMessageInfo();
        for (HekrLANDeviceListener listener : mDeviceListeners) {
            listener.onDisconnected();
        }

        if (mDeviceConnection != null) {
            mDeviceConnection.disconnect();
            if (!currentConnecting) {
                mConnectManager.start(getTag(), 2000);
            } else {
                LogUtil.d(TAG, "Error when is connecting, not restart.");
            }
        }
    }

    private class HekrMsgCallbackWrapper implements HekrMsgCallback {

        private HekrMsgCallback msgCallback;

        HekrMsgCallbackWrapper(HekrMsgCallback msgCallback) {
            this.msgCallback = msgCallback;
        }

        @Override
        public void onReceived(String msg) {
            msgCallback.onReceived(msg);
        }

        @Override
        public void onTimeout() {
            msgCallback.onTimeout();
        }

        @Override
        public void onError(int errorCode, String message) {
            msgCallback.onError(errorCode, message);
            if (2000119 == errorCode) {
                LogUtil.e(TAG, "Unauthorized user: " + message);
                retryAuth();
            }
        }
    }
}

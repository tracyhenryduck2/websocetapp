package me.siter.sdk;

import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.connection.CloudConnection;
import me.siter.sdk.connection.ConnectionStatusListener;
import me.siter.sdk.connection.IConnection;
import me.siter.sdk.dispatcher.MessageFilter;
import me.siter.sdk.inter.SiterClientListener;
import me.siter.sdk.inter.SiterMsgCallback;
import me.siter.sdk.monitor.AppStatusMonitor;
import me.siter.sdk.monitor.AppStatusObservable;
import me.siter.sdk.monitor.NetObservable;
import me.siter.sdk.monitor.NetworkMonitor;
import me.siter.sdk.service.ConnOptions;
import me.siter.sdk.utils.AndroidErrorMap;
import me.siter.sdk.utils.AppIdUtil;
import me.siter.sdk.utils.JSONObjectUtil;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.MessageCounter;
import me.siter.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 与云端通信的接口的实现
 */

public class SiterCloudClient implements NetObservable, Connectable, AppStatusObservable {

    private static final String TAG = SiterCloudClient.class.getSimpleName();
    // 心跳包发送间隔时间
    private static final int HEART_BEAT_RATE = 15 * 1000;

    private String mUrl;
    private IConnection mCloudConnection;
    private String mAppId;
    private volatile boolean isOnline;
    private volatile boolean isConnecting;
    private volatile boolean isNetOffBefore;
    private Timer mBeatTimer;
    private NetworkMonitor mNetMonitor;
    private AppStatusMonitor mAppStatusMonitor;
    private ConnectManager mConnectManager;
    private CopyOnWriteArrayList<SiterClientListener> mSiterClientListeners;
    private CopyOnWriteArrayList<MessageRequest> mMessageCacheList;
    private Handler mHandler;

    private volatile int mCurrentLoginMessageId = -1;
    private volatile int mCurrentBeatMessageId = -1;

    SiterCloudClient() {
        this.mAppId = AppIdUtil.getAppId(SiterSDK.getContext());
        this.mNetMonitor = NetworkMonitor.getInstance();
        this.mAppStatusMonitor = AppStatusMonitor.getInstance();
        this.mConnectManager = ConnectManager.getInstance();
        this.mBeatTimer = new Timer();
        this.isNetOffBefore = !NetworkUtil.isConnected(SiterSDK.getContext());
        this.mSiterClientListeners = new CopyOnWriteArrayList<>();
        this.mMessageCacheList = new CopyOnWriteArrayList<>();
        this.mHandler = new Handler(SiterSDK.getContext().getMainLooper());
        this.mAppStatusMonitor.add(this);
    }

    public void destroy(){
        this.mAppStatusMonitor.remove(this);
    }

    public void sendMessage(final JSONObject message, final SiterMsgCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tryToSend(message, callback);
            }
        });
    }

    public void sendMessage(final String devTid, final JSONObject message, final SiterMsgCallback callback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tryToSend(devTid, message, callback);
            }
        });
    }

    public synchronized void connect(final String url) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mUrl = url;
                ConnOptions options = new ConnOptions(ConnOptions.TYPE_CONN_WEBSOCKET, url, 186);
                mCloudConnection = new CloudConnection();
                mCloudConnection.bind(options);
                mCloudConnection.setConnectionStatusListener(new StatusListener());
                mNetMonitor.add(SiterCloudClient.this);
                mConnectManager.add(SiterCloudClient.this, getTag(), getConnType());
            }
        });
    }

    public synchronized void disconnect() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                isOnline = false;
                isConnecting = false;
                mNetMonitor.remove(SiterCloudClient.this);
                mConnectManager.remove(getTag());
                if (mCloudConnection != null) {
                    if (!mCloudConnection.isClosed()) {
                        mCloudConnection.close();
                    }
                    mCloudConnection = null;
                }
                for (SiterClientListener listener : mSiterClientListeners) {
                    listener.onDisconnected();
                }

                mMessageCacheList.clear();

                resetMessageInfo();

                endBeat();
            }
        });
    }

    private synchronized void tryToLogin() {
        try {
            JSONObject params = new JSONObject();
            JSONObject result = new JSONObject();
            JSONObject filter = new JSONObject();

            params.put("appTid", mAppId);
            params.put("token", Siter.getSiterUser().getToken());

            final int count = MessageCounter.increaseCount();

            result.put("msgId", count);
            result.put("action", "appLogin");
            result.put("params", params);

            filter.put("msgId", count);
            filter.put("action", "appLoginResp");

            // 将当前的登录ID记录下来，如果收到登录消息，先判断和当前的登录消息是否匹配
            mCurrentLoginMessageId = count;

            final MessageRequest request = new MessageRequest(result.toString(),
                    new MessageFilter(filter), new SiterMsgCallback() {

                @Override
                public void onReceived(String msg) {
                    onLoginReceived(msg, count);
                }

                @Override
                public void onTimeout() {
                    onLoginTimeout(count);
                }

                @Override
                public void onError(int errorCode, String message) {
                    onLoginError(errorCode, message, count);
                }
            });
            LogUtil.d(TAG, "Send login message to cloud");
            if (mCloudConnection != null && mCloudConnection.isConnected()) {
                mCloudConnection.send(request);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private synchronized void onLoginReceived(String msg, int count) {
        if (count == mCurrentLoginMessageId && isCurrentLoginMessage(msg)) {
            LogUtil.d(TAG, "Receive login message from the cloud");
            isConnecting = false;
            checkLogin(msg);
        } else {
            LogUtil.d(TAG, "Login message Id is not matched");
        }
    }

    private synchronized void onLoginTimeout(int count) {
        if (count == mCurrentLoginMessageId) {
            LogUtil.e(TAG, "Timeout when login the cloud");
            isConnecting = false;
            if (mCloudConnection != null) {
                mCloudConnection.disconnect();
                resetMessageInfo();
                mConnectManager.start(getTag());
            }
        } else {
            LogUtil.d(TAG, "Login message Id is not matched");
        }
    }

    private synchronized void onLoginError(int errorCode, String message, int count) {
        if (count == mCurrentLoginMessageId) {
            LogUtil.e(TAG, "Error when login the cloud");
            isConnecting = false;
        } else {
            LogUtil.d(TAG, "Login message Id is not matched");
        }
    }

    private boolean isCurrentLoginMessage(String msg) {
        JSONObject json = JSONObjectUtil.getJSONObject(msg);
        if (json != null) {
            if (json.has("msgId")) {
                try {
                    int msgId = json.getInt("msgId");
                    LogUtil.d(TAG, "Current login message id is: " + mCurrentLoginMessageId + ", received msgId is: " + msgId);
                    return msgId == mCurrentLoginMessageId;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LogUtil.e(TAG, "Get incorrect format message from cloud: " + msg);
        }
        return false;
    }

    private void checkLogin(String msg) {
        JSONObject json = JSONObjectUtil.getJSONObject(msg);
        if (json != null) {
            if (200 == json.optInt("code")) {
                LogUtil.d(TAG, "Success to login the cloud");
                isOnline = true;
                for (SiterClientListener listener : mSiterClientListeners) {
                    listener.onConnected();
                }
                startBeat();
                sendCachedMessage();
            } else {
                LogUtil.e(TAG, "Get error code from cloud: " + msg);
            }
        } else {
            LogUtil.e(TAG, "Get incorrect format message from cloud: " + msg);
        }
    }

    private synchronized void sendCachedMessage() {
        if (mMessageCacheList.size() == 0) {
            return;
        }

        if (isOnline && mCloudConnection != null && mCloudConnection.isConnected()) {
            Iterator<MessageRequest> iterator = mMessageCacheList.iterator();
            while (iterator.hasNext()) {
                MessageRequest request = iterator.next();
                LogUtil.d(TAG, "Send cached message: " + request.getMessage());
                mCloudConnection.send(request);
                mMessageCacheList.remove(request);
            }
        }
    }

    @Override
    public synchronized boolean isOnline() {
        return isOnline;
    }

    synchronized void addHekrClientListener(SiterClientListener listener) {
        mSiterClientListeners.add(listener);
    }

    synchronized void removeHekrClientListener(SiterClientListener listener) {
        mSiterClientListeners.remove(listener);
    }

    @Override
    public synchronized boolean isConnecting() {
        return isConnecting;
    }

    @Override
    public synchronized ConnType getConnType() {
        return ConnType.CONN_CLOUD;
    }

    @Override
    public synchronized void communicate() {
        if (mCloudConnection != null) {
            if (mCloudConnection.isConnected()) {
                LogUtil.i(TAG, "try to login the cloud");
                tryToLogin();
                return;
            }
            LogUtil.i(TAG, "try to connect the cloud");
            isConnecting = true;
            mCloudConnection.connect();
        }
    }

    @Override
    public synchronized String getTag() {
        return mUrl;
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
    public synchronized void onScreenOn() {
        connect(mUrl);
    }

    @Override
    public synchronized void onScreenOff() {
        disconnect();
    }

    private class BeatTask extends java.util.TimerTask {

        @Override
        public void run() {
            tryToBeat();
        }
    }

    private synchronized void tryToBeat() {
        if (isOnline && mCloudConnection != null && mCloudConnection.isConnected()) {
            JSONObject result = new JSONObject();
            try {
                final int count = MessageCounter.increaseCount();
                result.put("action", "heartbeat");
                result.put("msgId", count);
                JSONObject rule = new JSONObject();
                rule.putOpt("action", "heartbeatResp");
                rule.putOpt("msgId", count);

                mCurrentBeatMessageId = count;

                LogUtil.e(TAG, "Beat...");
                MessageFilter filter = new MessageFilter(rule);
                MessageRequest request = new MessageRequest(result.toString(), filter, new SiterMsgCallback() {
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
                if (mCloudConnection != null && mCloudConnection.isConnected()) {
                    mCloudConnection.send(request);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void onBeatReceived(int count) {
        // TODO: 2017/3/29 处理心跳的逻辑
        if (count == mCurrentBeatMessageId) {
            LogUtil.i(TAG, "Cloud beat success");
        } else {
            LogUtil.d(TAG, "Beat message Id is not matched");
        }
    }

    private synchronized void onBeatTimeout(int count) {
        if (count == mCurrentBeatMessageId) {
            LogUtil.e(TAG, "Cloud beat timeout");
            isOnline = false;
            isConnecting = false;
            resetMessageInfo();
            if (mCloudConnection != null) {
                mCloudConnection.disconnect();
                for (SiterClientListener listener : mSiterClientListeners) {
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
            LogUtil.e(TAG, "Cloud beat error:" + message);
            isOnline = false;
            isConnecting = false;
            resetMessageInfo();
            if (mCloudConnection != null) {
                mCloudConnection.disconnect();
                for (SiterClientListener listener : mSiterClientListeners) {
                    listener.onDisconnected();
                }
                mConnectManager.start(getTag(), 2000);
            }
        } else {
            LogUtil.d(TAG, "Beat message Id is not matched");
        }
    }

    private void tryToSend(String devTid, JSONObject message, SiterMsgCallback callback) {
        tryToSend(message, callback);
    }

    private void tryToSend(JSONObject message, SiterMsgCallback callback) {
        if (!checkStatus(message, callback)) {
            return;
        }
        checkConnection();
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
//            if (!TextUtils.isEmpty(devTid)) {
//                JSONObject params = new JSONObject();
//                params.put("devTid", devTid);
//                filterObject.put("params", params);
//            }
            MessageFilter filter = new MessageFilter(filterObject);
            MessageRequest request = new MessageRequest(message.toString(), filter, callback);
            if (isOnline && mCloudConnection != null && mCloudConnection.isConnected()) {
                mCloudConnection.send(request);
            } else {
                LogUtil.d(TAG, "The connection is not available, cache the message");
                mMessageCacheList.add(request);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR));
        }
    }

    private boolean checkStatus(JSONObject message, SiterMsgCallback callback) {
        if (message == null) {
            LogUtil.e(TAG, "Message is null");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_MESSAGE_NULL,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_MESSAGE_NULL));
            return false;
        }
        if (TextUtils.isEmpty(mAppId)) {
            LogUtil.e(TAG, "AppId is null");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_APP_ID_NULL,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_APP_ID_NULL));
            return false;
        }
        if (!message.has("params")) {
            LogUtil.e(TAG, "No param found in the message");
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_NO_PARAM,
                    AndroidErrorMap.errMap.get(AndroidErrorMap.ERROR_MESSAGE_NO_PARAM));
            return false;
        }
        return true;
    }

    private void checkConnection() {
        if (!isOnline || mCloudConnection == null || !mCloudConnection.isConnected()) {
            LogUtil.e(TAG, "No connection found when send message");
            // TODO: 2017/4/11 确定错误码
            // callback.onError(-1, "No connection found when send message");
            if (!isConnecting) {
                isOnline = false;
                isConnecting = false;
                resetMessageInfo();
                if (mCloudConnection != null) {
                    mCloudConnection.disconnect();
                    mConnectManager.start(getTag());
                }
            }
            // return;
        }
    }

    @Override
    public synchronized void onNetOn() {
        if (isNetOffBefore) {
            if (mCloudConnection != null) {
                mCloudConnection.disconnect();
                mConnectManager.start(getTag());
            }
        }
        isNetOffBefore = false;
    }

    @Override
    public synchronized void onNetOff() {
        isOnline = false;
        isConnecting = false;
        isNetOffBefore = true;
        resetMessageInfo();
        if (mCloudConnection != null) {
            if (mCloudConnection.isConnected()) {
                mCloudConnection.disconnect();
            }
        }
        for (SiterClientListener listener : mSiterClientListeners) {
            listener.onDisconnected();
        }
        mConnectManager.pause(getTag());
        endBeat();
    }

    private synchronized void resetMessageInfo() {
        mCurrentLoginMessageId = -1;
        mCurrentBeatMessageId = -1;
    }

    private class StatusListener implements ConnectionStatusListener {

        @Override
        public void onSuccess() {
            SiterCloudClient.this.onSuccess();
        }

        @Override
        public void onFail() {
            SiterCloudClient.this.onFail();
        }

        @Override
        public void onConnected() {
            SiterCloudClient.this.onConnected();
        }

        @Override
        public void onDisconnected() {
            SiterCloudClient.this.onDisconnected();
        }

        @Override
        public void onError() {
            SiterCloudClient.this.onError();
        }

    }

    private synchronized void onSuccess() {
        LogUtil.i(TAG, "Connection is established: " + mUrl);
        LogUtil.i(TAG, "Try to login after established");
        tryToLogin();
    }

    private synchronized void onFail() {
        LogUtil.i(TAG, "Connection is not established");
        isOnline = false;
        isConnecting = false;
    }

    private synchronized void onConnected() {
        LogUtil.i(TAG, "Connected");
    }

    private synchronized void onDisconnected() {
        LogUtil.i(TAG, "Disconnected");
        isOnline = false;
        isConnecting = false;
        resetMessageInfo();
        if (mCloudConnection != null) {
            mCloudConnection.disconnect();
            for (SiterClientListener listener : mSiterClientListeners) {
                listener.onDisconnected();
            }
            // 由于这个触发的会比网络状态广播早，所以在触发重连
            // 在实际没有网络的情况下，netty会发生运行时异常，经过两秒延时
            mConnectManager.start(getTag(), 2000);
        }
    }

    private synchronized void onError() {
        LogUtil.i(TAG, "onError");
        boolean currentConnecting = isConnecting;
        isOnline = false;
        isConnecting = false;
        resetMessageInfo();
        if (mCloudConnection != null) {
            mCloudConnection.disconnect();
            for (SiterClientListener listener : mSiterClientListeners) {
                listener.onDisconnected();
            }
            if (!currentConnecting) {
                mConnectManager.start(getTag(), 2000);
            } else {
                LogUtil.d(TAG, "Error when is connecting, not restart.");
            }
        }
    }
}

package me.siter.sdk;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.siter.sdk.dispatcher.Dispatcher;
import me.siter.sdk.dispatcher.MessageFilter;
import me.siter.sdk.entity.LanControlBean;
import me.siter.sdk.entity.LanDeviceBean;
import me.siter.sdk.inter.SIterLANDeviceListener;
import me.siter.sdk.inter.SiterLANListener;
import me.siter.sdk.inter.SiterLANStatusListener;
import me.siter.sdk.inter.SiterMsgCallback;
import me.siter.sdk.monitor.AppStatusMonitor;
import me.siter.sdk.monitor.AppStatusObservable;
import me.siter.sdk.monitor.NetObservable;
import me.siter.sdk.monitor.NetworkMonitor;
import me.siter.sdk.service.SiterConnectionService;
import me.siter.sdk.service.ServiceBinder;
import me.siter.sdk.utils.AndroidErrorMap;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.MessageCounter;
import me.siter.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 本地控制的封装
 */

class SiterLANControl implements ISiterLANControl, SiterLANListener, NetObservable, AppStatusObservable {

    private static final String TAG = SiterLANControl.class.getSimpleName();

    private boolean mLANControl = false;
    private volatile boolean isNetOffBefore;
    private Map<String, ISiterDeviceClient> mDeviceClients;
    private SiterDeviceScanner mSiterDeviceScanner;
    private Map<String, LanControlBean> mLANControlDevices;
    private ArrayList<SiterLANStatusListener> mLANStatusListeners;
    private Map<String, SIterLANDeviceListener> mHekrLANDeviceListeners;

    SiterLANControl() {
        this.isNetOffBefore = !NetworkUtil.isWifiConnected(SiterSDK.getContext());
        this.mSiterDeviceScanner = new SiterDeviceScanner();
        this.mLANControlDevices = new ConcurrentHashMap<>();
        this.mLANStatusListeners = new ArrayList<>();
        this.mDeviceClients = new ConcurrentHashMap<>();
        this.mHekrLANDeviceListeners = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized void enableLANControl(boolean enable) {
        mLANControl = enable;
        if (enable) {
            NetworkMonitor.getInstance().add(this);
            AppStatusMonitor.getInstance().add(this);
            openLANScanBackgroud();
            if (mLANStatusListeners.size() > 0) {
                for (SiterLANStatusListener listener : mLANStatusListeners) {
                    listener.onStatusChanged(true);
                }
            }
        } else {
            NetworkMonitor.getInstance().remove(this);
            AppStatusMonitor.getInstance().remove(this);
            closeLANScanBackgroud();
            clearDeviceClients();
            // 清除局域网设备
            clearLANDevices();
            if (mLANStatusListeners.size() > 0) {
                for (SiterLANStatusListener listener : mLANStatusListeners) {
                    listener.onStatusChanged(false);
                }
            }
        }
    }

    private synchronized void openLANScanBackgroud() {
        // 开始监听
        startScanForDevices(this);
    }

    private synchronized void closeLANScanBackgroud() {
        // 停止监听
        stopScanForDevices();
    }

    @Override
    public synchronized boolean isLANControlEnabled() {
        return mLANControl;
    }

    @Override
    public synchronized ISiterDeviceClient getDeviceClient(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return mDeviceClients.get(tag);
        }
        return null;
    }

    @Override
    public synchronized List<ISiterDeviceClient> getLANDeviceClients() {
        ArrayList<ISiterDeviceClient> clients = new ArrayList<>();
        for (String tag : mLANControlDevices.keySet()) {
            ISiterDeviceClient client = getDeviceClient(tag);
            if (client != null) {
                clients.add(client);
            }
        }
        return clients;
    }

    @Override
    public synchronized void onNewDevice(LanDeviceBean device) {
        LogUtil.d(TAG, "NewDevice:" + device.toString());
        checkDeviceConnectable(device);
    }

    @Override
    public synchronized void onGetDevices(List<LanDeviceBean> list) {
        LogUtil.d(TAG, "GetDevices:" + list.toString());
        for (LanDeviceBean bean : list) {
            checkDeviceConnectable(bean);
        }
    }

    private void checkClientsValid() {
        Iterator<Map.Entry<String, ISiterDeviceClient>> iterator = mDeviceClients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ISiterDeviceClient> entry = iterator.next();
            String tag = entry.getKey();
            if (!mLANControlDevices.keySet().contains(tag)) {
                if (getDeviceClient(tag) != null) {
                    getDeviceClient(tag).disconnect();
                }
                iterator.remove();
            }
        }
    }

    private void connectNewClients() {
        for (String tag : mLANControlDevices.keySet()) {
            final LanControlBean bean = mLANControlDevices.get(tag);
            final String finalTag = tag;
            if (mDeviceClients.get(tag) == null) {
                final ISiterDeviceClient client = putDeviceClient(tag, mLANControlDevices.get(tag).getCtrlKey());
                SIterLANDeviceListener listener = new SIterLANDeviceListener() {
                    @Override
                    public void onConnected() {
                        Log.d(TAG, "Connected, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        if (mLANStatusListeners.size() > 0) {
                            for (SiterLANStatusListener listener : mLANStatusListeners) {
                                listener.onDeviceStatusChanged(finalTag);
                            }
                        }
                    }

                    @Override
                    public void onDisconnected() {
                        Log.d(TAG, "Disconnected, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        if (mLANStatusListeners.size() > 0) {
                            for (SiterLANStatusListener listener : mLANStatusListeners) {
                                listener.onDeviceStatusChanged(finalTag);
                            }
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Log.d(TAG, "Connect error, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        Log.d(TAG, "Error code: " + errorCode + ", message: " + errorMsg);
                        for (SiterLANStatusListener listener : mLANStatusListeners) {
                            listener.onDeviceStatusError(finalTag, errorCode, errorMsg);
                        }
                    }
                };

                mHekrLANDeviceListeners.put(tag, listener);
                client.addLANDeviceListener(listener);
                client.connect(bean.getDeviceIP(), bean.getDevicePort());
            } else {
                ISiterDeviceClient client = getDeviceClient(tag);
                if (!client.isOnline()) {
                    client.connect(bean.getDeviceIP(), bean.getDevicePort());
                }
            }
        }
    }

    private void startScanForDevices(SiterLANListener listener) {
        mSiterDeviceScanner.addLANListener(listener);
        mSiterDeviceScanner.startSearch();
    }

    private void stopScanForDevices() {
        mSiterDeviceScanner.removeLANListener();
        mSiterDeviceScanner.stopSearch();
    }

    private void clearLANDevices() {
        for (String tag : mLANControlDevices.keySet()) {
            ISiterDeviceClient client = getDeviceClient(tag);
            if (client != null) {
                if (mHekrLANDeviceListeners.containsKey(tag)) {
                    SIterLANDeviceListener listener = mHekrLANDeviceListeners.get(tag);
                    client.removeLANDeviceListener(listener);
                    mHekrLANDeviceListeners.remove(tag);
                }
                removeDeviceClient(tag);
            }
        }
        mLANControlDevices.clear();
    }

    private void getExistDevices() {
        mSiterDeviceScanner.getExistDevices();
    }

    private void checkDeviceConnectable(LanDeviceBean device) {
        String devTid = device.getDevTid();
        if (!mLANControlDevices.containsKey(devTid)) {
            return;
        }
        ISiterDeviceClient client = mDeviceClients.get(devTid);
        if (client != null && (!TextUtils.equals(client.getIP(), device.getServiceIp()) || client.getPort() != device.getServicePort())) {
            LogUtil.d(TAG, "The client connection option is different from previous, try to reconnect :" + devTid + ", client ip: " + client.getIP() + ", device ip: " + device.getServiceIp());
            client.connect(device.getServiceIp(), device.getServicePort());
        }
    }

    @Override
    public synchronized void refreshLAN(List<LanControlBean> list) {
        if (mLANControl) {
            mLANControlDevices.clear();
            for (LanControlBean bean : list) {
                mLANControlDevices.put(bean.getDevTid(), bean);
            }
            // 先检查当前与设备的连接是否有效
            checkClientsValid();
            // 去连接新的设备
            connectNewClients();
            // 扫描局域网内的设备
            getExistDevices();
        }
    }

    @Override
    public void addLANStatusChangeListener(SiterLANStatusListener listener) {
        mLANStatusListeners.add(listener);
    }

    @Override
    public void removeLANStatusChangeListener(SiterLANStatusListener listener) {
        mLANStatusListeners.remove(listener);
    }

    @Override
    public synchronized void enableLANMulticast(final boolean enable) {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            ServiceBinder.getInstance().addListener(new ServiceBinder.ConnectServiceListener() {
                @Override
                public void onServiceConnected() {
                    ServiceBinder.getInstance().removeListener(this);
                    SiterConnectionService service = ServiceBinder.getInstance().getService();
                    service.enableMulticast(enable);
                }

                @Override
                public void onServiceDisconnected() {
                    ServiceBinder.getInstance().removeListener(this);
                }
            });
            ServiceBinder.getInstance().connect();
        } else {
            service.enableMulticast(enable);
        }
    }

    @Override
    public synchronized void enableLANBroadcast(final boolean enable) {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            ServiceBinder.getInstance().addListener(new ServiceBinder.ConnectServiceListener() {
                @Override
                public void onServiceConnected() {
                    ServiceBinder.getInstance().removeListener(this);
                    SiterConnectionService service = ServiceBinder.getInstance().getService();
                    service.enableBroadcast(enable);
                }

                @Override
                public void onServiceDisconnected() {
                    ServiceBinder.getInstance().removeListener(this);
                }
            });
            ServiceBinder.getInstance().connect();
        } else {
            service.enableBroadcast(enable);
        }
    }

    @Override
    public synchronized void sendCommonUdp(final JSONObject message, final String ip, final int port, SiterMsgCallback callback) {
        try {
            int count;
            if (!message.has("msgId")) {
                count = MessageCounter.increaseCount();
                message.put("msgId", count);
            } else {
                count = message.getInt("msgId");
            }

            JSONObject filter = new JSONObject();
            filter.put("msgId", count);
            filter.put("action", TextUtils.concat(message.getString("action"), "Resp"));

            MessageRequest request = new MessageRequest(message.toString(), new MessageFilter(filter), callback);
            request.setChannel(IMessageRequest.CHANNEL_COMMON_UDP);
            Dispatcher.getInstance().enqueue(request, ip, port, FilterType.FILTER_ONCE);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR, AndroidErrorMap.code2Desc(AndroidErrorMap.ERROR_MESSAGE_FORMAT_ERROR));
        }
    }

    @Override
    public synchronized int getCommonUdpPort() {
        SiterConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            return 0;
        } else {
            return service.getCommonUdpPort();
        }
    }

    @Override
    public synchronized void onNetOn() {
        if (isNetOffBefore) {
            if (mLANControl && NetworkUtil.isWifiConnected(SiterSDK.getContext())) {
                openLANScanBackgroud();
            }
        }
        isNetOffBefore = false;
    }

    @Override
    public synchronized void onNetOff() {
        isNetOffBefore = true;
        if (mLANControl) {
            closeLANScanBackgroud();
        }
    }

    private ISiterDeviceClient putDeviceClient(String tag, String ctrlKey) {
        if (mDeviceClients.containsKey(tag)) {
            return mDeviceClients.get(tag);
        }
        ISiterDeviceClient client = new SiterDeviceClient(tag, ctrlKey);
        mDeviceClients.put(tag, client);
        return client;
    }

    private void removeDeviceClient(String tag) {
        ISiterDeviceClient client = mDeviceClients.get(tag);
        if (client != null) {
            client.disconnect();
            mDeviceClients.remove(tag);
        }
    }

    private void clearDeviceClients() {
        for (String tag : mLANControlDevices.keySet()) {
            removeDeviceClient(tag);
        }
    }

    @Override
    public synchronized void onScreenOn() {

    }

    @Override
    public synchronized void onScreenOff() {

    }
}

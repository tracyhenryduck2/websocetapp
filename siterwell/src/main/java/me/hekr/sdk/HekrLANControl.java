package me.hekr.sdk;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.hekr.sdk.dispatcher.Dispatcher;
import me.hekr.sdk.dispatcher.MessageFilter;
import me.hekr.sdk.entity.LanControlBean;
import me.hekr.sdk.entity.LanDeviceBean;
import me.hekr.sdk.inter.HekrLANDeviceListener;
import me.hekr.sdk.inter.HekrLANListener;
import me.hekr.sdk.inter.HekrLANStatusListener;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sdk.monitor.AppStatusMonitor;
import me.hekr.sdk.monitor.AppStatusObservable;
import me.hekr.sdk.monitor.NetObservable;
import me.hekr.sdk.monitor.NetworkMonitor;
import me.hekr.sdk.service.HekrConnectionService;
import me.hekr.sdk.service.ServiceBinder;
import me.hekr.sdk.utils.AndroidErrorMap;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.MessageCounter;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 本地控制的封装
 */

class HekrLANControl implements IHekrLANControl, HekrLANListener, NetObservable, AppStatusObservable {

    private static final String TAG = HekrLANControl.class.getSimpleName();

    private boolean mLANControl = false;
    private volatile boolean isNetOffBefore;
    private Map<String, IHekrDeviceClient> mDeviceClients;
    private HekrDeviceScanner mHekrDeviceScanner;
    private Map<String, LanControlBean> mLANControlDevices;
    private ArrayList<HekrLANStatusListener> mLANStatusListeners;
    private Map<String, HekrLANDeviceListener> mHekrLANDeviceListeners;

    HekrLANControl() {
        this.isNetOffBefore = !NetworkUtil.isWifiConnected(HekrSDK.getContext());
        this.mHekrDeviceScanner = new HekrDeviceScanner();
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
                for (HekrLANStatusListener listener : mLANStatusListeners) {
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
                for (HekrLANStatusListener listener : mLANStatusListeners) {
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
    public synchronized IHekrDeviceClient getDeviceClient(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return mDeviceClients.get(tag);
        }
        return null;
    }

    @Override
    public synchronized List<IHekrDeviceClient> getLANDeviceClients() {
        ArrayList<IHekrDeviceClient> clients = new ArrayList<>();
        for (String tag : mLANControlDevices.keySet()) {
            IHekrDeviceClient client = getDeviceClient(tag);
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
        Iterator<Map.Entry<String, IHekrDeviceClient>> iterator = mDeviceClients.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, IHekrDeviceClient> entry = iterator.next();
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
                final IHekrDeviceClient client = putDeviceClient(tag, mLANControlDevices.get(tag).getCtrlKey());
                HekrLANDeviceListener listener = new HekrLANDeviceListener() {
                    @Override
                    public void onConnected() {
                        Log.d(TAG, "Connected, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        if (mLANStatusListeners.size() > 0) {
                            for (HekrLANStatusListener listener : mLANStatusListeners) {
                                listener.onDeviceStatusChanged(finalTag);
                            }
                        }
                    }

                    @Override
                    public void onDisconnected() {
                        Log.d(TAG, "Disconnected, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        if (mLANStatusListeners.size() > 0) {
                            for (HekrLANStatusListener listener : mLANStatusListeners) {
                                listener.onDeviceStatusChanged(finalTag);
                            }
                        }
                    }

                    @Override
                    public void onError(int errorCode, String errorMsg) {
                        Log.d(TAG, "Connect error, tag : " + finalTag + ", ip: " + client.getIP() + ", port: " + client.getPort());
                        Log.d(TAG, "Error code: " + errorCode + ", message: " + errorMsg);
                        for (HekrLANStatusListener listener : mLANStatusListeners) {
                            listener.onDeviceStatusError(finalTag, errorCode, errorMsg);
                        }
                    }
                };

                mHekrLANDeviceListeners.put(tag, listener);
                client.addLANDeviceListener(listener);
                client.connect(bean.getDeviceIP(), bean.getDevicePort());
            } else {
                IHekrDeviceClient client = getDeviceClient(tag);
                if (!client.isOnline()) {
                    client.connect(bean.getDeviceIP(), bean.getDevicePort());
                }
            }
        }
    }

    private void startScanForDevices(HekrLANListener listener) {
        mHekrDeviceScanner.addLANListener(listener);
        mHekrDeviceScanner.startSearch();
    }

    private void stopScanForDevices() {
        mHekrDeviceScanner.removeLANListener();
        mHekrDeviceScanner.stopSearch();
    }

    private void clearLANDevices() {
        for (String tag : mLANControlDevices.keySet()) {
            IHekrDeviceClient client = getDeviceClient(tag);
            if (client != null) {
                if (mHekrLANDeviceListeners.containsKey(tag)) {
                    HekrLANDeviceListener listener = mHekrLANDeviceListeners.get(tag);
                    client.removeLANDeviceListener(listener);
                    mHekrLANDeviceListeners.remove(tag);
                }
                removeDeviceClient(tag);
            }
        }
        mLANControlDevices.clear();
    }

    private void getExistDevices() {
        mHekrDeviceScanner.getExistDevices();
    }

    private void checkDeviceConnectable(LanDeviceBean device) {
        String devTid = device.getDevTid();
        if (!mLANControlDevices.containsKey(devTid)) {
            return;
        }
        IHekrDeviceClient client = mDeviceClients.get(devTid);
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
    public void addLANStatusChangeListener(HekrLANStatusListener listener) {
        mLANStatusListeners.add(listener);
    }

    @Override
    public void removeLANStatusChangeListener(HekrLANStatusListener listener) {
        mLANStatusListeners.remove(listener);
    }

    @Override
    public synchronized void enableLANMulticast(final boolean enable) {
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            ServiceBinder.getInstance().addListener(new ServiceBinder.ConnectServiceListener() {
                @Override
                public void onServiceConnected() {
                    ServiceBinder.getInstance().removeListener(this);
                    HekrConnectionService service = ServiceBinder.getInstance().getService();
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
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            ServiceBinder.getInstance().addListener(new ServiceBinder.ConnectServiceListener() {
                @Override
                public void onServiceConnected() {
                    ServiceBinder.getInstance().removeListener(this);
                    HekrConnectionService service = ServiceBinder.getInstance().getService();
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
    public synchronized void sendCommonUdp(final JSONObject message, final String ip, final int port, HekrMsgCallback callback) {
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
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            return 0;
        } else {
            return service.getCommonUdpPort();
        }
    }

    @Override
    public synchronized void onNetOn() {
        if (isNetOffBefore) {
            if (mLANControl && NetworkUtil.isWifiConnected(HekrSDK.getContext())) {
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

    private IHekrDeviceClient putDeviceClient(String tag, String ctrlKey) {
        if (mDeviceClients.containsKey(tag)) {
            return mDeviceClients.get(tag);
        }
        IHekrDeviceClient client = new HekrDeviceClient(tag, ctrlKey);
        mDeviceClients.put(tag, client);
        return client;
    }

    private void removeDeviceClient(String tag) {
        IHekrDeviceClient client = mDeviceClients.get(tag);
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

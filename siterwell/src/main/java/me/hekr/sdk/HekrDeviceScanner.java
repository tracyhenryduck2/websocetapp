package me.hekr.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import me.hekr.sdk.entity.LanDeviceBean;
import me.hekr.sdk.inter.HekrLANListener;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/
class HekrDeviceScanner implements IHekrDeviceScanner, ServiceListener {

    private static final String TAG = HekrDeviceScanner.class.getSimpleName();
    private static final String TYPE_SERVICE_UDP = "_hekr._udp.local.";

    // 服务实际类型
    private String mType = TYPE_SERVICE_UDP;
    private JmDNS mJmdns;
    private WifiManager.MulticastLock mLock;
    private boolean isDiscoveryStarted = false;
    private HekrLANListener mLANListener;
    private ExecutorService mSingleThreadPool = Executors.newSingleThreadExecutor();

    HekrDeviceScanner() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiMwanager = (WifiManager) HekrSDK.getContext().getSystemService(Context.WIFI_SERVICE);
        mLock = wifiMwanager.createMulticastLock(getRandomString(8));
    }

    /**
     * 开启发现服务
     */
    @Override
    public void startSearch() {
        mSingleThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                stopSafely();
                startSafely();
            }
        });
    }

    private synchronized void startSafely() {
        if (isDiscoveryStarted) {
            return;
        }
        if (!NetworkUtil.isWifiConnected(HekrSDK.getContext())) {
            return;
        }
        mLock.setReferenceCounted(false);
        mLock.acquire();
        try {
            LogUtil.i(TAG, "Start searching the LAN device");
            final InetAddress deviceIpAddress = NetworkUtil.getDeviceIpAddress(HekrSDK.getContext());
            mJmdns = JmDNS.create(deviceIpAddress);
            mJmdns.addServiceListener(mType, HekrDeviceScanner.this);
            isDiscoveryStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
            mJmdns = null;
            mLock.release();
        }
    }

    /**
     * 主动停止发现
     */
    @Override
    public void stopSearch() {
        mSingleThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                stopSafely();
            }
        });
    }

    private synchronized void stopSafely() {
        if (!isDiscoveryStarted) {
            return;
        }
        mLock.release();
        try {
            LogUtil.i(TAG, "Stop searching the LAN device");
            mJmdns.removeServiceListener(mType, this);
            mJmdns.close();
            isDiscoveryStarted = false;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            mJmdns = null;
        }
    }

    @Override
    public boolean isStarted() {
        return isDiscoveryStarted;
    }

    @Override
    public void addLANListener(HekrLANListener listener) {
        this.mLANListener = listener;
    }

    @Override
    public void removeLANListener() {
        mLANListener = null;
    }

    @Override
    public void getExistDevices() {
        mSingleThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                List<LanDeviceBean> list = new ArrayList<>();
                if (isDiscoveryStarted) {
                    ServiceInfo[] infos = mJmdns.list(mType);
                    for (ServiceInfo info : infos) {
                        LanDeviceBean bean = transformToControlBean(info);
                        if (bean != null) {
                            list.add(bean);
                        }
                    }
                }
                if (mLANListener != null) {
                    mLANListener.onGetDevices(list);
                }
            }
        });
    }

    private static String getRandomString(int length) { // length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    @Override
    public void serviceAdded(ServiceEvent serviceEvent) {
        LogUtil.i(TAG, "Service added: " + serviceEvent.toString());
        mJmdns.requestServiceInfo(serviceEvent.getType(), serviceEvent.getName());
    }

    @Override
    public void serviceRemoved(ServiceEvent serviceEvent) {
        LogUtil.i(TAG, "Service removed: " + serviceEvent.toString());
    }

    @Override
    public void serviceResolved(ServiceEvent serviceEvent) {
        LogUtil.d(TAG, "Service resolved: " + serviceEvent.toString());
        ServiceInfo info = serviceEvent.getInfo();
        if (info != null) {
            LanDeviceBean bean = transformToControlBean(serviceEvent.getInfo());
            // 告知发现新设备
            if (!TextUtils.isEmpty(info.getType()) &&
                    TextUtils.equals(mType, info.getType()) &&
                    bean != null) {
                if (mLANListener != null) {
                    mLANListener.onNewDevice(bean);
                }
            }
        }
    }

    private LanDeviceBean transformToControlBean(ServiceInfo info) {
        String infoStr = new String(info.getTextBytes());
        LogUtil.d(TAG, "ServiceInfo:" + infoStr);
        LanDeviceBean lanDeviceBean = null;
        if (!TextUtils.isEmpty(infoStr)) {
            // 将接收回的字节数组转换成以((分割的key-value
            String str = getNiceTextString(info.getTextBytes());
            HashMap<String, String> map;
            // 解析所发现服务的信息
            map = getMap(str, info);
            if (map.containsKey("devTid") &&
                    !TextUtils.isEmpty(map.get("devTid"))) {
                lanDeviceBean = map2Bean(map, info);
            }
        }
        return lanDeviceBean;
    }

    private LanDeviceBean map2Bean(HashMap<String, String> map, ServiceInfo info) {
        LanDeviceBean findDeviceBean = new LanDeviceBean();

        if (map.containsKey("MAC")) {
            findDeviceBean.setMAC(map.get("MAC"));
        }
        if (map.containsKey("SDKVer")) {
            findDeviceBean.setSDKVer(map.get("SDKVer"));
        }
        if (map.containsKey("SSID")) {
            findDeviceBean.setSSID(map.get("SSID"));
        }
        if (map.containsKey("binType")) {
            findDeviceBean.setBinType(map.get("binType"));
        }
        if (map.containsKey("binVer")) {
            findDeviceBean.setBinVer(map.get("binVer"));
        }
        if (map.containsKey("bindKey")) {
            findDeviceBean.setBindKey(map.get("bindKey"));
        }
        if (map.containsKey("devTid")) {
            findDeviceBean.setDevTid(map.get("devTid"));
        }
        if (map.containsKey("mid")) {
            findDeviceBean.setMid(map.get("mid"));
        }
        if (map.containsKey("serviceHost")) {
            findDeviceBean.setServiceHost(map.get("serviceHost"));
        }
        if (map.containsKey("servicePort")) {
            findDeviceBean.setServicePort(Integer.parseInt(map.get("servicePort")));
        }
        if (map.containsKey("tokenType")) {
            findDeviceBean.setTokenType(Integer.parseInt(map.get("tokenType")));
        } else {
            findDeviceBean.setTokenType(2);
        }
        if (map.containsKey("workMode")) {
            findDeviceBean.setWorkMode(Integer.parseInt(map.get("workMode")));
        } else {
            findDeviceBean.setWorkMode(0);
        }

        if (info != null && !TextUtils.isEmpty(info.getHostAddress())) {
            findDeviceBean.setServiceIp(info.getHostAddress());
        }

        if (info != null && info.getPort() != 0) {
            findDeviceBean.setServicePort(info.getPort());
        }
        return findDeviceBean;
    }

    /**
     * @param text ServiceInfo中的服务信息字节数组
     * @return 处理服务返回的字节数组
     */
    private String getNiceTextString(byte[] text) {

        StringBuilder buf = new StringBuilder();
        for (byte aText : text) {
            int ch = aText & 0xFF;
            if ((ch < ' ') || (ch > 127)) {
                buf.append("(");

            } else {
                buf.append((char) ch);
            }
        }
        return buf.toString();
    }

    /**
     * @param str  处理过的nice服务信息Str
     * @param info 服务数据源,可以获取ServiceInfo
     * @return 将服务信息以map形式返回
     */
    private HashMap<String, String> getMap(String str, ServiceInfo info) {
        HashMap<String, String> map = new HashMap<>();
        // 解析mico服务信息
        if (!TextUtils.isEmpty(str) && str.contains("(")) {
            String[] key_value = str.split("\\(");
            if (key_value.length > 0) {
                for (String aKey_value : key_value) {
                    String kvParam[] = aKey_value.split("=");

                    if (kvParam.length == 2) {
                        map.put(kvParam[0].trim().replace("(", ""), kvParam[1].trim().replace("(", ""));
                    }
                }
            }
        }
        // 解析esp8266服务信息
        if (!map.containsKey("devTid")) {
            map.clear();
            Enumeration<String> paraNames = info.getPropertyNames();
            while (paraNames.hasMoreElements()) {
                String thisName = paraNames.nextElement();
                String thisValue = info.getPropertyString(thisName);
                map.put(thisName, thisValue);
            }
        }
        return map;
    }
}

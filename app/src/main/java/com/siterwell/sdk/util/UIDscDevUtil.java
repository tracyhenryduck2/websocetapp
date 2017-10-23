package com.siterwell.sdk.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.*;

import com.siterwell.sdk.bean.FindDeviceBean;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 * Created by Administrator on 2017/10/16.
 */

public class UIDscDevUtil {

    private static final String TAG = "UIDscDevUtil";

    //服务实际类型
    private String type = "_hekr._udp.local.";
    private JmDNS jmdns = null;
    private MyDiscoverListener listener = null;

    private AtomicBoolean isStart = new AtomicBoolean(false);
    private List<FindDeviceBean> list = new ArrayList<>();
    private AtomicBoolean isCancel = new AtomicBoolean(false);

    private DeviceListCallBack deviceListCallBack;

    private Context context;
    private WifiManager.MulticastLock lock = null;

    public UIDscDevUtil(Context context) {
        this.context = context;
    }

    //配置相关信息
    private void informationConfig() {
        try {
            if (listener != null) {
                jmdns.removeServiceListener(type, listener);
                listener = null;
            }

            listener = new MyDiscoverListener();
            jmdns.addServiceListener(type, listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param number 发现服务总时长
     */
    public void startSearch(int number) {
        isStart.set(false);
        isCancel.set(false);
        list.clear();
        //开启线程每三秒钟调用一次发现
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!isStart.get()&&!isCancel.get()) {
                        //Log.i("hekrXMConfig","hekrXMConfig搜索...");
                        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        if (lock == null) {
                            lock = manager.createMulticastLock("localWifi");
                            lock.setReferenceCounted(true);
                            lock.acquire();
                        }

                        if (jmdns != null) {
                            informationConfig();
                        } else {
                            jmdns = JmDNS.create(InetAddress.getLocalHost());
                            if (listener != null) {
                                jmdns.addServiceListener(type, listener);
                            } else {
                                listener = new MyDiscoverListener();
                                jmdns.addServiceListener(type, listener);
                            }
                        }
                        Thread.sleep(3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        int count = 0;
        try {
            // number*1000毫秒是number分钟，这是发现服务的总体超时时间
            // 在1分钟内，不断去判断配置是否成功
            while ((!isStart.get()) && count < number) {
                // 每次判断之后，主线程休眠1000毫秒
                Thread.sleep(1000);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Log.i("hekrXMConfig","hekrXMConfig搜索结束...");
        stopSearch();
        //默认false，每次都回调数据
        if(!isCancel.get()) {
            //Log.i("hekrXMConfig","hekrXMConfig进行回调...");
            if (list.isEmpty()) {
                releaseListener();
                deviceListCallBack.callBackFail();
            } else {
                releaseListener();
                //Log.i(TAG, "list:" + list.toString());
                deviceListCallBack.callBackSuccess();
            }
        }
        //当外部对话框主动点击左上角取消配网时取消回调
        else{
            //Log.i("hekrXMConfig","hekrXMConfig不进行回调...");
            releaseListener();
            isCancel.set(false);
        }
    }

    /**
     * 主动停止发现
     */
    public void stopSearch() {
        isStart.set(true);
    }

    public void cancelCallback(){
        isCancel.set(true);
    }

    private void releaseListener() {
        if (jmdns != null) {
            if (listener != null) {
                jmdns.removeServiceListener(type, listener);
                listener = null;
            }
            jmdns.unregisterAllServices();
            try {
                jmdns.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jmdns = null;
        }

        if (lock != null&&lock.isHeld()) {
            lock.release();
            lock=null;
        }
    }

    public void setListener(DeviceListCallBack deviceListCallBack) {
        this.deviceListCallBack = deviceListCallBack;
    }

    class MyDiscoverListener implements ServiceListener {

        @Override
        public void serviceResolved(ServiceEvent serviceEvent) {

            //Log.i(TAG, "12:" + serviceEvent.getInfo().getNiceTextString());
            android.util.Log.i(TAG,"13:"+getNiceTextString(serviceEvent.getInfo().getTextBytes()));

            if (!TextUtils.isEmpty(new String(serviceEvent.getInfo().getTextBytes()))) {
                //将接收回的字节数组转换成以((分割的key-value
                String str = getNiceTextString(serviceEvent.getInfo().getTextBytes());

                Map<String, String> map;
                //解析所发现服务的信息
                map = getMap(str, serviceEvent);
                //Log.i(TAG,"14:"+map.toString());
                //BindDeviceBean bindDeviceBean= null;
                FindDeviceBean findDeviceBean = null;
                if (map.containsKey("devTid") &&
                        map.containsKey("bindKey") &&
                        !TextUtils.isEmpty(map.get("devTid")) &&
                        !TextUtils.isEmpty(map.get("bindKey"))) {
                    //bindDeviceBean= new BindDeviceBean(map.get("devTid"), map.get("bindKey"), "", "xm_text");
                    findDeviceBean = map2Bean(map, serviceEvent);
                }

                if (serviceEvent.getInfo() != null &&
                        !TextUtils.isEmpty(serviceEvent.getInfo().getType()) &&
                        TextUtils.equals(type, serviceEvent.getInfo().getType()) &&
                        findDeviceBean != null) {
                    //Log.i(TAG, "15:" + findDeviceBean.toString());
                    addNewDevice(findDeviceBean);
                }
            }
        }

        @Override
        public void serviceRemoved(ServiceEvent ev) {
            android.util.Log.i(TAG, "Service removed: " + ev.getName());
        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            // Required to force serviceResolved to be called again
            // (after the first search)
            jmdns.requestServiceInfo(event.getType(), event.getName());
        }
    }

    private FindDeviceBean map2Bean(Map<String, String> map, ServiceEvent serviceEvent) {

        FindDeviceBean findDeviceBean = new FindDeviceBean();

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

        if (serviceEvent != null && serviceEvent.getInfo() != null && !TextUtils.isEmpty(serviceEvent.getInfo().getHostAddress())) {
            findDeviceBean.setServiceIp(serviceEvent.getInfo().getHostAddress());
        }

        if (serviceEvent != null && serviceEvent.getInfo() != null && serviceEvent.getInfo().getPort() != 0) {
            findDeviceBean.setServicePort(serviceEvent.getInfo().getPort());
        }
        return findDeviceBean;
    }

    /**
     * @param findDeviceBean 发现的bindDeviceBean
     */
    public void addNewDevice(FindDeviceBean findDeviceBean) {
        //去除固件返回为空的BindKey
        if(!TextUtils.isEmpty(findDeviceBean.getBindKey())&&findDeviceBean.getBindKey().length()==32) {
            if (!list.isEmpty()) {
                //Log.i(TAG, "list-not-empty");
                int i = 0;
                //遍历已有list中的设备
                while (i < list.size()) {

                    //list有这个tid并且bindKey没更新
                    if (TextUtils.equals(findDeviceBean.getDevTid(), list.get(i).getDevTid()) &&
                            TextUtils.equals(findDeviceBean.getBindKey(), list.get(i).getBindKey())) {
                        break;
                    }
                    //list有这个tid但是bindKey有更新
                    if (TextUtils.equals(findDeviceBean.getDevTid(), list.get(i).getDevTid()) &&
                            !TextUtils.equals(findDeviceBean.getBindKey(), list.get(i).getBindKey())) {
                        list.get(i).setBindKey(findDeviceBean.getBindKey());
                        list.get(i).setServiceIp(findDeviceBean.getServiceIp());
                        list.get(i).setServicePort(findDeviceBean.getServicePort());
                        list.get(i).setMid(findDeviceBean.getMid());
                        android.util.Log.i(TAG, "新设备：" + findDeviceBean.toString());
                        deviceListCallBack.callBackDevice(findDeviceBean);
                        break;
                    }
                    //假如执行到这里那么list里面肯定是没有这个tid并且已经遍历到最后一个,那么将这个新设备添加进list中
                    if (i == list.size() - 1) {
                        list.add(findDeviceBean);
                        android.util.Log.i(TAG, "新设备：" + findDeviceBean.toString());
                        deviceListCallBack.callBackDevice(findDeviceBean);
                        break;
                    }
                    i++;
                }
            } else {
                list.add(findDeviceBean);
                android.util.Log.i(TAG, "新设备：" + findDeviceBean.toString());
                deviceListCallBack.callBackDevice(findDeviceBean);
            }
        }
    }

    /**
     * @param text ServiceInfo中的服务信息字节数组
     * @return 处理服务返回的字节数组
     */
    public String getNiceTextString(byte[] text) {

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
     * @param str          处理过的nice服务信息Str
     * @param serviceEvent 服务数据源,可以获取ServiceInfo
     * @return 将服务信息以map形式返回
     */
    public Map<String, String> getMap(String str, ServiceEvent serviceEvent) {
        Map<String, String> map = new HashMap<>();

        //解析mico服务信息
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
        //解析esp8266服务信息
        if (!map.containsKey("devTid") || !map.containsKey("bindKey")) {
            map.clear();
            Enumeration<String> paraNames = serviceEvent.getInfo().getPropertyNames();
            while (paraNames.hasMoreElements()) {

                String thisName = paraNames.nextElement();
                String thisValue = serviceEvent.getInfo().getPropertyString(thisName);
                map.put(thisName, thisValue);
            }
        }
        //Log.i(TAG, "服务信息:" + map.toString());
        return map;
    }

    public interface DeviceListCallBack {

        void callBackDevice(FindDeviceBean findDeviceBean);

        void callBackFail();

        void callBackSuccess();
    }
}

package com.siterwell.sdk.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import com.siterwell.sdk.bean.FindDeviceBean;
import com.siterwell.sdk.bean.Global;
import com.siterwell.sdk.event.CreateSocketEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ServiceDscDevUtil {
    private static final String TAG = "ServiceDscDevUtil";

    //服务实际类型
    private String type = "_hekr._udp.local.";
    private JmDNS jmdns = null;
    private MyDiscoverListener listener = null;

    private WifiManager.MulticastLock lock;
    private WifiManager wifi;
    private AtomicBoolean service_isStart = new AtomicBoolean(false);

    public ServiceDscDevUtil(Context context) {
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    //配置相关信息
    private void initListenerConfig() {
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
     * 开启发现服务
     */
    public void startSearch() {
        service_isStart.set(false);

        if (lock == null&&wifi!=null) {
            lock = wifi.createMulticastLock(getRandomString(8));
            lock.setReferenceCounted(false);
            lock.acquire();
        }

        //开启线程每三秒钟调用一次发现
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, "开始搜索局域网设备");
                    if (jmdns != null) {
                        initListenerConfig();
                    } else {
                        jmdns = JmDNS.create();
                        if (listener != null) {
                            jmdns.addServiceListener(type, listener);
                        } else {
                            listener = new MyDiscoverListener();
                            jmdns.addServiceListener(type, listener);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 主动停止发现
     */
    public void stopSearch() {
        try {
            service_isStart.set(true);
            if (jmdns != null) {
                if (listener != null) {
                    Log.i(TAG, "停止发现局域网设备");
                    jmdns.removeServiceListener(type, listener);
                    listener = null;
                }
                jmdns.unregisterAllServices();
                try {
                    jmdns.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    jmdns = null;
                }
            }
            if (lock != null && lock.isHeld()) {
                Log.i("LAN","try:释放锁");
                lock.release();
                lock=null;
            }
        } catch (Exception e) {
            Log.i("LAN","catch:释放锁");
            e.printStackTrace();
        }
    }

    private static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    class MyDiscoverListener implements ServiceListener {

        @Override
        public void serviceResolved(ServiceEvent serviceEvent) {
            // Test service info is resolved.
            //Log.i("serviceInfo","serviceInfo:"+serviceEvent.getInfo().getNiceTextString());
            //Log.i(TAG,"13:"+getNiceTextString(serviceEvent.getInfo().getTextBytes()));

            if (!TextUtils.isEmpty(new String(serviceEvent.getInfo().getTextBytes()))) {
                //将接收回的字节数组转换成以((分割的key-value
                String str = getNiceTextString(serviceEvent.getInfo().getTextBytes());

                HashMap<String, String> map;
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
                    //Log.i(TAG,"findDeviceBean:"+findDeviceBean.toString());
                }

                if (serviceEvent.getInfo() != null &&
                        !TextUtils.isEmpty(serviceEvent.getInfo().getType()) &&
                        TextUtils.equals(type, serviceEvent.getInfo().getType()) &&
                        findDeviceBean != null) {
                    //Log.i(TAG,"15:"+findDeviceBean.toString());
                    addNewDevice(findDeviceBean);
                }
            }
        }

        @Override
        public void serviceRemoved(ServiceEvent ev) {
            Log.i(TAG, "Service removed: " + ev.getName());
        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            // Test service is discovered. requestServiceInfo() will trigger
            // serviceResolved() callback.
            //Log.i(TAG, "serviceAdded: " + event.getInfo().toString());
            jmdns.requestServiceInfo(event.getType(), event.getName());
        }
    }

    private FindDeviceBean map2Bean(HashMap<String, String> map, ServiceEvent serviceEvent) {

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

        //if (!TextUtils.isEmpty(findDeviceBean.getBindKey()) && findDeviceBean.getBindKey().length() == 32) {
        if (Global.lanList != null) {
            if (!Global.lanList.isEmpty()) {
                int i = 0;
                //遍历已有list中的设备
                while (i < Global.lanList.size()) {

                    //list有这个tid并且ServiceIp没更新
                    if (TextUtils.equals(findDeviceBean.getDevTid(), Global.lanList.get(i).getDevTid()) &&
                            TextUtils.equals(findDeviceBean.getServiceIp(), Global.lanList.get(i).getServiceIp())) {
                        break;
                    }
                    //list有这个tid但是ServiceIp有更新
                    if (TextUtils.equals(findDeviceBean.getDevTid(), Global.lanList.get(i).getDevTid()) &&
                            !TextUtils.equals(findDeviceBean.getServiceIp(), Global.lanList.get(i).getServiceIp())) {
                        Global.lanList.get(i).setBindKey(findDeviceBean.getBindKey());
                        Global.lanList.get(i).setServiceIp(findDeviceBean.getServiceIp());
                        Global.lanList.get(i).setServicePort(findDeviceBean.getServicePort());
                        Global.lanList.get(i).setMid(findDeviceBean.getMid());
                        debugView("局域网发现新设备(相同的tid下新的ip)：tid:"+findDeviceBean.getDevTid()+"ip:"+findDeviceBean.getServiceIp());
                        debugView("局域网所有设备:"+Global.lanList.toString());
                        EventBus.getDefault().post(new CreateSocketEvent(findDeviceBean));
                        break;
                    }
                    //假如执行到这里那么list里面肯定是没有这个tid并且已经遍历到最后一个,那么将这个新设备添加进list中
                    if (i == Global.lanList.size() - 1) {
                        Global.lanList.add(findDeviceBean);
                        //Log.i(TAG, "Global.lanList:" + Global.lanList);
                        debugView("局域网发现新设备(新的tid)：tid:"+findDeviceBean.getDevTid()+"ip:"+findDeviceBean.getServiceIp());
                        debugView("局域网所有设备:"+Global.lanList.toString());
                        EventBus.getDefault().post(new CreateSocketEvent(findDeviceBean));
                        break;
                    }
                    i++;
                }
            } else {
                Global.lanList.add(findDeviceBean);
                //Log.i(TAG, "Global.lanList:" + Global.lanList);
                debugView("局域网发现新设备(新的tid)：tid:"+findDeviceBean.getDevTid()+"ip:"+findDeviceBean.getServiceIp());
                debugView("局域网所有设备:"+Global.lanList.toString());
                EventBus.getDefault().post(new CreateSocketEvent(findDeviceBean));
            }
        }
        //}
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
    public HashMap<String, String> getMap(String str, ServiceEvent serviceEvent) {
        HashMap<String, String> map = new HashMap<>();

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
        //Log.i(TAG,"服务信息:"+map.toString());
        return map;
    }

    /**
     * debugView界面
     */
    private void debugView(String msg) {
        ViewWindow.showView(msg);
    }
}

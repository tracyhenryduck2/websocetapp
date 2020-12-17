package com.siterwell.sdk.http;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.siterwell.sdk.http.bean.NewDeviceBean;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by TracyHenry on 2020/12/17.
 **/
public class SmartConfig {

    private static final String TAG = "SmartConfig";
    private static final int getDeviceCheckWhat = 10086;
    private static final int getDeviceSuccessWhat = 10087;
    private static final int getDeviceFailWhat = 10088;

    private AtomicBoolean isConfigOK = new AtomicBoolean(false);
    private AtomicBoolean isGetDevice = new AtomicBoolean(false);
    private AtomicBoolean isCheckDevice = new AtomicBoolean(false);

    private SmartConfigHandler smartConfigHandler;
    private NewDeviceListener newDeviceListener;

    private CopyOnWriteArrayList<NewDeviceBean> deviceList = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<NewDeviceBean> localList = new CopyOnWriteArrayList<>();

    private UserAction userAction;
    private WifiManager.MulticastLock lock = null;
    private WifiManager manager;

    private String reallyPinCode;
    private String ssid;
    private long startTime;
    private AirKissEncoder airKissEncoder;

    private Timer timer;
    private static final int TIMEOUT = 1000;

    public interface NewDeviceListener {

        //一次配网内云端接口，返回的新设备列表
        void getDeviceList(List<NewDeviceBean> newDeviceList);

        //开始配网后调用云端接口，与本地设备列表对比取出每次新增设备
        void getNewDevice(NewDeviceBean newDeviceBean);

        //一次配网内云端接口，返回的新增设备列表不为空
        void getDeviceSuccess();

        //一次配网内云端接口，返回的新增设备列表为空
        void getDeviceFail();

        void getPinCodeFail();
    }

    @SuppressLint("HandlerLeak")
    class SmartConfigHandler extends Handler {

        //private WeakReference<Context> weakReference;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //if (weakReference.get() != null) {
            switch (msg.what) {
                case getDeviceCheckWhat:
                    if (!TextUtils.isEmpty(reallyPinCode) && !TextUtils.isEmpty(ssid)) {
                        Log.i(TAG, "查询新设备");
                        userAction.getNewDevices(reallyPinCode, ssid, new SiterUser.GetNewDevicesListener() {
                            @Override
                            public void getSuccess(List<NewDeviceBean> list) {
                                if (list != null && !list.isEmpty()) {
                                    deviceList.clear();
                                    deviceList.addAll(list);
                                    if (deviceList.isEmpty()) {
                                        isGetDevice.set(false);
                                    } else {
                                        isGetDevice.set(true);
                                    }
                                    newDeviceListener.getDeviceList(deviceList);

                                    //去除list中和localList相同的部分
                                    list.removeAll(localList);
                                    long endTime = System.currentTimeMillis();
                                    long spendTime = (endTime - startTime) / 1000;
                                    for (NewDeviceBean s : list) {
                                        if (s.getBindResultCode() == 0) {
                                            Log.i(TAG, "绑定成功设备>>devTid:" + s.getDevTid() + "配网耗时：" + spendTime + "秒");
                                            debugView("绑定成功设备>>devTid:" + s.getDevTid() + "配网耗时：" + spendTime + "秒", android.R.color.holo_green_light);
                                        } else {
                                            Log.i(TAG, "绑定失败设备>>devTid:" + s.getDevTid() + "配网耗时：" + spendTime + "秒");
                                            debugView("绑定失败设备>>devTid:" + s.getDevTid() + "配网耗时：" + spendTime + "秒", android.R.color.holo_green_light);
                                        }
                                        newDeviceListener.getNewDevice(s);
                                    }
                                    localList.addAll(list);

                                }
                            }

                            @Override
                            public void getFail(int errorCode) {

                            }
                        });
                    } else {
                        stopConfig();
                    }
                    break;
                //调用getNewDevices接口有返回值
                case getDeviceSuccessWhat:
                    Log.i(TAG, "配网发送udp结束getDeviceSuccessWhat");
                    long endTime1 = System.currentTimeMillis();
                    long spendTime1 = (endTime1 - startTime) / 1000;
                    Log.i(TAG, "绑定成功>>配网发包耗时：" + spendTime1 + "秒");
                    debugView("配网发送udp结束");
                    newDeviceListener.getDeviceSuccess();
                    break;
                case getDeviceFailWhat:
                    Log.i(TAG, "配网发送udp结束getDeviceFailWhat");
                    long endTime = System.currentTimeMillis();
                    long spendTime = (endTime - startTime) / 1000;
                    Log.i(TAG, "绑定失败>>配网发包耗时：" + spendTime + "秒");
                    debugView("配网发送udp结束", android.R.color.holo_red_light);
                    newDeviceListener.getDeviceFail();
                    break;
                default:
                    break;
            }
            //} else {
            //stopConfig();
            //}
        }

        public SmartConfigHandler() {
            super();
            //weakReference = new WeakReference<>(context);
        }
    }

    public SmartConfig(Context context) {
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        userAction = UserAction.getInstance(context);
        smartConfigHandler = new SmartConfigHandler();
    }

    private void initConfig(String ssid) {
        this.ssid = ssid;
        deviceList.clear();
        localList.clear();
        isConfigOK.set(false);
        isGetDevice.set(false);
        isCheckDevice.set(false);
        Log.i(TAG, "开始配网");
        debugView("开始配网");
        startTime = System.currentTimeMillis();
    }

    /**
     * 开始配网
     *
     * @param ssid     wifi名称
     * @param password wifi密码
     * @param number   单次配网总时间
     */
    @Deprecated
    public void startConfig(final String ssid, final String password, final int number) {
        initConfig(ssid);

        userAction.getPinCode(ssid, new SiterUser.GetPinCodeListener() {
            @Override
            public void getSuccess(final String pinCode) {
                reallyPinCode = pinCode;
                Log.i(TAG, "成功获取pinCode:" + reallyPinCode);
                debugView("成功获取pinCode:" + reallyPinCode);

                if (!TextUtils.isEmpty(reallyPinCode)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendMsgMain(ssid, password, reallyPinCode, number);
                        }
                    }).start();
                } else {
                    newDeviceListener.getPinCodeFail();
                }
            }

            @Override
            public void getFail(int errorCode) {
                newDeviceListener.getPinCodeFail();
            }

            @Override
            public void getFailInSuccess() {
                newDeviceListener.getPinCodeFail();
            }
        });
    }

    /**
     * 开始配网
     *
     * @param ssid              wifi名称
     * @param password          wifi密码
     * @param number            单次配网总时间
     * @param newDeviceListener 配网回调
     */
    public void startConfig(final String ssid, final String password, final int number, final NewDeviceListener newDeviceListener) {
        this.newDeviceListener = newDeviceListener;
        startConfig(ssid, password, number);
    }

    public void stopConfig() {
        isConfigOK.set(true);
        //cancelCallBack();
        //BaseHttpUtil.getClient().cancelAllRequests(true);
    }

    public void stopFindDevice() {
        isCheckDevice.set(true);
        //BaseHttpUtil.getClient().cancelAllRequests(true);
    }

    private void sendMsgMain(final String ssid, final String password, final String pinCode, int number) {

        debugView("开始发送ssid:" + ssid + ">>>password:" + password + ">>>pinCode:" + pinCode);
        airKissEncoder = new AirKissEncoder(ssid, password, pinCode);
        //isConfigOK.set(false);
        //isCheckDevice.set(false);
        startCheckDevice();
        // 发送ssid和pass,pinCode
        //假如该线程不开则下面的count计时代码将无法进入
        new Thread() {
            public void run() {
                //byte[] data = (ssid + '\0' + password + '\0' + pinCode + '\0').getBytes();
                byte[] data = (ssid + '\0' + password + '\0').getBytes();
                long beginTime = System.currentTimeMillis();
                long passTime = getPassTime(beginTime);
                while (!isConfigOK.get()) {
                    if (lock == null) {
                        lock = manager.createMulticastLock("localWifi");
                        lock.setReferenceCounted(true);
                        lock.acquire();
                    }
                    long sleepTime;
                    if (passTime > 1000) {
                        sleepTime = getSleepTime(passTime, data.length);
                    } else {
                        sleepTime = getSleepTime(passTime, data.length + 1);
                    }
                    Log.i(TAG, "sleepTime:" + sleepTime);
                    try {
                        sendMsgToDevice(ssid + "", password + "", pinCode + "", (int) sleepTime);
                        Thread.sleep(sleepTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    passTime = getPassTime(beginTime);
                }
            }
        }.start();

        int count = 0;
        try {
            // number*1000毫秒是number分钟，这是配置的总体超时时间
            // 在number分钟内，不断去判断配置是否成功
            while ((!isConfigOK.get()) && count < number) {
                // 每次判断之后，主线程休眠1000毫秒
                Thread.sleep(1000);
                count++;
            }
            stopConfig();

            Message message = Message.obtain();

            if (!isGetDevice.get()) {
                message.what = getDeviceFailWhat;
            } else {
                message.what = getDeviceSuccessWhat;
            }

            smartConfigHandler.sendMessage(message);
            //quitCheckDevice();
            if (lock != null && lock.isHeld()) {
                lock.release();
                lock = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMsgToDevice(String ssid, String password, String pinCode, int time) throws IOException {
        // 创建用来发送数据报包的套接字
        Log.i(TAG, "发送ssid:" + ssid + ">>>password:" + password + ">>>pinCode:" + pinCode);
        Log.i(TAG, "开始发包");
        //debugView("开始发包");
        DatagramSocket ds = new DatagramSocket();

        byte[] data = (ssid + '\n' + password + '\n' + pinCode).getBytes("utf-8");
        int len = data.length;
        int encoded_data[] = airKissEncoder.getEncodedData();

        DatagramPacket dp;

        try {
            Log.i(TAG, "encoded_data.length *(data.length+2):" + encoded_data.length * (data.length + 2));
            for (int i = 0; i < encoded_data.length * (data.length + 2); i++) {
                if (isConfigOK.get()) {
                    //Log.i(TAG,"isConfigOK-------");
                    //Log.i(TAG, "isConfigOK--发包停止");
                    //debugView("isConfigOK--发包停止");
                    break;
                }

                int m = i % encoded_data.length;
                //Log.i(TAG, "m:" + m);
                int n = i % (data.length + 2);
                //Log.i(TAG, "n:" + n);
                //Log.i(TAG, "encoded_data[m]:" + encoded_data[m]);
                byte[] c = new byte[encoded_data[m]];
                for (int j = 0; j < encoded_data[m]; j++) {
                    c[j] = 1;
                }

                //Log.i("11111111", encoded_data[m] + "");

                String ip = (n == 0 || n == len + 1) ? "224.127." + len + ".255" : "224." + (n - 1) + "." + unsignedByteToInt(data[n - 1]) + ".255";

                dp = new DatagramPacket(c, encoded_data[m],
                        InetAddress.getByName(ip),
                        7001);

                ds.send(dp);
                //if (time > 0) {
                if (time > 4) {
                    Thread.sleep(time);
                }else{
                    Thread.sleep(4);
                }
                /*}else{
                    Thread.sleep(3);
                }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.i(TAG, "单次发包结束");
            //debugView("单次发包结束");
            ds.close();
        }
    }

    /**
     * 获取发送经历的时间
     */
    private long getPassTime(long beginTime) {
        return System.currentTimeMillis() - beginTime;
    }

    /**
     * 获取需要等待的时间，该变量变化不大，有时间可以多测试，写一个更好的公式
     */
    private long getSleepTime(long passTime, int length) {
        long param = passTime / 1000 - 3 > 0 ? passTime / 1000 - 3 : 0;
        //Log.i(TAG, "休眠时间:" + 100 / length * (1 + param / 6));
        return 100 / length * (1 + param / 6);
    }

    @Deprecated
    public void setNewDeviceListener(NewDeviceListener newDeviceListener) {
        this.newDeviceListener = newDeviceListener;
    }

    private void startCheckDevice() {
        quitCheckDevice();

        if (timer == null) {
            timer = new Timer();
        }

        PingTask pingTask = new PingTask();

        if (timer != null) {
            timer.schedule(pingTask, 0, TIMEOUT);
        }
    }

    private void quitCheckDevice() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    //心跳包发送
    private class PingTask extends TimerTask {
        @Override
        public void run() {
            if (!isCheckDevice.get()) {
                Message message = Message.obtain();
                message.what = getDeviceCheckWhat;
                smartConfigHandler.sendMessage(message);
            } else {
                Log.i(TAG, "停止查询设备");
                quitCheckDevice();
            }
        }
    }

    private int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    private void debugView(String content, int colorResId) {
        //ViewWindow.showView(content, colorResId);
    }

    private void debugView(String content) {
        //ViewWindow.showView(content);
    }

}

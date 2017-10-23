package com.siterwell.sdk.util;

import android.content.Context;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/*
@class HekrConfig
@autor Administrator
@time 2017/10/16 14:13
@email xuejunju_4595@qq.com
*/
public class HekrConfig {

    /**
     * 原子变量，决定是否需要继续发送ssid和password
     */
    private AtomicBoolean isSsidAndPassOK = new AtomicBoolean(false);

    private WifiManager.MulticastLock lock = null;
    private WifiManager manager;

    public HekrConfig(Context context) {
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * 获取发送经历的时间
     *
     * @param beginTime 开始时间
     */
    private long getPassTime(long beginTime) {
        return System.currentTimeMillis() - beginTime;
    }

    /**
     * 获取需要等待的时间，该变量变化不大，有时间可以多测试，写一个更好的公式
     *
     * @param passTime
     * @param length
     */
    private long getSleepTime(long passTime, int length) {
        long param = passTime / 1000 - 3 > 0 ? passTime / 1000 - 3 : 0;
        return 100 / length * (1 + param / 6);
    }

    /**
     * 具体执行配置的过程，启动两个线程，一个线程用于发送ssid和pass
     *
     * @param ssid wifi ssid
     * @param password wifi pwd
     */
    public void config(final String ssid, final String password,int number) {

        isSsidAndPassOK.set(false);
        // 发送ssid和pass的线程
        new Thread() {
            public void run() {
                byte[] data = (ssid + '\0' + password + '\0').getBytes();
                long beginTime = System.currentTimeMillis();
                long passTime = getPassTime(beginTime);
                while (!isSsidAndPassOK.get()) {
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
                    try {
                        hekrConfig(ssid + "", password + "", (int) sleepTime);
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
            while ((!isSsidAndPassOK.get()) && count < number) {
                // 每次判断之后，主线程休眠1000毫秒
                Thread.sleep(1000);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isSsidAndPassOK.set(true);
        if (lock != null&&lock.isHeld()) {
            lock.release();
            lock=null;
        }
    }

    public void config(final String ssid, final String password,final String pinCode,int number) {

        isSsidAndPassOK.set(false);
        // 发送ssid和pass的线程
        new Thread() {
            public void run() {
                byte[] data = (ssid + '\0' + password + '\0'+pinCode+'\0').getBytes();
                long beginTime = System.currentTimeMillis();
                long passTime = getPassTime(beginTime);
                while (!isSsidAndPassOK.get()) {
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
                    try {
                        hekrConfig(ssid + "", password + "", pinCode+"",(int) sleepTime);
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
            while ((!isSsidAndPassOK.get()) && count < number) {
                // 每次判断之后，主线程休眠1000毫秒
                Thread.sleep(1000);
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        isSsidAndPassOK.set(true);
        if (lock != null&&lock.isHeld()) {
            lock.release();
            lock=null;
        }
    }

    /**
     *
     * @param ssid wifi ssid
     * @param password wifi pwd
     * @param time
     *            每次发送之后休眠的时间，以免发送速度过快，路由器承受不住
     * @throws IOException
     */

    public void hekrConfig(String ssid, String password, int time) throws IOException {
        // 创建用来发送数据报包的套接字
        DatagramSocket ds = new DatagramSocket();
        byte[] ssidbs = ssid.getBytes("utf-8");
        byte[] passbs = password.getBytes("utf-8");
        int len = ssidbs.length + passbs.length + 2;
        byte[] d = "hekrconfig".getBytes("utf-8");
        DatagramPacket dp;
        dp = new DatagramPacket(d, d.length, InetAddress.getByName("224.127." + len + ".255"),
                7001);
        ds.send(dp);
        ds.close();

        byte[] data = (ssid + '\0' + password + '\0').getBytes("utf-8");

        for (int i = 0; i < data.length; i++) {
            ds = new DatagramSocket();
            dp = new DatagramPacket(d, d.length,
                    InetAddress.getByName("224." + i + "." + unsignedByteToInt(data[i]) + ".255"),
                    7001);
            ds.send(dp);
            ds.close();
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void hekrConfig(String ssid, String password, String pinCode,int time) throws IOException {
        // 创建用来发送数据报包的套接字
        DatagramSocket ds = new DatagramSocket();
        byte[] ssidBs = ssid.getBytes("utf-8");
        byte[] passBs = password.getBytes("utf-8");
        byte[] pinCodeBs = pinCode.getBytes("utf-8");

        int len = ssidBs.length + passBs.length + pinCodeBs.length + 2;
        byte[] d = "hekrconfig".getBytes("utf-8");
        DatagramPacket dp;
        dp = new DatagramPacket(d, d.length, InetAddress.getByName("224.127." + len + ".255"),
                7001);
        //Log.i("111",dp.getData().toString());
        ds.send(dp);
        ds.close();

        byte[] data = (ssid + '\n' + password + '\n'+ pinCode).getBytes("utf-8");

        //Log.i("111",dp.getData().toString());

        for (int i = 0; i < data.length; i++) {
            ds = new DatagramSocket();
            dp = new DatagramPacket(d, d.length,
                    InetAddress.getByName("224." + i + "." + unsignedByteToInt(data[i]) + ".255"),
                    7001);
            ds.send(dp);
            ds.close();
            if (time > 0) {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int unsignedByteToInt(byte b) {
        return (int) b & 0xFF;
    }

    public void stop() {
        isSsidAndPassOK.set(true);
    }

}

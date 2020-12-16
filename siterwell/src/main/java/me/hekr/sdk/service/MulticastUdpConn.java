package me.hekr.sdk.service;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import me.hekr.sdk.HekrSDK;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */

class MulticastUdpConn implements IAsyncConn {

    private static final String TAG = MulticastUdpConn.class.getSimpleName();

    private static final String MULTICAST_ADDRESS = "224.0.0.207";
    // A port number of 0 will let the system pick up an ephemeral port
    private static final int PORT_LOCAL = 24254;

    private static final int MAXIMUM_PACKET_BYTES = 102400;

    private static MulticastUdpConn multicastUdpConn = new MulticastUdpConn();

    private MulticastThread mThread;
    private WifiManager.MulticastLock mMulticastLock;
    private volatile boolean isRunning;

    static MulticastUdpConn getMulticast() {
        return multicastUdpConn;
    }

    private MulticastUdpConn() {

    }

    public synchronized void start(Context context) {
        requireMulticastLock(context);
        start();
    }

    @Override
    public synchronized void start() {
        if (isRunning) {
            LogUtil.d(TAG, "The MulticastUdpConn is running, no need to restart");
            return;
        }
        int perm = HekrSDK.getContext().checkCallingOrSelfPermission("android.permission.INTERNET");
        boolean has_perssion = perm == PackageManager.PERMISSION_GRANTED;
        if (!has_perssion) {
            LogUtil.e(TAG, "Has no permission:android.permission.INTERNET");
            releaseMulticastLock();
            return;
        }
        isRunning = true;
        mThread = new MulticastThread();
        mThread.start();
    }

    @Override
    public synchronized void stop() {
        isRunning = false;
        releaseMulticastLock();
        if (mThread != null) {
            mThread.stopMulticast();
            mThread = null;
        }
    }

    @Override
    public synchronized void send(String message) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            LogUtil.d(TAG, "The  channel is on, send message: " + message);
            try {
                byte[] data = message.getBytes();
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        InetAddress.getByName(MULTICAST_ADDRESS), PORT_LOCAL);
                mThread.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LogUtil.d(TAG, "The udp channel is off, can not send message...");
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized boolean isActive() {
        return mThread != null && mThread.isActive();
    }

    @Override
    public synchronized void reset(ConnOptions options) {
        Log.d(TAG, "Reset do nothing except stopping");
        stop();
    }

    private void requireMulticastLock(Context context) {
        if (mMulticastLock == null) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null) {
                mMulticastLock = wifiManager.createMulticastLock(MulticastUdpConn.class.getSimpleName());
            }
        }
        if (!mMulticastLock.isHeld()) {
            mMulticastLock.acquire();
        }
    }

    private void releaseMulticastLock() {
        if (mMulticastLock != null && mMulticastLock.isHeld()) {
            mMulticastLock.release();
        }
    }

    private class MulticastThread extends Thread {

        private MulticastSocket mSocket;
        private boolean mStop;

        MulticastThread() {
            mStop = false;
        }

        @Override
        public void run() {
            try {
                while (!mStop && !isInterrupted()) {
                    if (!NetworkUtil.isConnected(HekrSDK.getContext())) {
                        LogUtil.e(TAG, "Has no net, delays for 1000ms");
                        Thread.sleep(1000);
                        continue;
                    }
                    try {
                        LogUtil.d(TAG, "Multi Conn start, ip is: " + MULTICAST_ADDRESS + ", port is: " + PORT_LOCAL);
                        InetAddress address = InetAddress.getByName(MULTICAST_ADDRESS);
                        mSocket = new MulticastSocket(PORT_LOCAL);
                        mSocket.setReuseAddress(true);
                        mSocket.setTimeToLive(255);
                        mSocket.joinGroup(address);
                        while (!mStop) {
                            DatagramPacket packet = new DatagramPacket(new byte[MAXIMUM_PACKET_BYTES], MAXIMUM_PACKET_BYTES);
                            try {
                                Log.d(TAG, "Begin receiving......");
                                mSocket.receive(packet);
                                byte[] data = packet.getData();
                                int length = packet.getLength();
                                byte[] bytes = new byte[length];
                                System.arraycopy(data, 0, bytes, 0, length);
                                String message = new String(bytes);
                                String ip = packet.getAddress().getHostAddress();
                                int port = packet.getPort();
                                LogUtil.d(TAG, "The channel is on, receive message from " + ip + ":" + PORT_LOCAL + ": " + message);
                                Object json = new JSONTokener(message).nextValue();
                                if (json instanceof JSONObject) {
                                    JSONObject object = new JSONObject(message);
                                    if (!object.has("ip")) {
                                        object.put("ip", ip);
                                    }
                                    ServiceMonitor.getInstance().notifyMessageArrived(object.toString()
                                            , TextUtils.concat(ip, ":", String.valueOf(port)).toString());
                                } else if (json instanceof JSONArray) {
                                    ServiceMonitor.getInstance().notifyMessageArrived(message
                                            , TextUtils.concat(ip, ":", String.valueOf(port)).toString());
                                }
                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (mSocket != null) {
                            try {
                                mSocket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void stopMulticast() {
            mStop = true;
            if (mSocket != null) {
                mSocket.close();
                mSocket = null;
            }
            interrupt();
        }

        private void send(DatagramPacket packet) throws IOException {
            if (mSocket != null) {
                mSocket.send(packet);
            }
        }

        private boolean isActive() {
            return mSocket != null && !mSocket.isClosed();
        }
    }
}

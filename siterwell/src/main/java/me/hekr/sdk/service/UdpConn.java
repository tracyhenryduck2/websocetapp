package me.hekr.sdk.service;

import me.hekr.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接接口的实现，UDP连接
 * <p>
 * 网络状态变化时不进行任何处理
 * 由于UDP连接在网络状态变化时不会断开连接，所以对UDP来说，只要不主动关闭，通道就不会断掉
 * 所以这里就不进行任何的处理
 * 但是通过UDP 连接设备时必须通过心跳来确认对方是否仍然能跟我们通信
 * 如果不能，那么只能重新发现设备的IP
 */

class UdpConn implements IAsyncConn {

    private static final String TAG = UdpConn.class.getSimpleName();

    private ConnOptions mOptions;
    private String mHandler;
    private volatile boolean isRunning = false;

    UdpConn(ConnOptions options, String handler) {
        this.mOptions = new ConnOptions(options.getconnType(), options.getIpOrUrl(), options.getPort());
        this.mHandler = handler;
    }

    @Override
    public synchronized void start() {
        if (isRunning) {
            LogUtil.d(TAG, "The UdpConn is running, no need to restart");
            return;
        }
        isRunning = true;
        String ip = mOptions.getIpOrUrl();
        int port = mOptions.getPort();
        LogUtil.d(TAG, "Conn start, ip is: " + ip + ", port is: " + port);
        if (CommonUdpConn.getCommon().register(mHandler, ip, port)) {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_SUCCESS);
        } else {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_FAIL);
        }
    }

    @Override
    public synchronized void stop() {
        isRunning = false;
        CommonUdpConn.getCommon().unregister(mHandler);
    }

    @Override
    public synchronized void send(String message) {
        CommonUdpConn.getCommon().send(message, mHandler);
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized boolean isActive() {
        return CommonUdpConn.getCommon().isActive();
    }

    @Override
    public synchronized void reset(ConnOptions options) {
        stop();
        this.mOptions = new ConnOptions(options.getPrefix(), options.getconnType(), options.getIpOrUrl(), options.getPort());
    }
}

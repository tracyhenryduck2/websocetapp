package me.siter.sdk.connection;

import me.siter.sdk.FilterType;
import me.siter.sdk.IMessageRequest;
import me.siter.sdk.dispatcher.Dispatcher;
import me.siter.sdk.service.ConnOptions;
import me.siter.sdk.service.ConnStatusType;
import me.siter.sdk.service.HekrConnectionService;
import me.siter.sdk.service.IConnObserver;
import me.siter.sdk.service.ServiceBinder;
import me.siter.sdk.service.ServiceMonitor;
import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 与设备的连接
 */

public class DeviceConnection implements IConnection, IConnObserver {

    private static final String TAG = DeviceConnection.class.getSimpleName();

    private static final long DEVICE_EXPIRED_TIME = 2000L;

    private ConnOptions mConnOptions;
    private String mHandler;
    private volatile boolean isConnected = false;
    private volatile boolean isClosed = true;
    private ConnectionStatusListener mListener;


    public DeviceConnection() {

    }

    @Override
    public synchronized void bind(ConnOptions options) {
        if (mConnOptions != null) {
            throw new IllegalStateException("You should close the connection first");
        }
        this.mConnOptions = new ConnOptions(options.getPrefix(), options.getconnType(), options.getIpOrUrl(), options.getPort());
        isClosed = false;
    }

    @Override
    public synchronized void connect() {
        tryConnect();
    }

    @Override
    public synchronized void disconnect() {
        tryDisconnect();
    }

    @Override
    public synchronized void close() {
        if (isConnected) {
            disconnect();
        }
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service != null && service.deviceConnExist(mHandler)) {
            service.destroyDeviceConn(mHandler);
        }
        mConnOptions = null;
        isConnected = false;
        isClosed = true;
    }

    @Override
    public synchronized boolean isClosed() {
        return isClosed;
    }

    @Override
    public synchronized void send(IMessageRequest request) {
        if (isClosed() || !isConnected()) {
            LogUtil.e(TAG, "The DeviceConnection is closed or not connected");
            return;
        }
        request.setChannel(IMessageRequest.CHANNEL_DEVICE);
        request.setHandler(mHandler);
        Dispatcher.getInstance().enqueue(request, FilterType.FILTER_ONCE, DEVICE_EXPIRED_TIME);
    }

    @Override
    public synchronized boolean isConnected() {
        return isConnected;
    }

    @Override
    public void setConnectionStatusListener(ConnectionStatusListener listener) {
        this.mListener = listener;
    }

    private void tryConnect() {
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service == null) {
            ServiceBinder.getInstance().addListener(new ServiceBinder.ConnectServiceListener() {
                @Override
                public void onServiceConnected() {
                    ServiceBinder.getInstance().removeListener(this);
                    getAndConnect();
                }

                @Override
                public void onServiceDisconnected() {

                }
            });
            ServiceBinder.getInstance().connect();
        } else {
            getAndConnect();
        }
    }

    private synchronized void tryDisconnect() {
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        if (service != null && service.deviceConnExist(mHandler)) {
            service.disconnectDevice(mHandler);
        }
        isConnected = false;
        ServiceMonitor.getInstance().unregisterConnObserver(mHandler);
    }

    private synchronized void getAndConnect() {
        if (mConnOptions == null) {
            throw new IllegalStateException("You should bind the connection first");
        }
        HekrConnectionService service = ServiceBinder.getInstance().getService();
        mHandler = service.createDeviceConn(mConnOptions);
        // 所有的连接都保持在service中，service外最好不要拿到连接的实例
        ServiceMonitor.getInstance().registerConnObserver(mHandler, this);
        service.connectDevice(mHandler);
    }

    @Override
    public synchronized void onConnChanged(ConnStatusType status) {
        switch (status) {
            case CONN_STATUS_SUCCESS:
                isConnected = true;
                if (mListener != null) {
                    mListener.onSuccess();
                }
                break;
            case CONN_STATUS_FAIL:
                isConnected = false;
                if (mListener != null) {
                    mListener.onFail();
                }
                break;
            case CONN_STATUS_CONNECTED:
                isConnected = true;
                if (mListener != null) {
                    mListener.onConnected();
                }
                break;
            case CONN_STATUS_DISCONNECTED:
                isConnected = false;
                if (mListener != null) {
                    mListener.onDisconnected();
                }
                break;
            case CONN_STATUS_ERROR:
                isConnected = false;
                if (mListener != null) {
                    mListener.onError();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public synchronized void onError(ConnStatusType errorCode, Throwable throwable) {
        LogUtil.e(TAG, "Connection has an inner error: " + errorCode);
        isConnected = false;
    }
}

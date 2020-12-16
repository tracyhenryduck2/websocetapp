package me.siter.sdk.service;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.StatusLine;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import me.siter.sdk.HekrSDK;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.NetworkUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接接口的实现，WebSocket连接
 */

class WebSocketLiteConn implements IAsyncConn {

    private static final String TAG = WebSocketLiteConn.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 15000;

    private ConnOptions mOptions;

    private String mHandler;
    private volatile boolean isRunning = false;
    private WebsocketThread mCurrentThread;

    WebSocketLiteConn(ConnOptions options, String handler) {
        this.mOptions = new ConnOptions(options.getconnType(), options.getIpOrUrl(), options.getPort());
        this.mHandler = handler;
    }

    @Override
    public synchronized void start() {
        if (isRunning) {
            LogUtil.d(TAG, "The WebSocketLiteConn is running, no need to restart");
            return;
        }
        if (!NetworkUtil.isConnected(HekrSDK.getContext())) {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
            return;
        }
        int perm = HekrSDK.getContext().checkCallingOrSelfPermission("android.permission.INTERNET");
        boolean has_perssion = perm == PackageManager.PERMISSION_GRANTED;
        if (!has_perssion) {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
            return;
        }
        isRunning = true;
        mCurrentThread = new WebsocketThread();
        mCurrentThread.start();
    }

    @Override
    public synchronized void send(String message) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            LogUtil.d(TAG, "The websocket channel is on, send message: " + message);
            mCurrentThread.send(message);
        } else {
            LogUtil.d(TAG, "The websocket channel is off, can not send message...");
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void stop() {
        isRunning = false;
        if (mCurrentThread != null) {
            mCurrentThread.stopWebsocket();
            mCurrentThread = null;
        }
    }

    @Override
    public synchronized boolean isActive() {
        return mCurrentThread != null && mCurrentThread.isActive();
    }

    @Override
    public synchronized void reset(ConnOptions options) {
        stop();
        this.mOptions = new ConnOptions(options.getPrefix(), options.getconnType(), options.getIpOrUrl(), options.getPort());
    }

    private class WebSocketListener extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocket conn connected!");
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_SUCCESS);
            }
        }

        @Override
        public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
            super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocket conn disconnected!");
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_DISCONNECTED);
            }
        }

        @Override
        public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onCloseFrame(websocket, frame);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocket conn received closing");
            }
        }

        @Override
        public void onTextMessage(WebSocket websocket, String text) throws Exception {
            super.onTextMessage(websocket, text);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocket conn received message: " + text);
                String handler = null;
                if (mOptions != null) {
                    handler = mOptions.getIpOrUrl();
                }
                ServiceMonitor.getInstance().notifyMessageArrived(text, handler);
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onUnexpectedError(websocket, cause);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.e(TAG, "UnexpectedError");
                cause.printStackTrace();
                ServiceMonitor.getInstance().notifyConnError(mHandler, ConnStatusType.CONN_STATUS_ERROR, cause);
            }

            websocket.disconnect();
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocket conn received pong");
            }
        }

        @Override
        public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
            super.onStateChanged(websocket, newState);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.d(TAG, "WebSocketState: " + newState.toString());
                if (newState == WebSocketState.OPEN) {
                    ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_CONNECTED);
                }
            }
        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
            super.onError(websocket, cause);
            if (mCurrentThread != null && websocket == mCurrentThread.getWebsocket()) {
                LogUtil.e(TAG, "Error");
                cause.printStackTrace();
            }
        }
    }

    private class WebsocketThread extends Thread {

        private WebSocket websocket;

        @Override
        public void run() {
            try {
                if (!isRunning) {
                    return;
                }
                ConnOptions options = mOptions;
                if (options == null) {
                    return;
                }
                String url = options.getIpOrUrl();
                LogUtil.d(TAG, "Conn start, url is: " + url);
                URI uri = new URI(url);
                String scheme = uri.getScheme();
                if (scheme == null) {
                    throw new IllegalArgumentException("Please check your scheme of the url, which is needed");
                }
                final String host = uri.getHost();
                if (host == null) {
                    throw new IllegalArgumentException("Please check your host of the url, which is needed");
                }
                final int port;
                if (uri.getPort() == -1) {
                    if (options.getPort() > 63335 || options.getPort() < 0) {
                        throw new IllegalArgumentException("Please check your port of the url, which is needed");
                    }
                    port = options.getPort();
                    uri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), port, uri.getPath(), uri.getQuery(), uri.getFragment());
                }
                if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                    throw new IllegalArgumentException("Illegal agreement,either ws or wss");
                }
                final boolean ssl = "wss".equalsIgnoreCase(scheme);
                final WebSocketFactory factory = new WebSocketFactory();
                if (ssl) {
                    LogUtil.d(TAG, "Websocket wss connection");
                } else {
                    LogUtil.d(TAG, "WebSocket ws connection");
                }
                factory.setConnectionTimeout(CONNECTION_TIMEOUT);

                try {
                    websocket = factory.createSocket(uri);
                    websocket.addListener(new WebSocketListener());
                    websocket.connect();
                } catch (OpeningHandshakeException e) {
                    ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
                    // Status line.
                    StatusLine sl = e.getStatusLine();
                    LogUtil.d(TAG, "=== Status Line ===");
                    LogUtil.d(TAG, "HTTP Version  = " + sl.getHttpVersion());
                    LogUtil.d(TAG, "Status Code   = " + sl.getStatusCode());
                    LogUtil.d(TAG, "Reason Phrase = " + sl.getReasonPhrase());

                    // HTTP headers.
                    Map<String, List<String>> headers = e.getHeaders();
                    LogUtil.d(TAG, "=== HTTP Headers ===");
                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        // Header name.
                        String name = entry.getKey();

                        // Values of the header.
                        List<String> values = entry.getValue();

                        if (values == null || values.size() == 0) {
                            // Print the name only.
                            LogUtil.d(TAG, name);
                            continue;
                        }

                        for (String value : values) {
                            // Print the name and the value.
                            LogUtil.d(TAG, name + ": " + value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        private void send(String message) {
            if (websocket != null) {
                websocket.sendText(message);
            }
        }

        private boolean isActive() {
            return websocket != null && websocket.isOpen();
        }

        private void stopWebsocket() {
            if (websocket != null) {
                websocket.disconnect();
                websocket = null;
            }
            interrupt();
        }

        private WebSocket getWebsocket() {
            return websocket;
        }
    }
}

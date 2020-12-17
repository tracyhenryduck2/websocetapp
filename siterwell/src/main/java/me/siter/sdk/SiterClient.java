package me.siter.sdk;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.siter.sdk.dispatcher.Dispatcher;
import me.siter.sdk.dispatcher.IMessageFilter;
import me.siter.sdk.inter.SiterClientListener;
import me.siter.sdk.inter.SiterMsgCallback;
import me.siter.sdk.utils.AndroidErrorMap;
import me.siter.sdk.utils.CacheUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public class SiterClient implements ISIterClient {

    private static final String TAG = SiterClient.class.getSimpleName();

    private Map<String, SiterCloudClient> mClients;


    SiterClient() {
        mClients = new HashMap<>();
    }

    public synchronized void connect() {
        disconnect();
        setDefaultHostUrl();
    }

    @Override
    public synchronized void disconnect() {
        for (SiterCloudClient client : mClients.values()) {
            client.disconnect();
            client.destroy();
        }
        mClients.clear();
    }

    @Override
    public synchronized void setHosts(Set<String> hosts) {
        if (hosts == null) {
            return;
        }
        Set<String> set = new HashSet<>();
        for (String host : hosts) {
            String url;
            if (Constants.isTestSite() && !host.startsWith("test.")) {
                url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, host);
            } else {
                url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
            }
            set.add(url);
        }
        connectHosts(set);
        CacheUtil.setCloudUrls(set);
    }

    @Override
    public synchronized void clearHosts() {
        for (SiterCloudClient client : mClients.values()) {
            client.disconnect();
            client.destroy();
        }
        mClients.clear();
        CacheUtil.setCloudUrls(null);
    }

    @Override
    public synchronized void sendMessage(JSONObject message, SiterMsgCallback callback) {
        tryToSend(message, callback, getDefaultHostUrl());
    }

    @Deprecated
    @Override
    public synchronized void sendMessage(String devTid, JSONObject message, SiterMsgCallback callback) {
        tryToSend(message, callback, getDefaultHostUrl());
    }

    @Override
    public synchronized void sendMessage(JSONObject message, SiterMsgCallback callback, CloudHostType host) {
        String hostString = host.toString();
        String url;
        if (Constants.isTestSite() && !hostString.startsWith("test.")) {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, hostString);
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, hostString);
        }
        tryToSend(message, callback, url);
    }

    @Override
    public synchronized void sendMessage(JSONObject message, SiterMsgCallback callback, String host) {
        String url;
        if (Constants.isTestSite() && !host.startsWith("test.")) {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, host);
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
        }
        tryToSend(message, callback, url);
    }

    @Override
    public synchronized void receiveMessage(IMessageFilter filter, SiterMsgCallback callback) {
        tryToReceive(filter, callback, FilterType.FILTER_PERMANANT, 0L);
    }

    @Override
    public synchronized void receiveMessage(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expired) {
        tryToReceive(filter, callback, type, expired);
    }

    @Override
    public synchronized void deceiveMessage(IMessageFilter filter) {
        Dispatcher.getInstance().removeFilter(filter);
    }

    @Override
    public synchronized boolean isOnline() {
        for (SiterCloudClient client : mClients.values()) {
            if (client.isOnline()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized boolean isOnline(String host) {
        String url;
        if (Constants.isTestSite() && !host.startsWith("test.")) {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, host);
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
        }
        return mClients.containsKey(url) && mClients.get(url).isOnline();
    }

    @Override
    public synchronized void addSiterClientListener(SiterClientListener listener) {
        addSiterClientListener(listener, "");
    }

    @Override
    public synchronized void removeSiterClientListener(SiterClientListener listener) {
        removeSiterClientListener(listener, "");
    }

    @Override
    public synchronized void addSiterClientListener(SiterClientListener listener, String host) {
        String url;
        if (TextUtils.isEmpty(host)) {
            url = getDefaultHostUrl();
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
        }
        SiterCloudClient client = mClients.get(url);
        if (client != null) {
            client.addSiterclientListener(listener);
        }
    }

    @Override
    public synchronized void removeSiterClientListener(SiterClientListener listener, String host) {
        String url;
        if (TextUtils.isEmpty(host)) {
            url = getDefaultHostUrl();
        } else {
            if (Constants.isTestSite() && !host.startsWith("test.")) {
                url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, host);
            } else {
                url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
            }
        }
        SiterCloudClient client = mClients.get(url);
        if (client != null) {
            client.removeSiterClientListener(listener);
        }
    }

    @Override
    public List<CloudChannelStatus> getHostsStatus() {
        List<CloudChannelStatus> list = new ArrayList<>();
        for (Map.Entry<String, SiterCloudClient> entry : mClients.entrySet()) {
            SiterCloudClient client = entry.getValue();
            ChannelStatusType type;
            if (client.isOnline()) {
                type = ChannelStatusType.ONLINE;
            } else if (client.isConnecting()) {
                type = ChannelStatusType.CONNECTING;
            } else {
                type = ChannelStatusType.OFFLINE;
            }
            list.add(new CloudChannelStatus(entry.getKey(), type));
        }
        return list;
    }

    private void tryToSend(JSONObject message, SiterMsgCallback callback, String url) {
        SiterCloudClient client = mClients.get(url);
        if (client == null) {
            callback.onError(AndroidErrorMap.ERROR_MESSAGE_NO_CONNECTION, "No default connection");
        } else {
            client.sendMessage(message, callback);
        }
    }

    private void connectHosts(Set<String> urls) {
        Set<String> excludes = new HashSet<>();
        excludes.addAll(new ArrayList<>(mClients.keySet()));
        excludes.removeAll(urls);

        for (String url : excludes) {
            mClients.get(url).disconnect();
            mClients.get(url).destroy();
            mClients.remove(url);
        }
        for (String url : urls) {
            if (!mClients.containsKey(url)) {
                SiterCloudClient client = new SiterCloudClient();
                client.connect(url);
                mClients.put(url, client);
            }
        }
    }

    private void setDefaultHostUrl() {
        if (!mClients.containsKey(getDefaultHostUrl())) {
            SiterCloudClient client = new SiterCloudClient();
            client.connect(getDefaultHostUrl());
            mClients.put(getDefaultHostUrl(), client);
        }
    }

    private void tryToReceive(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expired) {
        if (type == FilterType.FILTER_PERMANANT) {
            Dispatcher.getInstance().addFilter(filter, callback);
        } else {
            Dispatcher.getInstance().addFilter(filter, callback, type, expired);
        }
    }

    private String getDefaultHostUrl() {
        if (Constants.isTestSite()) {
            return String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, CloudHostType.HOST_TEST_DEFAULT.toString());
        } else {
            return String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, CloudHostType.HOST_DEFAULT.toString());
        }
    }
}

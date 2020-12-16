package me.hekr.sdk;

import android.text.TextUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.hekr.sdk.dispatcher.Dispatcher;
import me.hekr.sdk.dispatcher.IMessageFilter;
import me.hekr.sdk.inter.HekrClientListener;
import me.hekr.sdk.inter.HekrMsgCallback;
import me.hekr.sdk.utils.AndroidErrorMap;
import me.hekr.sdk.utils.CacheUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public class HekrClient implements IHekrClient {

    private static final String TAG = HekrClient.class.getSimpleName();

    private Map<String, HekrCloudClient> mClients;


    HekrClient() {
        mClients = new HashMap<>();
    }

    public synchronized void connect() {
        disconnect();
        setDefaultHostUrl();
    }

    @Override
    public synchronized void disconnect() {
        for (HekrCloudClient client : mClients.values()) {
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
        for (HekrCloudClient client : mClients.values()) {
            client.disconnect();
            client.destroy();
        }
        mClients.clear();
        CacheUtil.setCloudUrls(null);
    }

    @Override
    public synchronized void sendMessage(JSONObject message, HekrMsgCallback callback) {
        tryToSend(message, callback, getDefaultHostUrl());
    }

    @Deprecated
    @Override
    public synchronized void sendMessage(String devTid, JSONObject message, HekrMsgCallback callback) {
        tryToSend(message, callback, getDefaultHostUrl());
    }

    @Override
    public synchronized void sendMessage(JSONObject message, HekrMsgCallback callback, CloudHostType host) {
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
    public synchronized void sendMessage(JSONObject message, HekrMsgCallback callback, String host) {
        String url;
        if (Constants.isTestSite() && !host.startsWith("test.")) {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL, host);
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
        }
        tryToSend(message, callback, url);
    }

    @Override
    public synchronized void receiveMessage(IMessageFilter filter, HekrMsgCallback callback) {
        tryToReceive(filter, callback, FilterType.FILTER_PERMANANT, 0L);
    }

    @Override
    public synchronized void receiveMessage(IMessageFilter filter, HekrMsgCallback callback, FilterType type, long expired) {
        tryToReceive(filter, callback, type, expired);
    }

    @Override
    public synchronized void deceiveMessage(IMessageFilter filter) {
        Dispatcher.getInstance().removeFilter(filter);
    }

    @Override
    public synchronized boolean isOnline() {
        for (HekrCloudClient client : mClients.values()) {
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
    public synchronized void addHekrClientListener(HekrClientListener listener) {
        addHekrClientListener(listener, "");
    }

    @Override
    public synchronized void removeHekrClientListener(HekrClientListener listener) {
        removeHekrClientListener(listener, "");
    }

    @Override
    public synchronized void addHekrClientListener(HekrClientListener listener, String host) {
        String url;
        if (TextUtils.isEmpty(host)) {
            url = getDefaultHostUrl();
        } else {
            url = String.format(Constants.UrlUtil.APP_WEBSOCKET_REPLACE_CLOUD_URL, host);
        }
        HekrCloudClient client = mClients.get(url);
        if (client != null) {
            client.addHekrClientListener(listener);
        }
    }

    @Override
    public synchronized void removeHekrClientListener(HekrClientListener listener, String host) {
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
        HekrCloudClient client = mClients.get(url);
        if (client != null) {
            client.removeHekrClientListener(listener);
        }
    }

    @Override
    public List<CloudChannelStatus> getHostsStatus() {
        List<CloudChannelStatus> list = new ArrayList<>();
        for (Map.Entry<String, HekrCloudClient> entry : mClients.entrySet()) {
            HekrCloudClient client = entry.getValue();
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

    private void tryToSend(JSONObject message, HekrMsgCallback callback, String url) {
        HekrCloudClient client = mClients.get(url);
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
                HekrCloudClient client = new HekrCloudClient();
                client.connect(url);
                mClients.put(url, client);
            }
        }
    }

    private void setDefaultHostUrl() {
        if (!mClients.containsKey(getDefaultHostUrl())) {
            HekrCloudClient client = new HekrCloudClient();
            client.connect(getDefaultHostUrl());
            mClients.put(getDefaultHostUrl(), client);
        }
    }

    private void tryToReceive(IMessageFilter filter, HekrMsgCallback callback, FilterType type, long expired) {
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

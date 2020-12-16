package me.siter.sdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 本地与设备通信的接口的实现
 */

class SiterLAN implements ISiterLAN {

    private static final String TAG = SiterLAN.class.getSimpleName();

    private Map<String, ISiterDeviceClient> mDeviceClients;


    SiterLAN() {
        mDeviceClients = new ConcurrentHashMap<>();
    }

    @Override
    public ISiterDeviceClient putDeviceClient(String tag, String ctrlKey) {
        if (mDeviceClients.containsKey(tag)) {
            return mDeviceClients.get(tag);
        }
        ISiterDeviceClient client = new SiterDeviceClient(tag, ctrlKey);
        mDeviceClients.put(tag, client);
        return client;
    }

    @Override
    public void removeDeviceClient(String tag) {
        ISiterDeviceClient client = mDeviceClients.get(tag);
        if (client != null) {
            client.disconnect();
            mDeviceClients.remove(tag);
        }
    }

    @Override
    public void clearDeviceClients() {
        for (String tag : mDeviceClients.keySet()) {
            removeDeviceClient(tag);
        }
    }

    @Override
    public ISiterDeviceClient getDeviceClient(String tag) {
        return mDeviceClients.get(tag);
    }

    @Override
    public List<ISiterDeviceClient> getDeviceClients() {
        return new ArrayList<>(mDeviceClients.values());
    }
}

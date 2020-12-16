package me.hekr.sdk;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 本地与设备通信的接口的实现
 */

class HekrLAN implements IHekrLAN {

    private static final String TAG = HekrLAN.class.getSimpleName();

    private Map<String, IHekrDeviceClient> mDeviceClients;


    HekrLAN() {
        mDeviceClients = new ConcurrentHashMap<>();
    }

    @Override
    public IHekrDeviceClient putDeviceClient(String tag, String ctrlKey) {
        if (mDeviceClients.containsKey(tag)) {
            return mDeviceClients.get(tag);
        }
        IHekrDeviceClient client = new HekrDeviceClient(tag, ctrlKey);
        mDeviceClients.put(tag, client);
        return client;
    }

    @Override
    public void removeDeviceClient(String tag) {
        IHekrDeviceClient client = mDeviceClients.get(tag);
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
    public IHekrDeviceClient getDeviceClient(String tag) {
        return mDeviceClients.get(tag);
    }

    @Override
    public List<IHekrDeviceClient> getDeviceClients() {
        return new ArrayList<>(mDeviceClients.values());
    }
}

package me.siter.sdk;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接不同地区的云端
 */

public enum CloudHostType {

    HOST_DEFAULT("192.168.12.163"),
    //HOST_DEFAULT("hub.hekr.me"),
    HOST_TEST_DEFAULT("test-hub.siter.me");

    private static final Map<String, CloudHostType> lookup = new HashMap<>();
    private String host;

    CloudHostType(String host) {
        this.host = host;
    }

    @Override
    public String toString() {
        return host;
    }

    static {
        for (CloudHostType e : EnumSet.allOf(CloudHostType.class)) {
            lookup.put(e.host, e);
        }
    }

    public static CloudHostType find(String channel, CloudHostType defaultValue) {
        CloudHostType value = lookup.get(channel);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}

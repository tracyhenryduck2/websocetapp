package me.hekr.sdk;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hucn on 2017/3/29.
 * Author: hucn
 * Description: 连接不同地区的云端
 */

public enum CloudHostType {

    HOST_DEFAULT("hub.hekr.me"), HOST_TEST_DEFAULT("test-hub.hekr.me");

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

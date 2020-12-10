package me.hekr.sdk;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hucn on 2017/3/29.
 * Author: hucn
 * Description: 连接不同地区的云端
 */

public enum CloudDomainType {

    DOMAIN_DEFAULT("hekr.me");

    private static final Map<String, CloudDomainType> lookup = new HashMap<>();
    private String domain;

    CloudDomainType(String domain) {
        this.domain = domain;
    }

    @Override
    public String toString() {
        return domain;
    }

    static {
        for (CloudDomainType e : EnumSet.allOf(CloudDomainType.class)) {
            lookup.put(e.domain, e);
        }
    }

    public static CloudDomainType find(String channel, CloudDomainType defaultValue) {
        CloudDomainType value = lookup.get(channel);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }
}

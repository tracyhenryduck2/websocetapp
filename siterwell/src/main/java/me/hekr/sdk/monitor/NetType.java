package me.hekr.sdk.monitor;

/**
 * Created by hucn on 2017/3/31.
 * Author: hucn
 * Description: 网络的类型
 */

public enum NetType {
    NONE(1),
    MOBILE(2),
    WIFI(4),
    OTHER(8);

    public int value;

    NetType(int value) {
        this.value = value;
    }
}

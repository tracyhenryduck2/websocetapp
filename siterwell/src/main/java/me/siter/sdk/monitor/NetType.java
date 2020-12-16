package me.siter.sdk.monitor;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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

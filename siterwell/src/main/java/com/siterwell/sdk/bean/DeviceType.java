package com.siterwell.sdk.bean;

/**
 * Created by gc-0001 on 2017/4/26.
 */

public enum DeviceType {

    // 利用构造函数传参
    BATTERY ("GS140"),
    WIFISOKECT ("GS351"),
    WATERSENEOR("GS156W");

    // 定义私有变量
    private String nCode ;

    // 构造函数，枚举类型只能为私有
    private DeviceType(String _nCode) {
        this . nCode = _nCode;
    }

    @Override
    public String toString() {
        return String.valueOf ( this . nCode );
    }

}

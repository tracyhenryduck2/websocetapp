package com.siterwell.sdk.http.bean;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Administrator on 2017/10/16.
 */

public class Global implements Serializable {
    private static final long serialVersionUID = -343741671460863057L;
    public static boolean isUDPOpen =false;
    //个推
    public static String clientId ="";
    //小米
    public static String mRegId ="";
    //华为
    public static String huaWeiToken ="";

    public static CopyOnWriteArrayList<FindDeviceBean> lanList = new CopyOnWriteArrayList<>();
}

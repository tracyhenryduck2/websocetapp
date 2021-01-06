package com.siterwell.sdk.bean;


import me.siter.sdk.http.bean.DeviceBean;

/**
 * Created by TracyHenry on 2018/2/6.
 */

public class WaterSensorBean extends DeviceBean {

    public WaterSensorBean(){
        super();
    }

    public int status;
    public int signal;
    public int battPercent;


    /**
     * 设备状态
     */
    public static final int STATUS_NORMAL = 0;//设备正常
    public static final int STATUS_EQUIPMENT_ALARM = 1;//设备报警(漏水)
    public static final int STATUS_EQUIPENT_LOW_VOLTAGE = 2;//设备低电压
    public static final int STATUS_EQUIPMENT_TROUBLE = 3;//设备故障
    public static final int STATUS_EQUIPMENT_SILENCE = 4;//设备静音
    public static final int STATUS_EQUIPMENT_NOT_CONNECT = 5;//设备未连接
    public static final int STATUS_LOW_VOLTAGE_SILENCE_TEN = 6;//低电压静音10小时
    public static final int STATUS_LINK = 7;//冰冻报警


    /**
     * 信号强度
     */
    public static final int SIGNAL_BAD = 1;//差
    public static final int SIGNAL_FINE = 2;//一般
    public static final int SIGNAL_GOOD = 3;//好
    public static final int SIGNAL_EXCELLENT = 4;//很好


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    public int getBattPercent() {
        return battPercent;
    }

    public void setBattPercent(int battPercent) {
        this.battPercent = battPercent;
    }
}

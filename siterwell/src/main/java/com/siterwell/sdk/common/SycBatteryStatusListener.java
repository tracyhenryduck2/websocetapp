package com.siterwell.sdk.common;

import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WaterSensorBean;

import java.util.List;

/**
 * Created by TracyHenry on 2018/2/7.
 */

public interface SycBatteryStatusListener {

    void success(List<BatteryBean> batteryBeans);

    void error(int error);

}

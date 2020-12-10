package com.siterwell.sdk.common;

import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.WaterSensorBean;

import java.util.List;

/**
 * Created by TracyHenry on 2018/2/7.
 */

public interface SycWaterSensorStatusListener {

    void success(List<WaterSensorBean> waterSensorBeans);

    void error(int error);

}

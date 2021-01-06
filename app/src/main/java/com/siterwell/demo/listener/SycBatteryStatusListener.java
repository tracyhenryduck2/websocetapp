package com.siterwell.demo.listener;

import com.siterwell.demo.bean.BatteryBean;

import java.util.List;

/**
 * Created by TracyHenry on 2018/2/7.
 */

public interface SycBatteryStatusListener {

    void success(List<BatteryBean> batteryBeans);

    void error(int error);

}

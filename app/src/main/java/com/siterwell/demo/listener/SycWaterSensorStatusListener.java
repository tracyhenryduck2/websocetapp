package com.siterwell.demo.listener;

import com.siterwell.demo.bean.WaterSensorBean;

import java.util.List;

/**
 * Created by TracyHenry on 2018/2/7.
 */

public interface SycWaterSensorStatusListener {

    void success(List<WaterSensorBean> waterSensorBeans);

    void error(int error);

}

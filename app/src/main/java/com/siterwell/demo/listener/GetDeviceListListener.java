package com.siterwell.demo.listener;

import java.util.List;

import me.siter.sdk.http.bean.DeviceBean;


/**
 * Created by TracyHenry on 2018/2/8.
 */

public interface GetDeviceListListener {

    void succuss(List<DeviceBean> deviceBean);

    void error(int error);

}

package com.siterwell.sdk.common;

import me.siter.sdk.http.bean.DeviceBean;

import java.util.List;


/**
 * Created by TracyHenry on 2018/2/8.
 */

public interface GetDeviceListListener {

    void succuss(List<DeviceBean> deviceBean);

    void error(int error);

}

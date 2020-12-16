package me.hekr.sdk.inter;

import java.util.List;

import me.hekr.sdk.entity.LanDeviceBean;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public interface HekrLANListener {

  void onNewDevice(LanDeviceBean device);

  void onGetDevices(List<LanDeviceBean> list);
}

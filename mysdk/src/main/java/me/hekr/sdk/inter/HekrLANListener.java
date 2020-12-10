package me.hekr.sdk.inter;

import java.util.List;

import me.hekr.sdk.entity.LanDeviceBean;

/**
 * Created by hucn on 2017/4/10.
 * Author:
 * Description:
 */

public interface HekrLANListener {

  void onNewDevice(LanDeviceBean device);

  void onGetDevices(List<LanDeviceBean> list);
}

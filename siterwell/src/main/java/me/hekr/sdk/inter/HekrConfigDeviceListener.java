package me.hekr.sdk.inter;

import org.json.JSONObject;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/

public interface HekrConfigDeviceListener {

  /**
   * 开始配网后调用云端接口，与本地设备列表对比取出每次新增设备
   */
  void getNewDevice(JSONObject newDeviceBean);

  /**
   * 一次配网内云端接口，返回的新增设备列表不为空
   */
  void getDeviceSuccess();

  /**
   * 一次配网内云端接口，返回的新增设备列表为空
   */
  void getDeviceFail(int errorCode, String message);
}

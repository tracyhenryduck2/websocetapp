package me.siter.sdk.inter;

import java.util.List;

import me.siter.sdk.config.ConfigSubDevice;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public interface HekrConfigSubCallback {

  void onStart();

  void onAppSendSuccess(String configSubDeviceAppResp);

  void onAppSendFail(int errorCode, String errorMsg);

  void onAppSendTimeout();

  void onAdd(ConfigSubDevice device);

  void onUpdate(ConfigSubDevice device);

  void onStop();

  /**
   * 当调用stopConfigSafely()这个方法的时候，等几秒会回调这个方法告知真的结束
   */
  void onResult(List<ConfigSubDevice> result);

  void onError();
}

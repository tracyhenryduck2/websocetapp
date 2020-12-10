package me.hekr.sdk.inter;

import java.util.List;

import me.hekr.sdk.config.ConfigSubDevice;

/**
 * Created by hucn on 2017/8/28.
 * Author:
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

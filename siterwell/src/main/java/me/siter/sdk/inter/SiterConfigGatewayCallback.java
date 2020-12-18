package me.siter.sdk.inter;

import java.util.List;

import me.siter.sdk.config.ConfigGatewayDevice;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public interface SiterConfigGatewayCallback {

  void onStart();

  void onAdd(ConfigGatewayDevice device);

  void onUpdate(ConfigGatewayDevice device);

  void onStop();

  /**
   * 当调用stopConfigSafely()这个方法的时候，等几秒会回调这个方法告知真的结束
   */
  void onResult(List<ConfigGatewayDevice> result);

  void onError();
}
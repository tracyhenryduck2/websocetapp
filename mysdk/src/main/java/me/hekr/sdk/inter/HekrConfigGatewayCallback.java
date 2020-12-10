package me.hekr.sdk.inter;

import java.util.List;

import me.hekr.sdk.config.ConfigDevice;
import me.hekr.sdk.config.ConfigGatewayDevice;

/**
 * Created by hucn on 2017/8/28.
 * Author:
 * Description:
 */

public interface HekrConfigGatewayCallback {

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

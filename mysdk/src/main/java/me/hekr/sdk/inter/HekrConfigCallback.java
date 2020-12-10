package me.hekr.sdk.inter;

import java.util.List;

import me.hekr.sdk.config.ConfigDevice;

/**
 * Created by hucn on 2017/8/28.
 * Author:
 * Description:
 */

public interface HekrConfigCallback {

  void onStart();

  void onAdd(ConfigDevice device);

  void onUpdate(ConfigDevice device);

  void onStop();

  /**
   * 当调用stopConfigSafely()这个方法的时候，等几秒会回调这个方法告知真的结束
   */
  void onResult(List<ConfigDevice> result);

  void onError();
}

package me.hekr.sdk.monitor;

/**
 * Created by hucn on 2017/3/31.
 * Author: hucn
 * Description: 网络状态变化的接口
 */

public interface NetObservable {

  void onNetOn();

  void onNetOff();
}

package me.hekr.sdk.monitor;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 网络状态变化的接口
 */

public interface NetObservable {

  void onNetOn();

  void onNetOff();
}

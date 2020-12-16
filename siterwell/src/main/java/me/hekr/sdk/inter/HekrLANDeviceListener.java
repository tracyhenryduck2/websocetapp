package me.hekr.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 局域网设备状态的监听
 */

public interface HekrLANDeviceListener {

  void onConnected();

  void onDisconnected();

  void onError(int errorCode, String errorMsg);
}

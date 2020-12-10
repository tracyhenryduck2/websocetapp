package me.hekr.sdk.inter;

/**
 * Created by hucn on 2017/4/12.
 * Author: hucn
 * Description: 局域网设备状态的监听
 */

public interface HekrLANDeviceListener {

  void onConnected();

  void onDisconnected();

  void onError(int errorCode, String errorMsg);
}

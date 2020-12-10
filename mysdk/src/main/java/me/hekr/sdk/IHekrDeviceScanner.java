package me.hekr.sdk;

import me.hekr.sdk.inter.HekrLANListener;

/**
 * Created by hucn on 2017/4/10.
 * Author: hucn
 * Description: jmdns 封装，发现局域网内的设备
 */

public interface IHekrDeviceScanner {

  void startSearch();

  void stopSearch();

  boolean isStarted();

  void addLANListener(HekrLANListener listener);

  void removeLANListener();

  void getExistDevices();
}

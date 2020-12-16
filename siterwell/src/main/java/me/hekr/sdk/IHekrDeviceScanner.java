package me.hekr.sdk;

import me.hekr.sdk.inter.HekrLANListener;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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

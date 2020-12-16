package me.siter.sdk;

import me.siter.sdk.inter.SiterLANListener;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: jmdns 封装，发现局域网内的设备
 */

public interface ISiterDeviceScanner {

  void startSearch();

  void stopSearch();

  boolean isStarted();

  void addLANListener(SiterLANListener listener);

  void removeLANListener();

  void getExistDevices();
}

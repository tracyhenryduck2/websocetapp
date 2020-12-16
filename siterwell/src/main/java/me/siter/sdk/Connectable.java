package me.siter.sdk;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 重连的接口
 */

interface Connectable {

  boolean isOnline();

  boolean isConnecting();

  ConnType getConnType();

  void communicate();

  String getTag();
}

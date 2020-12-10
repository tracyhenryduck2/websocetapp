package me.hekr.sdk;

/**
 * Created by hucn on 2017/3/31.
 * Author: hucn
 * Description: 重连的接口
 */

interface Connectable {

  boolean isOnline();

  boolean isConnecting();

  ConnType getConnType();

  void communicate();

  String getTag();
}

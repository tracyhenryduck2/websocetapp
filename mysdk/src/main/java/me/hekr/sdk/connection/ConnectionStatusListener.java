package me.hekr.sdk.connection;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 连接状态的监听
 */

public interface ConnectionStatusListener {

  void onSuccess();

  void onFail();

  void onConnected();

  void onDisconnected();

  void onError();
}

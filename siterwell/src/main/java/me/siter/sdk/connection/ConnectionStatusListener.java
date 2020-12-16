package me.siter.sdk.connection;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接状态的监听
 */

public interface ConnectionStatusListener {

  void onSuccess();

  void onFail();

  void onConnected();

  void onDisconnected();

  void onError();
}

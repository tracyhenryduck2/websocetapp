package me.hekr.sdk.service;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: Service提供给外部的连接接口
 */

public interface IAsyncConn {

  void start();

  void stop();

  void send(String message);

  boolean isRunning();

  boolean isActive();

  void reset(ConnOptions options);
}

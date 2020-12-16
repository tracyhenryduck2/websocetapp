package me.hekr.sdk.service;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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

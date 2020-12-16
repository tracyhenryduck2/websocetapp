package me.siter.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 监听Client中Websocket连接的变化
 */

public interface HekrClientListener {

  void onConnected();

  void onDisconnected();
}

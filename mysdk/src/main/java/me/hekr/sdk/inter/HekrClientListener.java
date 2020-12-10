package me.hekr.sdk.inter;

/**
 * Created by hucn on 2017/5/10.
 * Author: hucn
 * Description: 监听HekrClient中Websocket连接的变化
 */

public interface HekrClientListener {

  void onConnected();

  void onDisconnected();
}

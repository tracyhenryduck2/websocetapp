package me.hekr.sdk;

import org.json.JSONObject;

import me.hekr.sdk.inter.HekrLANDeviceListener;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 封装后的连接客户端
 */

public interface IHekrDeviceClient {

  void sendMessage(JSONObject message, HekrMsgCallback callback);

  void connect(String ip, int port);

  String getIP();

  int getPort();

  void disconnect();

  boolean isOnline();

  void addLANDeviceListener(HekrLANDeviceListener listener);

  void removeLANDeviceListener(HekrLANDeviceListener listener);
}

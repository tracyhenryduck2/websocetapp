package me.siter.sdk;

import org.json.JSONObject;

import me.siter.sdk.inter.HekrLANDeviceListener;
import me.siter.sdk.inter.HekrMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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

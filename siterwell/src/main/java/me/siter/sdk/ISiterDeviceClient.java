package me.siter.sdk;

import org.json.JSONObject;

import me.siter.sdk.inter.SIterLANDeviceListener;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 封装后的连接客户端
 */

public interface ISiterDeviceClient {

  void sendMessage(JSONObject message, SiterMsgCallback callback);

  void connect(String ip, int port);

  String getIP();

  int getPort();

  void disconnect();

  boolean isOnline();

  void addLANDeviceListener(SIterLANDeviceListener listener);

  void removeLANDeviceListener(SIterLANDeviceListener listener);
}

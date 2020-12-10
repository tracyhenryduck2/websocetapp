package me.hekr.sdk;

import me.hekr.sdk.dispatcher.IMessageFilter;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 消息的接口,给Dispatcher使用
 */

public interface IMessageRequest {

  int CHANNEL_CLOUD = 1;
  int CHANNEL_DEVICE = 2;
  int CHANNEL_COMMON_UDP = 3;

  void setMessage(String message);

  String getMessage();

  void setFilter(IMessageFilter filter);

  IMessageFilter getFilter();

  void setHekrMsgCallback(HekrMsgCallback callback);

  HekrMsgCallback getHekrMsgCallback();

  String getHandler();

  void setHandler(String handler);

  int getChannel();

  void setChannel(int channel);

  void cancel();

  boolean hasCanceled();

}

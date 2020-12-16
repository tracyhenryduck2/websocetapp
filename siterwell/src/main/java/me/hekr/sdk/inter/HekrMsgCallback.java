package me.hekr.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 消息接受的回调
 */

public interface HekrMsgCallback {

  void onReceived(String msg);

  void onTimeout();

  void onError(int errorCode, String message);
}

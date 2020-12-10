package me.hekr.sdk.inter;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: 消息接受的回调
 */

public interface HekrMsgCallback {

  void onReceived(String msg);

  void onTimeout();

  void onError(int errorCode, String message);
}

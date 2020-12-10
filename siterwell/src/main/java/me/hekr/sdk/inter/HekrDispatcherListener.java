package me.hekr.sdk.inter;

/**
 * Created by hucn on 2017/9/18.
 * Author:
 * Description:
 */

public interface HekrDispatcherListener {

  void onSend(String message, String to);

  void onReceive(String message, String from);
}

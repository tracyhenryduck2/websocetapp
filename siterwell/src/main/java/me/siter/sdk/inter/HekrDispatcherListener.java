package me.siter.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public interface HekrDispatcherListener {

  void onSend(String message, String to);

  void onReceive(String message, String from);
}

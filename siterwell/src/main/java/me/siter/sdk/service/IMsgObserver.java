package me.siter.sdk.service;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 观察者
 */

public interface IMsgObserver {

  void onReceived(String message, String from);
}

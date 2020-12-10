package me.hekr.sdk.service;

/**
 * Created by hucn on 2017/3/24.
 * Author: hucn
 * Description: 观察者
 */

public interface IMsgObserver {

  void onReceived(String message,String from);
}

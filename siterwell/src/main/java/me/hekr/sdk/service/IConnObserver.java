package me.hekr.sdk.service;

/**
 * Created by hucn on 2017/4/1.
 * Author: hucn
 * Description: 连接的观察类
 */

public interface IConnObserver {

  void onConnChanged(ConnStatusType status);

  void onError(ConnStatusType errorCode, Throwable throwable);
}

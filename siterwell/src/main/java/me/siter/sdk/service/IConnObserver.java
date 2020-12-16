package me.siter.sdk.service;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 连接的观察类
 */

public interface IConnObserver {

  void onConnChanged(ConnStatusType status);

  void onError(ConnStatusType errorCode, Throwable throwable);
}

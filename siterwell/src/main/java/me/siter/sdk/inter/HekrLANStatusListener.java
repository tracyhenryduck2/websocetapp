package me.siter.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description:
 */

public interface HekrLANStatusListener {

  void onDeviceStatusChanged(String tag);

  void onDeviceStatusError(String tag, int errorCode, String errorMessage);

  void onStatusChanged(boolean enable);
}


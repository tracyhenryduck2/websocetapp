package me.hekr.sdk.inter;

/**
 * Created by hucn on 2017/4/12.
 * Author:
 * Description:
 */

public interface HekrLANStatusListener {

  void onDeviceStatusChanged(String tag);

  void onDeviceStatusError(String tag, int errorCode, String errorMessage);

  void onStatusChanged(boolean enable);
}


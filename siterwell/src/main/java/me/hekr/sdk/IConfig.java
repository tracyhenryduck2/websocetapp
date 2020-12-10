package me.hekr.sdk;

import android.content.Context;

import java.util.Map;

import me.hekr.sdk.inter.HekrConfigDeviceListener;

/**
 * Created by hekr_xm on 2017/4/7.
 **/

public interface IConfig {

  void startConfig(Context context, Map configParams, HekrConfigDeviceListener listener);

  void stopConfig();

  void stopFindDevice();
}

package me.hekr.sdk;

import android.content.Context;

import java.util.Map;

import me.hekr.sdk.inter.HekrConfigDeviceListener;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/

public interface IConfig {

  void startConfig(Context context, Map configParams, HekrConfigDeviceListener listener);

  void stopConfig();

  void stopFindDevice();
}

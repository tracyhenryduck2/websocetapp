package me.siter.sdk;

import android.content.Context;

import java.util.Map;

import me.siter.sdk.inter.SiterConfigDeviceListener;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/

public interface IConfig {

  void startConfig(Context context, Map configParams, SiterConfigDeviceListener listener);

  void stopConfig();

  void stopFindDevice();
}
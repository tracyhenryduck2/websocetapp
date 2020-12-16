package me.siter.sdk;

import android.content.Context;

import java.util.List;
import java.util.Map;

import me.siter.sdk.config.ConfigGatewayDevice;
import me.siter.sdk.inter.HekrConfigGatewayCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/

public interface INewGatewayConfig {

  /**
   * 开始配网
   *
   * @param context      context
   * @param configParams 配网设置，需要传入ssid，password，pinCode这三个键值所对应的参数
   * @param callback     配网回调
   */
  void startConfig(Context context, Map configParams, HekrConfigGatewayCallback callback);


  /**
   * 在配网开始和结束中获取配网设备，在配网结束后设备将会被销毁，可以在{@link #stopConfig()}回调中获取
   * 配网设备的状态
   *
   * @return 配网设备的状态
   */
  List<ConfigGatewayDevice> getConfigDevices();

  /**
   * 强制停止配网,没有OnResult()回调
   */
  void stopConfig();

  /**
   * 先停止本地设备配网，等待一小段时间后停止查询云端
   *
   * @param delay 延时时间，0 表示没有延时
   */
  void stopConfigSafely(long delay);
}

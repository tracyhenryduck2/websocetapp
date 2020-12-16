package me.hekr.sdk;

import org.json.JSONObject;

import java.util.List;

import me.hekr.sdk.entity.LanControlBean;
import me.hekr.sdk.inter.HekrLANStatusListener;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * 局域网本地的控制。当开启时，会通过JmDNS发现局域网设备，然后刷新LanControlBean的List时去尝试连接设备。
 * IHekrLANControl与HekrLAN有所不同，这个类会发现和管理所有的本地局域网连接，而HekrLAN允许指定和连接某一个局域网设备。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface IHekrLANControl {

  /**
   * 是否开启局域网控制。
   *
   * @param enable 是否开启
   */
  void enableLANControl(boolean enable);

  /**
   * 是否已经开启局域网控制。
   *
   * @return boolean 是否开启
   */
  boolean isLANControlEnabled();

  /**
   * 获取局域网控制对象。
   *
   * @param tag 设备的devTid
   * @return IHekrDeviceClient 局域网设备控制的对象
   */
  IHekrDeviceClient getDeviceClient(String tag);

  /**
   * 获取所有的局域网控制对象。
   *
   * @return List 所有的局域网设备控制的对象
   */
  List<IHekrDeviceClient> getLANDeviceClients();

  /**
   * 刷新局域网控制列表。
   *
   * @param list 控制对象bean的list
   */
  void refreshLAN(List<LanControlBean> list);

  /**
   * 添加局域网状态变化的监听，可以设置多个。
   *
   * @param listener HekrLANStatusListener
   */
  void addLANStatusChangeListener(HekrLANStatusListener listener);

  /**
   * 移除局域网状态变化的监听。
   *
   * @param listener HekrLANStatusListener
   */
  void removeLANStatusChangeListener(HekrLANStatusListener listener);

  /**
   * 是否开启局域网组播。
   *
   * @param enable 是否开启
   */
  void enableLANMulticast(boolean enable);

  /**
   * 是否开启局域网广播。
   *
   * @param enable 是否开启
   */
  void enableLANBroadcast(boolean enable);

  /**
   * 通过默认的UDP通道发送数据
   *
   * @param message  消息内容
   * @param ip       ip
   * @param port     端口
   * @param callback 端口
   */
  void sendCommonUdp(JSONObject message, String ip, int port, HekrMsgCallback callback);

  /**
   * 获取当前默认UDP通道的端口
   */
  int getCommonUdpPort();
}

package me.siter.sdk;

import java.util.List;

/**
 * 局域网本地的控制。可以获取单个控制的对象。ISiterLAN会保存所有通过
 * {@link #putDeviceClient(String devTid, String ctrlKey) putDeviceClient}方法创建的实例。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface ISiterLAN {

  /**
   * 创建一个ISiterDeviceClient的实例。
   *
   * @param tag 设备的devTid
   * @param ctrlKey 设备的ctrlKey
   */
  ISiterDeviceClient putDeviceClient(String tag, String ctrlKey);

  /**
   * 删除一个ISiterDeviceClient的实例。
   *
   * @param tag 设备的devTid
   */
  void removeDeviceClient(String tag);

  /**
   * 清除所有的ISiterDeviceClient的实例。
   */
  void clearDeviceClients();

  /**
   * 获取一个ISiterDeviceClient的实例。
   *
   * @param tag 设备的devTid
   */
  ISiterDeviceClient getDeviceClient(String tag);

  /**
   * 获取所有ISiterDeviceClient的实例。
   */
  List<ISiterDeviceClient> getDeviceClients();
}

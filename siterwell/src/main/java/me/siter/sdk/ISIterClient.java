package me.siter.sdk;

import org.json.JSONObject;

import java.util.List;
import java.util.Set;

import me.siter.sdk.dispatcher.IMessageFilter;
import me.siter.sdk.inter.SiterClientListener;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 与设备或通信的接口
 */

public interface ISIterClient {

  /**
   * 开始连接
   */
  void connect();

  /**
   * 断开连接
   */
  void disconnect();

  /**
   * 设置连接的数据中心地址
   *
   * @param hosts 设置Websocket需要连接的数据中心(通道), 可以自定义数据中心地址
   */
  void setHosts(Set<String> hosts);

  /**
   * 清除记录的域名数据
   */
  void clearHosts();

  /**
   * 发送消息
   *
   * @param message  JSONObject类型的发送数据
   * @param callback 发送消息回调
   */
  void sendMessage(JSONObject message, SiterMsgCallback callback);

  /**
   * 发送消息
   *
   * @param devTid   devTid
   * @param message  JSONObject类型的发送数据
   * @param callback 发送消息回调
   */
  @Deprecated
  void sendMessage(String devTid, JSONObject message, SiterMsgCallback callback);

  /**
   * 发送消息
   *
   * @param message  JSONObject类型的发送数据
   * @param callback 发送消息回调
   * @param host  需要在哪一个地址发送
   */
  void sendMessage(JSONObject message, SiterMsgCallback callback, CloudHostType host);

  /**
   * 发送消息
   *
   * @param message  JSONObject类型的发送数据
   * @param callback 发送消息回调
   * @param host  需要在哪一个地址发送
   */
  void sendMessage(JSONObject message, SiterMsgCallback callback, String host);

  /**
   * 主动接收消息
   *
   * @param filter   消息过滤器，实现过滤消息的规则
   * @param callback 接收消息回调
   */
  void receiveMessage(IMessageFilter filter, SiterMsgCallback callback);

  /**
   * 主动接收消息
   *
   * @param filter   消息过滤器，实现过滤消息的规则
   * @param callback 接收消息回调
   * @param type     过滤类型
   * @param expired  过滤时间
   */
  void receiveMessage(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expired);

  /**
   * 取消主动消息接收，传入的filter对象必须和主动接受的filter对象一致
   *
   * @param filter 消息过滤器，实现过滤消息的规则
   */
  void deceiveMessage(IMessageFilter filter);

  /**
   * 判断是否和云端连接
   */
  boolean isOnline();

  /**
   * 判断是否与指定的地址有连接
   */
  boolean isOnline(String host);

  /**
   * 添加对连接状态的监听
   *
   * @param listener 监听
   */
  void addSiterClientListener(SiterClientListener listener);

  /**
   * 移除对连接状态的监听
   *
   * @param listener 监听
   */
  void removeSiterClientListener(SiterClientListener listener);

  /**
   * 添加对连接状态的监听
   *
   * @param listener 监听
   * @param host   域名
   */
  void addSiterClientListener(SiterClientListener listener, String host);

  /**
   * 移除对连接状态的监听
   *
   * @param listener 监听
   * @param host   域名
   */
  void removeSiterClientListener(SiterClientListener listener, String host);

  /**
   * 获取当前连接的状态
   *
   * @return Map 节点和状态的字符串
   */
  List<CloudChannelStatus> getHostsStatus();
}

package me.siter.sdk;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import me.siter.sdk.entity.SiterWebBean;
import me.siter.sdk.inter.SiterWebActionListener;
import me.siter.sdk.web.SiterWebInterface;
import me.siter.sdk.web.WebControlType;

/**
 * 如果想使集成ekr SDK中的Web功能，请先导入hekrweb模块，不然调用这个类会报错。
 * 这个模块封装了web页面，所以外部不用关心web的实现，只要在需要实现Web功能的Activity或者Fragment中持有这个单例，并且实现相应的接口后初始化就可以了。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface ISiterWebClient {

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface Activity或者Fragment或者其他组件需要实现此接口
   * @param webView      请传入HekrXWalkView或者其子类，不支持Android原生的WebView
   * @param siterWebBean  初始化HekrWeb需要的参数的bean
   */
  void initWithWeb(SiterWebInterface webInterface, View webView, SiterWebBean siterWebBean);

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface    Activity或者Fragment或者其他组件需要实现此接口
   * @param webView         请传入HekrXWalkView或者其子类，不支持Android原生的WebView
   * @param siterWebBean     初始化HekrWeb需要的参数的bean
   * @param pushJsonMessage 是否需要有推从消息
   */
  void initWithWeb(SiterWebInterface webInterface, View webView, SiterWebBean siterWebBean, String pushJsonMessage);

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface    Activity或者Fragment或者其他组件需要实现此接口
   * @param webView         请传入HekrXWalkView或者其子类，不支持Android原生的WebView
   * @param siterWebBean     初始化HekrWeb需要的参数的bean
   * @param pushJsonMessage 是否需要有推从消息
   * @param useCache        是否开启页面内容的缓存。如果不开启，那么每次都会从服务器请求页面的所有内容。
   */
  void initWithWeb(SiterWebInterface webInterface, View webView, SiterWebBean siterWebBean, String pushJsonMessage, boolean useCache);

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface Activity或者Fragment或者其他组件需要实现此接口
   * @param container    WebView容器布局，初始化后WebView会成为此布局的子控件
   * @param siterWebBean  初始化HekrWeb需要的参数的bean
   */
  void init(SiterWebInterface webInterface, ViewGroup container, SiterWebBean siterWebBean);

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface    Activity或者Fragment或者其他组件需要实现此接口
   * @param container       WebView容器布局，初始化后WebView会成为此布局的子控件
   * @param siterWebBean     初始化HekrWeb需要的参数的bean
   * @param pushJsonMessage 是否需要有推从消息
   */
  void init(SiterWebInterface webInterface, ViewGroup container, SiterWebBean siterWebBean, String pushJsonMessage);

  /**
   * 初始化HekrWeb的信息。
   *
   * @param webInterface    Activity或者Fragment或者其他组件需要实现此接口
   * @param container       WebView容器布局，初始化后WebView会成为此布局的子控件
   * @param siterWebBean     初始化HekrWeb需要的参数的bean
   * @param pushJsonMessage 是否需要有推从消息
   * @param useCache        是否开启页面内容的缓存。如果不开启，那么每次都会从服务器请求页面的所有内容。
   */
  void init(SiterWebInterface webInterface, ViewGroup container, SiterWebBean siterWebBean, String pushJsonMessage, boolean useCache);

  /**
   * 加载相关页面，请在{@link #init(SiterWebInterface webInterface, ViewGroup viewGroup, SiterWebBean hekrWebBean, String pushJsonMessage, boolean isOpenWebCache) init}
   * 后调用。
   */
  void load();

  /**
   * 重新加载页面。
   *
   * @param pushJsonMessage 是否需要有推从消息
   */
  void reload(String pushJsonMessage);

  /**
   * 重新加载页面。
   *
   * @param siterWebBean 初始化HekrWeb需要的参数的bean
   */
  void reload(SiterWebBean siterWebBean);

  /**
   * 重新加载页面。
   *
   * @param siterWebBean     初始化HekrWeb需要的参数的bean
   * @param pushJsonMessage 是否需要有推从消息
   */
  void reload(SiterWebBean siterWebBean, String pushJsonMessage);

  /**
   * 设置Web页面的监听，主要监听Web中的一些动作。
   *
   * @param siterWebActionListener 监听接口
   */
  void setWebActionListener(SiterWebActionListener siterWebActionListener);

  /**
   * 给HekrWeb添加额外的数据，一般情况下不会用到这个方法。
   *
   * @param messages 额外的数据
   */
  void putExtraMessages(Map<String, Object> messages);

  /**
   * 获取HekrWeb额外的数据，一般情况下不会用到这个方法。
   *
   * @return message 额外的数据
   */
  Map<String, Object> getExtraMessages();

  /**
   * 加载页面时是否加载本地缓存。
   *
   * @param enable true加载, false不加载
   */
  void useCache(boolean enable);

  /**
   * 设置web通过sdk控制设备的方式。
   *
   * @param type 控制方式，详情请见WebControlType
   */
  void setControlType(WebControlType type);

  /**
   * 获得传入的HekrWebBean。
   *
   * @return HekrWebBean 传入HekrWebClient的HekrWebBean
   */
  SiterWebBean getHekrWebBean();

  /**
   * 是否已经被初始化
   *
   * @return boolean 是否已经被初始化
   */
  boolean hasInitialized();

  /**
   * 设置是否是虚拟环境
   *
   * @param virtual 是否是虚拟设备
   */
  void setRunVirtual(boolean virtual);

  /**
   * 是否是虚拟环境
   *
   * @return 是否虚拟设备
   */
  boolean getRunVirtual();

  /**
   * 第三方功能预留 例：调用拍照。
   *
   * @param requestCode 请求码
   * @param resultCode  返回码
   * @param data        返回数据
   */
  void notifyActivityResult(int requestCode, int resultCode, Intent data);

  /**
   * 指纹功能预留。
   *
   * @param flag flag
   */
  void notifyFingerPrintResult(boolean flag);

  /**
   * 当Back键按下时可以调用这个方法通知WebView。
   */
  void notifyBackPressed();

  /**
   * 通知前端页面拍摄到的图片
   */
  void notifyPhoto(String key, String bitmapBase64);

  /**
   * 重置HekrWeb的信息。用于再次使用前清除所有信息，页面和回调。
   */
  void reset();

  /**
   * 当页面Destroy时可以调用这个方法通知WebView。
   */
  void destroy();
}

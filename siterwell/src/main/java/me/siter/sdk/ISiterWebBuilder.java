package me.siter.sdk;

import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import me.siter.sdk.entity.SiterWebBean;
import me.siter.sdk.inter.SiterWebActionListener;
import me.siter.sdk.web.SiterWebInterface;

/**
 * IWebClient的构造器。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface ISiterWebBuilder {

  /**
   * 设置Web需要的接口。
   *
   * @param webInterface 接口
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setWebInterface(SiterWebInterface webInterface);

  /**
   * 添加Web添加到的父控件，这个方法不需要自己在布局中设置XWalkView控件。setViewGroup和setWebView只需要使用其中一个即可
   *
   * @param container ViewGroup
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setViewGroup(ViewGroup container);

  /**
   * 设置XWalkView控件，请添加布局中的XWalkView控件。setViewGroup和setWebView只需要使用其中一个即可
   *
   * @param view View
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setWebView(View view);

  /**
   * 添加WebBean。
   *
   * @param bean WebBean
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setSiterWebBean(SiterWebBean bean);

  /**
   * 添加PushJsonMessage。
   *
   * @param pushJsonMessage String
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setPushJsonMessage(String pushJsonMessage);

  /**
   * 是否将页面内容存储在本地。
   *
   * @param enable 是否开启
   * @return IWebBuilder Builder
   */
  @Deprecated
  ISiterWebBuilder setCache(Boolean enable);

  /**
   * 设置Web的状态监听。
   *
   * @param siterWebActionListener 状态监听
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder setSiterWebActionListener(SiterWebActionListener siterWebActionListener);

  /**
   * 添加额外的数据，一般情况下不需要使用。
   *
   * @param message Map
   * @return IWebBuilder Builder
   */
  ISiterWebBuilder putExtraMessages(Map<String, Object> message);

  /**
   * 返回IWebClient实例。
   *
   * @return IWebClient IWebClient实例
   */
  ISiterWebClient build();
}

package me.hekr.sdk;

import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

import me.hekr.sdk.entity.HekrWebBean;
import me.hekr.sdk.inter.HekrWebActionListener;
import me.hekr.sdk.web.HekrWebInterface;

/**
 * IHekrWebClient的构造器。
 *
 * @author hucn
 */
public interface IHekrWebBuilder {

  /**
   * 设置HekrWeb需要的接口。
   *
   * @param webInterface 接口
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setWebInterface(HekrWebInterface webInterface);

  /**
   * 添加Web添加到的父控件，这个方法不需要自己在布局中设置HekrXWalkView控件。setViewGroup和setWebView只需要使用其中一个即可
   *
   * @param container ViewGroup
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setViewGroup(ViewGroup container);

  /**
   * 设置HekrXWalkView控件，请添加布局中的HekrXWalkView控件。setViewGroup和setWebView只需要使用其中一个即可
   *
   * @param view View
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setWebView(View view);

  /**
   * 添加HekrWebBean。
   *
   * @param bean HekrWebBean
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setHekrWebBean(HekrWebBean bean);

  /**
   * 添加PushJsonMessage。
   *
   * @param pushJsonMessage String
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setPushJsonMessage(String pushJsonMessage);

  /**
   * 是否将页面内容存储在本地。
   *
   * @param enable 是否开启
   * @return IHekrWebBuilder Builder
   */
  @Deprecated
  IHekrWebBuilder setCache(Boolean enable);

  /**
   * 设置Web的状态监听。
   *
   * @param hekrWebActionListener 状态监听
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder setHekrWebActionListener(HekrWebActionListener hekrWebActionListener);

  /**
   * 添加额外的数据，一般情况下不需要使用。
   *
   * @param message Map
   * @return IHekrWebBuilder Builder
   */
  IHekrWebBuilder putExtraMessages(Map<String, Object> message);

  /**
   * 返回IHekrWebClient实例。
   *
   * @return IHekrWebClient IHekrWebClient实例
   */
  IHekrWebClient build();
}

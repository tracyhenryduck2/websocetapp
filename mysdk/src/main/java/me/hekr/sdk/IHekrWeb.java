package me.hekr.sdk;

import android.content.Context;

import java.util.List;

import me.hekr.sdk.entity.HekrWebBean;

/**
 * 如果想使集成hekr SDK中的Web功能，请先导入hekrweb模块，不然调用这个类会报错。
 * 这个模块封装了web页面，所以外部不用关心web的实现，只要通过此接口拿到IHekrWebClient，并且实现相应的接口后初始化就可以了。
 *
 * @author hucn
 */
public interface IHekrWeb {

  /**
   * 创建一个新的IHekrWebClient，IHekrWebClient是WebView的封装。
   *
   * @return IHekrWebClient 返回一个新的IHekrWebClient的实例
   */
  IHekrWebClient createWebClient();

  /**
   * 创建一个新的IHekrWebClient的构造器。
   *
   * @return IHekrWebBuilder 返回一个新的IHekrWebClient的构造器
   */
  IHekrWebBuilder getWebBuilder();

  /**
   * 在后台缓存所传入的页面，不会直接显示。这个方法会调用WebView的缓存机制。
   *
   * @param context Context
   * @param pages   list
   */
  void cachePageBackground(Context context, List<HekrWebBean> pages);
}

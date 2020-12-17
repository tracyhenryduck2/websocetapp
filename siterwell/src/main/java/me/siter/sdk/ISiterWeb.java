package me.siter.sdk;

import android.content.Context;

import java.util.List;

import me.siter.sdk.entity.SiterWebBean;

/**
 * 如果想使集成SDK中的Web功能，请先导入web模块，不然调用这个类会报错。
 * 这个模块封装了web页面，所以外部不用关心web的实现，只要通过此接口拿到ISiterWebClient，并且实现相应的接口后初始化就可以了。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface ISiterWeb {

  /**
   * 创建一个新的ISiterWebClient，ISiterWebClient是WebView的封装。
   *
   * @return ISiterWebClient 返回一个新的ISiterWebClient的实例
   */
  ISiterWebClient createWebClient();

  /**
   * 创建一个新的ISiterWebClient的构造器。
   *
   * @return ISiterWebBuilder 返回一个新的ISiterWebClient的构造器
   */
  ISiterWebBuilder getWebBuilder();

  /**
   * 在后台缓存所传入的页面，不会直接显示。这个方法会调用WebView的缓存机制。
   *
   * @param context Context
   * @param pages   list
   */
  void cachePageBackground(Context context, List<SiterWebBean> pages);
}

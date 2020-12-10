package me.hekr.sdk.web;

import android.content.Context;

/**
 * Created by hucn on 2017/3/30.
 * Author: hucn
 * Description: 无论是Fragment还是Activity还是其他，如果想使用HekrWeb，都需要实现这个接口
 */

public interface HekrWebInterface {

  /**
   * HekrWeb内部会获取当前activity的Context，请不要使用与当前Activity无关的Context
   */
  Context getContext();

  /**
   * HekrWeb想要关闭当前页面
   */
  void onFinish();
}

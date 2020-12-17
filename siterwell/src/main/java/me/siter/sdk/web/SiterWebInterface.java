package me.siter.sdk.web;

import android.content.Context;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 无论是Fragment还是Activity还是其他，如果想使用Web，都需要实现这个接口
 */

public interface SiterWebInterface {

  /**
   * Web内部会获取当前activity的Context，请不要使用与当前Activity无关的Context
   */
  Context getContext();

  /**
   * Web想要关闭当前页面
   */
  void onFinish();
}

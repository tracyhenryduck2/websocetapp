package me.hekr.sdk.inter;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface HekrWebActionListener {

  /**
   * 可以在此添加加载动画
   *
   * @param url 加载页面初始URL
   */
  void onPageStarted(String url);

  /**
   * 加载页面出错
   *
   * @param errorCode   XWalkResourceClient错误码
   * @param description 错误描述
   */
  void onPageError(int errorCode, String description);

  /**
   * 可在此取消加载动画
   *
   * @param url 页面加载完毕URL
   */
  void onPageFinished(String url);

  /**
   * 控制页面关闭
   */
  void onAllPageClosed();

  /**
   * 加载时是否需要拦截url
   *
   * @param url url
   */
  boolean shouldOverrideUrlLoading(String url);

  /**
   * 跳转本地页面
   *
   * @param url url
   */
  void onJumpNative(String url);

  /**
   * 加载进度
   *
   * @param progressInPercent 请求码
   */
  void onProgressChanged(int progressInPercent);

  /**
   * Web返回背景色
   *
   * @param color 获取到的背景色
   */
  void onGetBackgroundColor(String color);

  /**
   * 二维码扫描
   *
   * @param requestCode 请求码
   */
  void openScan(int requestCode);

  /**
   * 指纹识别
   *
   * @param requestCode 请求码
   */
  void openFingerPrint(int requestCode);

  /**
   * Web调用摄像头
   *
   * @param key 请求码
   */
  void takePhoto(String key);
}

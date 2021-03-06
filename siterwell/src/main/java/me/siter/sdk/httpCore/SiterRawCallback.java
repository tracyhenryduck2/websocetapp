package me.siter.sdk.httpCore;

/**
 *  * Created by TracyHenry on 2020/12/16.
 *  * Author: TracyHenry
 * 回调，这个接口将直接返请求后的http信息。
 **/
public abstract class SiterRawCallback {

  /**
   * 请求成功的返回。
   *
   * @param httpCode http请求的返回码
   * @param bytes    http请求的返回内容，以byte[]形式返回
   */
  public abstract void onSuccess(int httpCode, byte[] bytes);

  /**
   * 请求错误的返回。
   *
   * @param httpCode http请求的返回码
   * @param bytes    http请求的返回内容，以byte[]形式返回
   */
  public abstract void onError(int httpCode, byte[] bytes);
}

package me.siter.sdk.inter;

/**
 *  * Created by TracyHenry on 2020/12/16.
 *  * Author: TracyHenry
 * 回调，这个接口返回成功和错误的信息。
 */

public interface SiterCallback {

  /**
   * 成功的返回。
   */
  void onSuccess();

  /**
   * 错误的返回。
   *
   * @param errorCode 错误码
   * @param message 错误信息
   */
  void onError(int errorCode, String message);
}

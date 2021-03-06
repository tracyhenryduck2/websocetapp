package me.siter.sdk.dispatcher;


/**
 * 如果希望使用消息过滤，那么继承此接口。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public interface IMessageFilter {

  /**
   * 过滤消息，true表示通过，false表示不通过。
   *
   * @param in 从云端或者局域网获取到的所有消息
   */
  boolean doFilter(String in);
}

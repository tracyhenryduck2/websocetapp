package me.hekr.sdk;

import me.hekr.sdk.http.HekrRawCallback;
import me.hekr.sdk.inter.HekrCallback;

/**
 * 用于用户登录和获取登录后的相关信息。一般情况下，用户必须先执行此接口中的
 * {@link #login(String username, String password, HekrCallback callback) login}或者
 * {@link #login(String username, String password, HekrRawCallback callback) login}
 * 方法进行登录，登录成功后才能使用SDK的功能。
 *
 * @author hucn
 */
public interface IHekrUser {

  /**
   * 登录成功后获取token。
   *
   * @return 如果没有登录或者登录不成功，那么此方法返回空字符串。登录成功或者刷新token成功后返回token值，一段时间后请求
   * 服务器时token会失效，请使用{@link #refreshToken(HekrRawCallback callback) refreshToken()}
   * 刷新token。
   */
  String getToken();

  /**
   * 登录成功后获取user id。
   *
   * @return 如果没有登录或者登录不成功，那么此方法返回空字符串。登录成功后返回user id值。
   */
  String getUserId();

  /**
   * 判断token是否过期。这个方法主要用于判断其他http请求返回的错误信息是否是由于token过期导致的。
   *
   * @param httpErrorCode 其他请求时返回的错误信息
   * @param bytes         其他请求返回的错误内容
   */
  boolean tokenIsExpired(int httpErrorCode, byte[] bytes);

  /**
   * 设置用户token。
   *
   * @param jsonString jsonString为登录成功后的String，适合第三方登录等调用之后调用此方法
   */
  void setToken(String jsonString);

  /**
   * 刷新用户token。当用户的token信息过期时，请调用此方法刷新token。
   *
   * @param callback 回调，提供登录后的返回内容，以byte[] 方式返回
   */
  void refreshToken(HekrRawCallback callback);

  /**
   * 使用用户名密码登录。
   *
   * @param username 用户名
   * @param password 密码
   * @param callback 回调 登录成功的信息不会提供，如果没有特殊需求，请使用这个方法。
   * @see HekrCallback
   */
  void login(String username, String password, final HekrCallback callback);

  /**
   * 使用用户名密码登录。
   *
   * @param username 用户名
   * @param password 密码
   * @param callback 回调，提供登录后的返回内容，以byte[] 方式返回
   * @see HekrRawCallback
   */
  void login(String username, String password, final HekrRawCallback callback);

  /**
   * 登出。
   *
   * @param callback 回调，是否登出成功
   * @see HekrCallback
   */
  void logout(HekrCallback callback);
}

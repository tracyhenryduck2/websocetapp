package me.hekr.sdk.http;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: http网络请求的接口
 */

public interface IHttpClient {

  void start();

  void stop();

  void add(HttpRequest request);

}

package me.hekr.sdk.http;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: http网络请求的接口
 */

public interface IHttpClient {

  void start();

  void stop();

  void add(HttpRequest request);

}

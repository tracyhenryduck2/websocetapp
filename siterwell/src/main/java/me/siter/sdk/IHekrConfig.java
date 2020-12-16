package me.siter.sdk;


import me.siter.sdk.http.IHttpClient;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 设备配置上网的接口
 */

public interface IHekrConfig {

  void setHttp(IHttpClient client);

  IConfig getConfig(ConfigType type);

  INewConfig getNewConfig(ConfigType type);

  INewSubConfig getNewSubConfig(ConfigType type);

  INewGatewayConfig getNewGatewayConfig(ConfigType type);
}

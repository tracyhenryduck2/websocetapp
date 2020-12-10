package me.hekr.sdk;


import me.hekr.sdk.http.IHttpClient;

/**
 * Created by hucn on 2017/3/17.
 * Author: hucn
 * Description: 设备配置上网的接口
 */

public interface IHekrConfig {

  void setHttp(IHttpClient client);

  IConfig getConfig(ConfigType type);

  INewConfig getNewConfig(ConfigType type);

  INewSubConfig getNewSubConfig(ConfigType type);

  INewGatewayConfig getNewGatewayConfig(ConfigType type);
}

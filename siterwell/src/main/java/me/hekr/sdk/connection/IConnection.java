package me.hekr.sdk.connection;

import me.hekr.sdk.IMessageRequest;
import me.hekr.sdk.service.ConnOptions;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 与服务器连接和与设备连接的抽象接口
 */

public interface IConnection {

  void bind(ConnOptions options);

  void connect();

  void disconnect();

  void close();

  boolean isClosed();

  void send(IMessageRequest request);

  boolean isConnected();

  void setConnectionStatusListener(ConnectionStatusListener listener);
}

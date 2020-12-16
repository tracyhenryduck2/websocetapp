package me.hekr.sdk.service;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 产生不同连接的工厂
 */

class ConnFactory {

    IAsyncConn getConn(ConnOptions options, String handler) {
        if (options.getconnType() == ConnOptions.TYPE_CONN_TCP_NORMAL) {
            return new TCPConn(options, handler);
        } else if (options.getconnType() == ConnOptions.TYPE_CONN_WEBSOCKET) {
            return new WebSocketConn(options, handler);
        } else if (options.getconnType() == ConnOptions.TYPE_CONN_UDP_NORMAL) {
            return new UdpConn(options, handler);
        } else {
            throw new IllegalArgumentException("The connection type of the option is not correct");
        }
    }
}

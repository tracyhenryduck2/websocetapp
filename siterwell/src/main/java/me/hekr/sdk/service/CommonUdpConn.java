package me.hekr.sdk.service;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;
import me.hekr.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: UDP 可以共用一个端口，所以就抽取成一个共用的类
 */

class CommonUdpConn {

    private static final String TAG = CommonUdpConn.class.getSimpleName();
    // A port number of 0 will let the system pick up an ephemeral port
    private static final int PORT_LOCAL = 0;

    private static CommonUdpConn commonUdpConn = new CommonUdpConn();
    private static EventLoopGroup mGroup;

    private ConcurrentHashMap<String, InetSocketAddress> mUdpMap;
    private UDPThread mCurrentThread;


    static CommonUdpConn getCommon() {
        return commonUdpConn;
    }

    private CommonUdpConn() {
        mUdpMap = new ConcurrentHashMap<>();
        if (mGroup == null) {
            mGroup = NettyGroupFactory.getGroup();
        }
    }

    public synchronized void start() {
        mCurrentThread = new UDPThread();
        mCurrentThread.start();
    }

    public synchronized void stop() {
        if (mCurrentThread != null) {
            mCurrentThread.stopUDP();
            mCurrentThread = null;
        }
    }

    synchronized boolean register(String handler, String ip, int port) {
        if (isActive()) {
            InetSocketAddress address = new InetSocketAddress(ip, port);
            mUdpMap.put(handler, address);
            return true;
        }
        return false;
    }

    synchronized void unregister(String handler) {
        mUdpMap.remove(handler);
    }

    synchronized void send(String message, String handler) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            final InetSocketAddress address = mUdpMap.get(handler);
            LogUtil.d(TAG, "The channel is on, send message: " + message + ", handler is: " + handler + ", channel is: " + address);
            if (address != null) {
                DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), address);
                mCurrentThread.send(packet);
            } else {
                LogUtil.e(TAG, "Udp address is null");
            }
        } else {
            LogUtil.d(TAG, "The udp channel is off, can not send message...");
        }
    }

    synchronized void send(String message, String ip, int port) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            final InetSocketAddress address = new InetSocketAddress(ip, port);
            LogUtil.d(TAG, "The  channel is on, send common message:" + message + ", channel is: " + address);
            DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), address);
            mCurrentThread.send(packet);
        } else {
            LogUtil.d(TAG, "The udp channel is off, can not send message...");
        }
    }

    synchronized boolean isActive() {
        return mCurrentThread != null && mCurrentThread.isActive();
    }

    public int getPort() {
        if (mCurrentThread != null && mCurrentThread.isActive()) {
            return mCurrentThread.getPort();
        } else {
            return 0;
        }
    }

    private class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                // 读取收到的数据
                ByteBuf buf = packet.copy().content();
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                String message = new String(req, CharsetUtil.UTF_8);
                InetAddress address = packet.sender().getAddress();
                String ip = "0.0.0.0";
                if (address != null) {
                    ip = address.getHostAddress();
                }
                int port = packet.sender().getPort();
                LogUtil.d(TAG, "The channel is on, receive message from " + ip + ":" + port + ": " + message);
                Object json = new JSONTokener(message).nextValue();
                if (json instanceof JSONObject) {
                    JSONObject object = new JSONObject(message);
                    if (!object.has("ip")) {
                        object.put("ip", ip);
                    }
                    ServiceMonitor.getInstance().notifyMessageArrived(object.toString()
                            , TextUtils.concat(ip, ":", String.valueOf(port)).toString());
                } else if (json instanceof JSONArray) {
                    ServiceMonitor.getInstance().notifyMessageArrived(message
                            , TextUtils.concat(ip, ":", String.valueOf(port)).toString());
                } else {
                    JSONObject object = new JSONObject();
                    object.put("action", "raw");
                    object.put("data", message);
                    ServiceMonitor.getInstance().notifyMessageArrived(object.toString()
                            , TextUtils.concat(ip, ":", String.valueOf(port)).toString());
                }
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                for (String handler : mUdpMap.keySet()) {
                    ServiceMonitor.getInstance().notifyConnChanged(handler, ConnStatusType.CONN_STATUS_CONNECTED);
                }
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                for (String handler : mUdpMap.keySet()) {
                    ServiceMonitor.getInstance().notifyConnChanged(handler, ConnStatusType.CONN_STATUS_DISCONNECTED);
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                for (String handler : mUdpMap.keySet()) {
                    ServiceMonitor.getInstance().notifyConnError(handler, ConnStatusType.CONN_STATUS_ERROR, cause);
                }
            }
            ctx.close();
        }
    }

    private class UDPThread extends Thread {

        private Channel channel;

        @Override
        public void run() {
            Bootstrap b = new Bootstrap();
            b.group(mGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new UdpHandler());
            try {
                channel = b.bind(PORT_LOCAL).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            LogUtil.d(TAG, "Udp channel open success");
                        } else {
                            LogUtil.d(TAG, "Udp channel open fail");
                        }
                    }
                }).sync().channel();
                channel.closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LogUtil.d(TAG, "UDP channel closed");
            }
        }

        private void send(DatagramPacket packet) {
            if (channel != null) {
                channel.writeAndFlush(packet);
            }
        }

        private boolean isActive() {
            return channel != null && channel.isActive();
        }

        private void stopUDP() {
            if (channel != null) {
                channel.close();
                channel = null;
            }
            interrupt();
        }

        private Channel getChannel() {
            return channel;
        }

        private int getPort() {
            if (channel != null) {
                SocketAddress address = channel.localAddress();
                if (address instanceof InetSocketAddress) {
                    InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
                    LogUtil.d(TAG, inetSocketAddress.toString());
                    return inetSocketAddress.getPort();
                }
            }
            return 0;
        }
    }
}

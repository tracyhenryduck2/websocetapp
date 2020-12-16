package me.siter.sdk.service;

import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.InetAddress;
import java.net.InetSocketAddress;

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
import me.siter.sdk.SiterSDK;
import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: UDP 可以共用一个端口，所以就抽取成一个共用的类
 */

class BroadcastUdpConn implements IAsyncConn {

    private static final String TAG = BroadcastUdpConn.class.getSimpleName();
    // A port number of 0 will let the system pick up an ephemeral port
    private static final int PORT_LOCAL = 24253;

    private static BroadcastUdpConn broadcastUdpConn = new BroadcastUdpConn();
    private static EventLoopGroup mGroup;

    private volatile boolean isRunning;
    private BroadcastThread mCurrentThread;

    private BroadcastUdpConn() {
        if (mGroup == null) {
            mGroup = NettyGroupFactory.getGroup();
        }
    }

    static BroadcastUdpConn getBroadcast() {
        return broadcastUdpConn;
    }

    public synchronized void start() {
        if (isRunning) {
            LogUtil.d(TAG, "The BroadcastUdpConn is running, no need to restart");
            return;
        }
        int perm = SiterSDK.getContext().checkCallingOrSelfPermission("android.permission.INTERNET");
        boolean has_perssion = perm == PackageManager.PERMISSION_GRANTED;
        if (!has_perssion) {
            LogUtil.e(TAG, "Has no permission:android.permission.INTERNET");
            return;
        }

        isRunning = true;
        mCurrentThread = new BroadcastThread();
        mCurrentThread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        if (mCurrentThread != null) {
            mCurrentThread.stopBroadcast();
            mCurrentThread = null;
        }
    }

    @Override
    public synchronized void send(String message) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            LogUtil.d(TAG, "The  channel is on, send message:" + message);
            String ip = getBroadcastIp();
            if (TextUtils.isEmpty(ip) || TextUtils.equals("0.0.0.0", ip)) {
                LogUtil.d(TAG, "The ip is invalid, ip is " + ip);
            } else {
                InetSocketAddress address = new InetSocketAddress(ip, PORT_LOCAL);
                DatagramPacket packet = new DatagramPacket(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8), address);
                mCurrentThread.send(packet);
            }
        } else {
            LogUtil.d(TAG, "The udp channel is off, can not send message...");
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized boolean isActive() {
        return mCurrentThread != null && mCurrentThread.isActive();
    }

    @Override
    public synchronized void reset(ConnOptions options) {
        stop();
        Log.d(TAG, "Reset do nothing except stopping");
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
                LogUtil.d(TAG, "The channel is on, receive message from " + ip + ": " + message);
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
                }
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                Log.d(TAG, "Begin receiving......");
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }
    }

    private class BroadcastThread extends Thread {

        private Channel channel;

        @Override
        public void run() {
            if (!isRunning) {
                return;
            }
            Bootstrap b = new Bootstrap();
            b.group(mGroup)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .option(ChannelOption.SO_REUSEADDR, true)
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

        private void stopBroadcast() {
            if (channel != null) {
                channel.close();
                channel = null;
            }
            interrupt();
        }

        private Channel getChannel() {
            return channel;
        }
    }

    private String getBroadcastIp() {
//        String ip = NetworkUtil.getHostIP();
//        if (TextUtils.isEmpty(ip)) {
//            return "0.0.0.0";
//        } else {
//            String[] ipsegments = ip.split(".");
//            if (ipsegments.length != 4) {
//                return "0.0.0.0";
//            }
//            return ipsegments[0] + "." + ipsegments[1] + "." + ipsegments[2] + ".255";
//        }
        return "255.255.255.255";
    }
}

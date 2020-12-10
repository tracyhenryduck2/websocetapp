package me.hekr.sdk.service;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import me.hekr.sdk.HekrSDK;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 连接接口的实现，TCP连接
 */

class TCPConn implements IAsyncConn {

    private static final String TAG = TCPConn.class.getSimpleName();

    private static EventLoopGroup mGroup;

    private ConnOptions mOptions;
    private String mHandler;
    private volatile boolean isRunning;
    private UDPThread mCurrentThread;

    TCPConn(ConnOptions options, String handler) {
        this.mOptions = new ConnOptions(options.getconnType(), options.getIpOrUrl(), options.getPort());
        this.mHandler = handler;
        if (mGroup == null) {
            mGroup = NettyGroupFactory.getGroup();
        }
    }

    @Override
    public synchronized void start() {
        if (isRunning) {
            LogUtil.d(TAG, "The TCPConn is running, no need to restart");
            return;
        }
        if (!NetworkUtil.isConnected(HekrSDK.getContext())) {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
            return;
        }
        int perm = HekrSDK.getContext().checkCallingOrSelfPermission("android.permission.INTERNET");
        boolean has_perssion = perm == PackageManager.PERMISSION_GRANTED;
        if (!has_perssion) {
            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
            return;
        }

        isRunning = true;
        mCurrentThread = new UDPThread();
        mCurrentThread.start();
    }

    @Override
    public synchronized void stop() {
        isRunning = false;
        if (mCurrentThread != null) {
            mCurrentThread.stopTCP();
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
            mCurrentThread.send(message);
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
        this.mOptions = new ConnOptions(options.getPrefix(), options.getconnType(), options.getIpOrUrl(), options.getPort());
    }

    private class TcpHandler extends SimpleChannelInboundHandler {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                ByteBuf buf = (ByteBuf) msg;
                byte[] req = new byte[buf.readableBytes()];
                buf.readBytes(req);
                String message = new String(req, CharsetUtil.UTF_8);
                String handler = null;
                if (mOptions != null) {
                    handler = mOptions.getIpOrUrl();
                }
                ServiceMonitor.getInstance().notifyMessageArrived(message, handler);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_CONNECTED);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_DISCONNECTED);
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                ServiceMonitor.getInstance().notifyConnError(mHandler, ConnStatusType.CONN_STATUS_ERROR, cause);
            }
            ctx.close();
        }
    }

    private class UDPThread extends Thread {

        private Channel channel;

        @Override
        public void run() {
            if (!isRunning) {
                return;
            }
            ConnOptions options = mOptions;
            if (options == null) {
                return;
            }
            String ip = options.getIpOrUrl();
            int port = options.getPort();
            LogUtil.d(TAG, "Conn start, ip is: " + ip + ", port is: " + port);
            Bootstrap b = new Bootstrap();
            b.group(mGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TcpHandler());
                }
            });
            try {
                channel = b.connect(ip, port).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if (channelFuture.isSuccess()) {
                            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_SUCCESS);
                        } else {
                            ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_FAIL);
                        }
                    }
                }).sync().channel();
                channel.closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LogUtil.d(TAG, "TCP channel closed");
            }
        }

        private void send(String message) {
            if (channel != null) {
                channel.writeAndFlush(message);
            }
        }

        private boolean isActive() {
            return channel != null && channel.isActive();
        }

        private void stopTCP() {
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
}

package me.hekr.sdk.service;

import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.net.URI;
import java.net.URISyntaxException;

import javax.net.ssl.SSLException;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import me.hekr.sdk.HekrSDK;
import me.hekr.sdk.utils.LogUtil;
import me.hekr.sdk.utils.NetworkUtil;

/**
 * Created by hucn on 2017/3/20.
 * Author: hucn
 * Description: 连接接口的实现，WebSocket连接
 */

class WebSocketConn implements IAsyncConn {

    private static final String TAG = WebSocketConn.class.getSimpleName();

    private static final int CONNECTION_TIMEOUT = 10000;
    private static final int CONNECTION_READER_IDLE_SECONDS = 10;
    private static final int CONNECTION_WRITER_IDLE_SECONDS = 0;
    private static final int CONNECTION_ALL_IDLE_SECONDS = 0;

    private static EventLoopGroup mGroup;

    private ConnOptions mOptions;
    private String mHandler;
    private volatile boolean isRunning;
    private WebsocketThread mCurrentThread;

    WebSocketConn(ConnOptions options, String handler) {
        this.mOptions = options;
        this.mHandler = handler;
        if (mGroup == null) {
            mGroup = NettyGroupFactory.getGroup();
        }
    }

    @Override
    public synchronized void start() {
        LogUtil.d(TAG, "Start websocket");
        if (isRunning) {
            LogUtil.d(TAG, "The WebSocketConn is running, no need to restart");
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
        mCurrentThread = new WebsocketThread();
        mCurrentThread.start();
    }

    @Override
    public synchronized void send(String message) {
        if (TextUtils.isEmpty(message)) {
            LogUtil.w(TAG, "Message is null or empty");
            return;
        }
        if (isActive()) {
            LogUtil.d(TAG, "The websocket channel is on, send message: " + message + ", " + "Channel is: " + mOptions);
            WebSocketFrame frame = new TextWebSocketFrame(message);
            mCurrentThread.send(frame);
        } else {
            LogUtil.d(TAG, "The websocket channel is off, can not send message...");
        }
    }

    @Override
    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public synchronized void stop() {
        LogUtil.d(TAG, "Stop current websocket");
        isRunning = false;
        if (mCurrentThread != null) {
            mCurrentThread.stopWebsocket();
            mCurrentThread = null;
        }
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

    private class WebSocketHandler extends SimpleChannelInboundHandler {
        private final WebSocketClientHandshaker handShaker;
        private ChannelPromise handshakeFuture;

        WebSocketHandler(WebSocketClientHandshaker handShaker) {
            this.handShaker = handShaker;
        }

        ChannelFuture handshakeFuture() {
            return handshakeFuture;
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            handshakeFuture = ctx.newPromise();
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                handShaker.handshake(ctx.channel());
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_CONNECTED);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_DISCONNECTED);
            }
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            Channel ch = ctx.channel();
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                if (!handShaker.isHandshakeComplete()) {
                    handShaker.finishHandshake(ch, (FullHttpResponse) msg);
                    LogUtil.d(TAG, "WebSocket conn connected!");
                    ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_SUCCESS);
                    handshakeFuture.setSuccess();
                    return;
                }

                if (msg instanceof FullHttpResponse) {
                    FullHttpResponse response = (FullHttpResponse) msg;
                    throw new IllegalStateException(
                            "Unexpected FullHttpResponse (getStatus=" + response.status() +
                                    ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
                }

                WebSocketFrame frame = (WebSocketFrame) msg;
                if (frame instanceof TextWebSocketFrame) {
                    TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
                    LogUtil.d(TAG, "WebSocket conn received message: " + textFrame.text());
                    String handler = null;
                    if (mOptions != null) {
                        handler = mOptions.getIpOrUrl();
                    }
                    ServiceMonitor.getInstance().notifyMessageArrived(textFrame.text(), handler);
                } else if (frame instanceof PongWebSocketFrame) {
                    LogUtil.d(TAG, "WebSocket conn received pong");
                } else if (frame instanceof CloseWebSocketFrame) {
                    LogUtil.d(TAG, "WebSocket conn received closing");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                if (!handshakeFuture.isDone()) {
                    handshakeFuture.setFailure(cause);
                }
                ServiceMonitor.getInstance().notifyConnError(mHandler, ConnStatusType.CONN_STATUS_ERROR, cause);

            }
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
                IdleStateEvent event = (IdleStateEvent) evt;
                if (event.state() == IdleState.READER_IDLE) {
                    if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                        LogUtil.d(TAG, "Reader idle for 10s");
                    }
                    if (!handShaker.isHandshakeComplete()) {
                        if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                            LogUtil.d(TAG, "The websocket connection takes too long to finish shaking, close the channel");
                        }
                        ctx.close();
                    }
                } else if (event.state() == IdleState.WRITER_IDLE) {
                    if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                        LogUtil.d(TAG, "Write idle");
                    }
                } else if (event.state() == IdleState.ALL_IDLE) {
                    if (mCurrentThread != null && ctx.channel() == mCurrentThread.getChannel()) {
                        LogUtil.d(TAG, "All idle");
                    }
                }
            }
        }
    }

    private class WebsocketThread extends Thread {

        private Channel channel;

        @Override
        public void run() {
            if (!isRunning) {
                return;
            }
            LogUtil.d(TAG, "Start websocket thread");
            try {
                ConnOptions options = mOptions;
                if (options == null) {
                    return;
                }
                String url = options.getIpOrUrl();
                if (mCurrentThread == this) {
                    LogUtil.d(TAG, "Conn start, url is: " + url);
                }
                URI uri = new URI(url);
                String scheme = uri.getScheme();
                if (scheme == null) {
                    throw new IllegalArgumentException("Please check your scheme of the url, which is needed");
                }
                final String host = uri.getHost();
                if (host == null) {
                    throw new IllegalArgumentException("Please check your host of the url, which is needed");
                }
                final int port;
                if (uri.getPort() == -1) {
                    if (options.getPort() > 63335 || options.getPort() < 0) {
                        throw new IllegalArgumentException("Please check your port of the url, which is needed");
                    }
                    port = options.getPort();
                } else {
                    port = uri.getPort();
                }
                if (!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
                    throw new IllegalArgumentException("Illegal agreement,either ws or wss");
                }
                final boolean ssl = "wss".equalsIgnoreCase(scheme);
                final SslContext sslCtx;
                if (ssl) {
                    sslCtx = SslContextBuilder.forClient()
                            .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
                } else {
                    sslCtx = null;
                }

                try {
                    final WebSocketHandler handler =
                            new WebSocketHandler(WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, false, new DefaultHttpHeaders()));
                    Bootstrap b = new Bootstrap();
                    b.group(mGroup)
                            .channel(NioSocketChannel.class)
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT)
                            .handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel ch) {
                                    ChannelPipeline p = ch.pipeline();
                                    p.addLast(new IdleStateHandler(CONNECTION_READER_IDLE_SECONDS,
                                            CONNECTION_WRITER_IDLE_SECONDS,
                                            CONNECTION_ALL_IDLE_SECONDS));
                                    if (sslCtx != null) {
                                        p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                                    }
                                    p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), handler);
                                }
                            });
                    channel = b.connect(uri.getHost(), port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (mCurrentThread == WebsocketThread.this) {
                                if (channelFuture.isSuccess()) {
                                    LogUtil.d(TAG, "Websocket build success");
                                } else {
                                    LogUtil.d(TAG, "Websocket build fail");
                                    // ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_FAIL);
                                }
                            }
                        }
                    }).sync().channel();
                    handler.handshakeFuture().sync();
                    channel.closeFuture().awaitUninterruptibly();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mCurrentThread == this) {
                        ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
                    }
                } finally {
                    if (mCurrentThread == this) {
                        LogUtil.d(TAG, "Websocket channel closed");
                    }
                }
            } catch (SSLException | URISyntaxException e) {
                e.printStackTrace();
                if (mCurrentThread == this) {
                    ServiceMonitor.getInstance().notifyConnChanged(mHandler, ConnStatusType.CONN_STATUS_ERROR);
                }
            }
        }

        private void send(WebSocketFrame frame) {
            if (channel != null) {
                channel.writeAndFlush(frame);
            }
        }

        private boolean isActive() {
            return channel != null && channel.isActive();
        }

        private void stopWebsocket() {
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

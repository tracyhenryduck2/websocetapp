package me.hekr.sdk.service;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by Mike on 2018/2/6.
 * Author:
 * Description:
 */

public class NettyGroupFactory {

    private static NioEventLoopGroup mGroup;

    public static NioEventLoopGroup getGroup() {
        if (mGroup == null) {
            synchronized (NettyGroupFactory.class) {
                if (mGroup == null) {
                    mGroup = new NioEventLoopGroup();
                }
            }
        }

        return mGroup;
    }
}

package me.siter.sdk.service;

import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
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

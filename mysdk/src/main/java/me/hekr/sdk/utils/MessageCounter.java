package me.hekr.sdk.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by hucn on 2017/3/28.
 * Author: hucn
 * Description: 消息计数器
 */

public class MessageCounter {

    private static AtomicInteger mCount = new AtomicInteger(0);

    private MessageCounter(){

    }

    public static int getCount() {
        return mCount.get();
    }

    public static int increaseCount() {
        mCount.compareAndSet(65535, 0);
        return mCount.incrementAndGet();
    }
}

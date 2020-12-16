package me.siter.sdk.dispatcher;

import me.siter.sdk.FilterType;
import me.siter.sdk.IMessageRequest;
import me.siter.sdk.inter.SiterDispatcherListener;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 消息通道的接口
 */

public abstract class IDispatcher {

  abstract void start();

  abstract void stop();

  abstract void enqueue(IMessageRequest request, FilterType type);

  public abstract void addFilter(IMessageFilter filter, SiterMsgCallback callback, long expired);

  abstract void addFilter(IMessageFilter filter, SiterMsgCallback callback);

  abstract void addFilter(String tag, IMessageFilter filter, SiterMsgCallback callback);

  abstract void addFilter(IMessageFilter filter, SiterMsgCallback callback, FilterType type, long expried);

  abstract void removeFilter(String tag);

  abstract void removeFilter(IMessageFilter filter);

  abstract void removeAllFilters();

  abstract int getFilterSize();

  public abstract void enqueue(IMessageRequest request, String ip, int port, FilterType type);

  public abstract void enqueue(IMessageRequest request, FilterType type, long expired);

  public abstract void addDispatcherListener(SiterDispatcherListener listener);

  public abstract void removeDispatcherListener(SiterDispatcherListener listener);
}

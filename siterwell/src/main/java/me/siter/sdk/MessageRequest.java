package me.siter.sdk;

import me.siter.sdk.dispatcher.IMessageFilter;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 内部封装的消息请求
 */

class MessageRequest implements IMessageRequest {

    private String mMessage;
    private IMessageFilter mFilter;
    private SiterMsgCallback mCallback;
    private String mHandler;
    private int mChannel;
    private boolean mCanceled;

    MessageRequest(String message) {
        this(message, null, null);
    }

    MessageRequest(String message, IMessageFilter filter, SiterMsgCallback callback) {
        this.mMessage = message;
        this.mFilter = filter;
        this.mCallback = callback;
    }

    MessageRequest(String message, IMessageFilter filter, SiterMsgCallback callback, int channel, String handler) {
        this.mMessage = message;
        this.mFilter = filter;
        this.mCallback = callback;
        this.mChannel = channel;
        this.mHandler = handler;
    }

    @Override
    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public void setFilter(IMessageFilter filter) {
        mFilter = filter;
    }

    @Override
    public String getMessage() {
        return mMessage;
    }

    @Override
    public IMessageFilter getFilter() {
        return mFilter;
    }

    @Override
    public void setMsgCallback(SiterMsgCallback callback) {
        mCallback = callback;
    }

    @Override
    public SiterMsgCallback getMsgCallback() {
        return mCallback;
    }

    @Override
    public String getHandler() {
        return mHandler;
    }

    @Override
    public void setHandler(String handler) {
        this.mHandler = handler;
    }

    @Override
    public int getChannel() {
        return mChannel;
    }

    @Override
    public void setChannel(int channel) {
        this.mChannel = channel;
    }

    @Override
    public void cancel() {
        mCanceled = true;
    }

    @Override
    public boolean hasCanceled() {
        return mCanceled;
    }
}

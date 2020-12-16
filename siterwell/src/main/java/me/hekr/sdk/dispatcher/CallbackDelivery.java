package me.hekr.sdk.dispatcher;

import android.content.Context;
import android.os.Handler;

import me.hekr.sdk.inter.HekrDispatcherListener;
import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 将回调的消息分发到指定线程
 */

public class CallbackDelivery {

    private Handler mCallbackHanlder;

    public void start(Context context) {
        mCallbackHanlder = new Handler(context.getMainLooper());
    }

    public void stop() {
        mCallbackHanlder.removeCallbacksAndMessages(null);
        mCallbackHanlder = null;
    }

    void PostSuccess(final String message, final HekrMsgCallback callback) {
        if (mCallbackHanlder == null) {
            return;
        }
        mCallbackHanlder.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onReceived(message);
                }
            }
        });
    }

    void postTimeout(final HekrMsgCallback callback) {
        if (mCallbackHanlder == null) {
            return;
        }
        mCallbackHanlder.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onTimeout();
                }
            }
        });
    }

    void postError(final int errorCode, final String errorMsg, final HekrMsgCallback callback) {
        if (mCallbackHanlder == null) {
            return;
        }
        mCallbackHanlder.post(new Runnable() {
            @Override
            public void run() {
                // TODO: 2017/4/11 确定错误信息和错误码
                if (callback != null) {
                    callback.onError(errorCode, errorMsg);
                }
            }
        });
    }

    void postSend(final String message, final String to, final HekrDispatcherListener listener) {
        if (mCallbackHanlder == null) {
            return;
        }
        mCallbackHanlder.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onSend(message, to);
                }
            }
        });
    }

    void postReceive(final String message, final String from, final HekrDispatcherListener listener) {
        if (mCallbackHanlder == null) {
            return;
        }
        mCallbackHanlder.post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onReceive(message, from);
                }
            }
        });
    }
}

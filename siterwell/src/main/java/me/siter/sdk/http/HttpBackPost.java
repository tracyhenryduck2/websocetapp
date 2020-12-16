package me.siter.sdk.http;

import android.content.Context;
import android.os.Handler;

import java.util.HashMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 返回类，主要作用是为了分发到主线程
 */

class HttpBackPost {

    private final Handler mHandler;

    HttpBackPost(Context context) {
        mHandler = new Handler(context.getMainLooper());
    }

    void postSuccess(HttpResponse response, BaseHttpResponse baseResponse) {
        mHandler.post(new SuccessRunnable(response, baseResponse));
    }

    void postFail(HttpResponse response, BaseHttpResponse baseResponse) {
        mHandler.post(new FailRunnable(response, baseResponse));
    }

    void postError(HttpResponse response, int errorCode) {
        mHandler.post(new ErrorRunnable(response, errorCode));
    }

    private class SuccessRunnable implements Runnable {

        HttpResponse response;
        BaseHttpResponse baseResponse;

        SuccessRunnable(HttpResponse response, BaseHttpResponse baseResponse) {
            this.response = response;
            this.baseResponse = baseResponse;
        }

        @Override
        public void run() {
            response.onSuccess(baseResponse.getCode(), baseResponse.getHeaders(), baseResponse.getData());
        }
    }

    private class FailRunnable implements Runnable {

        HttpResponse response;
        BaseHttpResponse baseResponse;


        FailRunnable(HttpResponse response, BaseHttpResponse baseResponse) {
            this.response = response;
            this.baseResponse = baseResponse;
        }

        @Override
        public void run() {
            response.onError(baseResponse.getCode(), baseResponse.getHeaders(), baseResponse.getData());
        }
    }

    private class ErrorRunnable implements Runnable {

        HttpResponse response;
        int errorCode;


        ErrorRunnable(HttpResponse response, int errorCode) {
            this.response = response;
            this.errorCode = errorCode;
        }

        @Override
        public void run() {
            response.onError(errorCode, new HashMap<String, String>(), new byte[0]);
        }
    }
}

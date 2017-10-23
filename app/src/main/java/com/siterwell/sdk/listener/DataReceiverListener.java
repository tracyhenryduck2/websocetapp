package com.siterwell.sdk.listener;

/**
 * Created by Administrator on 2017/10/16.
 */

public interface DataReceiverListener {

    void onReceiveSuccess(String msg);

    void onReceiveTimeout();

}

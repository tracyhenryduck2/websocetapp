package com.siterwell.sdk.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import com.litesuits.android.log.Log;
import com.siterwell.sdk.event.DebugEvent;
import com.siterwell.sdk.event.LogoutEvent;
import com.siterwell.sdk.util.ViewWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrViewDebugService extends Service {
    private static final String TAG = "HekrViewDebugService";
    public static final String NAME = "me.hekr.hekrsdk.service.HekrViewDebugService";

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        Log.d(TAG, "onCreate: ");
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        Log.d(TAG, "onDestroy: ");
        ViewWindow.removeView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DebugEvent event) {
        if (!TextUtils.isEmpty(event.getDebugLog())) {
            android.util.Log.d(TAG, "onEvent: " + event.getDebugLog());
            ViewWindow.showView(this, event.getDebugLog(), event.getColorResId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LogoutEvent event) {
        Log.d(TAG, "退出: ");
        if (event.isLogout()) {
            ViewWindow.clearView();
        }
    }

}

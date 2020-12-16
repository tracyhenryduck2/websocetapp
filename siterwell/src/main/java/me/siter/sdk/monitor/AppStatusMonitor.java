package me.siter.sdk.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.Display;

import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.HekrSDK;
import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 监听网络的变化
 */

public class AppStatusMonitor {

    private static final String TAG = AppStatusMonitor.class.getSimpleName();

    private CopyOnWriteArrayList<AppStatusObservable> mObservables;

    private ScreenReceiver mReceiver;

    private static AppStatusMonitor instance;

    public static AppStatusMonitor getInstance() {
        if (instance == null) {
            synchronized (NetworkMonitor.class) {
                if (instance == null) {
                    instance = new AppStatusMonitor();
                }
            }
        }
        return instance;
    }

    private AppStatusMonitor() {
        mReceiver = new ScreenReceiver();
        mObservables = new CopyOnWriteArrayList<>();
    }

    public void startMonitor() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        HekrSDK.getContext().registerReceiver(mReceiver, filter);
    }

    public void stopMonitor() {
        HekrSDK.getContext().unregisterReceiver(mReceiver);
    }

    private class ScreenReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                LogUtil.d(TAG, "Screen on");
                notifyScreenOn();
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                LogUtil.d(TAG, "Screen off");
                notifyScreenOff();
            }
        }
    }

    private boolean getScreenState() {
        boolean isScreenOn = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            DisplayManager manager = (DisplayManager) HekrSDK.getContext().getSystemService(Context.DISPLAY_SERVICE);
            for (Display display : manager.getDisplays()) {
                if (display.getState() != Display.STATE_OFF) {
                    isScreenOn = true;
                }
            }
        } else {
            PowerManager manager = (PowerManager) HekrSDK.getContext()
                    .getSystemService(Context.POWER_SERVICE);
            isScreenOn = manager.isScreenOn();
        }
        return isScreenOn;
    }

    private void notifyScreenOn() {
        for (AppStatusObservable observable : mObservables) {
            observable.onScreenOn();
        }
    }

    private void notifyScreenOff() {
        for (AppStatusObservable observable : mObservables) {
            observable.onScreenOff();
        }
    }

    public void add(AppStatusObservable observable) {
        if (!mObservables.contains(observable)) {
            mObservables.add(observable);
        }
    }

    public void remove(AppStatusObservable observable) {
        mObservables.remove(observable);
    }
}

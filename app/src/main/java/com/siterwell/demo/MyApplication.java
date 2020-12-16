package com.siterwell.demo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.siterwell.demo.common.CrashHandler;
import com.siterwell.sdk.common.SitewellSDK;

import java.util.List;

import me.hekr.sdk.HekrSDK;


/**
 * Created by hekr_jds on 6/30 0030.
 **/
public class MyApplication extends MultiDexApplication {
    private static MyApplication mApp;
    public static Activity sActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        //初始化HekrSDK
        HekrSDK.init(getApplicationContext(), R.raw.config);
        HekrSDK.enableDebug(true);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCacheExtraOptions(80, 80)
                .denyCacheImageMultipleSizesInMemory()
                //.writeDebugLogs()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .build();
        ImageLoader.getInstance().init(config);
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                sActivity=activity;

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
        SitewellSDK.getInstance(this).init();
        CrashHandler.getInstance().init(getApplicationContext());
    }
    public static Context getAppContext() {
        return mApp;
    }
    public static Activity getActivity(){
        return sActivity;
    }


}
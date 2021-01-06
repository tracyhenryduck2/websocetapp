package com.siterwell.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.siterwell.demo.common.CrashHandler;
import com.siterwell.demo.listener.SitewellSDK;

import me.siter.sdk.SiterSDK;


/**
 * Created by TracyHenry on 2020/12/16.
 **/
public class MyApplication extends MultiDexApplication {
    private static MyApplication mApp;
    public static Activity sActivity;


    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        //初始化SDK
        SiterSDK.init(getApplicationContext(), R.raw.config);
        SiterSDK.enableDebug(true);
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

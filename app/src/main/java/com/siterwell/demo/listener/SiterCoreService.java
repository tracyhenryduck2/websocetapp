package com.siterwell.demo.listener;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.siterwell.demo.MainActivity;
import com.siterwell.demo.MyApplication;
import com.siterwell.demo.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import me.siter.sdk.Constants;
import me.siter.sdk.Siter;
import me.siter.sdk.dispatcher.IMessageFilter;
import me.siter.sdk.inter.SiterMsgCallback;

/**
 * Created by TracyHenry on 2018/4/25.
 */

public class SiterCoreService extends Service {
    private final static String TAG = "SiterCoreService";
    private static final String CHANNEL_ID = "SiterCoreService";
    private static final String CHANNEL_NAME = "My Background Service";
    private static final int NOTIFICATION_ID = 101;
    private IMessageFilter filter;
    private SiterReceiver siterReceiver;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        startForeground();
    }

    private void startForeground() {
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel();
        }

        Intent intent = new Intent(this, MyApplication.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        Notification notification = builder.setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setContentTitle(getString(R.string.app_name))
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        getNotificationManager().createNotificationChannel(channel);
        return CHANNEL_ID;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        receiveAllMessage();
        if(siterReceiver==null){
            siterReceiver = new SiterReceiver();
            registerReceiver(siterReceiver, new IntentFilter(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE));
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(siterReceiver);
    }


    /**
     * 主动接收消息
     */
    private void receiveAllMessage() {
        if(filter == null){
            filter = new IMessageFilter() {
                @Override
                public boolean doFilter(String in) {
                    return true;
                }
            };
            Siter.getSiterClient().receiveMessage(filter, new SiterMsgCallback() {
                @Override
                public void onReceived(String msg) {
                    // 收到消息
                    Intent intent = new Intent(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(Constants.WS_PAYLOAD,msg);
                    sendBroadcast(intent);
                }

                @Override
                public void onTimeout() {
                    // 主动接受不会有这个回调
                    if(SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()!=null){
                        for(TimeOutListener timeOutListener: SitewellSDK.getInstance(SiterCoreService.this).getTimeoutListeners()){
                            timeOutListener.timeout();
                        }
                    }
                }

                @Override
                public void onError(int errorCode, String message) {
                    // 接收错误
                    Intent intent = new Intent(Constants.ActionStrUtil.ACTION_WS_DATA_RECEIVE);
                    intent.putExtra(Constants.WS_PAYLOAD,message);
                    sendBroadcast(intent);
                }
            });
        }

    }






}

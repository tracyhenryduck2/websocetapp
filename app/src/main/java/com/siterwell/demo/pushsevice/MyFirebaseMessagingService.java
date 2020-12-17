package com.siterwell.demo.pushsevice;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.siterwell.demo.MainActivity;
import com.siterwell.demo.R;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.device.bean.WaterSensorDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.http.UserAction;
import com.siterwell.sdk.http.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.i(TAG, "From: " + remoteMessage.getFrom());

        try {


            if(TextUtils.isEmpty(UserAction.getInstance(this).getJWT_TOKEN())){

                return;
            }
            Log.i(TAG, "Message data payload: " + remoteMessage.getData());        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            JSONObject jsonObject = new JSONObject(remoteMessage.getData());
            String alarmTitle = jsonObject.getString("title");


            String devid = jsonObject.getString("devTid");
            DeviceDao deviceDao = new DeviceDao(this);
            DeviceBean deviceBean = deviceDao.findDeviceBySid(devid);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("devTid",devid);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 35, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            // 通过Notification.Builder来创建通知，注意API Level
            // API16之后才支持
            String ds = getResources().getString(R.string.warning_alert);
            String ttt = "";
            String content = "";
            String title = "";
            if(TextUtils.isEmpty(deviceBean.getDeviceName())){
                if("GS140".equals(deviceBean.getModel())){
                    ttt = getResources().getString(R.string.battery);
                }else if("GS351".equals(deviceBean.getModel())){
                    ttt = getResources().getString(R.string.socket);
                }else if("GS156W".equals(deviceBean.getModel())){
                    ttt = getResources().getString(R.string.watersensor);
                }else{
                    ttt = getResources().getString(R.string.some_device);
                }
            }else{
                ttt = deviceBean.getDeviceName();
                if(ttt.equals("Battery-91"))
                    ttt = "Unijem Battery";
            }

                if("GS140".equals(deviceBean.getModel())){
                    title = BatteryDescBean.getStatusShortString(alarmTitle);
                    content = String.format(ds,ttt,BatteryDescBean.getStatusShortString(alarmTitle));

                }else if("GS156W".equals(deviceBean.getModel())){
                    ttt = getResources().getString(R.string.watersensor);
                    title = WaterSensorDescBean.getStatusShortString(alarmTitle);
                    content = String.format(ds,ttt,WaterSensorDescBean.getStatusShortString(alarmTitle));
                }

            Notification.Builder builder = new Notification.Builder(this)
//                        .setContentTitle(getResources().getString(R.string.dialog_title_alert))
                    .setContentTitle(title)
                    .setContentText(content)
                    .setContentIntent(pendingIntent)
                    .setContentIntent(pendingIntent);
            //兼容nexusandroid5.0
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                builder.setSmallIcon(R.mipmap.ic_launcher_alpha);
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher);
            }
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
            notification.defaults |= Notification.DEFAULT_SOUND;
            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
            long [] pattern = {300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400,300,400}; // 停止 开启 停止 开启
            vibrator.vibrate(pattern,-1); //重复两次上面的pattern 如果只想震动一次，index设为-1
            manager.notify(1,notification);

        }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.i(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.i(TAG, "Short lived task is done.");
    }


    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
        Log.i(TAG,"onDeletedMessages");
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}

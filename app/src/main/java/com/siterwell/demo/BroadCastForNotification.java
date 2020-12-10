package com.siterwell.demo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;

public class BroadCastForNotification extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		if(bundle != null) {
			String devId = bundle.get(ServiceConstant.DEVICE_ID).toString();
			if((int)bundle.get(NotificationConstant.CODE) == (int)NotificationConstant.SILENCE_CODE) {
				createNotification(context, devId+" is active", "Device is active now", NotificationConstant.SILENCE_CODE, devId);
			}
			else if((int)bundle.get(NotificationConstant.CODE) == (int)NotificationConstant.LOW_CHARGE) {
				createNotification(context, devId+" has LOW BATTERY", "Device battery is low", NotificationConstant.SILENCE_CODE, devId);
			}

		}
	}

	private void createNotification(Context context, String title, String subtitle, int code, String tag) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.mipmap.ic_launcher);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(subtitle);

		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);

		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);

		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// notificationID allows you to update the notification later on.
		mNotificationManager.notify(tag, code, mBuilder.build());
	}
}

package com.siterwell.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.siterwell.demo.BroadCastForNotification;
import com.siterwell.demo.NotificationConstant;
import com.siterwell.demo.ServiceConstant;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.folder.bean.LocalFolderBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.storage.FolderDao;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.SycBatteryStatusListener;
import com.siterwell.sdk.http.bean.DeviceBean;

public class BatteryNotificationService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LocalFolderBean folderBean;
		DeviceDao deviceDao = new DeviceDao(getApplicationContext());

		Bundle bundle = intent.getExtras();
		if(bundle != null){
			folderBean = (LocalFolderBean) intent.getSerializableExtra(ServiceConstant.FOLDER_BEAN);

			hitQueryInTime(deviceDao, folderBean);

		}
		return START_REDELIVER_INTENT;
	}


	private void createNotificationForBatteryOver(String devId) {
		// Create an intent that will be wrapped in PendingIntent
		Intent intent = new Intent(this, BroadCastForNotification.class);
		intent.putExtra(NotificationConstant.CODE, NotificationConstant.LOW_CHARGE);
		intent.putExtra(ServiceConstant.DEVICE_ID,devId);

		// Create the pending intent and wrap our intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NotificationConstant.LOW_CHARGE, intent, PendingIntent.FLAG_CANCEL_CURRENT );

		// Get the alarm manager service and schedule it to go off after 3s
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pendingIntent);

	}

	private void hitQueryInTime(final DeviceDao deviceDao, final LocalFolderBean folderBean){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				List<DeviceBean> datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());

				//List<BatteryBean> list = new ArrayList();
				for(int  i = 0; i < datalist.size(); i++){
					String devTid = datalist.get(i).getDevTid();
					BatteryDescBean batteryDescBean = deviceDao.findBatteryBySid(devTid);
					if (batteryDescBean.getBattPercent() <= 20) {
						createNotificationForBatteryOver(batteryDescBean.getDevTid());
					}

					/*BatteryBean bean = new BatteryBean();
					bean.setCtrlKey(datalist.get(i).getCtrlKey());
					bean.setDevTid(datalist.get(i).getDevTid());
					list.add(bean);*/
				}

				/*SitewellSDK.getInstance(getApplicationContext()).queryBaterriesStatus(list, new SycBatteryStatusListener() {
					@Override
					public void success(List<BatteryBean> list) {
						if(list != null) {
							for(int i=0; i< list.size(); i++ ) {
								BatteryBean bean = list.get(i);
								if (bean.getBattPercent() <= 20) {
									createNotificationForBatteryOver(bean.getDevTid());
								}
							}
						}

					}

					@Override
					public void error(int i) {

					}
				});*/
			}
		},0, 1000);
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}

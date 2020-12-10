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
import android.support.annotation.Nullable;

import com.siterwell.demo.BroadCastForNotification;
import com.siterwell.demo.NotificationConstant;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.folder.bean.LocalFolderBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.SycBatteryStatusListener;
import com.siterwell.sdk.http.HekrUserAction;
import com.siterwell.sdk.http.bean.DeviceBean;

public class SilenceOverNotificationService extends IntentService {
	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 *
	 */
	public SilenceOverNotificationService(){
		super("silenceService");
	}
	public SilenceOverNotificationService(String name) {
		super(name);
	}

	@Override
	public void onHandleIntent(Intent intent) {
		Bundle bundle = intent.getExtras();
		DeviceDao deviceDao = new DeviceDao(getApplicationContext());

		if(bundle != null){
			BatteryBean bean = new BatteryBean();
			LocalFolderBean folderBean = (LocalFolderBean) intent.getSerializableExtra(ServiceConstant.FOLDER_BEAN);
			List<DeviceBean> datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
			bean.setDevTid(bundle.get(ServiceConstant.DEVICE_ID).toString());
			bean.setCtrlKey(bundle.get(ServiceConstant.CTRL_KEY).toString());
			List<BatteryBean> list = new ArrayList<>();
			list.add(bean);
			hitQueryInTime(datalist, ServiceConstant.DEVICE_ID, deviceDao);

		}

	}

	private void createNotificationForSilenceOver(String devId) {
		// Create an intent that will be wrapped in PendingIntent
		Intent intent = new Intent(this, BroadCastForNotification.class);
		intent.putExtra(NotificationConstant.CODE, NotificationConstant.SILENCE_CODE);
		intent.putExtra(ServiceConstant.DEVICE_ID,devId);

		// Create the pending intent and wrap our intent
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, NotificationConstant.SILENCE_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT );

		// Get the alarm manager service and schedule it to go off after 3s
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100, pendingIntent);

	}

	private void hitQueryInTime(final List<DeviceBean> datalist, final String devId, final DeviceDao deviceDao){
		final Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				for(int  i = 0; i < datalist.size(); i++){
					String devTid = datalist.get(i).getDevTid();
					BatteryDescBean batteryDescBean = deviceDao.findBatteryBySid(devTid);
					if (batteryDescBean.getStatus() == 0) {
						createNotificationForSilenceOver(devId);
						timer.cancel();
						stopSelf();
					}


				}
				/*SitewellSDK.getInstance(getApplicationContext()).queryBaterriesStatus(list, new SycBatteryStatusListener() {
					@Override
					public void success(List<BatteryBean> list) {
						if(list != null) {
							BatteryBean bean = list.get(0);
							if(bean.getStatus() == 0) {
								createNotificationForSilenceOver(devId);
								stopSelf();
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
}

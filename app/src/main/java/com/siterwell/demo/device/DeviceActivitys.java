package com.siterwell.demo.device;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;


import com.siterwell.demo.MyApplication;
import com.siterwell.demo.R;
import com.siterwell.demo.bean.DeviceType;
import me.siter.sdk.http.bean.DeviceBean;


public class DeviceActivitys {

	private static Map<String, Class<?>> sDeviceActivityMap = new HashMap<String, Class<?>>();
	private static Map<String,String> sDeviceNameMap = new HashMap<String,String>();
	
	static {
		// 监控设备
		sDeviceActivityMap.put(DeviceType.BATTERY.toString(),
				BatteryDetailActivity.class);
		sDeviceActivityMap.put(DeviceType.WIFISOKECT.toString(),SocketDetatilActivity.class);
		sDeviceActivityMap.put(DeviceType.WATERSENEOR.toString(), WaterSensorDetailActivity.class);
		sDeviceNameMap.put(DeviceType.BATTERY.toString(), MyApplication.getActivity().getResources().getString(R.string.battery));
		sDeviceNameMap.put(DeviceType.WIFISOKECT.toString(), MyApplication.getActivity().getResources().getString(R.string.socket));
		sDeviceNameMap.put(DeviceType.WATERSENEOR.toString(), MyApplication.getActivity().getResources().getString(R.string.watersensor));
	}
	
	public static void startDeviceDetailActivity(Context context, DeviceBean deviceBean) {
		Class<?> a = sDeviceActivityMap.get(deviceBean.getModel());
		if ( null != a ) {
			Intent intent = new Intent();
			intent.setClass(context, a);
			intent.putExtra("deviceId", deviceBean.getDevTid());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}

	public static String getDeviceType(DeviceBean deviceBean) {
		String a = sDeviceNameMap.get(deviceBean.getModel());
		return a;
	}
	
	public static Class<?> getDeviceActivity(DeviceBean deviceBean) {
		if ( null == deviceBean ) {
			return null;
		}
		return sDeviceActivityMap.get(deviceBean.getModel());
	}
}

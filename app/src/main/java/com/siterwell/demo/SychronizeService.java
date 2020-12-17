package com.siterwell.demo;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import androidx.annotation.Nullable;

import com.siterwell.demo.BusEvents.GetDeviceStatusEvent;
import com.siterwell.demo.BusEvents.LogoutEvent;
import com.siterwell.demo.BusEvents.RefreshEvent;
import com.siterwell.demo.BusEvents.SychronizeEvent;
import com.siterwell.demo.common.ECPreferenceSettings;
import com.siterwell.demo.common.ECPreferences;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.device.DeviceActivitys;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.storage.WifiTimerDao;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.DeviceType;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WaterSensorBean;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.common.GetDeviceListListener;
import com.siterwell.sdk.common.RefreshBatteryListener;
import com.siterwell.sdk.common.RefreshWaterSensorListener;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.TokenTimeoutListener;
import com.siterwell.sdk.common.WIFISocketListener;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;
import com.siterwell.sdk.http.bean.DeviceBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import me.siter.sdk.Siter;
import me.siter.sdk.http.SiterRawCallback;


/**
 * Created by Administrator on 2017/7/26.
 */

public class SychronizeService extends Service implements RefreshBatteryListener,RefreshWaterSensorListener,WIFISocketListener,TokenTimeoutListener{
    private final static String TAG = "SychronizeService";
    private int count = 0;
    private List<String> folderlist;
    private ECAlertDialog ecAlertDialog;
    private DeviceDao deviceDao;
    private WifiTimerDao wifiTimerDao;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
        deviceDao = new DeviceDao(this);
        wifiTimerDao = new WifiTimerDao(this);
        SitewellSDK.getInstance(this).addRefreshWaterSensorListener(this);
        SitewellSDK.getInstance(this).addRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).addWifiSocketListener(this);
        SitewellSDK.getInstance(this).addTokenTimeoutListener(this);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件FirstEvent
    public  void onEventMainThread(SychronizeEvent event){

        if(event.getFolderids()!=null){

            if(event.getFolderids().size()>0 && count == 0){
                folderlist = event.getFolderids();
                getFolderdevices();
            }


        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件FirstEvent
    public  void onEventMainThread(GetDeviceStatusEvent event){

         if(event.getDeviceBeans()!=null && event.getDeviceBeans().size()>0){
            queryFolderDevices(event.getDeviceBeans());
        }

    }



    private String getUsername(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_USERNAME;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private String getPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PASSWORD;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }



    @Override
    public void onDestroy(){
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(null);
        SitewellSDK.getInstance(this).removeRefreshWaterSensorListener(this);
        SitewellSDK.getInstance(this).removeRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).removeWifiSocketListener(this);
        SitewellSDK.getInstance(this).removeTokenTimeoutListener(this);
    }



    private void getFolderdevices(){

       final String ds = folderlist.get(count);
        Log.i(TAG,"同步文件夹:"+folderlist.get(count));

        HekrUserAction.getInstance(this).getFoldDeviceList(this, 0, 20, ds, new GetDeviceListListener() {
            @Override
            public void succuss(List<DeviceBean> deviceBeans) {
                try {
                    DeviceDao deviceDao = new DeviceDao(SychronizeService.this);
                    List<DeviceBean> datalistold =  deviceDao.findAllDeviceBeanByFolderId(ds);


                    for(int i=0;i<datalistold.size();i++){
                        boolean flag = false;
                        for(DeviceBean deviceBean:deviceBeans){
                            if(deviceBean.getDevTid().equals(datalistold.get(i).getDevTid())){
                                flag = true;
                                break;
                            }
                        }

                        if(!flag){
                            deviceDao.deleteByDeviceId(datalistold.get(i).getDevTid());
                        }

                    }

                    deviceDao.insertDeviceList(deviceBeans);

                    handler.sendEmptyMessage(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void error(int error) {
                handler.sendEmptyMessage(1);
            }
        });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    count++;

                    if(count<folderlist.size()){
                        getFolderdevices();
                    }else{

                        Log.i(TAG,"结束同步服务");
                        DeviceDao deviceDao = new DeviceDao(SychronizeService.this);
                        List<DeviceBean> deviceBeanList = deviceDao.findAllDevice();
                        queryFolderDevices(deviceBeanList);
                    }
                    break;
            }
        }
    };


    private void queryFolderDevices(List<DeviceBean> deviceBeanList){

        HekrUserAction.getInstance(this).getGS140AndGS156WCurrentStatus(deviceBeanList, new HekrUser.GetGS140AndGS156WListener() {
            @Override
            public void getSuccess(List<BatteryBean> batteryBeanList, List<WaterSensorBean> WaterSensorBeanList) {
                DeviceDao deviceDao = new DeviceDao(SychronizeService.this);
                if(batteryBeanList!=null && batteryBeanList.size()>0){
                    deviceDao.updateBatterysList(batteryBeanList);
                }
                if(WaterSensorBeanList!=null && WaterSensorBeanList.size()>0){
                    deviceDao.updateWaterSensorsList(WaterSensorBeanList);
                }
                RefreshEvent refreshEvent = new RefreshEvent();
                EventBus.getDefault().post(refreshEvent);
                count = 0;
            }

            @Override
            public void getFail(int errorCode) {
                Log.i(TAG,"Sychronize err:"+errorCode);
            }
        });

    }


    /**
     * AlertShow:
     * 作者：Henry on 2017/3/13 15:26
     * 邮箱：xuejunju_4595@qq.com
     * 参数:alarm 代表警告类型id, deviceid设备id
     * 返回:
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void AlertShow(int alarm,String deviceid){

        //若是0代表正常，不弹框;
        if(alarm == 0) return;

        if(!UnitTools.isApplicationBroughtToBackground(this)){
            try {
                DeviceDao deviceDao = new DeviceDao(this);
                BatteryDescBean batteryBean = deviceDao.findBatteryBySid(deviceid);

                String ds = getResources().getString(R.string.warning_alert);

                String ttt1;
                if(TextUtils.isEmpty(batteryBean.getDeviceName())){
                    if(DeviceType.BATTERY.toString().equals(batteryBean.getModel())){
                        ttt1 = getResources().getString(R.string.battery);
                    }else if(DeviceType.WIFISOKECT.toString().equals(batteryBean.getModel())){
                        ttt1 =getResources().getString(R.string.socket);
                    }else if(DeviceType.WATERSENEOR.toString().equals(batteryBean.getModel())){
                        ttt1 = getResources().getString(R.string.watersensor);
                    }else{
                        ttt1 = getResources().getString(R.string.some_device);
                    }
                }else{
                    ttt1 = batteryBean.getDeviceName();
                    if(ttt1.equals("Battery-91"))
                        ttt1 = "Unijem Battery";
                }



                String title = String.format(ds,ttt1,batteryBean.getStatusDtail(alarm));
                if(ecAlertDialog==null||!ecAlertDialog.isShowing()){
                    ecAlertDialog = ECAlertDialog.buildAlert(MyApplication.getActivity(),
                            title,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        UnitTools.stopMusic(SychronizeService.this);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                            });
                    ecAlertDialog.setCancelable(false);
                    ecAlertDialog.setCanceledOnTouchOutside(false);
                    ecAlertDialog.show();
                }
                try {
                    UnitTools.playNotifycationMusic(SychronizeService.this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }catch (Exception e){
                Log.i(TAG,"no this scene");
            }
        }


    }


    @Override
    public void RefreshBattery(BatteryBean batteryBean) {
        BatteryDescBean batteryDescBean1 = new BatteryDescBean();
        batteryDescBean1.setDevTid(batteryBean.getDevTid());
        batteryDescBean1.setStatus(batteryBean.getStatus());
        batteryDescBean1.setBattPercent(batteryBean.getBattPercent());
        batteryDescBean1.setSignal(batteryBean.getSignal());
        deviceDao.updateBatteryStatus(batteryDescBean1);
        AlertShow(batteryBean.getStatus(),batteryBean.getDevTid());
    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {

    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {
        try {
            deviceDao.updateDeviceWifiSocketAllInfo(socketBean);
            wifiTimerDao.insertTimerList(socketBean.getWifiTimerBeans());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void deviceOffLineError() {

    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {
        deviceDao.updateDeviceWifiSocketInfo(socketBean);
    }

    @Override
    public void setCircleConfigSuccess(SocketBean socketBean) {

    }

    @Override
    public void setCountDownConfigSuccess(SocketBean socketBean) {

    }

    @Override
    public void setTimerConfigSuccess(WifiTimerBean wifiTimerBean) {

    }

    @Override
    public void deleteTimerSuccess(String id) {

    }

    @Override
    public void circleFinish(SocketBean socketBean) {
        deviceDao.updateDeviceWifiSocketInfo(socketBean);
        DeviceBean deviceBean2 = deviceDao.findDeviceBySid(socketBean.getDevTid());
        String name2 = TextUtils.isEmpty(deviceBean2.getDeviceName())?(DeviceActivitys.getDeviceType(deviceBean2)+socketBean.getDevTid()):deviceBean2.getDeviceName();
        ecAlertDialog = ECAlertDialog.buildPositiveAlert(MyApplication.getActivity(),name2+getResources().getString(R.string.circle_socket_switch), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ecAlertDialog.setCanceledOnTouchOutside(false);
        ecAlertDialog.show();
    }

    @Override
    public void countdownFinish(SocketBean socketBean) {
        deviceDao.updateDeviceWifiSocketInfo(socketBean);
        DeviceBean deviceBean3 = deviceDao.findDeviceBySid(socketBean.getDevTid());
        String name3 = TextUtils.isEmpty(deviceBean3.getDeviceName())?(DeviceActivitys.getDeviceType(deviceBean3)+socketBean.getDevTid()):deviceBean3.getDeviceName();
        ecAlertDialog = ECAlertDialog.buildPositiveAlert(MyApplication.getActivity(),name3+getResources().getString(R.string.countdown_socket_switch), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ecAlertDialog.setCanceledOnTouchOutside(false);
        ecAlertDialog.show();
    }

    @Override
    public void timerFinish(SocketBean socketBean, String timerid) {
        deviceDao.updateDeviceWifiSocketInfo(socketBean);
        String timercomplete = String.format(getResources().getString(R.string.timer_socket_switch),Integer.parseInt(timerid));

        DeviceBean deviceBean4 = deviceDao.findDeviceBySid(socketBean.getDevTid());
        String name4 = TextUtils.isEmpty(deviceBean4.getDeviceName())?(DeviceActivitys.getDeviceType(deviceBean4)+socketBean.getDevTid()):deviceBean4.getDeviceName();
        ecAlertDialog = ECAlertDialog.buildPositiveAlert(MyApplication.getActivity(),name4+timercomplete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        ecAlertDialog.setCanceledOnTouchOutside(false);
        ecAlertDialog.show();
    }

    @Override
    public void unknowError() {

    }

    @Override
    public void RefreshWaterSensor(WaterSensorBean waterSensorBean) {
        deviceDao.updateWaterSensor(waterSensorBean);
        AlertShow(waterSensorBean.getStatus(),waterSensorBean.getDevTid());
    }

    @Override
    public void tokentimeout() {
        Siter.getSiterUser().refreshToken(new SiterRawCallback() {
            @Override
            public void onSuccess(int httpCode, byte[] bytes) {
                Log.i(TAG,"刷新accesstoken成功");
            }

            @Override
            public void onError(int httpCode, byte[] bytes) {
                if(httpCode == 1){

                    LogoutEvent logoutEvent = new LogoutEvent();
                    EventBus.getDefault().post(logoutEvent);

                }
            }
        });
    }
}

package com.siterwell.sdk.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WaterSensorBean;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.http.SiterConstantsUtil;
import com.siterwell.sdk.protocol.ResolveSocket;
import com.siterwell.sdk.protocol.ResolveTimer;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by ST-020111 on 2017/4/14.
 */

public class SiterReceiver extends BroadcastReceiver {

    private final String TAG ="SiterReceiver";
    private Context context;

    public SiterReceiver(){

    }
    public SiterReceiver(Context context){
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //云端返回所有信息
        String backData=intent.getStringExtra(SiterConstantsUtil.WS_PAYLOAD);
        Log.i(TAG,backData);
        decodeFromJSON(backData);
    }

    private void decodeFromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String action = jsonObject.getString("action");
            if("devSend".equals(action)){
                JSONObject jsonObject1 = jsonObject.getJSONObject("params");
                String devid = jsonObject1.getString("devTid");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("data");

                if(jsonObject2.has("cmdId")){
                    int cmid = jsonObject2.getInt("cmdId");

                    switch (cmid){
                        case 1:
                            BatteryBean batteryDescBean1 = new BatteryBean();
                            batteryDescBean1.setDevTid(devid);
                            batteryDescBean1.setStatus(jsonObject2.getInt("status"));
                            batteryDescBean1.setBattPercent(jsonObject2.getInt("battPercent"));
                            batteryDescBean1.setSignal(jsonObject2.getInt("signal"));
                            if(SitewellSDK.getInstance(context).getRefreshBatteryListeners()!=null){
                                for(RefreshBatteryListener refreshBatteryListener:SitewellSDK.getInstance(context).getRefreshBatteryListeners())
                                {
                                    refreshBatteryListener.RefreshBattery(batteryDescBean1);
                                }
                            }

                            break;

                        case 104:

                            int push = jsonObject2.getInt("push");

                            String pushstr="";

                            if (Integer.toHexString(push).length() == 1) {
                                pushstr = "0000000" + Integer.toHexString(push);
                            } else if(Integer.toHexString(push).length() == 2){
                                pushstr = "000000" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 3){
                                pushstr = "00000" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 4){
                                pushstr = "0000" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 5){
                                pushstr = "000" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 6){
                                pushstr = "00" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 7){
                                pushstr = "0" + Integer.toHexString(push);
                            }else if(Integer.toHexString(push).length() == 8){
                                pushstr = "" + Integer.toHexString(push);
                            }

                            if(pushstr.length()==8){
                                try {
                                    int status = Integer.parseInt(pushstr.substring(0,2),16);
                                    int wifi = Integer.parseInt(pushstr.substring(2,4),16);
                                    int typea = Integer.parseInt(pushstr.substring(4,6),16);
                                    int timerid = Integer.parseInt(pushstr.substring(6,8),16);
                                    SocketBean socketDescBean = new SocketBean();
                                    socketDescBean.setDevTid(devid);
                                    socketDescBean.setSocketstatus(status);
                                    socketDescBean.setSignal(wifi);

                                    switch (typea){
                                        case 0:
                                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                                for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                                {
                                                    refreshBatteryListener.refreshSocketStatus(socketDescBean);
                                                }
                                            }
                                            break;
                                        case 1:
                                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                                for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                                {
                                                    refreshBatteryListener.circleFinish(socketDescBean);
                                                }
                                            }
                                            break;
                                        case 2:
                                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                                for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                                {
                                                    refreshBatteryListener.countdownFinish(socketDescBean);
                                                }
                                            }
                                            break;
                                        case 3:
                                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                                for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                                {
                                                    refreshBatteryListener.timerFinish(socketDescBean,String.valueOf(timerid));
                                                }
                                            }
                                            break;

                                    }

                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }


                            }else{
                                if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                    for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    {
                                        refreshBatteryListener.unknowError();
                                    }
                                }
                            }

                            break;


                        case 108:
                            int on_off = jsonObject2.getInt("On_Off");

                            String onoff="";

                            if (Integer.toHexString(on_off).length() == 1) {
                                onoff = "000" + Integer.toHexString(on_off);
                            } else if(Integer.toHexString(on_off).length() == 2){
                                onoff = "00" + Integer.toHexString(on_off);
                            }else if(Integer.toHexString(on_off).length() == 3){
                                onoff = "0" + Integer.toHexString(on_off);
                            }else if(Integer.toHexString(on_off).length() == 4){
                                onoff = "" + Integer.toHexString(on_off);
                            }


                            if(onoff.length()==4){
                                int statuss,wifisignals;
                                try {
                                    String status = onoff.substring(0, 2);
                                    String wifisignal = onoff.substring(2, 4);
                                    wifisignals = Integer.parseInt(wifisignal, 16);
                                    statuss = Integer.parseInt(status, 16);
                                    SocketBean socketDescBean = new SocketBean();
                                    socketDescBean.setDevTid(devid);
                                    socketDescBean.setSignal(wifisignals);
                                    socketDescBean.setSocketstatus(statuss);
                                    if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                        for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                        {
                                            refreshBatteryListener.refreshSocketStatus(socketDescBean);
                                        }
                                    }
                                }catch (NumberFormatException e){
                                    e.printStackTrace();
                                }

                            }else{
                                if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                    for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    {
                                        refreshBatteryListener.unknowError();
                                    }
                                }
                            }
                            break;
                        case 109:
                            String code = jsonObject2.getString("Data_Sync");
                            ResolveSocket resolveSocket = new ResolveSocket();
                            if(resolveSocket.isTarget(code,devid)){
                                SocketBean socketBean = resolveSocket.getSocketBean();
                                List<WifiTimerBean> wifiTimerBeanList = resolveSocket.getWifiTimerBeanList();
                                socketBean.setWifiTimerBeans(wifiTimerBeanList);
                                Log.i(TAG,"同步解析得出的SocketBean："+ socketBean.toString());
                                Log.i(TAG,"同步解析得出的wifiTimerBeanList："+wifiTimerBeanList.toString());
                                    if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                        for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                        {
                                            refreshBatteryListener.sycSocketStatusSuccess(socketBean);
                                        }
                                    }


                            }else{
                                if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                    for(WIFISocketListener refreshBatteryListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    {
                                        refreshBatteryListener.unknowError();
                                    }
                                }
                            }
                            break;
                        case 201:
                            WaterSensorBean waterSensorBean = new WaterSensorBean();
                            waterSensorBean.setDevTid(devid);
                            waterSensorBean.setStatus(jsonObject2.getInt("status"));
                            waterSensorBean.setBattPercent(jsonObject2.getInt("battPercent"));
                            waterSensorBean.setSignal(jsonObject2.getInt("signal"));

                            if(SitewellSDK.getInstance(context).getRefreshWaterSensorListeners()!=null){
                                for(RefreshWaterSensorListener refreshWaterSensorListener:SitewellSDK.getInstance(context).getRefreshWaterSensorListeners())
                                {
                                    refreshWaterSensorListener.RefreshWaterSensor(waterSensorBean);
                                }
                            }

                            break;
                        default:
                            break;
                    }
                }else{
                    int upgradeState = jsonObject2.getInt("upgradeState");
                    int upgradeProgress = jsonObject2.getInt("upgradeProgress");
                    if(upgradeState == 0){
                        if(SitewellSDK.getInstance(context).getUpgradeListeners()!=null){
                            for(UpgradeListener upgradeListener:SitewellSDK.getInstance(context).getUpgradeListeners())
                            {
                                upgradeListener.progressComplete(devid);
                            }
                        }
                    }else {
                        if(SitewellSDK.getInstance(context).getUpgradeListeners()!=null){
                            for(UpgradeListener upgradeListener:SitewellSDK.getInstance(context).getUpgradeListeners())
                            {
                                upgradeListener.progressIng(devid,upgradeProgress);
                            }
                        }
                    }
                }



            }else if("appLoginResp".equals(action)){

            }else if("appSendResp".equals(action)){
                int code = jsonObject.getInt("code");
                if(code==200){
                    JSONObject jsonObject1 = jsonObject.getJSONObject("params");
                    JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                    String devid = jsonObject1.getString("devTid");
                    int cmid = jsonObject2.getInt("cmdId");
                    switch (cmid){
                        case SitewellSDK.MODE_SWITCH:
                            int mode = jsonObject2.getInt("ID");
                            SocketBean socketDescBean2 = new SocketBean();
                            socketDescBean2.setDevTid(devid);
                            socketDescBean2.setSocketmodel(mode);
                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    wifiSocketListener.switchModeSuccess(socketDescBean2);
                            }
                            break;
                        case SitewellSDK.HANDLE_SOCKET_CONTROL:
                            int status = jsonObject2.getInt("On_Off");
                            SocketBean socketDescBean = new SocketBean();
                            socketDescBean.setDevTid(devid);
                            socketDescBean.setSocketstatus(status);
                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    wifiSocketListener.switchSocketSuccess(socketDescBean);
                            }
                            break;
                        case SitewellSDK.SET_CIRCLE_INFO:
                            String circle = jsonObject2.getString("Circle");
                            SocketBean socketDescBean3 = ResolveSocket.getSocketCicleInfo(circle,devid);
                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null){
                                for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    wifiSocketListener.setCircleConfigSuccess(socketDescBean3);
                            }
                            break;
                        case SitewellSDK.SET_COUNTDOWN_INFO:
                            String countdown = jsonObject2.getString("Count_down");
                            SocketBean socketDescBean4 = ResolveSocket.getSocketCountdownInfo(countdown,devid);
                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                                for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    wifiSocketListener.setCountDownConfigSuccess(socketDescBean4);
                            }
                            break;
                        case SitewellSDK.SET_TIMER_INFO:
                            String timing = jsonObject2.getString("Timing");
                            if(timing.length()==18){
                                timing = timing.substring(0,14);
                                ResolveTimer resolveTimer =new ResolveTimer(timing,devid);
                                if(resolveTimer.isTarget()){
                                    if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                                        for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                            wifiSocketListener.setTimerConfigSuccess(resolveTimer.getWifiTimerBean());
                                    }
                                }else{
                                    if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                                        for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                            wifiSocketListener.unknowError();
                                    }
                                }
                            }else{
                                if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                                    for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                        wifiSocketListener.unknowError();
                                }
                            }

                            break;
                        case SitewellSDK.DELETE_TIMER_INFO:
                            int ID = jsonObject2.getInt("ID");
                            String oo = String.valueOf(ID);
                            if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                                for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                                    wifiSocketListener.deleteTimerSuccess(oo);
                            }
                            break;
                    }
                }else if(code == 1400018){
                    if(SitewellSDK.getInstance(context).getWifiSocketListeners()!=null ){
                        for (WIFISocketListener wifiSocketListener:SitewellSDK.getInstance(context).getWifiSocketListeners())
                            wifiSocketListener.deviceOffLineError();
                    }
                }


            }else if("appLoginResp".equals(action)){
                int code = jsonObject.getInt("code");
                if(code == 1400002){

                    if(SitewellSDK.getInstance(context).getTokenTimeoutListeners()!=null ){
                        for (TokenTimeoutListener tokenTimeoutListener:SitewellSDK.getInstance(context).getTokenTimeoutListeners())
                            tokenTimeoutListener.tokentimeout();
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}

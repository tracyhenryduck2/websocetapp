package com.siterwell.demo.listener;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.siterwell.demo.bean.BatteryBean;
import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.bean.WaterSensorBean;
import com.siterwell.demo.bean.WifiTimerBean;
import com.siterwell.demo.protocol.ResolveSocket;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.siter.sdk.Constants;
import me.siter.sdk.Siter;
import me.siter.sdk.http.UserAction;


/**
 * Created by TracyHenry on 2018/2/6.
 */

public class SitewellSDK {
    private static String TAG = "SitewellSDK";
    public static final int MODE_SWITCH = 100;
    public static final int SET_CIRCLE_INFO = 101;
    public static final int SET_COUNTDOWN_INFO = 102;
    public static final int SET_TIMER_INFO = 103;
    public static final int DELETE_TIMER_INFO = 105;
    public static final int SET_ZONE_TIME = 106;
    public static final int SYNC_SOCKET_STATUS = 110;
    public static final int SEND_SOCKET_NAME = 111;
    public static final int HANDLE_SOCKET_CONTROL = 199;

    private WeakReference<Context> mContext;
    private String appTid;
    private List<RefreshWaterSensorListener> refreshWaterSensorListeners;
    private List<RefreshBatteryListener> refreshBatteryListeners;
    private List<TimeOutListener> timeOutListeners;
    private List<WIFISocketListener> wifiSocketListeners;
    private List<UpgradeListener> upgradeListeners;
    private List<TokenTimeoutListener> tokenTimeoutListeners;
    private static SitewellSDK instance;

    private SitewellSDK(Context context) {
        mContext = new WeakReference<>(context.getApplicationContext());
        if(refreshWaterSensorListeners == null){
            refreshWaterSensorListeners  = new CopyOnWriteArrayList<RefreshWaterSensorListener>();
        }
        if(refreshBatteryListeners == null){
            refreshBatteryListeners  = new CopyOnWriteArrayList<RefreshBatteryListener>();
        }
        if(timeOutListeners == null){
            timeOutListeners  = new CopyOnWriteArrayList<TimeOutListener>();
        }
        if(wifiSocketListeners == null){
            wifiSocketListeners  = new CopyOnWriteArrayList<WIFISocketListener>();
        }
        if(upgradeListeners == null){
            upgradeListeners  = new CopyOnWriteArrayList<UpgradeListener>();
        }
        if(tokenTimeoutListeners == null){
            tokenTimeoutListeners  = new CopyOnWriteArrayList<TokenTimeoutListener>();
        }
        if(TextUtils.isEmpty(appTid)){
            appTid = Settings.Secure.getString(mContext.get().getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        context.startService(new Intent(mContext.get(), SiterCoreService.class));
    };
    public static SitewellSDK getInstance(Context context) {
        synchronized (SitewellSDK.class) {
            if (instance == null) {
                instance = new SitewellSDK(context);
            }
        }

        return instance;
    }



    public void addRefreshWaterSensorListener(RefreshWaterSensorListener refreshWaterSensorListener){
        if(!refreshWaterSensorListeners.contains(refreshWaterSensorListener)){
            refreshWaterSensorListeners.add(refreshWaterSensorListener);
        }
    }

    public void removeRefreshWaterSensorListener(RefreshWaterSensorListener refreshWaterSensorListener){
        if(refreshWaterSensorListeners.contains(refreshWaterSensorListener)){
            refreshWaterSensorListeners.remove(refreshWaterSensorListener);
        }
    }

    public void addRefreshBatteryListener(RefreshBatteryListener refreshBatteryListener){
        if(!refreshBatteryListeners.contains(refreshBatteryListener)){
            refreshBatteryListeners.add(refreshBatteryListener);
        }
    }

    public void removeRefreshBatteryListener(RefreshBatteryListener refreshBatteryListener){
        if(refreshBatteryListeners.contains(refreshBatteryListener)){
            refreshBatteryListeners.remove(refreshBatteryListener);
        }
    }

    public List<RefreshWaterSensorListener> getRefreshWaterSensorListeners() {
        return refreshWaterSensorListeners;
    }

    public List<RefreshBatteryListener> getRefreshBatteryListeners() {
        return refreshBatteryListeners;
    }

    public void addWifiSocketListener(WIFISocketListener wifiSocketSwitchListener){
        if(!wifiSocketListeners.contains(wifiSocketSwitchListener)){
            wifiSocketListeners.add(wifiSocketSwitchListener);
        }

    }

    public void removeWifiSocketListener(WIFISocketListener wifiSocketListener){
        if(wifiSocketListeners.contains(wifiSocketListener)){
            wifiSocketListeners.remove(wifiSocketListener);
        }
    }

    public List<WIFISocketListener> getWifiSocketListeners() {
        return wifiSocketListeners;
    }

    public void addTimeoutListener(TimeOutListener timeOutListener) {
        if(!timeOutListeners.contains(timeOutListener)){
            timeOutListeners.add(timeOutListener);
        }
    }

    public void removeTimeoutListener(TimeOutListener timeOutListener) {
        if(timeOutListeners.contains(timeOutListener)){
            timeOutListeners.remove(timeOutListener);
        }
    }

    public List<TimeOutListener> getTimeoutListeners(){
        return timeOutListeners;
    }


    public void addUpgradeListener(UpgradeListener upgradeListener) {
        if(!upgradeListeners.contains(upgradeListener)){
            upgradeListeners.add(upgradeListener);
        }
    }

    public void removeUpgradeListener(UpgradeListener upgradeListener) {
        if(upgradeListeners.contains(upgradeListener)){
            upgradeListeners.remove(upgradeListener);
        }
    }

    public List<UpgradeListener> getUpgradeListeners(){
        return upgradeListeners;
    }


    public void addTokenTimeoutListener(TokenTimeoutListener tokenTimeoutListener) {
        if(!tokenTimeoutListeners.contains(tokenTimeoutListener)){
            tokenTimeoutListeners.add(tokenTimeoutListener);
        }
    }

    public void removeTokenTimeoutListener(TokenTimeoutListener tokenTimeoutListener) {
        if(tokenTimeoutListeners.contains(tokenTimeoutListener)){
            tokenTimeoutListeners.remove(tokenTimeoutListener);
        }
    }

    public List<TokenTimeoutListener> getTokenTimeoutListeners(){
        return tokenTimeoutListeners;
    }




    public void init(){
    }


    public void switchSocket(SocketBean socketBean){

        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",socketBean.getDevTid());
            params.put("ctrlKey",socketBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",HANDLE_SOCKET_CONTROL);
            dataParams.put("On_Off",socketBean.getSocketstatus());
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * methodname:switchMode
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送模式切换命令
     */
    public void switchMode(SocketBean socketBean){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",socketBean.getDevTid());
            params.put("ctrlKey",socketBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",MODE_SWITCH);
            dataParams.put("ID",socketBean.getSocketmodel());
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setCircleConfig(SocketBean socketBean){



        try {
            String code = ResolveSocket.getSocketCicleCode(socketBean);
            Log.i(TAG,"发送的设置循环模式参数:"+code);

            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",socketBean.getDevTid());
            params.put("ctrlKey",socketBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_CIRCLE_INFO);
            dataParams.put("Circle",code);
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public void setCountdownConfig(SocketBean socketBean){


        try {
            String code = ResolveSocket.getSocketCountDownCode(socketBean);
            Log.i(TAG,"发送的设置倒计时模式参数:"+code);

            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",socketBean.getDevTid());
            params.put("ctrlKey",socketBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_COUNTDOWN_INFO);
            dataParams.put("Count_down",code);
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * methodname:setTimerInfo
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送定时任务设置命令
     */
    public void setTimerInfoConfig(WifiTimerBean timer, String ctrlkey, String connecthost){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",timer.getDeviceid());
            params.put("ctrlKey",ctrlkey);
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_TIMER_INFO);
            dataParams.put("Timing",timer);
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null,connecthost);
        } catch (JSONException e) {
            e.printStackTrace();

        }
    }


    /**
     * methodname:deleteTimer
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送删除定时任务
     */
    public void deleteTimer(WifiTimerBean wifiTimerBean, String ctrlkey, String connecthost){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",wifiTimerBean.getDeviceid());
            params.put("ctrlKey",ctrlkey);
            params.put("appTid",appTid);
            dataParams.put("cmdId",DELETE_TIMER_INFO);
            dataParams.put("ID",Integer.parseInt(wifiTimerBean.getTimerid()));
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null,connecthost);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getWIFISocketStatus(SocketBean socketBean){

        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",socketBean.getDevTid());
            params.put("ctrlKey",socketBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SYNC_SOCKET_STATUS);
            dataParams.put("Data_Sync","0600000000");
            params.put("data",dataParams);
            command.put("params",params);
            Siter.getSiterClient().sendMessage(command,null);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /*
    @method queryBaterriesStatus(List<BatteryBean> batteryBeans)
    @autor TracyHenry
    @time 2018/2/7 下午4:32
    @email xuejunju_4595@qq.com
    批量查询智能电池状态
    */
    public void queryBaterriesStatus(List<BatteryBean> batteryBeans, final SycBatteryStatusListener sycBatteryStatusListener){

        org.json.JSONArray jsonArray = new org.json.JSONArray();


        for(int i=0;i<batteryBeans.size();i++){
            JSONObject J2 = new JSONObject();
            try {
                J2.put("devTid",batteryBeans.get(i).getDevTid());
                J2.put("ctrlKey",batteryBeans.get(i).getCtrlKey());
                jsonArray.put(i,J2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"jsonArray:"+jsonArray.toString());
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.QUERY_DEVICE_STATUS);
        UserAction.getInstance(mContext.get()).postSiterData(url.toString(),jsonArray.toString(), new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(object.toString());
                    List<BatteryBean> batteryBeanList = new ArrayList<BatteryBean>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String deviceid = obj.getString("devTid");
                            int status_current = 0;
                            int per_current = -1;
                            int signal = -1;

                            JSONObject status = obj.getJSONObject("status");

                            if(status.has("status")){
                                JSONObject s = status.getJSONObject("status");
                                status_current = s.isNull("currentValue")?0:s.getInt("currentValue");
                            }

                            if(status.has("battPercent")){
                                JSONObject p = status.getJSONObject("battPercent");
                                per_current = p.isNull("currentValue")?-1:p.getInt("currentValue");
                            }
                            if(status.has("signal")) {
                                JSONObject sig = status.getJSONObject("signal");
                                signal = sig.isNull("currentValue")?-1:sig.getInt("currentValue");
                            }

                            BatteryBean batteryBean = new BatteryBean();
                            batteryBean.setDevTid(deviceid);
                            batteryBean.setBattPercent(per_current);
                            batteryBean.setSignal(signal);
                            batteryBean.setStatus(status_current);
                            batteryBeanList.add(batteryBean);
                    }

                    if(sycBatteryStatusListener!=null){
                        sycBatteryStatusListener.success(batteryBeanList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void getFail(int errorCode) {
                if(sycBatteryStatusListener!=null){
                    sycBatteryStatusListener.error(errorCode);
                }
            }
        });
    }


    /*
    @method queryWaterSensorsStatus(List<WaterSensorBean> waterSensorBeans)
    @autor TracyHenry
    @time 2018/2/7 下午4:31
    @email xuejunju_4595@qq.com
    批量查询水感状态
    */
    public void queryWaterSensorsStatus(List<WaterSensorBean> waterSensorBeans, final SycWaterSensorStatusListener sycWaterSensorStatusListener){

        org.json.JSONArray jsonArray = new org.json.JSONArray();


        for(int i=0;i<waterSensorBeans.size();i++){
            JSONObject J2 = new JSONObject();
            try {
                J2.put("devTid",waterSensorBeans.get(i).getDevTid());
                J2.put("ctrlKey",waterSensorBeans.get(i).getCtrlKey());
                jsonArray.put(i,J2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG,"jsonArray:"+jsonArray.toString());
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.QUERY_DEVICE_STATUS);
        UserAction.getInstance(mContext.get()).postSiterData(url.toString(),jsonArray.toString(), new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    org.json.JSONArray jsonArray = new org.json.JSONArray(object.toString());
                    List<WaterSensorBean> waterSensorBeans = new ArrayList<WaterSensorBean>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String deviceid = obj.getString("devTid");
                        int status_current = 0;
                        int per_current = -1;
                        int signal = -1;

                        JSONObject status = obj.getJSONObject("status");

                        if(status.has("status")){
                            JSONObject s = status.getJSONObject("status");
                            status_current = s.isNull("currentValue")?0:s.getInt("currentValue");
                        }

                        if(status.has("battPercent")){
                            JSONObject p = status.getJSONObject("battPercent");
                            per_current = p.isNull("currentValue")?-1:p.getInt("currentValue");
                        }
                        if(status.has("signal")) {
                            JSONObject sig = status.getJSONObject("signal");
                            signal = sig.isNull("currentValue")?-1:sig.getInt("currentValue");
                        }

                        WaterSensorBean batteryBean = new WaterSensorBean();
                        batteryBean.setDevTid(deviceid);
                        batteryBean.setBattPercent(per_current);
                        batteryBean.setSignal(signal);
                        batteryBean.setStatus(status_current);
                        waterSensorBeans.add(batteryBean);
                    }

                    if(sycWaterSensorStatusListener!=null){
                        sycWaterSensorStatusListener.success(waterSensorBeans);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void getFail(int errorCode) {
                if(sycWaterSensorStatusListener!=null){
                    sycWaterSensorStatusListener.error(errorCode);
                }
            }
        });
    }




}

package com.siterwell.sdk.protocol;

import android.content.Context;
import android.provider.Settings;

import me.siter.sdk.http.bean.DeviceBean;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.util.TextUtils;
import me.siter.sdk.Siter;
import me.siter.sdk.inter.SiterMsgCallback;


/**
 * Created by ST-020111 on 2017/5/5.
 */

public class SocketCommand {
    public static final int MODE_SWITCH = 100;
    public static final int SET_CIRCLE_INFO = 101;
    public static final int SET_COUNTDOWN_INFO = 102;
    public static final int SET_TIMER_INFO = 103;
    public static final int DELETE_TIMER_INFO = 105;
    public static final int SET_ZONE_TIME = 106;
    public static final int SYNC_SOCKET_STATUS = 110;
    public static final int SET_SOCKET_NAME = 111;
    public static final int HANDLE_SOCKET_CONTROL = 199;

    private Context context;
    private DeviceBean deviceBean;
    private String appTid;


    public SocketCommand(DeviceBean deviceBean, Context context){
        this.deviceBean = deviceBean;
        this.context = context;

        appTid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


    }

    /**
     * methodname:switchMode
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送模式切换命令
     */
    public JSONObject switchMode(int mode){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",MODE_SWITCH);
            dataParams.put("ID",mode);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void switchMode(int mode , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(switchMode(mode),dataReceiverListener);
        }
    }

    /**
     * methodname:setCircleInfo
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送循环设置命令
     */
    public JSONObject setCircleInfo(String Circle){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_CIRCLE_INFO);
            dataParams.put("Circle",Circle);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCircleInfo(String Circle , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setCircleInfo(Circle),dataReceiverListener);
        }
    }


    /**
     * methodname:setCountdownInfo
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送倒计时设置命令
     */
    public JSONObject setCountdownInfo(String Count_down){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_COUNTDOWN_INFO);
            dataParams.put("Count_down",Count_down);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setCountdownInfo(String Count_down , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setCountdownInfo(Count_down),dataReceiverListener);
        }
    }


    /**
     * methodname:setTimerInfo
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送倒计时设置命令
     */
    public JSONObject setTimerInfo(String timer){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_TIMER_INFO);
            dataParams.put("Timing",timer);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setTimerInfo(String timer , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setTimerInfo(timer),dataReceiverListener);
        }
    }


    /**
     * methodname:deleteTimer
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送删除定时任务请求
     */
    public JSONObject deleteTimer(int timer){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",DELETE_TIMER_INFO);
            dataParams.put("ID",timer);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteTimer(int timer , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(deleteTimer(timer),dataReceiverListener);
        }
    }



    /**
     * methodname:setZoneAndTime
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 配网时通过内网发送时区和时间
     */
    public JSONObject setZoneAndTime(int zone,String timer){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_ZONE_TIME);
            dataParams.put("Time",zone);
            dataParams.put("Timing",timer);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setZoneAndTime(int zone,String timer , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setZoneAndTime(zone,timer),dataReceiverListener);
        }
    }

    /**
     * methodname:setSyncSocketStatus
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 发送同步请求
     */
    public JSONObject setSyncSocketStatusJson(String Data_Sync){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SYNC_SOCKET_STATUS);
            dataParams.put("Data_Sync",Data_Sync);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSyncSocketStatus(SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setSyncSocketStatusJson("0200000000"),dataReceiverListener);
        }
    }

    /**
     * methodname:setSocketControl
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 手动控制插座
     */
    public JSONObject setSocketControl(int On_Off){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",HANDLE_SOCKET_CONTROL);
            dataParams.put("On_Off",On_Off);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSocketControl(int On_Off , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setSocketControl(On_Off),dataReceiverListener);
        }
    }

    /**
     * methodname:setSocketControl
     * 作者：Henry on 2017/6/15 16:00
     * 邮箱：xuejunju_4595@qq.com
     * 参数:
     * 返回:
     * 手动控制插座
     */
    public JSONObject setSocketName(String name){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET_SOCKET_NAME);
            dataParams.put("name",name);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setSocketName(String name , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(setSocketName(name),dataReceiverListener);
        }
    }

}

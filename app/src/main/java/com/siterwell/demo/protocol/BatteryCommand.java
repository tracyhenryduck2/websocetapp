package com.siterwell.demo.protocol;

import android.content.Context;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.util.TextUtils;
import me.siter.sdk.Siter;
import me.siter.sdk.http.bean.DeviceBean;
import me.siter.sdk.inter.SiterMsgCallback;


/**
 * Created by ST-020111 on 2017/5/5.
 */

public class BatteryCommand {
    public static final int SET = 2;
    public static final int QUERYDEV = 0;
    public static int timer_count = 0;
    private Context context;
    private DeviceBean deviceBean;
    private Timer timer;
    private TimerTask timerTask;
    //    private String msgId;
//    private String action;
//    private String devTid;
    private String appTid;
    public BatteryCommand(DeviceBean deviceBean, Context context){
        this.deviceBean = deviceBean;
        this.context = context;
        appTid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    public void sendCommand(int cmdid , SiterMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Siter.getSiterClient().sendMessage(getCommand2(cmdid),dataReceiverListener);
        }
    }


    //command 0
    private JSONObject getCommand0(){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",QUERYDEV);
            dataParams.put("alarm",0);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    //command 2
    public JSONObject getCommand2(int commandCount){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",1);
            command.put("action","appSend");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            dataParams.put("cmdId",SET);
            dataParams.put("command",commandCount);
            params.put("data",dataParams);
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }




    public void resetTimer(){
        timer_count = 0;
        timer.cancel();
        timerTask.cancel();
        timer = null;
        timerTask = null;
    }
}

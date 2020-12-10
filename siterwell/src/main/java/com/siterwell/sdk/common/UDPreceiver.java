package com.siterwell.sdk.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.siterwell.sdk.event.SetSmokeTypeEvent;
import com.siterwell.sdk.event.SilenceEvent;
import com.siterwell.sdk.udp.UDPRecData;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by TracyHenry on 2018/3/23.
 */

public class UDPreceiver extends BroadcastReceiver {
    private static final String TAG = "UDPreceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String backData=intent.getStringExtra(UDPRecData.UDPUpload);
        Log.i(TAG,backData);
        decodeFromJSON(backData);
    }


    private void decodeFromJSON(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);
            String action = jsonObject.getString("action");

            if("appSend".equals(action)){
                JSONObject jsonObject1 = jsonObject.getJSONObject("params");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
                String devid = jsonObject1.getString("devTid");
                int cmid = jsonObject2.getInt("cmdId");
                int command = jsonObject2.getInt("command");
                switch (cmid){
                    case 2:
                        if(command==2||command==3){
                            SetSmokeTypeEvent setSmokeTypeEvent = new SetSmokeTypeEvent();
                            setSmokeTypeEvent.setDevTid(devid);
                            EventBus.getDefault().post(setSmokeTypeEvent);
                        }
                        else if(command==1){
                            SilenceEvent silenceEvent = new SilenceEvent();
                            silenceEvent.setDevTid(devid);
                            silenceEvent.setSuccess(1);
                            EventBus.getDefault().post(silenceEvent);
                        }

                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

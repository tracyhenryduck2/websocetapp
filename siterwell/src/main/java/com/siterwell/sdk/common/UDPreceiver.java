package com.siterwell.sdk.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.siterwell.sdk.udp.UDPRecData;
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

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

package com.siterwell.sdk.protocol;

import android.content.Context;
import android.provider.Settings;

import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.http.bean.FirmwareBean;
import com.siterwell.sdk.udp.SiterwellUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.util.TextUtils;
import me.siter.sdk.Hekr;
import me.siter.sdk.inter.HekrMsgCallback;


/**
 * Created by ST-020111 on 2017/5/5.
 */

public class UpgradeCommand {

    private Context context;
    private DeviceBean deviceBean;
    private FirmwareBean firmwareBean;
    private String appTid;
    public UpgradeCommand(DeviceBean deviceBean, FirmwareBean file, Context context){
        this.deviceBean = deviceBean;
        this.firmwareBean = file;
        this.context = context;
        appTid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }

    public void sendUpgradeCommand(HekrMsgCallback dataReceiverListener){
        if(! TextUtils.isEmpty(deviceBean.getDevTid())){
            Hekr.getHekrClient().sendMessage(getUpgradeInfo(),dataReceiverListener,deviceBean.getDcInfo().getConnectHost());
        }
    }

    //command 2
    public JSONObject getUpgradeInfo(){
        try {
            JSONObject command = new JSONObject();
            JSONObject params = new JSONObject();
            JSONObject dataParams = new JSONObject();
            command.put("msgId",16810);
            command.put("action","devUpgrade");
            params.put("devTid",deviceBean.getDevTid());
            params.put("ctrlKey",deviceBean.getCtrlKey());
            params.put("appTid",appTid);
            params.put("binUrl",firmwareBean.getBinUrl());
            params.put("md5",firmwareBean.getMd5());
            params.put("binType",firmwareBean.getBinUrl());
            params.put("binVer",firmwareBean.getBinUrl());
            params.put("size",firmwareBean.getSize());
            command.put("params",params);
            return command;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


}

package com.siterwell.sdk.util;

import android.text.TextUtils;

import com.siterwell.sdk.bean.CtrlBean;
import com.siterwell.sdk.bean.Global;
import com.siterwell.sdk.event.ClearFilterEvent;
import com.siterwell.sdk.event.CommandEvent;
import com.siterwell.sdk.listener.DataReceiverListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2017/10/16.
 */

public class MsgUtil {
    public static final int PASSTHROUGH = 0;
    public static final int MASTERCONTROL = 1;

    private static final String TAG = "MsgUtil";

    /**
     * from sdk 0.0.1
     *
     * @param object               Activity引用
     * @param devTid               需要控制的设备tid
     * @param protocol             控制命令("http://docs.hekr.me/v4/reference/protocol/json/#2")
     * @param dataReceiverListener 控制命令回调响应
     * @param isAutoPassageway     是否优先使用局域网控制
     */
    public static void sendMsg(Object object, String devTid, JSONObject protocol, DataReceiverListener dataReceiverListener, boolean isAutoPassageway) {

        if (isAutoPassageway) {
            if (Global.lanList != null && !Global.lanList.isEmpty()) {

                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.LAN_DATA_SEND_WHAT, new CtrlBean(object, devTid, protocol, dataReceiverListener)));
            } else {
                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, new CtrlBean(object, devTid, protocol, dataReceiverListener)));
            }
        } else {
            EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_SEND_WHAT, new CtrlBean(object, devTid, protocol, dataReceiverListener)));
        }
    }

    /**
     * from sdk1.1.3
     *
     * @param object               Activity引用
     * @param devTid               需要控制的设备tid
     * @param ctrlKey              设备控制key
     * @param dataReceiverListener 控制命令回调响应
     * @param isAutoPassageway     是否优先使用局域网控制
     * @param protocolType         数据类型 0(48 透传) or 1(json 主控)
     * @param protocolContent      协议数据(48协议字符串 或 主控json字符串)
     */
    public static void sendMsg(Object object, String devTid, String ctrlKey, boolean isAutoPassageway, int protocolType, String protocolContent,DataReceiverListener dataReceiverListener) {

        if (!(protocolType == MsgUtil.PASSTHROUGH || protocolType == MsgUtil.MASTERCONTROL))
            throw new NullPointerException("protocolType must be 0(48 passthrough) or 1(json master control)");

        JSONObject command = new JSONObject();
        JSONObject params = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            //480C01000101000000000057协议
            if (protocolType == MsgUtil.PASSTHROUGH) {
                data.put("raw", protocolContent);
                params.put("data", data);
            }
            if (protocolType == MsgUtil.MASTERCONTROL) {
                params.put("data", new JSONObject(protocolContent));
            }

            params.put("devTid", devTid);
            params.put("ctrlKey", ctrlKey);

            command.put("action", "appSend");
            command.put("params", params);

            MsgUtil.sendMsg(object, devTid, command, dataReceiverListener, isAutoPassageway);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * from sdk 0.0.1
     *
     * @param object               Activity引用
     * @param filter               监听某个设备的某个动作
     * @param dataReceiverListener 当前设备收到信息
     */
    public static void receiveMsg(Object object, JSONObject filter, DataReceiverListener dataReceiverListener) {

        try {
            if (Global.lanList != null && !Global.lanList.isEmpty()) {

                //网关子设备
                if (filter.getJSONObject("params").has("subDevTid")) {
                    EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.LAN_DATA_RECEIVE_WHAT, new CtrlBean(object, filter.getJSONObject("params").getString("subDevTid"), filter, dataReceiverListener)));
                } else {
                    EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.LAN_DATA_RECEIVE_WHAT, new CtrlBean(object, filter.getJSONObject("params").getString("devTid"), filter, dataReceiverListener)));
                }
            }
            if (filter.getJSONObject("params").has("subDevTid")) {
                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_RECEIVE_WHAT, new CtrlBean(object, filter.getJSONObject("params").getString("subDevTid"), filter, dataReceiverListener)));
            } else {
                EventBus.getDefault().post(new CommandEvent(ConstantsUtil.ServiceCode.WS_DATA_RECEIVE_WHAT, new CtrlBean(object, filter.getJSONObject("params").getString("devTid"), filter, dataReceiverListener)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "接收过滤器格式出错!");
        }
    }

    /**
     * from sdk 1.1.3
     * 设备主动上报状态信息，设置一次即可
     *
     * @param object               当前Activity引用
     * @param devTid               设备tid
     * @param dataReceiverListener 当前设备(devTid)收到的devSend动作的信息
     */
    public static void receiveDevSendMsg(Object object, String devTid, DataReceiverListener dataReceiverListener) {

        if (TextUtils.isEmpty(devTid))
            throw new NullPointerException("devTid is not allow null");

        JSONObject filter = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            filter.put("action", "devSend");
            params.put("devTid", devTid);
            filter.put("params", params);
            receiveMsg(object, filter, dataReceiverListener);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除设备主动上报监听器
     * @param object Activity引用
     */
    public static void removeDevSendActionFilter(Object object) {
        EventBus.getDefault().post(new ClearFilterEvent(ClearFilterEvent.CLEARDEVSENDFILTER, object));
    }

    /**
     * 清除所有命令监听器
     * @param object Activity引用
     */
    public static void clearAllActionFilter(Object object) {
        EventBus.getDefault().post(new ClearFilterEvent(ClearFilterEvent.CLEARALLFILTER, object));
    }
}

package me.hekr.sdk.config;

import org.json.JSONObject;

import java.io.Serializable;

import me.hekr.sdk.utils.LogUtil;

/**
 * Created by hucn on 2017/8/28.
 * Author:
 * Description: 设备配置状态
 */

public class ConfigSubDevice implements Cloneable,Serializable {

    private static final String TAG = ConfigSubDevice.class.getSimpleName();

    private ConfigSubStatus currentStatus = ConfigSubStatus.NONE;
    private ConfigSubStatus errorStatus = ConfigSubStatus.NONE;

    private int step = -1;
    private String devTidAndSubDevTid = "";
    private int errorCode = 0;
    private JSONObject json;

    public ConfigSubDevice(String devTidAndSubDevTid) {
        this.devTidAndSubDevTid = devTidAndSubDevTid;
    }

    public void next(int step) {
        // 如果传入的step小于当前的step,则不进行处理
        if (step <= this.step) {
            return;
        }
        this.step = step;
        if (errorStatus != ConfigSubStatus.NONE) {
            LogUtil.d(TAG, "The state is in error status, do nothing");
            return;
        }
        if (step == SubDeviceStatus.DEVICE_STEP0) {
            currentStatus = ConfigSubStatus.DEVICE_CONNECTED_GATEWAY;
        } else if (step==SubDeviceStatus.DEVICE_STEP1) {
            currentStatus = ConfigSubStatus.CLOUD_VERIFY_DEVICE;
        } else if (step==SubDeviceStatus.DEVICE_STEP2) {
            currentStatus = ConfigSubStatus.DEVICE_LOGIN_CLOUD;
        } else if (step == SubDeviceStatus.DEVICE_STEP3) {
            currentStatus = ConfigSubStatus.DEVICE_BIND_SUCCESS;
        }
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void error(int step, int errorCode) {
        // 如果传入的step小于当前的step,则不进行处理
        if (step <= this.step) {
            return;
        }
        this.step = step;
        this.errorCode = errorCode;
        // 根据错误码更改设备的错误状态
        if (step <= SubDeviceStatus.DEVICE_STEP0) {
            currentStatus = ConfigSubStatus.NONE;
            errorStatus = ConfigSubStatus.DEVICE_CONNECTED_GATEWAY;
        } else if (step <= SubDeviceStatus.DEVICE_STEP1) {
            currentStatus = ConfigSubStatus.DEVICE_CONNECTED_GATEWAY;
            errorStatus = ConfigSubStatus.CLOUD_VERIFY_DEVICE;
        } else if (step <= SubDeviceStatus.DEVICE_STEP2) {
            currentStatus = ConfigSubStatus.DEVICE_CONNECTED_GATEWAY;
            errorStatus = ConfigSubStatus.DEVICE_LOGIN_CLOUD;
        }
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void bindComplete(JSONObject object) {
        this.step = SubDeviceStatus.DEVICE_STEP3;
        currentStatus = ConfigSubStatus.DEVICE_BIND_SUCCESS;
        errorStatus = ConfigSubStatus.NONE;
        json = object;
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public String getDevTidAndSubDevTid() {
        return devTidAndSubDevTid;
    }

    public void setDevTidAndSubDevTid(String devTidAndSubDevTid) {
        this.devTidAndSubDevTid = devTidAndSubDevTid;
    }

    public ConfigSubStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ConfigSubStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ConfigSubStatus getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(ConfigSubStatus errorStatus) {
        this.errorStatus = errorStatus;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject mJson) {
        this.json = mJson;
    }

    @Override
    public String toString() {
        return "ConfigSubDevice{" +
                "currentStatus=" + currentStatus +
                ", errorStatus=" + errorStatus +
                ", step=" + step +
                ", devTidAndSubDevTid='" + devTidAndSubDevTid + '\'' +
                ", json=" + json +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

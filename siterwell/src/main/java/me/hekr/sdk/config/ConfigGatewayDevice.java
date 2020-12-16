package me.hekr.sdk.config;

import org.json.JSONObject;

import java.io.Serializable;

import me.hekr.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author:
 * Description: 设备配置状态
 */

public class ConfigGatewayDevice implements Cloneable,Serializable {

    private static final String TAG = ConfigGatewayDevice.class.getSimpleName();

    private ConfigGatewayStatus currentStatus = ConfigGatewayStatus.NONE;
    private ConfigGatewayStatus errorStatus = ConfigGatewayStatus.NONE;

    private int step = -1;
    private String devTid = "";
    private int errorCode = 0;
    private JSONObject json;

    public ConfigGatewayDevice(String devTid) {
        this.devTid = devTid;
    }

    public void next(int step) {
        // 如果传入的step小于当前的step,则不进行处理
        if (step <= this.step) {
            return;
        }
        this.step = step;
        if (errorStatus != ConfigGatewayStatus.NONE) {
            LogUtil.d(TAG, "The state is in error status, do nothing");
            return;
        }
        if (step == GatewayDeviceStatus.DEVICE_STEP0) {
            currentStatus = ConfigGatewayStatus.CLOUD_VERIFY_DEVICE;
        } else if (step==GatewayDeviceStatus.DEVICE_STEP1) {
            currentStatus = ConfigGatewayStatus.DEVICE_BIND_SUCCESS;
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
        if (step <= GatewayDeviceStatus.DEVICE_STEP0) {
            currentStatus = ConfigGatewayStatus.NONE;
            errorStatus = ConfigGatewayStatus.CLOUD_VERIFY_DEVICE;
        } else if (step <= GatewayDeviceStatus.DEVICE_STEP1) {
            currentStatus = ConfigGatewayStatus.CLOUD_VERIFY_DEVICE;
            errorStatus = ConfigGatewayStatus.DEVICE_BIND_SUCCESS;
        }
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void bindComplete(JSONObject object) {
        this.step = GatewayDeviceStatus.DEVICE_STEP1;
        currentStatus = ConfigGatewayStatus.DEVICE_BIND_SUCCESS;
        errorStatus = ConfigGatewayStatus.NONE;
        json = object;
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void bindError(JSONObject object, int errorCode) {
        this.step = GatewayDeviceStatus.DEVICE_STEP1;
        currentStatus = ConfigGatewayStatus.CLOUD_VERIFY_DEVICE;
        errorStatus = ConfigGatewayStatus.DEVICE_BIND_SUCCESS;
        this.errorCode = errorCode;
        json = object;
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public ConfigGatewayStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ConfigGatewayStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ConfigGatewayStatus getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(ConfigGatewayStatus errorStatus) {
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
                ", devTid='" + devTid + '\'' +
                ", json=" + json +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

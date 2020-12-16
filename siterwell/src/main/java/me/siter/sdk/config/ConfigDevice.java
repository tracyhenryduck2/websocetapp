package me.siter.sdk.config;

import org.json.JSONObject;

import me.siter.sdk.utils.LogUtil;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author:
 * Description: 设备配置状态
 */

public class ConfigDevice implements Cloneable {

    private static final String TAG = ConfigDevice.class.getSimpleName();

    private ConfigStatus currentStatus = ConfigStatus.NONE;
    private ConfigStatus errorStatus = ConfigStatus.NONE;

    private int step = -1;
    private String devTid = "";
    private int errorCode = 0;
    private JSONObject json;
    private String ip;

    public ConfigDevice(String devTid) {
        this.devTid = devTid;
    }

    public void next(int step) {
        // 如果传入的step小于当前的step,则不进行处理
        if (step <= this.step) {
            return;
        }
        this.step = step;
        if (errorStatus != ConfigStatus.NONE) {
            LogUtil.d(TAG, "The state is in error status, do nothing");
            return;
        }
        if (step == DeviceStatus.DEVICE_STEP0) {
            currentStatus = ConfigStatus.GET_SECURITY_CODE;
        } else if (step > DeviceStatus.DEVICE_STEP0 && step < DeviceStatus.DEVICE_STEP5) {
            currentStatus = ConfigStatus.DEVICE_CONNECTED_ROUTER;
        } else if (step < DeviceStatus.DEVICE_STEP9) {
            currentStatus = ConfigStatus.CLOUD_VERIFY_DEVICE;
        } else if (step == DeviceStatus.DEVICE_STEP9) {
            currentStatus = ConfigStatus.DEVICE_LOGIN_CLOUD;
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
        if (step <= DeviceStatus.DEVICE_STEP1) {
            currentStatus = ConfigStatus.GET_SECURITY_CODE;
            errorStatus = ConfigStatus.DEVICE_CONNECTED_ROUTER;
        } else if (step <= DeviceStatus.DEVICE_STEP5) {
            currentStatus = ConfigStatus.DEVICE_CONNECTED_ROUTER;
            errorStatus = ConfigStatus.CLOUD_VERIFY_DEVICE;
        } else if (step <= DeviceStatus.DEVICE_STEP9) {
            currentStatus = ConfigStatus.CLOUD_VERIFY_DEVICE;
            errorStatus = ConfigStatus.DEVICE_LOGIN_CLOUD;
        }
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void bindComplete(JSONObject object) {
        this.step = DeviceStatus.DEVICE_STEP10;
        currentStatus = ConfigStatus.DEVICE_BIND_SUCCESS;
        errorStatus = ConfigStatus.NONE;
        json = object;
        LogUtil.d(TAG, "Current status: " + currentStatus.name());
        LogUtil.d(TAG, "Current error: " + errorStatus.name());
    }

    public void bindError(JSONObject object, int errorCode) {
        this.step = DeviceStatus.DEVICE_STEP10;
        currentStatus = ConfigStatus.DEVICE_LOGIN_CLOUD;
        errorStatus = ConfigStatus.DEVICE_BIND_SUCCESS;
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

    public ConfigStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(ConfigStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public ConfigStatus getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(ConfigStatus errorStatus) {
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "ConfigDevice{" +
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

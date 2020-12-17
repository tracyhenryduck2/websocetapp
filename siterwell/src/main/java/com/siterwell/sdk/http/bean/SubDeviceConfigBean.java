package com.siterwell.sdk.http.bean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class SubDeviceConfigBean {
    private int msgId;
    private String action;
    private ParamsBean params;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ParamsBean getParams() {
        return params;
    }

    public void setParams(ParamsBean params) {
        this.params = params;
    }

    public static class ParamsBean {
        /**
         * devTid : b28c9356c4ad42c0b22f035a0df851eb
         * subDevTid : 00124b0004207a98
         * subMid : 012345abcdef
         * subConnectType : ZigBee_HA
         * online : true
         * binVer : 3.0.1.2
         * subDevName :
         * subDevModel :
         * manufacturerName :
         * description :
         */

        private String devTid;
        private String subDevTid;
        private String subMid;
        private String subConnectType;
        private boolean online;
        private String binVer;
        private String subDevName;
        private String subDevModel;
        private String manufacturerName;
        private String description;

        public String getDevTid() {
            return devTid;
        }

        public void setDevTid(String devTid) {
            this.devTid = devTid;
        }

        public String getSubDevTid() {
            return subDevTid;
        }

        public void setSubDevTid(String subDevTid) {
            this.subDevTid = subDevTid;
        }

        public String getSubMid() {
            return subMid;
        }

        public void setSubMid(String subMid) {
            this.subMid = subMid;
        }

        public String getSubConnectType() {
            return subConnectType;
        }

        public void setSubConnectType(String subConnectType) {
            this.subConnectType = subConnectType;
        }

        public boolean isOnline() {
            return online;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        public String getBinVer() {
            return binVer;
        }

        public void setBinVer(String binVer) {
            this.binVer = binVer;
        }

        public String getSubDevName() {
            return subDevName;
        }

        public void setSubDevName(String subDevName) {
            this.subDevName = subDevName;
        }

        public String getSubDevModel() {
            return subDevModel;
        }

        public void setSubDevModel(String subDevModel) {
            this.subDevModel = subDevModel;
        }

        public String getManufacturerName() {
            return manufacturerName;
        }

        public void setManufacturerName(String manufacturerName) {
            this.manufacturerName = manufacturerName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Override
    public String toString() {
        return "SubDeviceConfigBean{" +
                "msgId=" + msgId +
                ", action='" + action + '\'' +
                ", params=" + params +
                '}';
    }
}

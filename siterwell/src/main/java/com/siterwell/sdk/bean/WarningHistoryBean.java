package com.siterwell.sdk.bean;

import java.io.Serializable;

/**
 * Created by gc-0001 on 2017/5/2.
 */

public class WarningHistoryBean implements Serializable {
   private String warningId;
    private String warningsubject;
    private String content;
    private long reportTime;
    private String deviceid;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getReportTime() {
        return reportTime;
    }

    public void setReportTime(long reportTime) {
        this.reportTime = reportTime;
    }

    public String getWarningId() {
        return warningId;
    }

    public void setWarningId(String warningId) {
        this.warningId = warningId;
    }

    public String getWarningsubject() {
        return warningsubject;
    }

    public void setWarningsubject(String warningsubject) {
        this.warningsubject = warningsubject;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
}

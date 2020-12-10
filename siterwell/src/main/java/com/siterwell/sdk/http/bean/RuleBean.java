package com.siterwell.sdk.http.bean;

import com.alibaba.fastjson.JSON;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class RuleBean implements Serializable {

    private static final long serialVersionUID = 8885593685099031197L;
    /**
     * devTid : ESP_xxx
     * ctrlKey : xxxxx
     * code : json透传{"power":1} 48协议("raw":"48xxxxxx00")
     * taskName : xxx
     * schedulerType : ONCE
     * timeZoneOffset : 480
     * taskKey : xxxx
     * desc : xxxx
     * isForce : false
     * triggerDateTime : 2016-01-07T12:00:00
     * triggerTime : 12:00:00
     * repeatList : ["MONDAY","SUNDAY"]
     */

    private String devTid;
    private String ctrlKey;
    private JSONObject code;
    private String taskName;
    private String schedulerType;
    private int timeZoneOffset;
    private String taskKey;
    private String desc;
    private boolean isForce;
    private String cronExpr;
    private boolean feedback;
    private String triggerDateTime;
    private String triggerTime;
    private List<String> repeatList;

    private int taskId;
    private String uid;
    private boolean enable;
    private int count;
    private String timerTaskStatus;
    private boolean expired;
    private String nextTriggerTime;
    private int serverTimeZoneOffset;
    private String createTime;
    private String modifyTime;
    private JSONObject serverTime;

    public RuleBean() {
    }

    public String getDevTid() {
        return devTid;
    }

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public String getCtrlKey() {
        return ctrlKey;
    }

    public void setCtrlKey(String ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public JSONObject getCode() {
        return code;
    }

    public void setCode(JSONObject code) {
        this.code = code;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getSchedulerType() {
        return schedulerType;
    }

    public void setSchedulerType(String schedulerType) {
        this.schedulerType = schedulerType;
    }

    public int getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(int timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isIsForce() {
        return isForce;
    }

    public void setIsForce(boolean isForce) {
        this.isForce = isForce;
    }

    public String getTriggerDateTime() {
        return triggerDateTime;
    }

    public void setTriggerDateTime(String triggerDateTime) {
        this.triggerDateTime = triggerDateTime;
    }

    public String getTriggerTime() {
        return triggerTime;
    }

    public void setTriggerTime(String triggerTime) {
        this.triggerTime = triggerTime;
    }

    public List<String> getRepeatList() {
        return repeatList;
    }

    public void setRepeatList(List<String> repeatList) {
        this.repeatList = repeatList;
    }

    public boolean isFeedback() {
        return feedback;
    }

    public void setFeedback(boolean feedback) {
        this.feedback = feedback;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }


    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTimerTaskStatus() {
        return timerTaskStatus;
    }

    public void setTimerTaskStatus(String timerTaskStatus) {
        this.timerTaskStatus = timerTaskStatus;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public String getNextTriggerTime() {
        return nextTriggerTime;
    }

    public void setNextTriggerTime(String nextTriggerTime) {
        this.nextTriggerTime = nextTriggerTime;
    }

    public int getServerTimeZoneOffset() {
        return serverTimeZoneOffset;
    }

    public void setServerTimeZoneOffset(int serverTimeZoneOffset) {
        this.serverTimeZoneOffset = serverTimeZoneOffset;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }

    public JSONObject getServerTime() {
        return serverTime;
    }

    public void setServerTime(JSONObject serverTime) {
        this.serverTime = serverTime;
    }

    /**
     * 4.4.5 添加预约任务
     *
     * @param devTid         设备ID
     * @param ctrlKey        控制码
     * @param code           预约任务代码
     * @param taskName       预约任务名称
     * @param timeZoneOffset 时区偏移，请注意javascript中获取的东八区时区偏移为-480，该处需要填写480
     * @param taskKey        由客户端生成唯一标识该任务的字符串，一经生成不可修改
     * @param desc           任务描述
     * @param isForce        创建时任务重复是否强制覆盖
     * @param cronExpr       任务触发的时间cron表达式，当创建GENERIC型预约时必须提供
     * @param feedback       任务执行完毕后是否回馈
     */
    public RuleBean(String devTid, String ctrlKey, JSONObject code, String taskName, int timeZoneOffset, String taskKey, String desc, boolean isForce, String cronExpr, boolean feedback) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.code = code;
        this.taskName = taskName;
        this.schedulerType = "GENERIC";
        this.timeZoneOffset = timeZoneOffset;
        this.taskKey = taskKey;
        this.desc = desc;
        this.isForce = isForce;
        this.cronExpr = cronExpr;
        this.feedback = feedback;
    }


    /**
     * 4.4.5.1 添加一次性预约任务
     *
     * @param devTid          设备ID
     * @param ctrlKey         控制码
     * @param code            预约任务代码
     * @param taskName        预约任务名称
     * @param timeZoneOffset  时区偏移，请注意javascript中获取的东八区时区偏移为-480，该处需要填写480
     * @param taskKey         由客户端生成唯一标识该任务的字符串，一经生成不可修改
     * @param desc            任务描述
     * @param isForce         创建时任务重复是否强制覆盖
     * @param feedback        任务执行完毕后是否回馈
     * @param triggerDateTime 一次性预约触发时间，当创建ONCE型预约时必须提供
     */
    public RuleBean(String devTid, String ctrlKey, JSONObject code, String taskName, int timeZoneOffset, String taskKey, String desc, boolean isForce, boolean feedback, String triggerDateTime) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.code = code;
        this.taskName = taskName;
        this.timeZoneOffset = timeZoneOffset;
        this.schedulerType = "ONCE";
        this.taskKey = taskKey;
        this.desc = desc;
        this.isForce = isForce;
        this.feedback = feedback;
        this.triggerDateTime = triggerDateTime;
    }

    /**
     * 4.4.5.2 添加循环预约任务
     *
     * @param devTid         设备ID
     * @param ctrlKey        控制码
     * @param code           预约任务代码
     * @param taskName       预约任务名称
     * @param timeZoneOffset 时区偏移，请注意javascript中获取的东八区时区偏移为-480，该处需要填写480
     * @param taskKey        由客户端生成唯一标识该任务的字符串，一经生成不可修改
     * @param desc           任务描述
     * @param isForce        创建时任务重复是否强制覆盖
     * @param feedback       任务执行完毕后是否回馈
     * @param triggerTime    循环预约触发时间，当创建LOOP型预约时必须提供
     * @param repeatList     循环预约触发天，当创建LOOP型预约时必须提供
     */
    public RuleBean(String devTid, String ctrlKey, JSONObject code, String taskName, int timeZoneOffset, String taskKey, String desc, boolean isForce, boolean feedback, String triggerTime, List<String> repeatList) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.code = code;
        this.taskName = taskName;
        this.schedulerType = "LOOP";
        this.timeZoneOffset = timeZoneOffset;
        this.taskKey = taskKey;
        this.desc = desc;
        this.isForce = isForce;
        this.feedback = feedback;
        this.triggerTime = triggerTime;
        this.repeatList = repeatList;
    }


    /**
     * 4.4.7 编辑预约任务
     */
    public RuleBean(String taskName, String desc, JSONObject code, boolean enable, String cronExpr, boolean feedback) {
        this.taskName = taskName;
        this.desc = desc;
        this.code = code;
        this.enable = enable;
        this.cronExpr = cronExpr;
        this.feedback = feedback;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}

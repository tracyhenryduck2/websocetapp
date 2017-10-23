package com.siterwell.sdk.bean;

/*
@class LogBean
@autor Administrator
@time 2017/10/16 14:03
@email xuejunju_4595@qq.com
*/

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LogBean {
    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private long logTime;
    private int logLevel;
    private String logTag;
    private String logMsg;
    private int colorResId = -1;

    public long getLogTime() {
        return logTime;
    }

    public void setLogTime(long logTime) {
        this.logTime = logTime;
    }

    public int getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    public String getLogTag() {
        return logTag;
    }

    public void setLogTag(String logTag) {
        this.logTag = logTag;
    }

    public String getLogMsg() {
        return logMsg;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }

    public void setLogMsg(String logMsg) {
        this.logMsg = logMsg;
    }

    /**
     * 适用于log
     *
     * @param logTime  时间
     * @param logLevel 级别
     * @param logTag   tag
     * @param logMsg   message
     */
    public LogBean(long logTime, int logLevel, String logTag, String logMsg) {
        this.logTime = logTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
    }

    /**
     * 适用于log，带颜色
     *
     * @param logTime    时间
     * @param logLevel   级别
     * @param logTag     tag
     * @param logMsg     message
     * @param colorResId 颜色
     */
    public LogBean(long logTime, int logLevel, String logTag, String logMsg, int colorResId) {
        this.logTime = logTime;
        this.logLevel = logLevel;
        this.logTag = logTag;
        this.logMsg = logMsg;
        this.colorResId = colorResId;
    }

    /**
     * 适用于debugView
     *
     * @param logMsg     内容
     * @param colorResId 颜色
     */
    public LogBean(String logMsg, int colorResId) {
        this.logMsg = logMsg;
        this.colorResId = colorResId;
    }

    /**
     * 适用于debugView
     *
     * @param logMsg 内容
     */
    public LogBean(String logMsg) {
        this.logMsg = logMsg;
    }

    @Override
    public String toString() {
        Date date = new Date(logTime);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault());
        return TextUtils.concat(sdf.format(date), ":", logTag, ":", logMsg).toString();
    }
}

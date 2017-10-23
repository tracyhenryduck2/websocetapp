package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class DebugEvent {

    private String debugLog;
    private int colorResId;

    public String getDebugLog() {
        return debugLog;
    }

    public int getColorResId() {
        return colorResId;
    }

    public void setColorResId(int colorResId) {
        this.colorResId = colorResId;
    }

    public DebugEvent(String debugLog) {
        this.debugLog = debugLog;
    }

    public DebugEvent(String debugLog, int colorResId) {
        this.debugLog = debugLog;
        this.colorResId = colorResId;
    }

    @Override
    public String toString() {
        return "DebugEvent{" +
                "debugLog='" + debugLog + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DebugEvent that = (DebugEvent) o;

        return debugLog != null ? debugLog.equals(that.debugLog) : that.debugLog == null;

    }

    @Override
    public int hashCode() {
        return debugLog != null ? debugLog.hashCode() : 0;
    }
}

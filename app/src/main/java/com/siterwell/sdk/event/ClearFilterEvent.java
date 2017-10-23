package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class ClearFilterEvent {
    private int clearType;
    private Object object;
    public static final int CLEARALLFILTER=100001;
    public static final int CLEARDEVSENDFILTER=100002;

    public ClearFilterEvent(int clearType,Object object) {
        this.object = object;
        this.clearType = clearType;
    }

    public int getClearType() {
        return clearType;
    }

    public void setClearType(int clearType) {
        this.clearType = clearType;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "ClearFilterEvent{" +
                "clearType=" + clearType +
                ", object=" + object +
                '}';
    }
}

package com.siterwell.sdk.event;

/**
 * Created by Administrator on 2017/10/16.
 */

public class LogoutEvent {
    private boolean logout;

    public boolean isLogout() {
        return logout;
    }

    public void setLogout(boolean logout) {
        this.logout = logout;
    }

    public LogoutEvent(boolean logout) {
        this.logout = logout;
    }
}

package com.siterwell.sdk.event;

import com.siterwell.sdk.bean.CtrlBean;

/**
 * Created by Administrator on 2017/10/16.
 */

public class CommandEvent {
    private int command;
    private CtrlBean ctrlBean;

    public CommandEvent(int command, CtrlBean ctrlBean) {
        this.command = command;
        this.ctrlBean = ctrlBean;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public CtrlBean getCtrlBean() {
        return ctrlBean;
    }

    public void setCtrlBean(CtrlBean ctrlBean) {
        this.ctrlBean = ctrlBean;
    }

    @Override
    public String toString() {
        return "CommandEvent{" +
                "command=" + command +
                ", ctrlBean=" + ctrlBean +
                '}';
    }
}

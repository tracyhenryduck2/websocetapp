package com.siterwell.demo.wheelwidget.helper;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xuejunjun on 2016/12/2.
 */
public class TimerBean implements Serializable {
    private ArrayList<Integer> weekdays;
    private int hour;
    private int min;

    public TimerBean() {
    }

    public ArrayList<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}

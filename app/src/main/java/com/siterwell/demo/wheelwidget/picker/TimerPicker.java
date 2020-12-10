package com.siterwell.demo.wheelwidget.picker;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.siterwell.demo.wheelwidget.helper.TimerBean;
import com.siterwell.demo.wheelwidget.view.WeekListView;
import com.siterwell.demo.wheelwidget.view.WheelView;

import java.util.ArrayList;


/**
 * 描述 选择定时的时间，包括星期ji，时，分
 *
 *
 */
public class TimerPicker extends WheelPicker<TimerBean> {
    private WeekListView weekWheelView;
    private WheelView hourWheelView;
    private WheelView minWheelView;
    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private TimerBean bean = new TimerBean();

    public TimerPicker(Activity activity) {
        super(activity);
    }



    @Override
    protected LinearLayout initWheelView() {
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        weekWheelView = new WeekListView(activity);
        weekWheelView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT,1.0f));
        weekWheelView.setDivider(null);
        hourWheelView = new WheelView(activity);
        LinearLayout.LayoutParams layoutParams =  new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT,1.0f);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        hourWheelView.setLayoutParams(layoutParams);
        minWheelView  = new WheelView(activity);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(MATCH_PARENT,WRAP_CONTENT,1.0f);
        layoutParams2.gravity = Gravity.CENTER_VERTICAL;
        minWheelView.setLayoutParams(layoutParams2);
        rootLayout.addView(weekWheelView);
        rootLayout.addView(hourWheelView);
        rootLayout.addView(minWheelView);
        setLabel();
        return rootLayout;
    }

    @Override
    protected TimerBean getCurrentItem() {
        bean.setWeekdays(weekWheelView.getCurrentItem());
        bean.setHour(hourWheelView.getCurrentItem());
        bean.setMin(minWheelView.getCurrentItem());

        return bean;
    }



    public void setRange() {

        weekWheelView.init();

        for (int i = 0; i < 24; i ++) {

            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_hour.add(item);
        }

        hourWheelView.setAdapter(new NumberAdapter(items_hour));
        hourWheelView.setCurrentItem(0);

        for (int i = 0; i < 60; i ++) {
            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_min.add(item);
        }

        minWheelView.setAdapter(new NumberAdapter(items_min));
        minWheelView.setCurrentItem(0);


    }


    @Override
    public void setCyclic(boolean cyclic) {

        hourWheelView.setCyclic(cyclic);
        minWheelView.setCyclic(cyclic);
    }

    @Override
    public void setScrollingDuration(int scrollingDuration) {
        hourWheelView.setScrollingDuration(scrollingDuration);
        minWheelView.setScrollingDuration(scrollingDuration);
    }


    /**
     * 设置单位
     */
    public void setLabel() {

            hourWheelView.setLabel("时");
            minWheelView.setLabel("分");
    }

    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items) {
            super(items);
        }

    }


}


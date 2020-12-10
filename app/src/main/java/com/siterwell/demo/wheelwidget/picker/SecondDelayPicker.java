package com.siterwell.demo.wheelwidget.picker;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.siterwell.demo.wheelwidget.view.WheelView;

import java.util.ArrayList;


/**
 * 描述 选择的延时秒数
 *
 *
 */
public class SecondDelayPicker extends WheelPicker<int[]> {
    private WheelView minwheelView;
    private WheelView secondwheelView;
    private ArrayList<String> items = new ArrayList<String>();

    public SecondDelayPicker(Activity activity) {
        super(activity);
    }

    @Override
    protected LinearLayout initWheelView() {
        LinearLayout rootLayout = new LinearLayout(activity);
        rootLayout.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);
        minwheelView = new WheelView(activity);
        minwheelView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT,1.0f));
        minwheelView.setLabel("分");

        secondwheelView = new WheelView(activity);
        secondwheelView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT,1.0f));
        secondwheelView.setLabel("秒");
        rootLayout.addView(minwheelView);
        rootLayout.addView(secondwheelView);
        return rootLayout;
    }

    @Override
    protected int[] getCurrentItem() {
        int[] currentItems = new int[2];

        currentItems[0]= Integer.parseInt(items.get( minwheelView.getCurrentItem()));
        currentItems[1]= Integer.parseInt(items.get( secondwheelView.getCurrentItem()));

        return  currentItems;
    }

    public void setRange(int startNumber, int endNumber) {
        setRange(startNumber, endNumber, 1);
    }

    public void setRange(int startNumber, int endNumber, int step) {



        for (int i = startNumber; i <= endNumber; i = i + step) {
            items.add(String.valueOf(i));
        }
        minwheelView.setAdapter(new NumberAdapter(items));
        secondwheelView.setCurrentItem(0);
        secondwheelView.setAdapter(new NumberAdapter(items));
        secondwheelView.setCurrentItem(0);

    }


    @Override
    public void setCyclic(boolean cyclic) {
        minwheelView.setCyclic(cyclic);
        secondwheelView.setCyclic(cyclic);
    }

    @Override
    public void setScrollingDuration(int scrollingDuration) {
        minwheelView.setScrollingDuration(scrollingDuration);
        secondwheelView.setScrollingDuration(scrollingDuration);
    }

    public void setSelectedNumber(int number) {
        int size = items.size();
        if (size == 0) {
            secondwheelView.setCurrentItem(0);
        }
        for (int i = 0; i < size; i++) {
            if (items.get(i).equals(String.valueOf(number))) {
                secondwheelView.setCurrentItem(i);
                break;
            }
        }
    }


    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items) {
            super(items);
        }

    }

}


package com.siterwell.sdk.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.siterwell.sdk.bean.LogBean;
import com.siterwell.siterapp.R;

import java.util.List;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrAdapter extends BaseAdapter {
    private List<LogBean> lists;
    private Context context;
    private LayoutInflater mInflater;

    public HekrAdapter(List<LogBean> lists, Context context) {
        this.lists = lists;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogBean logBean = lists.get(position);

        String text = logBean.getLogMsg();
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.task_item, parent, false);
            viewHolder.textView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(text);
        try {
            if (logBean.getColorResId() != -1) {
                viewHolder.textView.setTextColor(ContextCompat.getColor(context, logBean.getColorResId()));
            } else {
                viewHolder.textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            }
        } catch (Exception e) {
            e.printStackTrace();
            viewHolder.textView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
        }
        return convertView;
    }


    private static class ViewHolder {
        TextView textView;
    }
}

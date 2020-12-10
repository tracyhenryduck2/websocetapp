package com.siterwell.demo.device;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.DateUtil;
import com.siterwell.demo.device.bean.WarningHistoryBean;
import com.siterwell.demo.device.bean.WaterSensorDescBean;

import java.util.List;


/**
 * Created by jishu0001 on 2016/9/2.
 */
public class WaterSensorHistoryParaAdapter extends BaseAdapter {
    private Context mContext;
    private List<WarningHistoryBean> mLists;

    public WaterSensorHistoryParaAdapter(Context context, List<WarningHistoryBean> lists ){
        this.mContext =context;
        this.mLists = lists;
    }

    public int getCount(){
        return mLists.size();
    }

    @Override
    public boolean areAllItemsEnabled(){
        return false;
    }


    public WarningHistoryBean getItem(int position){
        return mLists.get(position);
    }

    public long getItemId(int position){
        return position;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        View tv = null;
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.cell_battery_history, parent,
                    false);
            holder = new ViewHolder();
            holder.timetextView = (TextView)convertView.findViewById(R.id.time);
            holder.contentView = (TextView)convertView.findViewById(R.id.content);
            holder.pot = (ImageView) convertView.findViewById(R.id.pot);
            convertView.setTag(holder);
        }else{
            tv = convertView;
            holder = (ViewHolder) tv.getTag();
        }

        holder.timetextView.setText(DateUtil.getDateFormat(mLists.get(position).getReportTime()));
       holder.pot.setImageResource(WaterSensorDescBean.getStatusColor(mLists.get(position).getWarningsubject()));
       holder.contentView.setText(WaterSensorDescBean.getStatusString(mLists.get(position).getWarningsubject()));
        return convertView;
    }


    private class ViewHolder {
        private TextView timetextView;
        private TextView contentView;
        private ImageView pot;
    }

    public void Refresh(List<WarningHistoryBean> list){
        this.mLists=list;
        notifyDataSetChanged();
    }

}

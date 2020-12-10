package com.siterwell.demo.device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.DataUtils;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.protocol.ResolveTimer;

import java.util.List;

/**
 * Created by gc-0001 on 2017/6/14.
 */

public class SocketTimerAdapter extends BaseAdapter{

    private List<WifiTimerBean> list;
    private Context context;
    private switchItemListener doneWithItemListener;
    private UnitTools unitTools;


    public SocketTimerAdapter(Context context, List<WifiTimerBean> list,switchItemListener doneWithItemListener) {
        this.context = context;
        this.list = list;
        this.doneWithItemListener = doneWithItemListener;
        unitTools = new UnitTools(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null) {
            convertView = LayoutInflater.from(context
            ).inflate(R.layout.cell_socket_timer, parent,
                    false);

            holder.hour = (TextView) convertView.findViewById(R.id.time_hour);
            holder.min = (TextView) convertView.findViewById(R.id.time_min);
            holder.week = (TextView)convertView.findViewById(R.id.weektime);
            holder.status = (TextView) convertView.findViewById(R.id.tostatus);
            holder.notice = (ImageView) convertView.findViewById(R.id.notice);
            holder.btn_enable = (ImageButton)convertView.findViewById(R.id.enable);
            holder.delete1 = (RelativeLayout)convertView.findViewById(R.id.delete1);
            holder.other1 = (RelativeLayout)convertView.findViewById(R.id.other1);
            holder.delete2 = (RelativeLayout)convertView.findViewById(R.id.delete2);
            holder.other2 = (RelativeLayout)convertView.findViewById(R.id.other2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        WifiTimerBean item = list.get(position);


        holder.status.setText(item.getTostatus()==1?context.getResources().getString(R.string.on):context.getResources().getString(R.string.off));
        if(item.getNotice()==1) holder.notice.setVisibility(View.VISIBLE);
        else                  holder.notice.setVisibility(View.GONE);
        holder.hour.setText(item.getHour()<10? ("0"+String.valueOf(item.getHour())):String.valueOf(item.getHour()));
        holder.min.setText(item.getMin()<10? ("0"+String.valueOf(item.getMin())):String.valueOf(item.getMin()));
        holder.week.setText(DataUtils.getWeekinfo(item.getWeek(),context));
        if(item.getEnable()==1){
            holder.btn_enable.setImageResource(R.drawable.on_kg_icon);
        }else{
            holder.btn_enable.setImageResource(R.drawable.off_kg_icon);
        }
        holder.btn_enable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneWithItemListener.switchclick(position);
            }
        });
        convertView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                doneWithItemListener.click(position);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                doneWithItemListener.longclick(position);
                return true;
            }
        });
        return convertView;
    }


    private  class ViewHolder {
        public TextView hour;
        public TextView min;
        public TextView status;
        public TextView week;
        public ImageView notice;
        public ImageButton btn_enable;
        public RelativeLayout delete1;
        public RelativeLayout other1;
        public RelativeLayout delete2;
        public RelativeLayout other2;

    }

    public void refreshList(List<WifiTimerBean> mlists){
        this.list = mlists;
        notifyDataSetChanged();
    }

    public interface switchItemListener{
        void switchclick(int position);
        void longclick(int position);
        void click(int position);
    }

}
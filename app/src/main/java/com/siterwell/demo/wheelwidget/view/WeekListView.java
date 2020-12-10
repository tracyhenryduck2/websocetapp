package com.siterwell.demo.wheelwidget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.siterwell.demo.R;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by gc-0001 on 2016/12/2.
 */
public class WeekListView extends ListView {
    private Context mContext;
    private final String[] weekstr= {"星期一","星期二","星期三","星期四","星期五","星期六","星期日"};
    private MyweekAdapter myweekAdapter;
    private ArrayList<Integer> mlist;

    public WeekListView(Context context){
        super(context);
        this.mContext = context;
    }

    public WeekListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }

    public void init(){

//        this.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        myweekAdapter = new MyweekAdapter(mContext,weekstr);
        this.setAdapter(myweekAdapter);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);



    }

    public ArrayList<Integer> getCurrentItem(){
        mlist =new ArrayList<Integer>();

        for(int i=0;i<weekstr.length;i++){
            if(myweekAdapter.isSelected.get(i)){
                mlist.add(i);
            }
        }


        return mlist;
    }

    private class MyweekAdapter extends BaseAdapter {
        String[] str;
        private Context mcontext;
        private ViewHolder holder;
        public HashMap<Integer, Boolean> isSelected;

        public MyweekAdapter(Context mc, String[] str) {
            this.mcontext = mc;
            this.str = str;
            init();
        }


        private void init() {
            isSelected = new HashMap<Integer, Boolean>();

                for (int i = 0; i < weekstr.length; i++) {
                    isSelected.put(i,false);
                }

        }


        @Override
        public int getCount() {
            return str.length;
        }

        @Override
        public String getItem(int position) {
            return str[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if( convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mcontext).inflate(R.layout.cell_timer_week,null);
                holder.checkBox= (CheckBox) convertView.findViewById(R.id.checkboxo);
                holder.textView = (TextView) convertView.findViewById(R.id.weekName);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(str[position]);
            updataBackground(position,holder.checkBox);
            holder.checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                         isSelected.put(position,!isSelected.get(position));

                }
            });
            return convertView;
        }

        @SuppressLint("NewApi")
        protected void updataBackground(int position, CheckBox view){

            if(isSelected.get(position)){
                view.setChecked(true);
            }else{
                view.setChecked(false);
            }

        };

        private class ViewHolder {
            CheckBox checkBox;
            TextView textView;
        }

    }

}

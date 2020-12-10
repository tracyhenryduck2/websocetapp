package com.siterwell.demo.device;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.DateUtil;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.demo.device.bean.SocketHistoryBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.http.bean.DeviceBean;

import java.util.List;

/**
 * Created by gc-0001 on 2017/4/24.
 */

public class SocketHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener  {
    private Context mContext;
    private List<SocketHistoryBean> datas;
    private DeviceDao deviceDao;
    //普通布局的type
    static final int TYPE_ITEM = 0;
    //脚布局
    static final int TYPE_FOOTER = 1;
    //正在加载更多
    static final int LOADING_MORE = 1;
    //没有更多
    static final int NO_MORE = 2;
    //  //上拉加载更多
    static final int PULL_LOAD_MORE = 0;
    //脚布局当前的状态,默认为没有更多
    int footer_state = PULL_LOAD_MORE;


    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, DeviceBean deviceBean);
        void onItemLongClick(View view, DeviceBean deviceBean);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public SocketHistoryAdapter(Context context, List<SocketHistoryBean> datas) {
        mContext=context;
        this.datas=datas;
        deviceDao = new DeviceDao(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(mContext
            ).inflate(R.layout.cell_alarm_total, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            return holder;
        } else if (viewType == TYPE_FOOTER) {
            //脚布局
            View view = View.inflate(mContext, R.layout.recyclerview_foot, null);
            FootViewHolder footViewHolder = new FootViewHolder(view);
            return footViewHolder;
        }
        return null;





    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){

            DeviceBean deviceBean = deviceDao.findTotalDeviceBySid(datas.get(position).getDeviceid());
            if(deviceBean!=null){
                if(TextUtils.isEmpty(deviceBean.getDeviceName())){
                    ((MyViewHolder) holder).nametextView.setText(DeviceActivitys.getDeviceType(deviceBean));
                }else{
                    ((MyViewHolder) holder).nametextView.setText(deviceBean.getDeviceName());
                }

                ((MyViewHolder) holder).typetextView.setText(DeviceActivitys.getDeviceType(deviceBean));
            }else{
                ((MyViewHolder) holder).typetextView.setText("");
                ((MyViewHolder) holder).nametextView.setText(datas.get(position).getDeviceid());
            }

            ((MyViewHolder) holder).timetextView.setText(DateUtil.getDateFormat(datas.get(position).getReportTime()));
            try {
                 ((MyViewHolder) holder).contentView.setText(SocketDescBean.getStatusShortString(datas.get(position).getWarningsubject()));
            }catch (NullPointerException e){
                ((MyViewHolder) holder).contentView.setText(SocketDescBean.getStatusShortString(datas.get(position).getWarningsubject()));
            }


            ((SocketHistoryAdapter.MyViewHolder) holder).itemView.setTag(datas.get(position));
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            if (position == 0) {//如果第一个就是脚布局,,那就让他隐藏
                footViewHolder.mProgressBar.setVisibility(View.GONE);
                footViewHolder.tv_line1.setVisibility(View.GONE);
                footViewHolder.tv_line2.setVisibility(View.GONE);
                footViewHolder.tv_state.setText("");
            }
            switch (footer_state) {//根据状态来让脚布局发生改变


                        case PULL_LOAD_MORE://上拉加载
          footViewHolder.mProgressBar.setVisibility(View.GONE);
          footViewHolder.tv_state.setText(mContext.getResources().getString(R.string.pull_download_more));
          break;
                case LOADING_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.VISIBLE);
                    footViewHolder.tv_line1.setVisibility(View.GONE);
                    footViewHolder.tv_line2.setVisibility(View.GONE);
                    footViewHolder.tv_state.setText(mContext.getResources().getString(R.string.downloading));
                    break;
                case NO_MORE:
                    footViewHolder.mProgressBar.setVisibility(View.GONE);
                    footViewHolder.tv_line1.setVisibility(View.VISIBLE);
                    footViewHolder.tv_line2.setVisibility(View.VISIBLE);
                    footViewHolder.tv_state.setText(mContext.getResources().getString(R.string.i_have_line));
                    footViewHolder.tv_state.setTextColor(Color.parseColor("#ff00ff"));
                    break;
            }
        }

    }

    @Override
    public int getItemCount()
    {
        return datas != null ? datas.size() + 1 : 0;
    }

    @Override
    public int getItemViewType(int position) {
        //如果position加1正好等于所有item的总和,说明是最后一个item,将它设置为脚布局
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
             DeviceBean deviceBean = (DeviceBean)v.getTag();
            mOnItemClickListener.onItemClick(v,deviceBean);
        }

    }


    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            DeviceBean deviceBean = (DeviceBean)v.getTag();
            mOnItemClickListener.onItemLongClick(v,deviceBean);
        }
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView timetextView;
        private TextView contentView;
        private TextView typetextView;
        private TextView nametextView;

        public MyViewHolder(View view)
        {
            super(view);
            timetextView = (TextView)view.findViewById(R.id.time);
            typetextView = (TextView)view.findViewById(R.id.type);
            nametextView = (TextView)view.findViewById(R.id.name);
            contentView = (TextView)view.findViewById(R.id.status);

        }
    }



    /**
     * 脚布局的ViewHolder
     */
    public static class FootViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar mProgressBar;
        private TextView tv_state;
        private TextView tv_line1;
        private TextView tv_line2;


        public FootViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            tv_state = (TextView) itemView.findViewById(R.id.foot_view_item_tv);
            tv_line1 = (TextView) itemView.findViewById(R.id.tv_line1);
            tv_line2 = (TextView) itemView.findViewById(R.id.tv_line2);

        }
    }


    public void Refresh(List<SocketHistoryBean> list){
        this.datas=list;
        this.footer_state = PULL_LOAD_MORE;
        notifyDataSetChanged();
    }

    /**
     * 改变脚布局的状态的方法,在activity根据请求数据的状态来改变这个状态
     *
     * @param state
     */
    public void changeState(int state) {
        this.footer_state = state;
        notifyDataSetChanged();
    }


}

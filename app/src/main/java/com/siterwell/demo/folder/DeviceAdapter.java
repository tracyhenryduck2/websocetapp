package com.siterwell.demo.folder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.siterwell.demo.R;
import com.siterwell.demo.device.DeviceActivitys;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.device.bean.WaterSensorDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.bean.DeviceType;
import me.siter.sdk.http.bean.DeviceBean;

import java.util.List;


/**
 * Created by gc-0001 on 2017/4/24.
 */

public class DeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener  {
    private Context mContext;
    private List<DeviceBean> datas;
    private DeviceDao deviceDao;
    private final String TAG = "DeviceAdapter";

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view,DeviceBean deviceBean);
        void onItemLongClick(View view,DeviceBean deviceBean);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public DeviceAdapter(Context context, List<DeviceBean> datas) {
        mContext=context;
        this.datas=datas;
        deviceDao = new DeviceDao(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

            View view = LayoutInflater.from(mContext
            ).inflate(R.layout.cell_device, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            return holder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
             if(DeviceType.BATTERY.toString().equals(datas.get(position).getModel())){
                 ((MyViewHolder) holder).iv.setImageResource(R.mipmap.bat2);
             }else if(DeviceType.WIFISOKECT.toString().equals(datas.get(position).getModel())){
                 ((MyViewHolder) holder).iv.setImageResource(R.mipmap.wifisocket);
             }else if(DeviceType.WATERSENEOR.toString().equals(datas.get(position).getModel())){
                 ((MyViewHolder) holder).iv.setImageResource(R.mipmap.waters1);
             }else{
                 ((MyViewHolder) holder).iv.setImageResource(R.mipmap.smk);
             }


            if(TextUtils.isEmpty(datas.get(position).getDeviceName())){
                ((MyViewHolder) holder).textView.setText( DeviceActivitys.getDeviceType(datas.get(position)));
            }else{
                if(datas.get(position).getDeviceName().contains("Battery-")){
                    ((MyViewHolder) holder).textView.setText(datas.get(position).getDeviceName().replace("Battery-","Unijem Battery-"));
                }else if(datas.get(position).getDeviceName().contains("智能电池-")){
                    ((MyViewHolder) holder).textView.setText(datas.get(position).getDeviceName().replace("智能电池-","Unijem Battery-"));
                }else{
                    ((MyViewHolder) holder).textView.setText(datas.get(position).getDeviceName());
                }

            }

            if(DeviceType.BATTERY.toString().equals(datas.get(position).getModel())){
                BatteryDescBean batteryDescBean = deviceDao.findBatteryBySid(datas.get(position).getDevTid());
                ((MyViewHolder) holder).statusView.setText(batteryDescBean.getStatusDtail(batteryDescBean.getStatus()));
                ((MyViewHolder) holder).statusView.setTextColor((ColorStateList) mContext.getResources().getColorStateList(BatteryDescBean.getStatusColor(batteryDescBean.getStatus())));
            }else if(DeviceType.WATERSENEOR.toString().equals(datas.get(position).getModel())){
                BatteryDescBean batteryDescBean = deviceDao.findBatteryBySid(datas.get(position).getDevTid());
                ((MyViewHolder) holder).statusView.setText(batteryDescBean.getStatusDtail(batteryDescBean.getStatus()));
                ((MyViewHolder) holder).statusView.setTextColor((ColorStateList) mContext.getResources().getColorStateList(WaterSensorDescBean.getStatusColor(batteryDescBean.getStatus())));
            }else {
                ((MyViewHolder) holder).statusView.setText(datas.get(position).isOnline()?mContext.getResources().getString(R.string.online):mContext.getResources().getString(R.string.offline));
            }
            ((DeviceAdapter.MyViewHolder) holder).itemView.setTag(datas.get(position));
        }
    }

    @Override
    public int getItemCount()
    {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {

            return 0;
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
        private ImageView iv;
        private TextView textView;
        private TextView statusView;

        public MyViewHolder(View view)
        {
            super(view);
            iv = (ImageView) view.findViewById(R.id.cellScenceImage);
            textView = (TextView)view.findViewById(R.id.cellScenceName);
            statusView = (TextView)view.findViewById(R.id.status);
        }
    }

    public void Refresh(List<DeviceBean> list){
        this.datas=list;
        notifyDataSetChanged();
    }

}

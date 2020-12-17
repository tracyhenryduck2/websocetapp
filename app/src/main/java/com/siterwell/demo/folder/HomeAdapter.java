package com.siterwell.demo.folder;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.siterwell.demo.R;
import com.siterwell.demo.folder.bean.LocalFolderBean;

import java.util.List;

/**
 * Created by gc-0001 on 2017/4/24.
 */

public class HomeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener  {
    private Context mContext;
    private List<LocalFolderBean> datas;

    private int[] images = {
        R.mipmap.parlor_icon,
        R.mipmap.stairs_icon,
        R.mipmap.masterbedroom_icon,
        R.mipmap.guestroom_icon,
        R.mipmap.kitchen_icon,
        R.mipmap.home_icon,
        R.mipmap.home_icon
    };

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view,LocalFolderBean deviceBean);
        void onItemLongClick(View view,LocalFolderBean deviceBean);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public HomeAdapter(Context context, List<LocalFolderBean> datas) {
        mContext=context;
        this.datas=datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

            View view = LayoutInflater.from(mContext
            ).inflate(R.layout.folder_cell, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            return holder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){

            if(TextUtils.isEmpty(datas.get(position).getImage())){
                ((MyViewHolder) holder).iv.setImageResource(images[0]);
            }else{
                if("ufile0".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[0]);
                }else if("ufile1".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[1]);
                }else if("ufile2".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[2]);
                }else if("ufile3".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[3]);
                }else if("ufile4".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[4]);
                }else if("ufile5".equals(datas.get(position).getImage())){
                    ((MyViewHolder) holder).iv.setImageResource(images[5]);
                }else{
                    ((MyViewHolder) holder).iv.setImageResource(images[0]);
                }
            }
            if("root".equals(datas.get(position).getFolderName()))
                ((MyViewHolder) holder).textView.setText(R.string.root);
            else
                ((MyViewHolder) holder).textView.setText(datas.get(position).getFolderName());
            ((MyViewHolder) holder).itemView.setTag(datas.get(position));
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
            LocalFolderBean folderBean = (LocalFolderBean)v.getTag();
            mOnItemClickListener.onItemClick(v,folderBean);
        }

    }


    @Override
    public boolean onLongClick(View v) {
        if (mOnItemClickListener != null) {
            LocalFolderBean folderBean = (LocalFolderBean)v.getTag();
            mOnItemClickListener.onItemLongClick(v,folderBean);
        }
        return false;
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView iv;
        private TextView textView;

        public MyViewHolder(View view)
        {
            super(view);
            iv = (ImageView) view.findViewById(R.id.cellImageView);
            textView = (TextView)view.findViewById(R.id.cellTextView);
        }
    }

    public void Refresh(List<LocalFolderBean> list){
        this.datas=list;
        notifyDataSetChanged();
    }

}

package com.siterwell.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by gc-0001 on 2017/4/24.
 */

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private List<QuestionBean> datas;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public QuestionAdapter(Context context, List<QuestionBean> datas) {
        mContext=context;
        this.datas=datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

            View view = LayoutInflater.from(mContext
            ).inflate(R.layout.cell_questions_answers, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);

            view.findViewById(R.id.top).setOnClickListener(this);

            return holder;


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            ((QuestionAdapter.MyViewHolder) holder).nametextView.setText(datas.get(position).getQuestion());
            ((QuestionAdapter.MyViewHolder) holder).contentView.setText(datas.get(position).getAnswer());
            if(datas.get(position).isopen()){
                ((MyViewHolder) holder).arrow.setImageResource(R.drawable.arrowdown);
                ((MyViewHolder) holder).content.setVisibility(View.VISIBLE);
            }else{
                ((MyViewHolder) holder).arrow.setImageResource(R.drawable.arrow2);
                ((MyViewHolder) holder).content.setVisibility(View.GONE);
            }
            ((QuestionAdapter.MyViewHolder) holder).top.setTag(position);
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
            int position = (int)v.getTag();
            mOnItemClickListener.onItemClick(v,position);
        }

    }



    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private LinearLayout top;
        private LinearLayout content;
        private ImageView arrow;
        private TextView nametextView;
        private TextView contentView;

        public MyViewHolder(View view)
        {
            super(view);
            top = (LinearLayout)view.findViewById(R.id.top);
            arrow = (ImageView)view.findViewById(R.id.arrow);
            nametextView = (TextView)view.findViewById(R.id.question);
            contentView = (TextView)view.findViewById(R.id.answer);
            content = (LinearLayout)view.findViewById(R.id.content);

        }
    }

    public void Refresh(List<QuestionBean> list){
        this.datas=list;
        notifyDataSetChanged();
    }

}

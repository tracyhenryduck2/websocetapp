package com.siterwell.demo.folder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.siterwell.demo.R;
import com.siterwell.demo.folder.bean.LocalFolderBean;

import java.util.List;

/**
 * Created by gc-0001 on 2017/6/8.
 */

public class IcosAdpater extends BaseAdapter {

    private int[] images = {
        R.mipmap.parlor_icon,
        R.mipmap.stairs_icon,
        R.mipmap.masterbedroom_icon,
        R.mipmap.guestroom_icon,
        R.mipmap.kitchen_icon,
        R.mipmap.home_icon,
        R.mipmap.home_icon
    };

   private Context context;
    private  List<LocalFolderBean> lists;
    private ViewHolder holder;


    public IcosAdpater(Context context, List<LocalFolderBean> lists){
        this.context = context;
        this.lists = lists;
    }


    private class ViewHolder {
        ImageView imageView_item_image;
        ImageView imageView_item_select;
        TextView imageView_item_name;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public LocalFolderBean getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        holder = null;
        final LocalFolderBean eq = lists.get(position);
        if( convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.cell_ico,null);
            holder.imageView_item_image= (ImageView)convertView.findViewById(R.id.id_item_image);
            holder.imageView_item_select = (ImageView)convertView.findViewById(R.id.id_item_select);
            holder.imageView_item_name = (TextView)convertView.findViewById(R.id.ico_name);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        holder.imageView_item_name.setText(eq.getFolderName());
        holder.imageView_item_image.setImageResource(images[position]);
        if(eq.Isselect())
        holder.imageView_item_select.setImageResource(R.mipmap.pictures_selected);
        else
        holder.imageView_item_select.setImageResource(R.mipmap.picture_unselected);
        return convertView;
    }

    public void refreshLists(List<LocalFolderBean> mlists){
        this.lists = mlists;
        notifyDataSetChanged();
    }

}

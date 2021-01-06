package com.siterwell.demo.common;

import android.content.Context;
import android.util.Log;

import com.siterwell.demo.R;
import com.siterwell.demo.protocol.ByteUtil;

import java.util.HashMap;

/**
 * Created by TracyHenry on 2018/4/28.
 */

public class DataUtils {
    public static final String TAG = "DataUtils";
    public static String getWeekinfo(String week,Context context){

        try {
            String weektime = "";
            byte ds = ByteUtil.hexStr2Bytes(week)[0];
            byte f = 0x00;
            for(int i=0;i<7;i++){

                f =   (byte)((0x02 << i) & ds);
                if(f!=0){
                    weektime += (context.getResources().getStringArray(R.array.week)[i] + "、");
                }
            }
            weektime = weektime.substring(0,weektime.length()-1);


            return  weektime;
        }catch (Exception e){
            Log.i(TAG,"week is null");
            return "";
        }

    }

    public static String getWeekinfoHash(HashMap<Integer,Boolean> week, Context context){


        String weektime = "";
        for(int i=0;i<7;i++){


            if(week.get(i)){
                weektime += (context.getResources().getStringArray(R.array.week)[i] + "、");
            }
        }
        weektime = weektime.substring(0,weektime.length()-1);


        return ( weektime + context.getResources().getString(R.string.repeat));


    }

}

package com.siterwell.demo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.litesuits.android.log.Log;
import com.siterwell.sdk.bean.WifiTimerBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by gc-0001 on 2017/4/26.
 */

public class WifiTimerDao {
    private final String TAG = "WifiTimerDao";
    private SysDB sys;
    private SQLiteDatabase db;
    Context context;
    public WifiTimerDao(Context context){
        this.context = context;
        this.sys = new SysDB(context);
        this.db = this.sys.getWritableDatabase();
    }



    /**
     * 插入定时数列到数据库
     *
     * @param wifiTimerBeanList
     * @return
     */
    public ArrayList<Long> insertTimerList(List<WifiTimerBean> wifiTimerBeanList) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (WifiTimerBean c : wifiTimerBeanList) {
                long rowId = insertWifiTimer(c);
                if (rowId != -1L) {
                    rows.add(rowId);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return rows;
    }

    /**
     * 插入Wifi定时数据
     * @param wifiTimerBean
     * @return
     */
    public long insertWifiTimer(WifiTimerBean wifiTimerBean) {

        if(wifiTimerBean == null || TextUtils.isEmpty(wifiTimerBean.getDeviceid())) {
            return -1L;
        }
        ContentValues values = null;
        try {
            values = new ContentValues();
            values.put("timerid", wifiTimerBean.getTimerid());
            values.put("deviceid", wifiTimerBean.getDeviceid());
            values.put("zone", wifiTimerBean.getNotice());
            values.put("week",wifiTimerBean.getWeek());
            values.put("hour",wifiTimerBean.getHour());
            values.put("min",wifiTimerBean.getMin());
            values.put("enable",wifiTimerBean.getEnable());
            values.put("tostatus",wifiTimerBean.getTostatus());
            if(!isHasTimer(wifiTimerBean.getDeviceid(),wifiTimerBean.getTimerid()) && 255 != wifiTimerBean.getEnable()) {
                return db.insert("sockettimer", null, values);
            } else {
                //若week为0则删除该定时任务
                if(255 == wifiTimerBean.getEnable()){
                    return db.delete("sockettimer", "deviceid =? and timerid =?",new String[]{wifiTimerBean.getDeviceid(),wifiTimerBean.getTimerid()});
                }else{
                    return db.update("sockettimer",values , "deviceid = '"+wifiTimerBean.getDeviceid()+"' and timerid ='" + wifiTimerBean.getTimerid()+"'",null);
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (values != null) {
                values.clear();
            }
        }
        return -1L;
    }


    //判断是否有该定时数据
    public boolean isHasTimer(String devid,String timerid){

        WifiTimerBean wifiTimerBean = findTimerByTid(devid,timerid);
        try {
            if(TextUtils.isEmpty(wifiTimerBean.getTimerid())){
                return false;
            }else{
                return true;
            }

        }catch (NullPointerException e){
            return false;
        }

    }



    /**
     * @return WifiTimerBean
     */
    public WifiTimerBean findTimerByTid(String devid, String timerid){
        WifiTimerBean wifiTimerBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from sockettimer where deviceid = '"+devid+"' and timerid = '"+timerid+"'",null);
            if(cursor.moveToFirst()) {
                wifiTimerBean = new WifiTimerBean();
                wifiTimerBean.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                wifiTimerBean.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                wifiTimerBean.setNotice(cursor.getInt(cursor.getColumnIndex("zone")));
                wifiTimerBean.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                wifiTimerBean.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                wifiTimerBean.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                wifiTimerBean.setMin(cursor.getInt(cursor.getColumnIndex("min")));
                wifiTimerBean.setTostatus(cursor.getInt(cursor.getColumnIndex("tostatus")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no timer");
        }finally {
            return wifiTimerBean;
        }
    }


    /**
     * methodname:findAllTimerByTime
     * 作者：Henry on 2017/6/15 14:32
     * 邮箱：xuejunju_4595@qq.com
     * 参数:hour,min
     * 返回:获取特定时间的定时星期数据
     */
    public List<String> findAllTimerByTime(int hour,int min){
        List<String> sys2 = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery("select * from sockettimer where hour = "+hour+" and min = "+min+" order by timerid",null);
            while (cursor.moveToNext()){
                sys2.add(cursor.getString(cursor.getColumnIndex("week")));
            }

        }catch (NullPointerException e){
            Log.i(TAG,"no choosed device");
        }finally {
            return sys2;
        }

    }


    /**
     * @return sid List
     */
    public List<String> findAllTimerTid(String deviceid){
        List<String> list = new ArrayList<String>();
        try {
            Cursor cursor = db.rawQuery("select timerid from sockettimer where deviceid = '"+deviceid+"' and  timerid is not null order by timerid,id",null);
            while (cursor.moveToNext()){
                list.add( cursor.getString(cursor.getColumnIndex("timerid")));
            }
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed device");
        }finally {
            return list;
        }

    }

    /**
     * @return sid List
     */
    public List<WifiTimerBean> findAllTimer(String deviceid){
        List<WifiTimerBean> list = new ArrayList<WifiTimerBean>();
        try {
            Cursor cursor = db.rawQuery("select * from sockettimer where deviceid = '"+deviceid+"' and timerid is not null order by timerid,id",null);
            while (cursor.moveToNext()){
               WifiTimerBean wifiTimerBean = new WifiTimerBean();
                wifiTimerBean.setTimerid(cursor.getString(cursor.getColumnIndex("timerid")));
                wifiTimerBean.setDeviceid(cursor.getString(cursor.getColumnIndex("deviceid")));
                wifiTimerBean.setNotice(cursor.getInt(cursor.getColumnIndex("zone")));
                wifiTimerBean.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                wifiTimerBean.setEnable(cursor.getInt(cursor.getColumnIndex("enable")));
                wifiTimerBean.setHour(cursor.getInt(cursor.getColumnIndex("hour")));
                wifiTimerBean.setMin(cursor.getInt(cursor.getColumnIndex("min")));
                wifiTimerBean.setTostatus(cursor.getInt(cursor.getColumnIndex("tostatus")));
                list.add(wifiTimerBean);
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed device");
        }finally {
            return list;
        }

    }


    public void deleteByTimerid(String timerid,String deviceid){
        String where = "timerid = ? and deviceid = ?";
        String[] whereValue ={ timerid,deviceid };
        db.delete("sockettimer", where, whereValue);
    }


    public void updateTimerEnable(WifiTimerBean wifiTimerBean){

        try {
            String where = "deviceid = ? and timerid = ?";
            String[] whereValue = {wifiTimerBean.getDeviceid(),wifiTimerBean.getTimerid()};
            ContentValues cv = new ContentValues();
            cv.put("enable", wifiTimerBean.getEnable());

            db.update("sockettimer", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void reset(){

        db.close();
        sys.close();
    }

}

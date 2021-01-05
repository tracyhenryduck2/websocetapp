package com.siterwell.demo.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.litesuits.android.log.Log;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.device.bean.WaterSensorDescBean;
import com.siterwell.sdk.bean.WaterSensorBean;
import com.siterwell.sdk.http.bean.DeviceBean;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by gc-0001 on 2017/4/26.
 */

public class DeviceDao {
    private final String TAG = "DeviceDao";
    private SysDB sys;
    private SQLiteDatabase db;
    Context context;


    public DeviceDao(Context context){
        this.context = context;
        this.sys = new SysDB(context);
        this.db = this.sys.getWritableDatabase();
    }


    /**
     * 插入设备列表到数据库
     *
     * @param deviceBeenlist
     * @return
     */
    public ArrayList<Long> insertDeviceList(List<DeviceBean> deviceBeenlist) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (DeviceBean c : deviceBeenlist) {
                long rowId = insertDevice(c);
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


    public long insertDevice(DeviceBean deviceBean) {
        if (deviceBean == null || TextUtils.isEmpty(deviceBean.getDevTid())) {
            return -1;
        }
        try {
            ContentValues values = new ContentValues();

            values.put("name", deviceBean.getDeviceName());
            values.put("deviceid",deviceBean.getDevTid());
            values.put("model",deviceBean.getModel());
            values.put("folderid",deviceBean.getFolderId());
            values.put("online",deviceBean.isOnline()?1:0);
            values.put("bindkey",deviceBean.getBindKey());
            values.put("ctrlkey",deviceBean.getCtrlKey());
            values.put("ppkey",deviceBean.getProductPublicKey());
            if (!isHasDevice(deviceBean.getDevTid())) {
                return db.insert("device", null, values);
            }else{
                return db.update("device", values, "deviceid = '" + deviceBean.getDevTid() + "'", null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 批量更新设备状态到数据库
     *
     * @param deviceBeenlist
     * @return
     */
    public ArrayList<Long> updateBatterysList(List<BatteryBean> deviceBeenlist) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (BatteryBean c : deviceBeenlist) {
                long rowId = updateBattery(c);
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


   /*
   @method updateBatterys
   @autor Administrator
   @time 2017/7/27 10:23
   @email xuejunju_4595@qq.com
   */
    public long updateBattery(BatteryBean deviceBean) {
        if (deviceBean == null || TextUtils.isEmpty(deviceBean.getDevTid())) {
            return -1;
        }
        try {
            ContentValues values = new ContentValues();


            values.put("deviceid",deviceBean.getDevTid());
            values.put("percent", deviceBean.getBattPercent());
            values.put("status",deviceBean.getStatus());
            values.put("signal",deviceBean.getSignal());

                return db.update("device", values, "deviceid = '" + deviceBean.getDevTid() + "'", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * 批量更新设备状态到数据库
     *
     * @param deviceBeenlist
     * @return
     */
    public ArrayList<Long> updateWaterSensorsList(List<WaterSensorBean> deviceBeenlist) {

        ArrayList<Long> rows = new ArrayList<Long>();
        try {

            db.beginTransaction();
            for (WaterSensorBean c : deviceBeenlist) {
                long rowId = updateWaterSensor(c);
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

    /*
    @method updateWaterSensor
    @autor Administrator
    @time 2017/7/27 10:23
    @email xuejunju_4595@qq.com
    */
    public long updateWaterSensor(WaterSensorBean waterSensorBean) {
        if (waterSensorBean == null || TextUtils.isEmpty(waterSensorBean.getDevTid())) {
            return -1;
        }
        try {
            ContentValues values = new ContentValues();


            values.put("deviceid",waterSensorBean.getDevTid());
            values.put("percent", waterSensorBean.getBattPercent());
            values.put("status",waterSensorBean.getStatus());
            values.put("signal",waterSensorBean.getSignal());

            return db.update("device", values, "deviceid = '" + waterSensorBean.getDevTid() + "'", null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * @return BatteryDescBean
     */
    public BatteryDescBean findBatteryBySid(String devid){
        BatteryDescBean folderBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from device where deviceid = '"+devid+"'",null);
            if(cursor.moveToFirst()) {
                folderBean = new BatteryDescBean();
                folderBean.setDevTid(devid);
                folderBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                folderBean.setDeviceName(cursor.getString(cursor.getColumnIndex("name")));
                folderBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                folderBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                folderBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
                folderBean.setBattPercent(cursor.getInt(cursor.getColumnIndex("percent")));
                folderBean.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                folderBean.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no battery");
        }finally {
            return folderBean;
        }
    }


    /**
     * @return BatteryDescBean
     */
    public WaterSensorDescBean findWaterSensorBySid(String devid){
        WaterSensorDescBean folderBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from device where deviceid = '"+devid+"'",null);
            if(cursor.moveToFirst()) {
                folderBean = new WaterSensorDescBean();
                folderBean.setDevTid(devid);
                folderBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                folderBean.setDeviceName(cursor.getString(cursor.getColumnIndex("name")));
                folderBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                folderBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                folderBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
                folderBean.setBattPercent(cursor.getInt(cursor.getColumnIndex("percent")));
                folderBean.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                folderBean.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no battery");
        }finally {
            return folderBean;
        }
    }


    /**
     * @return DeviceBean
     */
    public DeviceBean findDeviceBySid(String devid){
        DeviceBean folderBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from device where deviceid = '"+devid+"'",null);
            if(cursor.moveToFirst()) {
                folderBean = new DeviceBean();
                folderBean.setDevTid(devid);
                folderBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
				String name = cursor.getString(cursor.getColumnIndex("name"));
				if(name.equals("Battery-91"))
					name = "Unijem Battery";
                folderBean.setDeviceName(name);
                folderBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                folderBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                folderBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return folderBean;
        }
    }


    /**
     * @return DeviceBean
     */
    public List<DeviceBean> findAllDevice(){
        List<DeviceBean> deviceBeanList = new ArrayList<>();
        try {

            Cursor cursor = db.rawQuery("select * from device order by deviceid",null);
            while (cursor.moveToNext()){
                DeviceBean folderBean = new DeviceBean();
                folderBean.setDevTid(cursor.getString(cursor.getColumnIndex("deviceid")));
                folderBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                folderBean.setDeviceName(cursor.getString(cursor.getColumnIndex("name")));
                folderBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                folderBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                folderBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
                deviceBeanList.add(folderBean);
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return deviceBeanList;
        }
    }

    /**
     * @return DeviceBean
     */
    public DeviceBean findTotalDeviceBySid(String devid){
        DeviceBean folderBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from device where deviceid = '"+devid+"'",null);
            if(cursor.moveToFirst()) {
                folderBean = new DeviceBean();
                folderBean.setDevTid(devid);
                folderBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                folderBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                folderBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                folderBean.setDeviceName(cursor.getString(cursor.getColumnIndex("name")));
                folderBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                folderBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                folderBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no folder");
        }finally {
            return folderBean;
        }
    }

    public List<DeviceBean> findAllDeviceBeanByFolderId(String folderid){
        List<DeviceBean> sys2 = new ArrayList<DeviceBean>();
        try {
            Cursor cursor = db.rawQuery("select * from device where folderid = '"+folderid+"' order by deviceid",null);
            while (cursor.moveToNext()){
                DeviceBean sb = new DeviceBean();
                sb.setDevTid(cursor.getString(cursor.getColumnIndex("deviceid")));
                sb.setModel(cursor.getString(cursor.getColumnIndex("model")));
                sb.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                sb.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                String name = cursor.getString(cursor.getColumnIndex("name"));
				if(name.equals("Battery-91"))
					name = "Unijem Battery";
                sb.setDeviceName(name);
                sb.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                sb.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                sb.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
                sys2.add(sb);
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no choosed device");
        }finally {
            return sys2;
        }

    }


    //判断是否有该设备
    public boolean isHasDevice(String devid){

        DeviceBean deviceBean = findDeviceBySid(devid);
        try {
            if(TextUtils.isEmpty(deviceBean.getDevTid())){
                return false;
            }else{
                return true;
            }

        }catch (NullPointerException e){
            return false;
        }

    }

    public void updateBatteryStatus(BatteryDescBean batteryDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {batteryDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("status", batteryDescBean.getStatus());
            cv.put("signal", batteryDescBean.getSignal());
            cv.put("percent", batteryDescBean.getBattPercent());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void updateBatteryAlarm(BatteryDescBean batteryDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {batteryDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("status", batteryDescBean.getStatus());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void updateDeviceName(String deviceid,String name){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {deviceid};
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void deleteByDeviceId(String deviceid){
        String where = "deviceid = ?";
        String[] whereValue ={ deviceid };
        db.delete("device", where, whereValue);
    }


    public void updateDeviceFolderid(DeviceBean deviceBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {deviceBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("folderid", deviceBean.getFolderId());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    
    /*
    @method 同时更新循环和倒计时
    @autor Administrator
    @time 2017/6/24 9:28
    @email xuejunju_4595@qq.com
    */
    public void updateDeviceWifiSocketAllInfo(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketstatus", socketDescBean.getSocketstatus());
            cv.put("signal", socketDescBean.getSignal());
            cv.put("socketmodel", socketDescBean.getSocketmodel());
            cv.put("circleon", socketDescBean.getCircleon());
            cv.put("circleoff", socketDescBean.getCircleoff());
            cv.put("circlenumber", socketDescBean.getCirclenumber());
            cv.put("countdowntime", socketDescBean.getCountdowntime());
            cv.put("action", socketDescBean.getAction());
            cv.put("notice", socketDescBean.getNotice());
            cv.put("countdownenable", socketDescBean.getCountdownenable());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    /*
    @method 只更新循环
    @autor Administrator
    @time 2017/6/24 9:28
    @email xuejunju_4595@qq.com
    */
    public void updateDeviceWifiSocketOnlyCycle(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketstatus", socketDescBean.getSocketstatus());
            cv.put("signal", socketDescBean.getSignal());
            cv.put("socketmodel", socketDescBean.getSocketmodel());
            cv.put("circleon", socketDescBean.getCircleon());
            cv.put("circleoff", socketDescBean.getCircleoff());
            cv.put("circlenumber", socketDescBean.getCirclenumber());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    /*
@method 只更新倒计时
@autor Administrator
@time 2017/6/24 9:28
@email xuejunju_4595@qq.com
*/
    public void updateDeviceWifiSocketOnlyCountdown(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketstatus", socketDescBean.getSocketstatus());
            cv.put("signal", socketDescBean.getSignal());
            cv.put("socketmodel", socketDescBean.getSocketmodel());
            cv.put("countdowntime", socketDescBean.getCountdowntime());
            cv.put("action", socketDescBean.getAction());
            cv.put("notice", socketDescBean.getNotice());
            cv.put("countdownenable", socketDescBean.getCountdownenable());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void updateDeviceWifiSocketInfo(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketstatus", socketDescBean.getSocketstatus());
            cv.put("signal", socketDescBean.getSignal());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void updateDeviceWifiSocketSwitch(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketstatus", socketDescBean.getSocketstatus());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void updateDeviceWifiSocketMode(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketmodel", socketDescBean.getSocketmodel());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void updateDeviceWifiSocketCircle(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("circleon", socketDescBean.getCircleon());
            cv.put("circleoff", socketDescBean.getCircleoff());
            cv.put("circlenumber", socketDescBean.getCirclenumber());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void updateDeviceWifiSocketCountDown(SocketBean socketDescBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketDescBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("countdowntime", socketDescBean.getCountdowntime());
            cv.put("action", socketDescBean.getAction());
            cv.put("notice", socketDescBean.getNotice());
            cv.put("countdownenable", socketDescBean.getCountdownenable());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    //删除所有包下的设备操作
    public void deleteByFolderId(String folderid)
    {
        String where = "folderid = ?";
        String[] whereValue ={ folderid };
        db.delete("device", where, whereValue);
    }

    /**
     * @return BatteryDescBean
     */
    public SocketDescBean findSocketBySid(String devid){
        SocketDescBean socketDescBean = null;
        try {

            Cursor cursor = db.rawQuery("select * from device where deviceid = '"+devid+"'",null);
            if(cursor.moveToFirst()) {
                socketDescBean = new SocketDescBean();
                socketDescBean.setDevTid(devid);
                socketDescBean.setModel(cursor.getString(cursor.getColumnIndex("model")));
                socketDescBean.setFolderId(cursor.getString(cursor.getColumnIndex("folderid")));
                socketDescBean.setOnline(cursor.getInt(cursor.getColumnIndex("online"))==0?false:true);
                socketDescBean.setDeviceName(cursor.getString(cursor.getColumnIndex("name")));
                socketDescBean.setBindKey(cursor.getString(cursor.getColumnIndex("bindkey")));
                socketDescBean.setCtrlKey(cursor.getString(cursor.getColumnIndex("ctrlkey")));
                socketDescBean.setProductPublicKey(cursor.getString(cursor.getColumnIndex("ppkey")));
                socketDescBean.setSocketmodel(cursor.getInt(cursor.getColumnIndex("socketmodel")));
                socketDescBean.setSocketstatus(cursor.getInt(cursor.getColumnIndex("socketstatus")));
                socketDescBean.setCircleon(cursor.getString(cursor.getColumnIndex("circleon")));
                socketDescBean.setCircleoff(cursor.getString(cursor.getColumnIndex("circleoff")));
                socketDescBean.setCirclenumber(cursor.getInt(cursor.getColumnIndex("circlenumber")));
                socketDescBean.setCountdowntime(cursor.getString(cursor.getColumnIndex("countdowntime")));
                socketDescBean.setAction(cursor.getInt(cursor.getColumnIndex("action")));
                socketDescBean.setNotice(cursor.getInt(cursor.getColumnIndex("notice")));
                socketDescBean.setCountdownenable(cursor.getInt(cursor.getColumnIndex("countdownenable")));
                socketDescBean.setSignal(cursor.getInt(cursor.getColumnIndex("signal")));
            }
            cursor.close();
        }catch (NullPointerException e){
            Log.i(TAG,"no SocketDescBean");
        }finally {
            return socketDescBean;
        }
    }

    public void updateSocket(SocketBean socketBean){

        try {
            String where = "deviceid = ?";
//        String[] whereValue = {Integer.toString(eq.getEquipmentId())};
            String[] whereValue = {socketBean.getDevTid()};
            ContentValues cv = new ContentValues();
            cv.put("socketmodel", socketBean.getSocketmodel());
            cv.put("socketstatus", socketBean.getSocketstatus());
            db.update("device", cv, where, whereValue);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    public void reset(){

        db.close();
        sys.close();
    }
}

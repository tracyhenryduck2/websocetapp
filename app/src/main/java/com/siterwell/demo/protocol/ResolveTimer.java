package com.siterwell.demo.protocol;

import android.text.TextUtils;

import com.siterwell.demo.bean.WifiTimerBean;


/**
 * Created by gc-0001 on 2017/5/16.
 */

public class ResolveTimer {
    private static final String TAG = "ResolveTimer";
    private String code ;
    private WifiTimerBean wifiTimerBean;
    private boolean target;

    public ResolveTimer(String code,String deviceid){
        this.code = code;
        this.target = isTarget(code,deviceid);
    }

    private boolean isTarget(String code,String deviceid){
        if(TextUtils.isEmpty(code)) return false;

        if(code.length()!=14) return false;

        wifiTimerBean =new WifiTimerBean();

        int timerid = Integer.parseInt(code.substring(0,2),16);
        int enable = Integer.parseInt(code.substring(2,4),16);
        int notice = Integer.parseInt(code.substring(4,6),16);
        int tostatus = Integer.parseInt(code.substring(6,8),16);
        int hour = Integer.parseInt(code.substring(8,10),16);
        int min  = Integer.parseInt(code.substring(10,12),16);
        String week = code.substring(12,14);

        wifiTimerBean.setMin(min);
        wifiTimerBean.setHour(hour);
        wifiTimerBean.setWeek(week);
        wifiTimerBean.setTostatus(tostatus);
        wifiTimerBean.setNotice(notice);
        wifiTimerBean.setEnable(enable);
        wifiTimerBean.setTimerid(String.valueOf(timerid));
        wifiTimerBean.setDeviceid(deviceid);
        return true;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isTarget() {
        return target;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

    public WifiTimerBean getWifiTimerBean() {
        return wifiTimerBean;
    }


    public static String getCode(WifiTimerBean timerGatewayBean){
        String code = "";
        try {
            int timerid =  Integer.parseInt(timerGatewayBean.getTimerid());
            String oo = "0";
            if (Integer.toHexString(timerid).length() < 2) {
                oo = oo + Integer.toHexString(timerid);
            } else {
                oo = Integer.toHexString(timerid);
            }

            int enable = timerGatewayBean.getEnable();
            String enable00 = "0";
            if(enable == 1){
                enable00 +="1";
            }else{
                enable00 +="0";
            }

            int notice = timerGatewayBean.getNotice();
            String notice00 = "0";
            if (Integer.toHexString(notice).length() < 2) {
                notice00 = notice00 + Integer.toHexString(notice);
            } else {
                notice00 = Integer.toHexString(notice);
            }


            int tostatus = timerGatewayBean.getTostatus();
            String tostatus00 = "0";
            if (Integer.toHexString(tostatus).length() < 2) {
                tostatus00 = tostatus00 + Integer.toHexString(tostatus);
            } else {
                tostatus00 = Integer.toHexString(tostatus);
            }



            int hour = timerGatewayBean.getHour();

            String hour00 = "0";
            if (Integer.toHexString(hour).length() < 2) {
                hour00 = hour00 + Integer.toHexString(hour);
            } else {
                hour00 = Integer.toHexString(hour);
            }


            int min = timerGatewayBean.getMin();

            String min00 = "0";
            if (Integer.toHexString(min).length() < 2) {
                min00 = min00 + Integer.toHexString(min);
            } else {
                min00 = Integer.toHexString(min);
            }

            String week00 = timerGatewayBean.getWeek();

            String fullcode = oo + enable00 + notice00 + tostatus00  + hour00 + min00 + week00;
            String crc = ByteUtil.CRCmakerChar(fullcode);

            code = fullcode + crc;
        }catch (NullPointerException e){
            return "bean is null";
        }
        return code;
    }

    public static String getCRCCode(WifiTimerBean wifiTimerBean){
        String crc = "";
        try {
            int timerid =  Integer.parseInt(wifiTimerBean.getTimerid());
            String oo = "0";
            if (Integer.toHexString(timerid).length() < 2) {
                oo = oo + Integer.toHexString(timerid);
            } else {
                oo = Integer.toHexString(timerid);
            }

            int enable = wifiTimerBean.getEnable();
            String enable00 = "0";
            if(enable == 1){
                enable00 +="1";
            }else{
                enable00 +="0";
            }

            int modeid = wifiTimerBean.getTostatus();
            String mode00 = "0";
            if (Integer.toHexString(modeid).length() < 2) {
                mode00 = mode00 + Integer.toHexString(modeid);
            } else {
                mode00 = Integer.toHexString(modeid);
            }

            String week00 = wifiTimerBean.getWeek();

            int hour = wifiTimerBean.getHour();

            String hour00 = "0";
            if (Integer.toHexString(hour).length() < 2) {
                hour00 = hour00 + Integer.toHexString(hour);
            } else {
                hour00 = Integer.toHexString(hour);
            }

            int notice = wifiTimerBean.getNotice();
            String notice00 = "0";
            if (Integer.toHexString(notice).length() < 2) {
                notice00 = notice00 + Integer.toHexString(notice);
            } else {
                notice00 = Integer.toHexString(notice);
            }


            int min = wifiTimerBean.getMin();

            String min00 = "0";
            if (Integer.toHexString(min).length() < 2) {
                min00 = min00 + Integer.toHexString(min);
            } else {
                min00 = Integer.toHexString(min);
            }

            String fullcode = oo + enable00 +notice00 + mode00  + hour00 + min00 + week00;
            crc = ByteUtil.CRCmakerChar(fullcode);
        }catch (NullPointerException e){
            return "bean is null";
        }
        return crc;
    }


}

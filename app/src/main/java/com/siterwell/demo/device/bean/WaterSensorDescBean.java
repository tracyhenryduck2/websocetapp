package com.siterwell.demo.device.bean;

import com.siterwell.demo.bean.WaterSensorBean;
import com.siterwell.demo.MyApplication;
import com.siterwell.demo.R;

/**
 * Created by TracyHenry on 2018/2/6.
 */

public class WaterSensorDescBean extends WaterSensorBean {

    public WaterSensorDescBean(){
        super();
    }
    private static int[] backgroud = new int[]{
            R.color.green,
            R.color.red,
            R.color.tu_yellow,
            R.color.yellow,
            R.color.colorPrimary,
            R.color.red,
            R.color.colorPrimary,
            R.color.white
    };

    private static int[] alarmbackgroud = new int[]{
            R.drawable.lv,
            R.drawable.hong,
            R.drawable.tuyellow,
            R.drawable.yellow,
            R.drawable.cc,
            R.drawable.hong,
            R.drawable.cc,
            R.drawable.white
    };

    public static int[] imageQ = new int[]{
            R.drawable.q0,
            R.drawable.q1,
            R.drawable.q2,
            R.drawable.q3,
            R.drawable.q4,
    };
    public static int[] imageS = new int[]{
            R.drawable.s0,
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3
    };



    /**
     * 获取状态信息
     * @param status
     * @return
     */
    public String getStatusDtail(int status){
        String statusDetail = "";
        switch (status){
            case STATUS_NORMAL:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_normal);
                break;
            case STATUS_EQUIPMENT_ALARM:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_alarm);
                break;
            case STATUS_EQUIPENT_LOW_VOLTAGE:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_lowvolt);
                break;
            case STATUS_EQUIPMENT_TROUBLE:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_trouble);
                break;
            case STATUS_EQUIPMENT_SILENCE:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_silence);
                break;
            case STATUS_EQUIPMENT_NOT_CONNECT:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_not_connect);
                break;
            case STATUS_LOW_VOLTAGE_SILENCE_TEN:
                statusDetail = MyApplication.getAppContext().getResources().getString(R.string.battery_lvolt_silence);
                break;
            case STATUS_LINK:
                 statusDetail = MyApplication.getAppContext().getResources().getString(R.string.linkage_alarm);
                break;
            default:
                break;
        }
        return statusDetail;
    }

    /**
     * 获取状态颜色配置
     * @param status
     * @return
     */
    public static int getStatusColor(int status){
        int back = 0;
        switch (status){
            case STATUS_NORMAL:
                back = backgroud[0];
                break;
            case STATUS_EQUIPMENT_ALARM:
                back = backgroud[1];
                break;
            case STATUS_EQUIPENT_LOW_VOLTAGE:
                back = backgroud[2];
                break;
            case STATUS_EQUIPMENT_TROUBLE:
                back = backgroud[3];
                break;
            case STATUS_EQUIPMENT_SILENCE:
                back = backgroud[4];
                break;
            case STATUS_EQUIPMENT_NOT_CONNECT:
                back = backgroud[5];
                break;
            case STATUS_LINK:
                back = backgroud[6];
                break;
            default:
                back = backgroud[0];
                break;
        }
        return back;
    }

    /**
     * 获取状态颜色配置
     * @param status
     * @return
     */
    public static int getStatusColor(String status){
        int back = 0;
        if("设备正常".equals(status) || "Normal product".equals(status)  || "NORMAL".equals(status)){
            back = alarmbackgroud[0];
        }else if("产品低电压".equals(status) || "Low voltage product".equals(status)  || "NIEDRIGE VOLTZAHL".equals(status) || "BASSE TENSION".equals(status)){
            back = alarmbackgroud[2];
        }else if("报警器故障".equals(status) || "Product failure".equals(status)  || "PROBLEM".equals(status) || "PROBLÈME".equals(status)){
            back = alarmbackgroud[3];
        }else if("报警已取消".equals(status) || "Alarm cancelled".equals(status) || "STILL".equals(status) || "MUET".equals(status)){
            back = alarmbackgroud[4];
        }else if("未连接报警器".equals(status) || "Unconnected alarm".equals(status)  || "NICHT VERBUNDEN".equals(status) || "PAS CONNECTÉ".equals(status)){
            back = alarmbackgroud[5];
        }else if("低电压静音".equals(status) || "Low voltage mute".equals(status)){
            back = alarmbackgroud[6];
        }else if("冰冻报警".equals(status) || "Frozen Alarm".equals(status) || "ALARM EINGEFROHREN".equals(status)|| "ALARME GELÉ".equals(status)){
            back = alarmbackgroud[6];
        }else if("发生漏水".equals(status) || "Water leakage".equals(status) || "WASSERLECK".equals(status)|| "FUITE D’EAU".equals(status)){
            back = alarmbackgroud[1];
        }else {
            back = alarmbackgroud[0];
        }
        return back;
    }

    /**
     * 获取历史告警文字
     * @param status
     * @return
     */
    public static String getStatusString(String status){
        String back = "";
        if("设备正常".equals(status) || "Normal product".equals(status) || "NORMAL".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_normal_content);
        }else if("产品低电压".equals(status) || "Low voltage product".equals(status)  || "NIEDRIGE VOLTZAHL".equals(status) || "BASSE TENSION".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_lowvolt_content);
        }else if("报警器故障".equals(status) || "Product failure".equals(status) || "Product failure".equals(status)  || "PROBLEM".equals(status) || "PROBLÈME".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_trouble_content);
        }else if("报警已取消".equals(status) || "Alarm cancelled".equals(status) || "STILL".equals(status) || "MUET".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_silence_content);
        }else if("未连接报警器".equals(status) || "Unconnected alarm".equals(status) || "NICHT VERBUNDEN".equals(status) || "PAS CONNECTÉ".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_not_connect_content);
        }else if("低电压静音".equals(status) || "Low voltage mute".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_lvolt_silence_content);
        }else if("发生漏水".equals(status) || "Water leakage".equals(status)  || "WASSERLECK".equals(status)|| "FUITE D’EAU".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.water_alarm_content);
        }else if("冰冻报警".equals(status)  || "Frozen Alarm".equals(status) || "ALARM EINGEFROHREN".equals(status)|| "ALARME GELÉ".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.water_test_alerm_content);
        }else {
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_normal_content);
        }
        return back;
    }


    /**
     * 获取历史告警短文字
     * @param status
     * @return
     */
    public static String getStatusShortString(String status){
        String back = "";
        if("设备正常".equals(status) || "Normal product".equals(status) || "NORMAL".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_normal);
        }else if("产品低电压".equals(status) || "Low voltage product".equals(status)  || "NIEDRIGE VOLTZAHL".equals(status) || "BASSE TENSION".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_lowvolt);
        }else if("报警器故障".equals(status) || "Product failure".equals(status) || "Product failure".equals(status)  || "PROBLEM".equals(status) || "PROBLÈME".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_trouble);
        }else if("报警已取消".equals(status) || "Alarm cancelled".equals(status) || "STILL".equals(status) || "MUET".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_silence);
        }else if("未连接报警器".equals(status) || "Unconnected alarm".equals(status) || "NICHT VERBUNDEN".equals(status) || "PAS CONNECTÉ".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_not_connect);
        }else if("低电压静音".equals(status) || "Low voltage mute".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_lvolt_silence);
        }else if("发生漏水".equals(status) || "Water leakage".equals(status) || "WASSERLECK".equals(status)|| "FUITE D’EAU".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.flood_alert);
        }else if("冰冻报警".equals(status) || "Frozen Alarm".equals(status) || "ALARM EINGEFROHREN".equals(status)|| "ALARME GELÉ".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.linkage_alarm);
        }
        else {
            back = MyApplication.getAppContext().getResources().getString(R.string.battery_normal);
        }
        return back;
    }



    /**
     * 显示信号强度
     * @param signal
     * @return
     */
    public static int getSignal(int signal){
        int signalShow = 0;
        switch (signal){
            case SIGNAL_BAD:
                signalShow = imageS[0];
                break;
            case SIGNAL_FINE:
                signalShow = imageS[1];
                break;
            case SIGNAL_GOOD:
                signalShow = imageS[2];
                break;
            case SIGNAL_EXCELLENT:
                signalShow = imageS[3];
                break;
            default:
                signalShow = imageS[3];
                break;

        }
        return signalShow;
    }

    /**
     * 显示电量
     * @return
     */
    public static int getQuantinity(int battPercent){
        int quantinityShow = 0;
        if(battPercent <= 20 && battPercent >= 0){
            quantinityShow = imageQ[0];
        }else if(battPercent>20 && battPercent <=40){
            quantinityShow = imageQ[1];
        }else if(battPercent >40 && battPercent <= 60){
            quantinityShow = imageQ[2];
        }else if(battPercent > 60 && battPercent <=80){
            quantinityShow = imageQ[3];
        }else if(battPercent >80){
            quantinityShow = imageQ[4];
        }else if(battPercent < 0){
            quantinityShow = imageQ[4];
        }
        return quantinityShow;
    }


}

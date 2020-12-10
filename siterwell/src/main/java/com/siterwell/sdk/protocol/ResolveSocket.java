package com.siterwell.sdk.protocol;

import android.text.TextUtils;

import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.utils.ByteUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc-0001 on 2017/6/17.
 */
/*
@class ResolveSocket
@autor Administrator
@time 2017/6/23 13:51
*/
public class ResolveSocket {
    private static final String TAG = "ResolveSocket";
    private String code ;
    private SocketBean socketBean;
    private List<WifiTimerBean> wifiTimerBeanList;
    private int syc_type = 0; //1代表循环和倒计时模式参数都更新，2代表只更新循环模式，3代表只更新倒计时模式，0代表都不更新


    public ResolveSocket() {

    }

    public boolean isTarget(String code,String deviceid){

        try {
            if(TextUtils.isEmpty(code)) return false;

            if(code.length()<14) return false;

            int current_status = Integer.parseInt(code.substring(0,2),16);
            int wifi_signal    = Integer.parseInt(code.substring(2,4),16);
            int cycle_info = Integer.parseInt(code.substring(4,6),16);
            int countdown_info = Integer.parseInt(code.substring(6,8),16);
            int sync_timer_num = Integer.parseInt(code.substring(8,10),16);
            int current_mode = Integer.parseInt(code.substring(10,12),16);


            int last_info_length =(cycle_info==1?10:0) + (countdown_info==1?10:0) + sync_timer_num * 14;

            if((last_info_length+16) != code.length()){
                return false;
            }
            socketBean =new SocketBean();
            wifiTimerBeanList = new ArrayList<>();
            wifiTimerBeanList.clear();
            int cyclenumber=0,countdown_enable=0,countdown_notice=0,countdown_action=0;
            String cycle_on = "0000";
            String cycle_off = "0000";
            String countdowntime = "0000";
            if(cycle_info == 1 && countdown_info == 1){
                setSyc_type(1);
                cyclenumber = Integer.parseInt(code.substring(12,14),16);
                cycle_on = code.substring(14,18);
                cycle_off =code.substring(18,22);
                countdown_enable = Integer.parseInt(code.substring(22,24),16);
                countdown_notice = Integer.parseInt(code.substring(24,26),16);
                countdown_action = Integer.parseInt(code.substring(26,28),16);
                countdowntime = code.substring(28,32);

                int length_of_timer = (code.length() - 36)/14;
                for(int i=0;i<length_of_timer;i++){
                    ResolveTimer resolveTimer = new ResolveTimer(code.substring(32+i*14,46+i*14),deviceid);
                    wifiTimerBeanList.add(resolveTimer.getWifiTimerBean());
                }


            }else if(cycle_info == 1 && countdown_info != 1){
                setSyc_type(2);
                cyclenumber = Integer.parseInt(code.substring(12,14),16);
                cycle_on = code.substring(14,18);
                cycle_off =code.substring(18,22);

                countdown_enable = 0;
                countdown_notice = 0;
                countdown_action = 0;

                int length_of_timer = (code.length() - 26)/14;
                for(int i=0;i<length_of_timer;i++){
                    ResolveTimer resolveTimer = new ResolveTimer(code.substring(22+i*14,36+i*14),deviceid);
                    wifiTimerBeanList.add(resolveTimer.getWifiTimerBean());
                }

            }else if(cycle_info != 1 && countdown_info == 1){
                setSyc_type(3);
                cyclenumber = 0;
                countdown_enable = Integer.parseInt(code.substring(12,14),16);
                countdown_notice = Integer.parseInt(code.substring(14,16),16);
                countdown_action = Integer.parseInt(code.substring(16,18),16);
                countdowntime = code.substring(18,22);

                int length_of_timer = (code.length() - 26)/14;
                for(int i=0;i<length_of_timer;i++){
                    ResolveTimer resolveTimer = new ResolveTimer(code.substring(22+i*14,36+i*14),deviceid);
                    wifiTimerBeanList.add(resolveTimer.getWifiTimerBean());
                }

            }else if(cycle_info != 1 && countdown_info != 1){
                setSyc_type(0);
                cyclenumber = 0;
                countdown_enable = 0;
                countdown_notice = 0;
                countdown_action = 0;

                int length_of_timer = (code.length() - 16)/14;
                for(int i=0;i<length_of_timer;i++){
                    ResolveTimer resolveTimer = new ResolveTimer(code.substring(12+i*14,26+i*14),deviceid);
                    wifiTimerBeanList.add(resolveTimer.getWifiTimerBean());
                }
            }
            socketBean.setSocketstatus(current_status);
            socketBean.setSocketmodel(current_mode);
            socketBean.setSignal(wifi_signal);
            socketBean.setCirclenumber(cyclenumber);
            socketBean.setCircleon(cycle_on);
            socketBean.setCircleoff(cycle_off);
            socketBean.setCountdownenable(countdown_enable);
            socketBean.setNotice(countdown_notice);
            socketBean.setAction(countdown_action);
            socketBean.setCountdowntime(countdowntime);
            socketBean.setDevTid(deviceid);
            socketBean.setWifiTimerBeans(wifiTimerBeanList);
            return true;
        }catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }

    }


    public SocketBean getSocketBean() {
        return socketBean;
    }



    public List<WifiTimerBean> getWifiTimerBeanList() {
        return wifiTimerBeanList;
    }

    public void setWifiTimerBeanList(List<WifiTimerBean> wifiTimerBeanList) {
        this.wifiTimerBeanList = wifiTimerBeanList;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    /**
     * methodname:getSocketCicleCode
     * 作者：Henry on 2017/6/19 13:36
     * 邮箱：xuejunju_4595@qq.com
     * 参数:socketDescBean
     * 返回:发送循环设置命令解析
     */
    public static String getSocketCicleCode(SocketBean socketDescBean){
        String crc = "";
        try {

            int circlenumber= socketDescBean.getCirclenumber();

            String oo = "0";
            if (Integer.toHexString(circlenumber).length() < 2) {
                oo = oo + Integer.toHexString(circlenumber);
            } else {
                oo = Integer.toHexString(circlenumber);
            }

            String circleon = socketDescBean.getCircleon();

            String circleoff = socketDescBean.getCircleoff();

            String fullcode = oo + circleon + circleoff;
            crc = ByteUtil.CRCmakerChar(fullcode);

            return fullcode + crc;
        }catch (Exception e){
            return "";
        }

    }

    /*
    @method getSocketCicleCRC
    @autor Administrator
    @time 2017/6/23 14:01
    @email xuejunju_4595@qq.com
    获取循环模式的CRC
    */
    public static String getSocketCicleCRC(SocketBean socketDescBean){
        String crc = "";
        try {

            int circlenumber= socketDescBean.getCirclenumber();

            String oo = "0";
            if (Integer.toHexString(circlenumber).length() < 2) {
                oo = oo + Integer.toHexString(circlenumber);
            } else {
                oo = Integer.toHexString(circlenumber);
            }
            String fullcode = "";

            String circleon = socketDescBean.getCircleon();

            String circleoff = socketDescBean.getCircleoff();

            if(TextUtils.isEmpty(circleon) || TextUtils.isEmpty(circleoff)){
                return "0000";
            }else{
                fullcode = oo + circleon + circleoff;
                crc = ByteUtil.CRCmakerChar(fullcode);

                return crc;
            }

        }catch (Exception e){
            return "0000";
        }

    }


    /**
     * methodname:getSocketCicleInfo
     * 作者：Henry on 2017/6/19 13:42
     * 邮箱：xuejunju_4595@qq.com
     * 参数:code
     * 返回:SocketDescBean
     * 通过数据解析设置信息;
     */
     public  static SocketBean getSocketCicleInfo(String code, String deviceid){

         if(code.length()!=14) return null;

         SocketBean socketbean = new SocketBean();

         int circlenumber = Integer.parseInt(code.substring(0,2),16);
         String circleon = code.substring(2,6);
         String circleoff = code.substring(6,10);

         socketbean.setCirclenumber(circlenumber);
         socketbean.setCircleon(circleon);
         socketbean.setCircleoff(circleoff);
         socketbean.setDevTid(deviceid);

         return socketbean;
     }
    /**
     * methodname:getSocketCountDownCode
     * 作者：Henry on 2017/6/19 13:36
     * 邮箱：xuejunju_4595@qq.com
     * 参数:socketDescBean
     * 返回:发送倒计时设置命令解析
     */
    public static String getSocketCountDownCode(SocketBean socketDescBean){
        String crc = "";
        try {

            int enable= socketDescBean.getCountdownenable();

            String enable00 = "0";
            if (Integer.toHexString(enable).length() < 2) {
                enable00 = enable00 + Integer.toHexString(enable);
            } else {
                enable00 = Integer.toHexString(enable);
            }

            int notice= socketDescBean.getNotice();

            String notice00 = "0";
            if (Integer.toHexString(notice).length() < 2) {
                notice00 = notice00 + Integer.toHexString(notice);
            } else {
                notice00 = Integer.toHexString(notice);
            }

            int action= socketDescBean.getAction();

            String action00 = "0";
            if (Integer.toHexString(action).length() < 2) {
                action00 = action00 + Integer.toHexString(action);
            } else {
                action00 = Integer.toHexString(action);
            }

            String countdowntime = socketDescBean.getCountdowntime();

            String fullcode  = enable00 + notice00 + action00 + countdowntime;
            crc = ByteUtil.CRCmakerChar(fullcode);

            return fullcode + crc;
        }catch (Exception e){
            return "";
        }

    }

    /*
    @method getSocketCountDownCRC
    @autor Administrator
    @time 2017/6/23 14:51
    @email xuejunju_4595@qq.com
    */
    public static String getSocketCountDownCRC(SocketBean socketDescBean){
        String crc = "";
        try {

            int enable= socketDescBean.getCountdownenable();

            String enable00 = "0";
            if (Integer.toHexString(enable).length() < 2) {
                enable00 = enable00 + Integer.toHexString(enable);
            } else {
                enable00 = Integer.toHexString(enable);
            }

            int notice= socketDescBean.getNotice();

            String notice00 = "0";
            if (Integer.toHexString(notice).length() < 2) {
                notice00 = notice00 + Integer.toHexString(notice);
            } else {
                notice00 = Integer.toHexString(notice);
            }

            int action= socketDescBean.getAction();

            String action00 = "0";
            if (Integer.toHexString(action).length() < 2) {
                action00 = action00 + Integer.toHexString(action);
            } else {
                action00 = Integer.toHexString(action);
            }

            String fullcode  = "";

            String countdowntime = socketDescBean.getCountdowntime();

            if(TextUtils.isEmpty(countdowntime)){
                return "0000";
            }else{
                fullcode  = enable00 + notice00 + action00 + countdowntime;
                crc = ByteUtil.CRCmakerChar(fullcode);

                return crc;
            }


        }catch (Exception e){
            return "0000";
        }

    }
    /**
     * methodname:getSocketCountdownInfo
     * 作者：Henry on 2017/6/19 13:42
     * 邮箱：xuejunju_4595@qq.com
     * 参数:code
     * 返回:SocketDescBean
     * 通过数据解析设置信息;
     */
    public  static SocketBean getSocketCountdownInfo(String code, String deviceid){

        if(code.length()!=14) return null;

        SocketBean socketbean = new SocketBean();

        int enable = Integer.parseInt(code.substring(0,2),16);
        int notice = Integer.parseInt(code.substring(2,4),16);
        int action = Integer.parseInt(code.substring(4,6),16);

        String countdowntime = code.substring(6,10);

        socketbean.setCountdownenable(enable);
        socketbean.setNotice(notice);
        socketbean.setAction(action);
        socketbean.setCountdowntime(countdowntime);
        socketbean.setDevTid(deviceid);

        return socketbean;
    }

//    /*
//    @method getSendCrcCode
//    @autor Administrator
//    @time 2017/6/23 13:52
//    @email xuejunju_4595@qq.com
//    */
//    public static String getSendCrcCode(Context context,String deviceid){
//
//       WifiTimerDao wifiTimerdao = new WifiTimerDao(context);
//        DeviceDao devicedao = new DeviceDao(context);
//
//        SocketBean socketbean = devicedao.findSocketBySid(deviceid);
//        List<WifiTimerBean> list = wifiTimerdao.findAllTimer(deviceid);
//
//
//        String circle = getSocketCicleCRC(socketbean);
//        String countdown = getSocketCountDownCRC(socketbean);
//
//
//
//        int length = 2;
//        if(list.size()==0){
//            return "02"+circle+countdown;
//        }else{
//
//           int f = Integer.parseInt( list.get(list.size()-1).getTimerid());
//            length +=(f+1);
//            String length00 = "0";
//            if (Integer.toHexString(length).length() < 2) {
//                length00 = length00 + Integer.toHexString(length);
//            } else {
//                length00 = Integer.toHexString(length);
//            }
//
//            List<String> timerlist = new ArrayList<>();
//            for (WifiTimerBean e : list) {
//                timerlist.add(e.getTimerid());
//            }
//            String statusCRC = "";
//            for (int i = 0; i <= f; i++) {
//                if (timerlist.contains(String.valueOf(i))) {
//                    WifiTimerBean w =  wifiTimerdao.findTimerByTid(deviceid,String.valueOf(i));
//                    statusCRC +=  ResolveTimer.getCRCCode(w);
//                } else {
//                    statusCRC += "0000";
//                }
//            }
//
//
//
//            return length00 + circle + countdown + statusCRC;
//
//
//        }
//
//    }

    public int getSyc_type() {
        return syc_type;
    }

    public void setSyc_type(int syc_type) {
        this.syc_type = syc_type;
    }

    public static void main(String args[]) {
//		test_convertUint8toByte();
//		test_convertChar2Uint8();
//		test_splitUint8To2bytes();
//		test_combine2bytesToOne();
//		test_parseBssid();
        String abc = "";
        System.out.print(""+ByteUtil.CRCmakerChar(abc));
//		int[] arrayData = {1,2,4,5,6,7,5,6,7,3,8,9,10,12,11,20,30,40};
//		Arrays.sort(arrayData);
//		for (int a : arrayData){
//			System.out.print("" + a + ";");
//		}


    }

}

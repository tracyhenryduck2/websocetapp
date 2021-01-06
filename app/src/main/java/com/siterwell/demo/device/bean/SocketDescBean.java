package com.siterwell.demo.device.bean;

import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.MyApplication;
import com.siterwell.demo.R;
import com.siterwell.demo.common.UnitTools;

/**
 * Created by gc-0001 on 2017/6/13.
 */

public class SocketDescBean extends SocketBean {

    public static int[] imageSwitch = new int[]{
            R.drawable.off_kg_icon,
            R.drawable.on_kg_icon,
    };

    public static int[] imageS = new int[]{
            R.drawable.s0,
            R.drawable.s1,
            R.drawable.s2,
            R.drawable.s3,
    };


    public static int[] imageCurrentMode = new int[]{
            R.mipmap.xhms_icon,
            R.mipmap.xhms_icon_en,
            R.mipmap.yyms_icon,
            R.mipmap.yyms_icon_en,
            R.mipmap.djsms_icon,
            R.mipmap.djsms_icon_en,
    };



    public SocketDescBean(){
        super();
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
     * 显示信号强度
     * @param socketstatus
     * @return
     */
    public static int getStatus(int socketstatus){
        int signalShow = 0;
        switch (socketstatus){
            case 0:
                signalShow = imageSwitch[0];
                break;
            case 1:
                signalShow = imageSwitch[1];
                break;
            default:
                signalShow = imageSwitch[0];
                break;

        }
        return signalShow;
    }

    /**
     * 显示当前模式图片
     * @param currentmode
     * @return
     */
    public static int getCurrentMode(int currentmode, UnitTools unitTools){
        int modeShow = 0;
        switch (currentmode){
            case 1:
                if("zh".equals(unitTools.readLanguage())){
                    modeShow = imageCurrentMode[0];
                }else{
                    modeShow = imageCurrentMode[1];
                }

                break;
            case 2:
                if("zh".equals(unitTools.readLanguage())){
                    modeShow = imageCurrentMode[4];
                }else{
                    modeShow = imageCurrentMode[5];
                }
                break;
            case 3:
                if("zh".equals(unitTools.readLanguage())){
                    modeShow = imageCurrentMode[2];
                }else {
                    modeShow = imageCurrentMode[3];
                }
                break;
            case 255:
                if("zh".equals(unitTools.readLanguage())){
                    modeShow = imageCurrentMode[0];
                }else{
                    modeShow = imageCurrentMode[1];
                }

                break;
            default:
                if("zh".equals(unitTools.readLanguage())){
                    modeShow = imageCurrentMode[0];
                }else{
                    modeShow = imageCurrentMode[1];
                }
                break;

        }
        return modeShow;
    }

    /**
     * 获取历史告警短文字
     * @param status
     * @return
     */
    public static String getStatusShortString(String status){
        String back = "";
        if("插座关闭".equals(status) || "Socket Off".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.socket_off);
        }else if("插座打开".equals(status) || "Socket On".equals(status)){
            back = MyApplication.getAppContext().getResources().getString(R.string.socket_on);
        }
        else {
            back = MyApplication.getAppContext().getResources().getString(R.string.socket_on);
        }
        return back;
    }


}

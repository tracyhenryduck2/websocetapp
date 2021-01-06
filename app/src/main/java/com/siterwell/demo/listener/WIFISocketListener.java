package com.siterwell.demo.listener;

import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.bean.WifiTimerBean;

/**
 * Created by TracyHenry on 2018/2/8.
 */

public interface WIFISocketListener {

    void switchSocketSuccess(SocketBean socketBean);     //切换开关状态回调

    void switchModeSuccess(SocketBean socketBean);       //切换模式回调

    void sycSocketStatusSuccess(SocketBean socketBean);  //获取设备状态以及设置参数

    void deviceOffLineError(); //设备不在线

    void refreshSocketStatus(SocketBean socketBean); //主动上报

    void setCircleConfigSuccess(SocketBean socketBean);  //设置循环模式参数回调

    void setCountDownConfigSuccess(SocketBean socketBean); //设置倒计时模式参数回调

    void setTimerConfigSuccess(WifiTimerBean wifiTimerBean); //设置定时任务参数；(包括添加和编辑，使能切换)

    void deleteTimerSuccess(String id);  //删除定时任务成功回调

    void circleFinish(SocketBean socketBean);             //循环模式完成

    void countdownFinish(SocketBean socketBean);          //倒计时模式完成

    void timerFinish(SocketBean socketBean, String timerid); //某条定时任务执行完成

    void unknowError();    //解析错误
}

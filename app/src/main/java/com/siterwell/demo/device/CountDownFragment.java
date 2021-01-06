package com.siterwell.demo.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.R;
import com.siterwell.demo.commonview.SettingItem;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.wheelwidget.view.WheelView;
import com.siterwell.demo.bean.WifiTimerBean;
import com.siterwell.demo.listener.SitewellSDK;
import com.siterwell.demo.listener.WIFISocketListener;
import me.siter.sdk.http.bean.DeviceBean;
import com.siterwell.demo.protocol.ResolveSocket;
import com.siterwell.demo.protocol.SocketCommand;


import java.util.ArrayList;


/**
 * Created by gc-0001 on 2017/6/13.
 */
@SuppressLint("ValidFragment")
public class CountDownFragment extends Fragment implements View.OnClickListener,WIFISocketListener{
    private final String TAG  = "CountDownFragment";
    private View view = null;
    private WheelView wheelView_countdown_hour,wheelView_countdown_min;
    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private SettingItem settingItem_notice,settingItem_action;
    private Button btn_start,btn_stop;
    private String deviceid;
    private DeviceDao deviceDao;
    private SocketBean socketDescBean;
    private SocketCommand socketCommand;
    public CountDownFragment()
    {
        super();

    }

    public CountDownFragment(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_fragment_countdown, null);
            initdata();
            initView();
            SitewellSDK.getInstance(this.getActivity()).addWifiSocketListener(this);
        }
        //缓存的rootView需要判断是否已经被加载过parent,如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误
        ViewGroup viewparent = (ViewGroup) view.getParent();
        if (viewparent != null) {
            viewparent.removeView(view);
        }


        return view;
    }

    private void initdata(){
        for (int i = 0; i < 24; i ++) {

            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_hour.add(item);
        }

        for (int i = 0; i < 60; i ++) {
            String item = String.valueOf(i);

            if (item != null && item.length() == 1) {
                item = "0" + item;
            }

            items_min.add(item);
        }
    }

    private void initView(){
        deviceDao = new DeviceDao(this.getActivity());
        btn_start = (Button)view.findViewById(R.id.start);
        btn_stop  = (Button)view.findViewById(R.id.stop);
        wheelView_countdown_hour = (WheelView)view.findViewById(R.id.countdown_hour);
        wheelView_countdown_min  = (WheelView)view.findViewById(R.id.countdown_min);
        settingItem_notice       = (SettingItem)view.findViewById(R.id.notice);
        settingItem_action       = (SettingItem)view.findViewById(R.id.action);
        wheelView_countdown_hour.setLabel(":");
        wheelView_countdown_hour.setAdapter(new NumberAdapter(items_hour));
        wheelView_countdown_min.setAdapter(new NumberAdapter(items_min));
        DeviceBean deviceBean = deviceDao.findDeviceBySid(deviceid);
        socketCommand = new SocketCommand(deviceBean,this.getActivity());
        btn_start.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.start:
                 setCountdown(1);
                 break;
             case R.id.stop:
                 setCountdown(0);
                 break;
         }
    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {

    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {
        try {
            int countdown_hour = Integer.parseInt(socketBean.getCountdowntime().substring(0,2),16);
            int countdown_min  = Integer.parseInt(socketBean.getCountdowntime().substring(2,4),16);
            wheelView_countdown_hour.setCurrentItem(countdown_hour);
            wheelView_countdown_min.setCurrentItem(countdown_min);
        }catch (Exception e){
            e.printStackTrace();
        }
        settingItem_notice.setChecked(socketBean.getNotice()==1?true:false);
        settingItem_action.setChecked(socketBean.getAction()==1?true:false);
    }

    @Override
    public void deviceOffLineError() {

    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {

    }

    @Override
    public void setCircleConfigSuccess(SocketBean socketBean) {

    }

    @Override
    public void setCountDownConfigSuccess(SocketBean socketBean) {
        deviceDao.updateDeviceWifiSocketCountDown(socketBean);
        if(socketBean.getCountdownenable()==1){
            Toast.makeText(this.getActivity(),getResources().getString(R.string.countdown_start),Toast.LENGTH_LONG).show();
        }else if(socketBean.getCountdownenable() ==2){
            Toast.makeText(this.getActivity(),getResources().getString(R.string.countdown_stop),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void setTimerConfigSuccess(WifiTimerBean wifiTimerBean) {

    }

    @Override
    public void deleteTimerSuccess(String id) {

    }

    @Override
    public void circleFinish(SocketBean socketBean) {

    }

    @Override
    public void countdownFinish(SocketBean socketBean) {


    }

    @Override
    public void timerFinish(SocketBean socketBean, String timerid) {

    }

    @Override
    public void unknowError() {

    }


    private class NumberAdapter extends WheelView.WheelArrayAdapter<String> {

        public NumberAdapter(ArrayList<String> items) {
            super(items);
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this.getActivity()).removeWifiSocketListener(this);
    }



    private void  refresh(){
        socketDescBean = deviceDao.findSocketBySid(deviceid);
        try {
            int countdown_hour = Integer.parseInt(socketDescBean.getCountdowntime().substring(0,2),16);
            int countdown_min  = Integer.parseInt(socketDescBean.getCountdowntime().substring(2,4),16);
            wheelView_countdown_hour.setCurrentItem(countdown_hour);
            wheelView_countdown_min.setCurrentItem(countdown_min);
        }catch (Exception e){
            e.printStackTrace();
        }
        settingItem_notice.setChecked(socketDescBean.getNotice()==1?true:false);
        settingItem_action.setChecked(socketDescBean.getAction()==1?true:false);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void setCountdown(int enable){
        SocketDescBean socketDescBean = new SocketDescBean();

        int countdown_hour = wheelView_countdown_hour.getCurrentItem();
        int countdown_min = wheelView_countdown_min.getCurrentItem();


        String countdown_hour_str = "0";
        String countdown_min_str = "0";

        if (Integer.toHexString(countdown_hour).length() < 2) {
            countdown_hour_str = countdown_hour_str + Integer.toHexString(countdown_hour);
        } else {
            countdown_hour_str = Integer.toHexString(countdown_hour);
        }

        if (Integer.toHexString(countdown_min).length() < 2) {
            countdown_min_str = countdown_min_str + Integer.toHexString(countdown_min);
        } else {
            countdown_min_str = Integer.toHexString(countdown_min);
        }

        socketDescBean.setCountdowntime(countdown_hour_str+countdown_min_str);
        socketDescBean.setAction(settingItem_action.isChecked()?1:0);
        socketDescBean.setNotice(settingItem_notice.isChecked()?1:0);
        socketDescBean.setCountdownenable(enable);
        String code = ResolveSocket.getSocketCountDownCode(socketDescBean);
        Log.i(TAG,"发送的命令:"+code);
        socketCommand.setCountdownInfo(code,null);
    }
}

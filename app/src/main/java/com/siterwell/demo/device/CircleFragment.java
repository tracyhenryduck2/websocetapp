package com.siterwell.demo.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.demo.R;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.wheelwidget.view.WheelView;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.WIFISocketListener;
import me.siter.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.protocol.ResolveSocket;
import com.siterwell.sdk.protocol.SocketCommand;


import java.util.ArrayList;


/**
 * Created by gc-0001 on 2017/6/13.
 */
@SuppressLint("ValidFragment")
public class CircleFragment extends Fragment implements View.OnClickListener,WIFISocketListener{
    private final String TAG  = "CircleFragment";
    private View view = null;
    private SeekBar seekBar;
    private WheelView wheelView_on_hour,wheelView_on_min,wheelView_off_hour,wheelView_off_min;
    private Button btn_setting;
    private TextView textView_number;
    private ArrayList<String> items_hour = new ArrayList<String>();
    private ArrayList<String> items_min = new ArrayList<String>();
    private DeviceDao deviceDao;
    private String deviceid;
    private SocketBean socketDescBean;
    private SocketCommand socketCommand;
    public CircleFragment()
    {
        super();

    }

    public CircleFragment(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_fragment_circle, null);
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

    private void initView(){
        btn_setting = (Button)view.findViewById(R.id.set);
        btn_setting.setOnClickListener(this);
        wheelView_on_hour = (WheelView)view.findViewById(R.id.circle_on_hour);
        wheelView_on_min  = (WheelView)view.findViewById(R.id.circle_on_min);
        wheelView_off_hour = (WheelView)view.findViewById(R.id.circle_off_hour);
        wheelView_off_min  = (WheelView)view.findViewById(R.id.circle_off_min);
        seekBar            = (SeekBar)view.findViewById(R.id.seekbar_self);
        textView_number = (TextView)view.findViewById(R.id.current_number);
        wheelView_on_hour.setLabel(":");
        wheelView_off_hour.setLabel(":");
        wheelView_on_hour.setAdapter(new NumberAdapter(items_hour));
        wheelView_off_hour.setAdapter(new NumberAdapter(items_hour));
        wheelView_on_min.setAdapter(new NumberAdapter(items_min));
        wheelView_off_min.setAdapter(new NumberAdapter(items_min));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if(i==0){
                            textView_number.setText(getResources().getString(R.string.circle_no_limit));
                        }else{
                            textView_number.setText(String.valueOf(i));
                        }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        DeviceBean deviceBean = deviceDao.findDeviceBySid(deviceid);
        socketCommand = new SocketCommand(deviceBean,this.getActivity());

    }


    private void initdata(){
        deviceDao = new DeviceDao(this.getActivity());
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

    @Override
    public void onClick(View view) {
        setCircle();
    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {

    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {
        Log.i(TAG,"socketDescBean:"+ socketBean);
        try {
            int circleon_hour = Integer.parseInt(socketBean.getCircleon().substring(0,2),16);
            int circleon_min = Integer.parseInt(socketBean.getCircleon().substring(2,4),16);
            int circleoff_hour = Integer.parseInt(socketBean.getCircleoff().substring(0,2),16);
            int circleoff_min = Integer.parseInt(socketBean.getCircleoff().substring(2,4),16);
            wheelView_on_hour.setCurrentItem(circleon_hour);
            wheelView_on_min.setCurrentItem(circleon_min);
            wheelView_off_hour.setCurrentItem(circleoff_hour);
            wheelView_off_min.setCurrentItem(circleoff_min);
        }catch (Exception e){
            e.printStackTrace();
        }
        seekBar.setProgress(socketBean.getCirclenumber());
    }

    @Override
    public void deviceOffLineError() {

    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {

    }

    @Override
    public void setCircleConfigSuccess(SocketBean socketBean) {
        deviceDao.updateDeviceWifiSocketCircle(socketBean);
        Toast.makeText(this.getActivity(),getResources().getString(R.string.circle_set_success),Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCountDownConfigSuccess(SocketBean socketBean) {

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
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
       socketDescBean = deviceDao.findSocketBySid(deviceid);
        Log.i(TAG,"socketDescBean:"+ socketDescBean);
        try {
            int circleon_hour = Integer.parseInt(socketDescBean.getCircleon().substring(0,2),16);
            int circleon_min = Integer.parseInt(socketDescBean.getCircleon().substring(2,4),16);
            int circleoff_hour = Integer.parseInt(socketDescBean.getCircleoff().substring(0,2),16);
            int circleoff_min = Integer.parseInt(socketDescBean.getCircleoff().substring(2,4),16);
            wheelView_on_hour.setCurrentItem(circleon_hour);
            wheelView_on_min.setCurrentItem(circleon_min);
            wheelView_off_hour.setCurrentItem(circleoff_hour);
            wheelView_off_min.setCurrentItem(circleoff_min);
        }catch (Exception e){
            e.printStackTrace();
        }
        seekBar.setProgress(socketDescBean.getCirclenumber());


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this.getActivity()).removeWifiSocketListener(this);
    }


    private void setCircle(){
        SocketDescBean socketDescBean = new SocketDescBean();
        socketDescBean.setCirclenumber(seekBar.getProgress());

        int circleon_hour = wheelView_on_hour.getCurrentItem();
        int circleon_min = wheelView_on_min.getCurrentItem();
        int circleoff_hour = wheelView_off_hour.getCurrentItem();
        int circleoff_min = wheelView_off_min.getCurrentItem();


        String circleon_hour_str = "0";
        String circleon_min_str = "0";
        String circleoff_hour_str = "0";
        String circleoff_min_str = "0";
        if (Integer.toHexString(circleon_hour).length() < 2) {
            circleon_hour_str = circleon_hour_str + Integer.toHexString(circleon_hour);
        } else {
            circleon_hour_str = Integer.toHexString(circleon_hour);
        }

        if (Integer.toHexString(circleon_min).length() < 2) {
            circleon_min_str = circleon_min_str + Integer.toHexString(circleon_min);
        } else {
            circleon_min_str = Integer.toHexString(circleon_min);
        }

        if (Integer.toHexString(circleoff_hour).length() < 2) {
            circleoff_hour_str = circleoff_hour_str + Integer.toHexString(circleoff_hour);
        } else {
            circleoff_hour_str = Integer.toHexString(circleoff_hour);
        }

        if (Integer.toHexString(circleoff_min).length() < 2) {
            circleoff_min_str = circleoff_min_str + Integer.toHexString(circleoff_min);
        } else {
            circleoff_min_str = Integer.toHexString(circleoff_min);
        }

        socketDescBean.setCircleon(circleon_hour_str+circleon_min_str);
        socketDescBean.setCircleoff(circleoff_hour_str+circleoff_min_str);

        String code = ResolveSocket.getSocketCicleCode(socketDescBean);
        Log.i(TAG,"发送的命令:"+code);
        socketCommand.setCircleInfo(code,null);
    }

}

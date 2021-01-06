package com.siterwell.demo;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.siterwell.demo.bean.BatteryBean;
import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.bean.WaterSensorBean;
import com.siterwell.demo.bean.WifiTimerBean;
import com.siterwell.demo.listener.RefreshWaterSensorListener;
import com.siterwell.demo.listener.SitewellSDK;
import com.siterwell.demo.listener.WIFISocketListener;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.listener.RefreshBatteryListener;

/**
 * Created by TracyHenry on 2018/2/7.
 */

public class TestActivity extends TopbarSuperActivity implements RefreshBatteryListener,RefreshWaterSensorListener,WIFISocketListener {
    private Button button_test;

    @Override
    protected void onCreateInit() {
      getTopBarView().setTopBarStatus(R.drawable.back, -1, null, 1, new View.OnClickListener() {
          @Override
          public void onClick(View view) {

          }
      },null,R.color.white);
        button_test = (Button)findViewById(R.id.test);
        button_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SocketBean socketBean = new SocketBean();
                socketBean.setDevTid("01_28943f78fff2c54017b0171c268ce");
                socketBean.setSocketstatus(1);
                socketBean.setCtrlKey("d52b314c6e824979bbd42953770d7ed0");
                SitewellSDK.getInstance(TestActivity.this).switchSocket(socketBean);
            }
        });
        SitewellSDK.getInstance(this).addRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).addRefreshWaterSensorListener(this);
        SitewellSDK.getInstance(this).addWifiSocketListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void RefreshBattery(BatteryBean batteryBean) {
        Toast.makeText(this,""+batteryBean.getBattPercent(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void RefreshWaterSensor(WaterSensorBean waterSensorBean) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this).removeRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).removeRefreshWaterSensorListener(this);
        SitewellSDK.getInstance(this).removeWifiSocketListener(this);
    }


    @Override
    public void switchSocketSuccess(SocketBean socketBean) {
        Toast.makeText(this,getResources().getString(R.string.switch_success),Toast.LENGTH_LONG).show();
    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {

    }

    @Override
    public void deviceOffLineError() {
        Toast.makeText(this,getResources().getString(R.string.device_offline_text),Toast.LENGTH_LONG).show();
    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {
      Toast.makeText(this,getResources().getString(R.string.reported),Toast.LENGTH_LONG).show();
    }

    @Override
    public void setCircleConfigSuccess(SocketBean socketBean) {

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
}

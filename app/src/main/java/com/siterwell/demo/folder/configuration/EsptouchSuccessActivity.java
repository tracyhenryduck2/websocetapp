package com.siterwell.demo.folder.configuration;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.ProgressWheel;
import com.siterwell.demo.device.Controller;

import me.siter.sdk.http.bean.DeviceBean;

import java.util.Timer;
import java.util.TimerTask;



/**
 * Created by gc-0001 on 2017/2/17.
 */
public class EsptouchSuccessActivity extends TopbarSuperActivity {
    private final static String TAG = EsptouchSuccessActivity.class.getName();
    private ProgressWheel progressWheelInterpolated;
    private Timer timer = null;
    private MyTask timerTask;
    private DeviceBean suc_deviceBean;
    @Override
    protected void onCreateInit() {
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gou;
    }

    private void init(){
        suc_deviceBean = (DeviceBean) getIntent().getSerializableExtra("dev");
        Log.i(TAG,"suc_deviceBean"+suc_deviceBean.toString());
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.success_Esptouch), 1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null,R.color.bar_bg);

        progressWheelInterpolated = (ProgressWheel) findViewById(R.id.interpolated);
        progressWheelInterpolated.setBarColor(Color.argb(255, 51, 167, 255));
        progressWheelInterpolated.setRimColor(Color.TRANSPARENT);


        setProgress(1.0f);

    }



    private void setProgress(float progress) {
        progressWheelInterpolated.setCallback(new ProgressWheel.ProgressCallback() {
            @Override
            public void onProgressUpdate(float progress) {
                Log.i("ceshi","progress:"+progress);
                if(progress==1f){
                    progressWheelInterpolated.beginDrawTick();
                    timer = new Timer();
                    timerTask = new MyTask();
                    timer.schedule(timerTask,1000,1000);
                }
            }
        });

        progressWheelInterpolated.setProgress(progress);
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {
            handler.sendMessage(handler.obtainMessage(1));

        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if(timer!=null){
                        if(timerTask!=null){
                            timerTask =null;
                        }
                        timer.cancel();
                        timer = null;
                        try {
                            Controller.getInstance().deviceTid = suc_deviceBean.getDevTid();
                            Controller.getInstance().ctrlKey = suc_deviceBean.getCtrlKey();
                            Controller.getInstance().model = suc_deviceBean.getModel();
//                            if(DeviceType.BATTERY.toString().equals(suc_deviceBean.getModel())){
//
//                                Controller.getInstance().deviceTid = suc_deviceBean.getDevTid();
//                                Log.i(TAG,"Controller.getInstance().deviceTid"+Controller.getInstance().deviceTid);
//                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        finish();
                    }


                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
        if(timerTask!=null){
            timerTask =null;
        }
    }
}

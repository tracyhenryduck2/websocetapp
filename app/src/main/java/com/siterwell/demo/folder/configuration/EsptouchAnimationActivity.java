package com.siterwell.demo.folder.configuration;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.siterwell.demo.QuestionActivity;
import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.RoundProgressView;
import com.siterwell.demo.folder.FolderPojo;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.bean.DeviceType;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.http.bean.NewDeviceBean;
import com.siterwell.sdk.protocol.BatteryCommand;
import com.siterwell.sdk.protocol.GS140Command;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;



/**
 * Created by gc-0001 on 2017/2/15.
 */
public class EsptouchAnimationActivity extends TopbarSuperActivity implements View.OnClickListener{
    private final String TAG = EsptouchAnimationActivity.class.getName();
    private RoundProgressView roundProgressView;
    private Timer timer = null;
    private TextView textView;
    private TextView textView_tip;
    private TextView textView_tip_help;
    private MyTask timerTask;
    private int count = 0;
    private final int SPEED1 = 1;
    private final int SPEED2 = 30;
    private final int SPEED3 =50000;
    private int Now_speed;
    private String apSsid;
    private String apPassword;
    private Button btn_retry;
    private int flag = -1;  //1代表绑定成功,2代表绑定失败，3代表已绑定其他设备,4代表回调绑定失败
    private String failmsg = null;
    //示例demo逻辑跳转处理flag可自行根据逻辑修改
    private AtomicBoolean isSuccess = new AtomicBoolean(false);
    private AtomicBoolean isfailed = new AtomicBoolean(false);
    private DeviceBean newDeviceBean2;
    private DeviceDao deviceDao;
    private GS140Command BatteryType;
    private String Device_type;
    private Intent serviceIntent;
    @Override
    protected void onCreateInit() {
        init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_esp_animation;
    }

    private void init(){
        EventBus.getDefault().register(this);
        deviceDao =new DeviceDao(this);
        apSsid = getIntent().getStringExtra("wifi");
        apPassword = getIntent().getStringExtra("pwd");
        BatteryType = (GS140Command)getIntent().getSerializableExtra("dev");
        Device_type = getIntent().getStringExtra("devicetype");

        if(TextUtils.isEmpty(Device_type)){
            finish();
        }

        Now_speed = SPEED1;
        btn_retry =(Button)findViewById(R.id.retry);
        btn_retry.setOnClickListener(this);
        textView = (TextView)findViewById(R.id.tishi);
        textView_tip = (TextView)findViewById(R.id.tip);
        textView_tip_help = (TextView)findViewById(R.id.tip2);
        textView_tip_help.setText(getClickableSpan(0));
        textView_tip_help.setMovementMethod(LinkMovementMethod.getInstance());//必须设置否则无效
        textView_tip.setVisibility(View.GONE);
        textView_tip_help.setVisibility(View.GONE);
        textView.setText(getResources().getString(R.string.esptouch_is_configuring));
        roundProgressView = (RoundProgressView)findViewById(R.id.roundprogress);
        roundProgressView.setMax(1f);
        roundProgressView.setProgress(0.00f);
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.wifi_contect), 1, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if(DeviceType.BATTERY.toString().equals(Device_type)){
                      //startActivity(new Intent(EsptouchAnimationActivity.this,GuideBattery1Activty.class));
                      //onBackPressed();
                  }
                finish();
            }
        }, null,R.color.bar_bg);
        timer = new Timer();
        timerTask = new MyTask();
        timer.schedule(timerTask,0,1);
        config();
    }

    private void UpdateInfo(int count){
        roundProgressView.setProgress(((float) count)/100000f);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int progress = (int)msg.obj;
                    UpdateInfo(progress);
                    break;
                case 2:
                    Now_speed = SPEED1;
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                        count = 0;
                    }

                    roundProgressView.setErrStatus();

                    btn_retry.setVisibility(View.VISIBLE);
                    switch (flag){
                        case -1:
                            textView.setText(getResources().getString(R.string.failed_Esptouch_check_eq));
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                        case 2:
                            textView.setText(getResources().getString(R.string.pincode_get_err));
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                        case 3:
                            textView.setText(getResources().getString(R.string.failed_to_find_device));
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                        case 4:
                            if(!TextUtils.isEmpty(failmsg))
                            textView.setText(failmsg);
                            failmsg = "";
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                        case 6:
                            textView.setText(getResources().getString(R.string.put_to_folder_fail));
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                        case 7:
                            textView.setText(getResources().getString(R.string.self_bind));
                            if(DeviceType.BATTERY.toString().equals(Device_type)){
                                textView_tip.setVisibility(View.VISIBLE);
                            }
                            textView_tip_help.setVisibility(View.VISIBLE);
                            break;
                    }
                    break;
                case 3:
                    Log.i(TAG,"跳转到成功页面");
                    if(timer!=null){
                        timer.cancel();
                        timer = null;
                    }
                    Intent tent = new Intent(EsptouchAnimationActivity.this, EsptouchSuccessActivity.class);
                    tent.putExtra("dev",newDeviceBean2);
                    startActivity(tent);
                    finish();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.retry:

//                if(serviceIntent!=null) stopService(serviceIntent);
//                textView.setText(getResources().getString(R.string.esptouch_is_configuring));
//                btn_retry.setVisibility(View.GONE);
//                textView_tip.setVisibility(View.GONE);
//                textView_tip_help.setVisibility(View.GONE);
//                timer = new Timer();
//                timerTask = new MyTask();
//                timer.schedule(timerTask,0,1);
//                smartConfig = new SmartConfig(this);
//                config();

                if(DeviceType.BATTERY.toString().equals(Device_type)){
                    //startActivity(new Intent(EsptouchAnimationActivity.this,GuideBattery1Activty.class));
                    //onBackPressed();
                }else{
                    Intent intent = new Intent(EsptouchAnimationActivity.this,ConfigurationActivity.class);
                    intent.putExtra("devicetype",Device_type);
                    startActivity(intent);
                }
                finish();
                break;
        }
    }

    class MyTask extends TimerTask {
        @Override
        public void run() {

            if(count>=100000){
               if(flag==1){

                   if(timer!=null){
                       timer.cancel();
                       timer = null;
                   }
                   handler.sendMessage(handler.obtainMessage(3));
               }
                else handler.sendMessage(handler.obtainMessage(2,flag));
            }
            else{
                count = count+Now_speed;
                handler.sendMessage(handler.obtainMessage(1, count));


            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(timer!=null){
            timer.cancel();
            timer = null;
        }
       if(serviceIntent!=null) stopService(serviceIntent);
    }

    private Map<String, String> getConfiguation(String pincode) {
        HashMap<String, String> configuration = new HashMap<>();
        // 路由器的SSID
        configuration.put("ssid", apSsid);
        // 路由器的密码
        configuration.put("password", apPassword);
        // 获取的PinCode
        configuration.put("pinCode", pincode);
        return configuration;
    }

    /**
     * 点击配网按钮之后
     * 1、发送ssid &&pwd
     * 2、启动发现服务
     */
    private void config() {


        isSuccess.set(false);
        isfailed.set(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(DeviceType.BATTERY.toString().equals(Device_type)){
                //startActivity(new Intent(EsptouchAnimationActivity.this,GuideBattery1Activty.class));
                //onBackPressed();
            }
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }




    private SpannableString getClickableSpan(int start) {
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EsptouchAnimationActivity.this, QuestionActivity.class);
                intent.putExtra("help",true);
                startActivity(intent);
            }
        };

        SpannableString spanableInfo = new SpannableString(
                getResources().getString(R.string.guide_fail_help));

        int end = spanableInfo.length();
        spanableInfo.setSpan(new Clickable(l), start, end,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanableInfo;
    }

    /**
     * 内部类，用于截获点击富文本后的事件
     */
    class Clickable extends ClickableSpan implements View.OnClickListener {
        private final View.OnClickListener mListener;

        public Clickable(View.OnClickListener mListener) {
            this.mListener = mListener;
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getResources().getColor(R.color.edit_color));
            ds.setUnderlineText(true);    //去除超链接的下划线
        }
    }

}

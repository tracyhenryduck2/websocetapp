package com.siterwell.demo.device;

import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.siterwell.demo.BusEvents.GetDeviceStatusEvent;
import com.siterwell.demo.ServiceConstant;
import com.siterwell.demo.SilenceOverNotificationService;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.ProgressDialog;
import com.siterwell.demo.folder.bean.LocalFolderBean;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.demo.R;
import com.siterwell.demo.common.DateUtil;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.ParallaxListView;
import com.siterwell.demo.device.bean.BatteryDescBean;
import com.siterwell.demo.device.bean.WarningHistoryBean;
import com.siterwell.demo.storage.DeviceDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import com.siterwell.sdk.common.RefreshBatteryListener;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.http.UserAction;
import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.protocol.BatteryCommand;

/**
 * Created by ST-020111 on 2017/4/14.
 */

public class BatteryDetailActivity extends TopbarSuperActivity implements View.OnClickListener,ParallaxListView.IXListViewListener,RefreshBatteryListener{
    private final String TAG = "BatteryDetailActivity";
    private String deviceId;
    private ImageView signal,battery;
    private ImageView backbtn;
    private ImageView settinbtn;
    private TextView status;
    private DeviceDao batteryDao;
    private BatteryDescBean batteryDescBean;
    private BatteryHistoryParaAdapter batteryHistoryAdapter;
    private List<WarningHistoryBean> hislist;
    private ParallaxListView recyclerView_his;
    private int page = 0;
    private int height = 0;
    private boolean show = false;
    private RelativeLayout relativeLayout_tip;//测试提示栏
    private Button btn_silence;
    private LocalFolderBean folderBean;
    private ProgressDialog progressDialog;
    private BatteryCommand batteryCommand;
    private ECAlertDialog ecAlertDialogSilent;
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_battery_detail;
    }

    private void initView(){
        SitewellSDK.getInstance(this).addRefreshBatteryListener(this);
        EventBus.getDefault().register(this);
        batteryDao = new DeviceDao(this);
        folderBean = (LocalFolderBean) getIntent().getSerializableExtra("folderBean");
        height=(int)getResources().getDimension(R.dimen.battery_tip_test_height);
        deviceId = getIntent().getStringExtra("deviceId");
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.battery_detail), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        },null,R.color.bar_bg);
        getTopBarView().setVisibility(View.GONE);

        View headerView = View.inflate(this, R.layout.battery_header_view, null);
        ImageView ivBackground = (ImageView) headerView.findViewById(R.id.ivBackground);
        relativeLayout_tip = (RelativeLayout)headerView.findViewById(R.id.tishi);
        relativeLayout_tip.setVisibility(View.GONE);
        btn_silence =(Button)headerView.findViewById(R.id.btnConfirm);
       // btn_silence.setVisibility(View.GONE);
        btn_silence.setOnClickListener(this);
        backbtn = (ImageView)headerView.findViewById(R.id.back);
        settinbtn = (ImageView)headerView.findViewById(R.id.more);
        signal = (ImageView)headerView.findViewById(R.id.signal);
        battery = (ImageView)headerView.findViewById(R.id.battery);
        status = (TextView)headerView.findViewById(R.id.status);
        recyclerView_his = (ParallaxListView)findViewById(R.id.historylist);
        recyclerView_his.setXListViewListener(this);
        recyclerView_his.addHeaderView(headerView);
        recyclerView_his.setParallaxImageView(ivBackground);
        recyclerView_his.setPullLoadEnable(true);
        hislist = new ArrayList<>();
        batteryHistoryAdapter = new BatteryHistoryParaAdapter(this,hislist);
        recyclerView_his.setAdapter(batteryHistoryAdapter);
        recyclerView_his.setPullLoadEnable(false);
        backbtn.setOnClickListener(this);
        settinbtn.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        page = 0;
        queryhistory();
        doActions();
    }

    private void refresh(){
        batteryDescBean = batteryDao.findBatteryBySid(deviceId);
        Log.i(TAG," batteryDescBean.getStatus():"+ batteryDescBean.getStatus());

        getTopBarView().setTextTitle(TextUtils.isEmpty(batteryDescBean.getDeviceName())?DeviceActivitys.getDeviceType(batteryDescBean): batteryDescBean.getDeviceName());
        signal.setImageResource(BatteryDescBean.getSignal(batteryDescBean.getSignal()));
        battery.setImageResource(BatteryDescBean.getQuantinity(batteryDescBean.getBattPercent()));
        status.setText(batteryDescBean.getStatusDtail(batteryDescBean.getStatus()));
        status.setTextColor((ColorStateList)getResources().getColorStateList(BatteryDescBean.getStatusColor(batteryDescBean.getStatus())));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
        SitewellSDK.getInstance(this).removeRefreshBatteryListener(this);
    }


    private void queryhistory(){
        Log.i(TAG,"page:"+page);

        UserAction.getInstance(this).getAlarmHistory(page, 20, batteryDescBean, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG,"object:"+object.toString());
                try {
                    JSONObject jsonObject = new JSONObject(object.toString());
                    JSONArray his = jsonObject.getJSONArray("content");
                    int pageload = jsonObject.getInt("number");
                    if(pageload == 0)  {
                        page = 0;
                        hislist.clear();
                    }


                    for(int i=0;i<his.length();i++){
                        WarningHistoryBean batteryHistoryBean = new WarningHistoryBean();
                        batteryHistoryBean.setWarningId(his.getJSONObject(i).getString("id"));
                        batteryHistoryBean.setContent(his.getJSONObject(i).getString("content"));
                        batteryHistoryBean.setWarningsubject(his.getJSONObject(i).getString("subject"));
                        batteryHistoryBean.setReportTime(his.getJSONObject(i).getLong("reportTime"));
                        hislist.add(batteryHistoryBean);
                    }

                    boolean last = jsonObject.getBoolean("last");
                    if(!last) {
                        page = pageload + 1;
                        handler.sendEmptyMessage(5);
                    }else{
                        handler.sendEmptyMessage(6);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void getFail(int errorCode) {
                handler.sendEmptyMessage(6);
                Toast.makeText(BatteryDetailActivity.this, Errcode.errorCode2Msg(BatteryDetailActivity.this,errorCode),Toast.LENGTH_LONG).show();
            }
        });


    }


    //在view加载完成时设定缩放级别
    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            recyclerView_his.setViewsBounds(2);
        }

    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.back:
                 finish();
                 break;
             case R.id.more:
                 Intent intent = new Intent(this,DeviceSettingActivity.class);
                 intent.putExtra("deviceid",deviceId);
                 startActivity(intent);
                 break;
             case R.id.btnConfirm:
                 //batteryCommand.sendCommand(1,dataReceiverListener);
                 break;
         }
    }

    //private Handler timer = Handler()

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 4:
                    page = 0;
                    queryhistory();
                    break;
                case 5:
                    queryhistory();
                    break;
                case 6:
                    showfilterList();
                    break;
            }
        }
    };


    private void showfilterList(){


        List<WarningHistoryBean> deletelist = new ArrayList<WarningHistoryBean>();
        if(hislist.size()<=1){
            batteryHistoryAdapter.Refresh(hislist);
        }else{
            for(int i=0;i<hislist.size()-1;i++){

                if(hislist.get(i).getWarningsubject().equals(hislist.get(i+1).getWarningsubject())){
                     if(Math.abs(hislist.get(i).getReportTime() - hislist.get(i+1).getReportTime())<=60000l){
                         deletelist.add(hislist.get(i+1));
                     }
                }
            }
            Log.i(TAG,"ds需要删除的索引:"+deletelist.toString());

            hislist.removeAll(deletelist);

            batteryHistoryAdapter.Refresh(hislist);
        }

        if(hislist.size()==0){
            performAnim2(true);
        }else{
            if(DateUtil.is_current_7daybefore(hislist.get(0).getReportTime())){
                performAnim2(true);
            }else{
                performAnim2(false);
            }
        }

    }


    private void doActions() {

        List<DeviceBean> deviceBeanList = new ArrayList<>();

        DeviceBean deviceBean = batteryDao.findDeviceBySid(deviceId);
        deviceBeanList.add(deviceBean);

        GetDeviceStatusEvent getDeviceStatusEvent = new GetDeviceStatusEvent();
        getDeviceStatusEvent.setDeviceBeans(deviceBeanList);
        EventBus.getDefault().post(getDeviceStatusEvent);

    }


    @Override
    public void onLoadMore() {
        queryhistory();
    }


    private void performAnim2(boolean open){
        //View是否显示的标志
        show = open;
        //属性动画对象
        ValueAnimator va ;
        if(show){
            //显示view，高度从0变到height值
            relativeLayout_tip.setVisibility(View.VISIBLE);
            va = ValueAnimator.ofInt(0,height);
        }else{
            //隐藏view，高度从height变为0
            va = ValueAnimator.ofInt(height,0);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                relativeLayout_tip.getLayoutParams().height = h;
                relativeLayout_tip.requestLayout();
            }
        });
        va.setDuration(1000);
        //开始动画
        va.start();
    }

    @Override
    public void RefreshBattery(BatteryBean batteryBean) {
        signal.setImageResource(BatteryDescBean.getSignal(batteryBean.getSignal()));
        battery.setImageResource(BatteryDescBean.getQuantinity(batteryBean.getBattPercent()));
        status.setText(batteryDescBean.getStatusDtail(batteryBean.getStatus()));
        status.setTextColor((ColorStateList)getResources().getColorStateList(BatteryDescBean.getStatusColor(batteryBean.getStatus())));

        if(batteryBean.getSignal()!=0 || batteryBean.getBattPercent() != 0 || batteryBean.getStatus() !=0){
            new Thread(new Runnable(){

                public void run(){

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.sendEmptyMessage(4); //告诉主线程执行任务

                }

            }).start();
        }

    }

    private void startService() {
        Intent serv = new Intent(this, SilenceOverNotificationService.class);
        serv.putExtra(ServiceConstant.CTRL_KEY,batteryDescBean.getCtrlKey());
        serv.putExtra(ServiceConstant.DEVICE_ID,deviceId);
        serv.putExtra(ServiceConstant.FOLDER_BEAN, folderBean);
        startService(serv);
    }
}

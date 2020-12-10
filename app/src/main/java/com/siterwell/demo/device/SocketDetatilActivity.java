package com.siterwell.demo.device;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.litesuits.android.log.Log;
import com.siterwell.sdk.bean.DeviceType;
import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.ECListDialog;
import com.siterwell.demo.commonview.ProgressDialog;
import com.siterwell.demo.commonview.RefreshableView;
import com.siterwell.demo.device.bean.SocketDescBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.wheelwidget.helper.Common;
import com.siterwell.sdk.bean.SocketBean;
import com.siterwell.sdk.bean.WifiTimerBean;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.TimeOutListener;
import com.siterwell.sdk.common.WIFISocketListener;
import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.protocol.SocketCommand;


import java.util.ArrayList;
import java.util.List;


import me.hekr.sdk.inter.HekrMsgCallback;

/**
 * Created by gc-0001 on 2017/6/13.
 */

public class SocketDetatilActivity extends TopbarSuperActivity implements View.OnClickListener,WIFISocketListener,TimeOutListener{
    private final String  TAG = "SocketDetatilActivity";
    private ViewPager viewPager;
    private List<Fragment> fragments;// Tab页面列表
    private int currIndex=0;
    private int changeIndex;
    private ImageView imageView_cicle,imageView_timer,imageView_countdown,imageView_back,imageView_more,imageView_signal,imageView_socket,imageView_current_mode;
    private TextView textView_circle,textView_timer,textView_countdown;
    private DeviceDao deviceDao;
    private SocketDescBean socketDescBean;
    private String devid;
    private SocketCommand socketCommand;
    private HekrMsgCallback dataReceiverListener;
    private UnitTools unitTools;
    private RefreshableView refreshableView;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreateInit() {
        SitewellSDK.getInstance(this).addWifiSocketListener(this);
        SitewellSDK.getInstance(this).addTimeoutListener(this);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_socket_detail;
    }

    private void  initView(){
        unitTools = new UnitTools(this);
        progressDialog = new ProgressDialog(this);
        devid = getIntent().getStringExtra("deviceId");
        deviceDao = new DeviceDao(this);
        DeviceBean deviceBean = deviceDao.findDeviceBySid(devid);
        socketCommand = new SocketCommand(deviceBean,this);
        dataReceiverListener = new HekrMsgCallback() {
            @Override
            public void onReceived(String msg) {
                refreshableView.finishRefreshing();
            }

            @Override
            public void onTimeout() {
                Log.i(TAG,"onReceiveTimeout()");
                refreshableView.finishRefreshing();
                if(progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                Toast.makeText(SocketDetatilActivity.this,getResources().getString(R.string.net_err),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(int errorCode, String message) {

            }
        };
         getTopBarView().setVisibility(View.GONE);
         InitViewPager();
         initViewPagerHeight();

    }


    private void initViewPagerHeight(){

       int height = UnitTools.getScreenHeight(this);
        int statushieght = UnitTools.getStatusBarHeight(this);

        ViewGroup.LayoutParams params=viewPager.getLayoutParams();

        params.height=height-Common.toPx(this,240)-statushieght;

        viewPager.setLayoutParams(params);
    }

    /**
     * 初始化Viewpager页
     */
    private void InitViewPager() {
        refreshableView = (RefreshableView) findViewById(R.id.refr);
        imageView_current_mode = (ImageView)findViewById(R.id.current_mode);
        imageView_socket = (ImageView)findViewById(R.id.kaiguan);
        imageView_signal = (ImageView)findViewById(R.id.signal);
        imageView_cicle = (ImageView)findViewById(R.id.circle_btn);
        imageView_timer = (ImageView)findViewById(R.id.timer_btn);
        imageView_countdown = (ImageView)findViewById(R.id.countdown_btn);
        imageView_back = (ImageView)findViewById(R.id.back);
        imageView_more = (ImageView)findViewById(R.id.more);
        textView_circle = (TextView)findViewById(R.id.txt_circle);
        textView_timer =  (TextView)findViewById(R.id.txt_timer);
        textView_countdown = (TextView)findViewById(R.id.txt_countdown);
        viewPager = (ViewPager) findViewById(R.id.operation);
        fragments = new ArrayList<Fragment>();
        fragments.add(new CircleFragment(devid));

        fragments.add(new TimerFragment(devid));
        fragments.add(new CountDownFragment(devid));
        viewPager.setAdapter(new myPagerAdapter(getSupportFragmentManager(),
                fragments));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
        imageView_cicle.setOnClickListener(this);
        imageView_timer.setOnClickListener(this);
        imageView_countdown.setOnClickListener(this);
        imageView_back.setOnClickListener(this);
        imageView_more.setOnClickListener(this);
        imageView_socket.setOnClickListener(this);


        refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                getSocketInfo();
                //refreshableView.finishRefreshing();
            }
        },1);

            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendMessage(handler.obtainMessage(1));
                }
            }.start();

    }



    private void refreshBaseInfo(SocketBean socketBean){
        imageView_signal.setImageResource(SocketDescBean.getSignal(socketBean.getSignal()));
        imageView_socket.setImageResource(SocketDescBean.getStatus(socketBean.getSocketstatus()));
    }

    private void refreshMode(SocketBean socketBean){
        imageView_current_mode.setImageResource(SocketDescBean.getCurrentMode(socketBean.getSocketmodel(),unitTools));
        switch (socketBean.getSocketmodel()){
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(2);
                break;
            case 3:
                viewPager.setCurrentItem(1);
                break;
            case 255:
                viewPager.setCurrentItem(0);
                break;
            default:
                viewPager.setCurrentItem(0);
                break;
        }
    }


    private void refresh(){
        socketDescBean = deviceDao.findSocketBySid(devid);
        imageView_signal.setImageResource(SocketDescBean.getSignal(socketDescBean.getSignal()));
        imageView_socket.setImageResource(SocketDescBean.getStatus(socketDescBean.getSocketstatus()));
        imageView_current_mode.setImageResource(SocketDescBean.getCurrentMode(socketDescBean.getSocketmodel(),unitTools));

        switch (socketDescBean.getSocketmodel()){
            case 1:
                viewPager.setCurrentItem(0);
                break;
            case 2:
                viewPager.setCurrentItem(2);
                break;
            case 3:
                viewPager.setCurrentItem(1);
                break;
            case 255:
                viewPager.setCurrentItem(0);
                break;
            default:
                viewPager.setCurrentItem(0);
                break;
        }

    }


    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.circle_btn:
                 if(socketDescBean.isOnline()) {
                     viewPager.setCurrentItem(0);
                 }else{

                     String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                     String devenma = String.format(getResources().getString(R.string.device_offline),name);

                     Toast.makeText(this,devenma,Toast.LENGTH_LONG).show();
                 }
                 break;
             case R.id.timer_btn:
                 if(socketDescBean.isOnline()) {
                     viewPager.setCurrentItem(1);
                 }else{

                     String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                     String devenma = String.format(getResources().getString(R.string.device_offline),name);

                     Toast.makeText(this,devenma,Toast.LENGTH_LONG).show();
                 }
                 break;
             case R.id.countdown_btn:
                 if(socketDescBean.isOnline()) {
                     viewPager.setCurrentItem(2);
                 }else{
                     String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                     String devenma = String.format(getResources().getString(R.string.device_offline),name);
                     Toast.makeText(this,devenma,Toast.LENGTH_LONG).show();
                 }
                 break;
             case R.id.back:
                 finish();
                 break;
             case R.id.kaiguan:
                 if(socketDescBean.isOnline()) {
                     int tostauts = socketDescBean.getSocketstatus() == 1 ? 0 : 1;
                     socketCommand.setSocketControl(tostauts, null);
                 }else{

                     String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                     String devenma = String.format(getResources().getString(R.string.device_offline),name);

                     Toast.makeText(this,devenma,Toast.LENGTH_LONG).show();
                 }
                 break;
             case R.id.more:



                 ECListDialog ecListDialog = new ECListDialog(this,getResources().getStringArray(R.array.more_setting));
                 ecListDialog.setTitle(getResources().getString(R.string.see_more));
                 ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                     @Override
                     public void onDialogItemClick(Dialog d, int position) {

                         switch (position){
                             case 0:
                                 Intent intent2 = new Intent(SocketDetatilActivity.this,SocketHistoryActivity.class);
                                 intent2.putExtra("socketbean", socketDescBean);
                                 startActivity(intent2);
                                 break;
                             case 1:
                                 Intent intent = new Intent(SocketDetatilActivity.this,DeviceSettingActivity.class);
                                 intent.putExtra("deviceid",devid);
                                 startActivity(intent);
                                 break;
                             default:
                                 break;
                         }

                     }
                 });
                 ecListDialog.show();

                 break;
         }
    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {
        socketDescBean.setSocketstatus(socketBean.getSocketstatus());
        deviceDao.updateDeviceWifiSocketSwitch(socketBean);
        refreshBaseInfo(socketBean);
    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {
        socketDescBean.setSocketmodel(socketBean.getSocketmodel());
        deviceDao.updateDeviceWifiSocketMode(socketBean);
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        imageView_current_mode.setImageResource(SocketDescBean.getCurrentMode(socketBean.getSocketmodel(),unitTools));
    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        refreshBaseInfo(socketBean);
        refreshMode(socketBean);
    }

    @Override
    public void deviceOffLineError() {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Toast.makeText(SocketDetatilActivity.this,getResources().getString(R.string.offline),Toast.LENGTH_LONG).show();
    }


    @Override
    public void refreshSocketStatus(SocketBean socketBean) {
        refreshBaseInfo(socketBean);
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

    @Override
    public void timeout() {
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        Toast.makeText(SocketDetatilActivity.this,getResources().getString(R.string.timeout),Toast.LENGTH_LONG).show();
    }

    /**
     * 定义适配器
     */
    class myPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragmentList;

        public myPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        /**
         * 得到每个页面
         */
        @Override
        public Fragment getItem(int arg0) {
            return (fragmentList == null || fragmentList.size() == 0) ? null
                    : fragmentList.get(arg0);
        }

        /**
         * 每个页面的title
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }

        /**
         * 页面的总个数
         */
        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }
    }



    /**
     * 为选项卡绑定监听器
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {




        public void onPageScrollStateChanged(int index) {
            try {
                changeIndex = index;
                Log.i(TAG,"onPageScrollStateChanged:"+index);
                if(socketDescBean.isOnline()) {
                    if (progressDialog == null || (progressDialog != null && !progressDialog.isShowing())) {
                        progressDialog.show();
                    }
                    if (changeIndex == 0) {
                        switch (currIndex) {
                            case 0:
                                socketCommand.switchMode(1, null);
                                break;
                            case 1:
                                socketCommand.switchMode(3, null);
                                break;
                            case 2:
                                socketCommand.switchMode(2, null);
                                break;
                        }

                    }
                }else{
                    if(changeIndex == 0){
                        String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                        String devenma = String.format(getResources().getString(R.string.device_offline),name);

                        Toast.makeText(SocketDetatilActivity.this,devenma,Toast.LENGTH_LONG).show();
                        viewPager.setCurrentItem(currIndex);
                    }

                }
            }catch (NullPointerException e){
                Log.i(TAG,"tintManager is null");
            }

        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int index) {

            Log.i(TAG,"onPageSelected:"+index);
               resetButton();
               currIndex = index;
               switch (index) {
                   case 0:
                       imageView_cicle.setImageResource(R.mipmap.in_hk01_icon);
                       textView_circle.setTextColor((ColorStateList) getResources().getColorStateList(R.color.edit_color));
                       break;
                   case 1:
                       imageView_timer.setImageResource(R.mipmap.in_hk02_icon);
                       textView_timer.setTextColor((ColorStateList) getResources().getColorStateList(R.color.edit_color));
                       break;
                   case 2:
                       imageView_countdown.setImageResource(R.mipmap.in_hk03_icon);
                       textView_countdown.setTextColor((ColorStateList) getResources().getColorStateList(R.color.edit_color));
                       break;
               }
        }
    }

    private void resetButton(){
        imageView_cicle.setImageResource(R.mipmap.in_hk01_icon_2);
        textView_circle.setTextColor((ColorStateList)getResources().getColorStateList(R.color.near_black));
        imageView_timer.setImageResource(R.mipmap.in_hk02_icon_2);
        textView_timer.setTextColor((ColorStateList)getResources().getColorStateList(R.color.near_black));
        imageView_countdown.setImageResource(R.mipmap.in_hk03_icon_2);
        textView_countdown.setTextColor((ColorStateList)getResources().getColorStateList(R.color.near_black));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this).removeWifiSocketListener(this);
        SitewellSDK.getInstance(this).removeTimeoutListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }



    private void getSocketInfo(){
        socketCommand.setSyncSocketStatus(dataReceiverListener);
    }
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        if(!socketDescBean.isOnline()){
                            String name = TextUtils.isEmpty(socketDescBean.getDeviceName())?(DeviceType.WIFISOKECT.name()+"("+ socketDescBean.getDevTid().substring(socketDescBean.getDevTid().length()-4)+")"): socketDescBean.getDeviceName();
                            String devenma = String.format(getResources().getString(R.string.device_offline),name);

                            Toast.makeText(SocketDetatilActivity.this,devenma,Toast.LENGTH_LONG).show();
                        }else {
                            getSocketInfo();
                            refreshableView.setRrefresh();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
            }
        }
    };

}

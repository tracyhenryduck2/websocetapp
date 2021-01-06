package com.siterwell.demo.device;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.siterwell.demo.R;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.SlideListView;
import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.bean.WifiTimerBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.storage.WifiTimerDao;
import com.siterwell.demo.listener.SitewellSDK;
import com.siterwell.demo.listener.WIFISocketListener;
import me.siter.sdk.http.bean.DeviceBean;
import com.siterwell.demo.protocol.ResolveTimer;
import com.siterwell.demo.protocol.SocketCommand;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by gc-0001 on 2017/6/13.
 */
@SuppressLint("ValidFragment")
public class TimerFragment extends Fragment implements SocketTimerAdapter.switchItemListener,View.OnClickListener,WIFISocketListener{
    private final String TAG  = "TimerFragment";
    private View view = null;
    private SlideListView slideListView;
    private SocketTimerAdapter socketTimerAdapter;
    private List<WifiTimerBean> wifiTimerBeanList;
    private ImageView imageView_add;
    private String deviceid;
    private WifiTimerDao wifiTimerDao;
    private DeviceDao deviceDao;
    private SocketCommand socketCommand;
    public TimerFragment()
    {
        super();

    }

    public TimerFragment(String deviceid) {
        this.deviceid = deviceid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_fragment_timer, null);
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
        deviceDao = new DeviceDao(this.getActivity());
        wifiTimerDao = new WifiTimerDao(this.getActivity());

        wifiTimerBeanList= new ArrayList<WifiTimerBean>();
        DeviceBean deviceBean = deviceDao.findDeviceBySid(deviceid);
        socketCommand = new SocketCommand(deviceBean,this.getActivity());
    }

    private void initView(){
        slideListView = (SlideListView)view.findViewById(R.id.timer);
        socketTimerAdapter = new SocketTimerAdapter(this.getActivity(),wifiTimerBeanList,this);
        slideListView.initSlideMode(SlideListView.MOD_FORBID);
        slideListView.setAdapter(socketTimerAdapter);
        imageView_add = (ImageView)view.findViewById(R.id.addtimer);
        imageView_add.setOnClickListener(this);

    }

    @Override
    public void switchclick(int position) {
           if(wifiTimerBeanList.get(position).getEnable()==1){
               WifiTimerBean wifiTimerBean = wifiTimerBeanList.get(position);
               wifiTimerBean.setEnable(0);
               socketCommand.setTimerInfo(ResolveTimer.getCode(wifiTimerBean),null);
           }else{
               WifiTimerBean wifiTimerBean = wifiTimerBeanList.get(position);
               wifiTimerBean.setEnable(1);
               wifiTimerDao.updateTimerEnable(wifiTimerBean);
               socketCommand.setTimerInfo(ResolveTimer.getCode(wifiTimerBean),null);
           }

    }

    @Override
    public void longclick(final int position) {

        ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(this.getActivity(),
                    getResources().getString(R.string.are_u_sure_to_delete),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int d = Integer.parseInt(wifiTimerBeanList.get(position).getTimerid());
                            socketCommand.deleteTimer(d,null);
                        }

                    });
            ecAlertDialog.show();

    }

    @Override
    public void click(int position) {
        Intent intent = new Intent(this.getActivity(),AddTimerActivity.class);
        intent.putExtra("deviceid",deviceid);
        intent.putExtra("timerid",wifiTimerBeanList.get(position).getTimerid());
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this.getActivity(),AddTimerActivity.class);
        intent.putExtra("deviceid",deviceid);
        startActivity(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this.getActivity()).removeWifiSocketListener(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    public void refresh(){
        wifiTimerBeanList = wifiTimerDao.findAllTimer(deviceid);
        Log.i(TAG,"wifiTimerBeanList="+wifiTimerBeanList.toString());
        socketTimerAdapter.refreshList(wifiTimerBeanList);

    }

    @Override
    public void switchSocketSuccess(SocketBean socketBean) {

    }

    @Override
    public void switchModeSuccess(SocketBean socketBean) {

    }

    @Override
    public void sycSocketStatusSuccess(SocketBean socketBean) {
        wifiTimerBeanList = socketBean.getWifiTimerBeans();
        socketTimerAdapter.refreshList(wifiTimerBeanList);
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

    }

    @Override
    public void setTimerConfigSuccess(WifiTimerBean wifiTimerBean) {
        wifiTimerDao.insertWifiTimer(wifiTimerBean);
        wifiTimerBeanList = wifiTimerDao.findAllTimer(deviceid);
        socketTimerAdapter.refreshList(wifiTimerBeanList);
    }

    @Override
    public void deleteTimerSuccess(String id) {
        wifiTimerDao.deleteByTimerid(id,deviceid);
        wifiTimerBeanList = wifiTimerDao.findAllTimer(deviceid);
        socketTimerAdapter.refreshList(wifiTimerBeanList);

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

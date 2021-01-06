package com.siterwell.demo.folder;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.siterwell.demo.BusEvents.GetDeviceStatusEvent;
import com.siterwell.demo.BusEvents.RefreshEvent;
import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.bean.BatteryBean;
import com.siterwell.demo.bean.DeviceType;
import com.siterwell.demo.bean.SocketBean;
import com.siterwell.demo.bean.WaterSensorBean;
import com.siterwell.demo.bean.WifiTimerBean;
import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.ECListDialog;
import com.siterwell.demo.device.Controller;
import com.siterwell.demo.device.DeviceActivitys;
import com.siterwell.demo.folder.bean.LocalFolderBean;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.demo.storage.FolderDao;
import com.siterwell.demo.listener.RefreshBatteryListener;
import com.siterwell.demo.listener.RefreshWaterSensorListener;
import com.siterwell.demo.listener.SitewellSDK;
import com.siterwell.demo.listener.WIFISocketListener;
import me.siter.sdk.http.SiterUser;
import me.siter.sdk.http.UserAction;
import me.siter.sdk.http.bean.DeviceBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import me.siter.sdk.Constants;


/**
 * Created by gc-0001 on 2017/4/25.
 */

public class DeviceListActivity extends TopbarSuperActivity implements DeviceAdapter.OnRecyclerViewItemClickListener,RefreshBatteryListener,RefreshWaterSensorListener,WIFISocketListener{
    private final String TAG = "DeviceListActivity";
    private RecyclerView recyclerView;
    private DeviceAdapter deviceAdapter;
    private GridLayoutManager mLayoutManager;
    private List<DeviceBean> datalist;
    private LocalFolderBean folderBean;
    private DeviceDao deviceDao;
    private FolderDao folderDao;
    private ECAlertDialog alertDialog;
    private ECAlertDialog testecListDialog;
    private int page = 0;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_list2;
    }


    private void initView(){
        EventBus.getDefault().register(this);
        SitewellSDK.getInstance(this).addWifiSocketListener(this);
        SitewellSDK.getInstance(this).addRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).addRefreshWaterSensorListener(this);
        deviceDao = new DeviceDao(this);
        folderDao = new FolderDao(this);
        folderBean = (LocalFolderBean) getIntent().getSerializableExtra("folderBean");
        recyclerView = (RecyclerView)findViewById(R.id.devicelist);
        mLayoutManager=new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        String title = folderBean.getFolderName();
        if("root".equals(title)){
            title = getResources().getString(R.string.root);
        }
        getTopBarView().setTopBarStatus(R.drawable.back, R.drawable.add_black, title, 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                     FolderPojo.getInstance().folderId = folderBean.getFolderId();
                     startActivity(new Intent(DeviceListActivity.this,AddDeviceTypeActivity.class));
                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        },R.color.bar_bg);
        recyclerView.setLayoutManager(mLayoutManager);
        datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
        deviceAdapter = new DeviceAdapter(this,datalist);
        deviceAdapter.setOnItemClickListener(this);
        recyclerView.setAdapter(deviceAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeviceList();
            }
        });
        swipeRefreshLayout_em.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeviceList();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(datalist.size()==0){
            swipeRefreshLayout.setVisibility(View.GONE);
            swipeRefreshLayout_em.setVisibility(View.VISIBLE);
        }else{
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            swipeRefreshLayout_em.setVisibility(View.GONE);
        }


        if(!TextUtils.isEmpty(Controller.getInstance().deviceTid)){
            UserAction.getInstance(this).devicesPutFolder(FolderPojo.getInstance().folderId, Controller.getInstance().ctrlKey, Controller.getInstance().deviceTid, new SiterUser.DevicePutFolderListener() {
                @Override
                public void putSuccess() {
                    Log.i(TAG,"分组成功");
                    getDeviceList();
                    if(DeviceType.BATTERY.toString().equals(Controller.getInstance().model)&&(testecListDialog==null || !testecListDialog.isShowing())){
                        String d = String.format(getResources().getString(R.string.please_test_battery),Controller.getInstance().deviceTid);
                        testecListDialog = ECAlertDialog.buildPositiveAlert(DeviceListActivity.this, d, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Controller.getInstance().deviceTid = null;
                                Controller.getInstance().ctrlKey = null;
                                Controller.getInstance().model = null;
                            }
                        });
                        testecListDialog.setCanceledOnTouchOutside(false);
                        testecListDialog.setCancelable(false);
                        testecListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            Controller.getInstance().deviceTid = null;
                            Controller.getInstance().ctrlKey = null;
                            Controller.getInstance().model = null;
                        }
                    });
                       testecListDialog.show();
                    }else{
                        Controller.getInstance().deviceTid = null;
                        Controller.getInstance().ctrlKey = null;
                        Controller.getInstance().model = null;
                    }
                }

                @Override
                public void putFail(int errorCode) {
                    Log.i(TAG,"putFail()");
                }
            });
        }else{
            page = 0;
            getDeviceList();
        }
    }

    private void getDeviceList(){
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout_em.setRefreshing(true);
        UserAction.getInstance(this).getSiterData(Constants.UrlUtil.BASE_USER_URL+"device/"+folderBean.getFolderId()+"?size=20&page="+page, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {


                try {

                    Log.i(TAG,object.toString());
                   if(page == 0) datalist.clear();
                    JSONArray jsonArray = JSON.parseArray(object.toString());


                        for(int i=0;i<jsonArray.size();i++){
                            DeviceBean deviceBean = new DeviceBean();
                            deviceBean.setFolderId(jsonArray.getJSONObject(i).getString("folderId"));
                            deviceBean.setDevTid(jsonArray.getJSONObject(i).getString("devTid"));
                            deviceBean.setModel(jsonArray.getJSONObject(i).getString("model"));
                            String model = jsonArray.getJSONObject(i).getString("model");
                            String name = jsonArray.getJSONObject(i).getString("deviceName");
                            if(TextUtils.isEmpty(name)){
                                if(DeviceType.BATTERY.toString().equals(model)){
                                    name = getResources().getString(R.string.battery);
                                }else if(DeviceType.WIFISOKECT.toString().equals(model)){
                                    name = name.replaceAll("WIFI插座-",(getResources().getString(R.string.socket)+"-"));
                                }else if(DeviceType.WATERSENEOR.toString().equals(model)){
                                    name = name.replaceAll("siterLink-",(getResources().getString(R.string.watersensor)+"-"));
                                }
                            }

                            deviceBean.setDeviceName(name);
                            deviceBean.setOnline(jsonArray.getJSONObject(i).getBoolean("online")?true:false);
                            deviceBean.setCtrlKey(jsonArray.getJSONObject(i).getString("ctrlKey"));
                            deviceBean.setBindKey(jsonArray.getJSONObject(i).getString("bindKey"));
                            deviceBean.setProductPublicKey(jsonArray.getJSONObject(i).getString("productPublicKey"));
                            datalist.add(deviceBean);
                        }
                    if(jsonArray.size()<20){
                        page = 0;

                        List<DeviceBean> listjiu = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());

                        for(int i=0;i<listjiu.size();i++){
                            boolean flag = false;
                            for(int j=0;j<datalist.size();j++){
                                if(datalist.get(j).getDevTid().equals(listjiu.get(i).getDevTid())){
                                    flag = true;
                                    break;
                                }
                            }

                            if(!flag) {
                                deviceDao.deleteByDeviceId(listjiu.get(i).getDevTid());
                            }

                        }


                        deviceDao.insertDeviceList(datalist);
                        datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
                        deviceAdapter.Refresh(datalist);
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout_em.setRefreshing(false);
                        if(datalist.size()==0){
                            swipeRefreshLayout.setVisibility(View.GONE);
                            swipeRefreshLayout_em.setVisibility(View.VISIBLE);
                        }else{
                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                            swipeRefreshLayout_em.setVisibility(View.GONE);
                        }

                        GetDeviceStatusEvent getDeviceStatusEvent = new GetDeviceStatusEvent();
                        getDeviceStatusEvent.setDeviceBeans(datalist);
                        EventBus.getDefault().post(getDeviceStatusEvent);
                    }else{
                        page ++;
                        getDeviceList();
                    }





                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void getFail(int errorCode) {
                page = 0;
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout_em.setRefreshing(false);
                Toast.makeText(DeviceListActivity.this, Errcode.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, DeviceBean deviceBean) {
        DeviceActivitys.startDeviceDetailActivity(this,deviceBean);
    }

    @Override
    public void onItemLongClick(View view,final DeviceBean deviceBean) {
        final ECListDialog ecListDialog = new ECListDialog(this,getResources().getStringArray(R.array.device_operation));
        String ds = TextUtils.isEmpty(deviceBean.getDeviceName())?DeviceActivitys.getDeviceType(deviceBean):deviceBean.getDeviceName();
        ecListDialog.setTitle(ds);
        ecListDialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
            @Override
            public void onDialogItemClick(Dialog d, int position) {

                switch (position){
                    case 0:
                        alertDialog = ECAlertDialog.buildAlert(DeviceListActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.dialog_btn_cancel),getResources().getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.setDismissFalse(true);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                                final String newname = text.getText().toString().trim();

                                if(!TextUtils.isEmpty(newname)){
                                    UserAction.getInstance(DeviceListActivity.this).renameDevice(deviceBean.getDevTid(), deviceBean.getCtrlKey(), newname, null, new SiterUser.RenameDeviceListener() {
                                        @Override
                                        public void renameDeviceSuccess() {
                                            alertDialog.setDismissFalse(true);
                                            deviceDao.updateDeviceName(deviceBean.getDevTid(),newname);
                                            datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
                                            deviceAdapter.Refresh(datalist);
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.success_modify),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void renameDeviceFail(int errorCode) {
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(DeviceListActivity.this,Errcode.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void NameLongErr() {
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.name_is_too_long),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void NameContainEmojiErr() {
                                            alertDialog.setDismissFalse(false);
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                                else{
                                    alertDialog.setDismissFalse(false);
                                    Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDialog.setContentView(R.layout.edit_alert);
                        alertDialog.setTitle(getResources().getString(R.string.update_name));
                        EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                        text.setText(deviceBean.getDeviceName());
                        text.setSelection(deviceBean.getDeviceName().length());
                        alertDialog.show();
                        break;
                    case 1:

                        if(TextUtils.isEmpty(deviceBean.getBindKey())){
                            String thing = String.format(getResources().getString(R.string.cancel_ouath_hint),deviceBean.getDeviceName());
                            alertDialog = ECAlertDialog.buildAlert(DeviceListActivity.this, thing,getResources().getString(R.string.dialog_btn_cancel),getResources().getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.setDismissFalse(true);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    UserAction.getInstance(DeviceListActivity.this).cancelOAuth(deviceBean.getCtrlKey(), CCPAppManager.getClientUser().getId(), new SiterUser.CancelOAuthListener() {
                                        @Override
                                        public void CancelOAuthSuccess() {
                                            deviceDao.deleteByDeviceId(deviceBean.getDevTid());
                                            datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
                                            deviceAdapter.Refresh(datalist);
                                            if(datalist.size()==0){
                                                swipeRefreshLayout.setVisibility(View.GONE);
                                                swipeRefreshLayout_em.setVisibility(View.VISIBLE);
                                            }else{
                                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                                swipeRefreshLayout_em.setVisibility(View.GONE);
                                            }
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.success_delete),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void CancelOauthFail(int errorCode) {
                                            Toast.makeText(DeviceListActivity.this,Errcode.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                                        }
                                    });






                                }
                            });
                            alertDialog.show();
                        }else{
                            String thing = String.format(getResources().getString(R.string.unbind_hint),deviceBean.getDeviceName());
                            alertDialog = ECAlertDialog.buildAlert(DeviceListActivity.this, thing,getResources().getString(R.string.dialog_btn_cancel),getResources().getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.setDismissFalse(true);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    UserAction.getInstance(DeviceListActivity.this).deleteDevice(deviceBean.getDevTid(), deviceBean.getBindKey(), new SiterUser.DeleteDeviceListener() {
                                        @Override
                                        public void deleteDeviceSuccess() {
                                            deviceDao.deleteByDeviceId(deviceBean.getDevTid());
                                            datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
                                            deviceAdapter.Refresh(datalist);
                                            if(datalist.size()==0){
                                                swipeRefreshLayout.setVisibility(View.GONE);
                                                swipeRefreshLayout_em.setVisibility(View.VISIBLE);
                                            }else{
                                                swipeRefreshLayout.setVisibility(View.VISIBLE);
                                                swipeRefreshLayout_em.setVisibility(View.GONE);
                                            }
                                            Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.success_delete),Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void deleteDeviceFail(int errorCode) {
                                            Toast.makeText(DeviceListActivity.this,Errcode.errorCode2Msg(DeviceListActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                                        }
                                    });






                                }
                            });
                            alertDialog.show();
                        }


                        break;
                    case 2:
                        final List<LocalFolderBean> list = folderDao.findAllFolders();
                        List<String> slit = new ArrayList<String>();
                        for(int i=0;i<list.size();i++){
                            String ds = getResources().getString(R.string.root);
                            if(!"root".equals(list.get(i).getFolderName())){
                                ds = list.get(i).getFolderName();
                            }
                            slit.add(ds);
                        }
                        ECListDialog ecListDialog1 = new ECListDialog(DeviceListActivity.this, slit);
                        ecListDialog1.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
                            @Override
                            public void onDialogItemClick(Dialog d,final int position) {


                                UserAction.getInstance(DeviceListActivity.this).devicesPutFolder(list.get(position).getFolderId(), deviceBean.getCtrlKey(), deviceBean.getDevTid(), new SiterUser.DevicePutFolderListener() {
                                    @Override
                                    public void putSuccess() {
                                        DeviceBean deviceBean1 = new DeviceBean();
                                        deviceBean1.setDevTid(deviceBean.getDevTid());
                                        deviceBean1.setFolderId(list.get(position).getFolderId());
                                        deviceDao.updateDeviceFolderid(deviceBean1);
                                        Toast.makeText(DeviceListActivity.this,getResources().getString(R.string.success_move),Toast.LENGTH_LONG).show();
                                        datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
                                        deviceAdapter.Refresh(datalist);

                                        if(datalist.size()==0){
                                            swipeRefreshLayout.setVisibility(View.GONE);
                                            swipeRefreshLayout_em.setVisibility(View.VISIBLE);
                                        }else{
                                            swipeRefreshLayout.setVisibility(View.VISIBLE);
                                            swipeRefreshLayout_em.setVisibility(View.GONE);
                                        }

                                    }

                                    @Override
                                    public void putFail(int errorCode) {

                                        Log.i(TAG,"putFail()");
                                    }
                                });

                            }
                        });
                        ecListDialog1.setTitle(getResources().getString(R.string.all_folders));
                        ecListDialog1.show();
                        break;
                    default:
                        break;
                }

            }
        });
        ecListDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SitewellSDK.getInstance(this).removeWifiSocketListener(this);
        SitewellSDK.getInstance(this).removeRefreshBatteryListener(this);
        SitewellSDK.getInstance(this).removeRefreshWaterSensorListener(this);
    }

    @Override
    public void RefreshBattery(BatteryBean batteryBean) {
        getDeviceList();
        if((batteryBean.getDevTid().equals(Controller.getInstance().deviceTid))){
            if(testecListDialog!=null && testecListDialog.isShowing()){
                testecListDialog.dismiss();
                testecListDialog = null;
                Controller.getInstance().deviceTid = null;
            }
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
    public void RefreshWaterSensor(WaterSensorBean waterSensorBean) {
        getDeviceList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)         //订阅事件FirstEvent
    public  void onEventMainThread(RefreshEvent event){
        datalist = deviceDao.findAllDeviceBeanByFolderId(folderBean.getFolderId());
        deviceAdapter.Refresh(datalist);
    }
}

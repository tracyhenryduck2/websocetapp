package com.siterwell.demo.device;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.siterwell.demo.R;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.SettingItem;
import com.siterwell.demo.storage.DeviceDao;
import com.siterwell.sdk.common.SitewellSDK;
import com.siterwell.sdk.common.TimeOutListener;
import com.siterwell.sdk.common.UpgradeListener;
import com.siterwell.sdk.http.SiterUser;
import com.siterwell.sdk.http.UserAction;
import com.siterwell.sdk.http.bean.DeviceBean;
import com.siterwell.sdk.http.bean.FirmwareBean;
import com.siterwell.sdk.protocol.UpgradeCommand;
import com.zbar.lib.UIFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/7/29.
 */

public class DeviceSettingActivity extends TopbarSuperActivity implements View.OnClickListener,TimeOutListener,UpgradeListener{
    private static final String TAG = "DeviceSettingActivity";
    private SettingItem settingItem_firmware,settingItem_name,settingItem_deviceid,settingItem_bintype;
    private String deviceid;
    private DeviceDao deviceDao;
    private ECAlertDialog alertDialog;
    private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
    private DeviceBean deviceBean=null;
    private FirmwareBean file;
    private ProgressDialog progressBar;
    private ImageView scancode_imageView;
    private Bitmap mQrCodeBmp = null;

    @Override
    protected void onCreateInit() {
        SitewellSDK.getInstance(this).addTimeoutListener(this);
        SitewellSDK.getInstance(this).addUpgradeListener(this);
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_device_setting;
    }


    private void initView(){
        deviceDao = new DeviceDao(this);
        deviceid = getIntent().getStringExtra("deviceid");

        if(TextUtils.isEmpty(deviceid)){
            finish();
            return;
        }
        progressBar = new ProgressDialog(this);
        progressBar.setCanceledOnTouchOutside(false);
        progressBar.setTitle(getResources().getString(R.string.upgrade_holding));
        progressBar.setMessage(getResources().getString(R.string.upgrade_hold_on));
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.device_setting), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null,R.color.white);

        scancode_imageView = (ImageView)findViewById(R.id.imgDeviceQRCode);

        settingItem_bintype  = (SettingItem)findViewById(R.id.bintype);
        settingItem_deviceid = (SettingItem)findViewById(R.id.device_id);
        settingItem_firmware = (SettingItem)findViewById(R.id.firmware_ver);
        settingItem_name     = (SettingItem)findViewById(R.id.device_name);
        deviceBean = deviceDao.findDeviceBySid(deviceid);
        if(!TextUtils.isEmpty(deviceBean.getDeviceName())){

            if(deviceBean.getDeviceName().contains("Battery-")){
                settingItem_name.setDetailText(deviceBean.getDeviceName().replace("Battery-","Unijem Battery-"));
            }else if(deviceBean.getDeviceName().contains("智能电池-")){
                settingItem_name.setDetailText(deviceBean.getDeviceName().replace("智能电池-","Unijem Battery-"));
            }else{
            settingItem_name.setDetailText(deviceBean.getDeviceName());
            }


        }else{
            settingItem_name.setDetailText(DeviceActivitys.getDeviceType(deviceBean));
        }
        settingItem_name.setOnClickListener(this);
        settingItem_firmware.setOnClickListener(this);
        settingItem_deviceid.setDetailText(deviceBean.getDevTid());

        getFirmwareInfo();
        createScanCode();
    }


    private void getFirmwareInfo(){
        UserAction.getInstance(this).getDevices(deviceid, new SiterUser.GetDevicesListener() {
            @Override
            public void getDevicesSuccess(List<DeviceBean> devicesLists) {
               Log.i(TAG,devicesLists.toString());

                if(devicesLists.size()>0){
                    String ver = devicesLists.get(0).getBinVersion();
                    settingItem_firmware.setDetailText(ver);
                    String deviceid = devicesLists.get(0).getDevTid();
                    String bintype = devicesLists.get(0).getBinType();
                    settingItem_bintype.setDetailText(bintype);
                    String ppk = devicesLists.get(0).getProductPublicKey();
                    UserAction.getInstance(DeviceSettingActivity.this).checkFirmwareUpdate(deviceid, ppk, bintype, ver, new SiterUser.CheckFwUpdateListener() {
                        @Override
                        public void checkNotNeedUpdate() {
                            Log.i(TAG,"无需更新");
                        }

                        @Override
                        public void checkNeedUpdate(FirmwareBean firmwareBean) {
                            settingItem_firmware.setNewUpdateVisibility(true);
                            atomicBoolean.set(true);
                            file = firmwareBean;
                        }

                        @Override
                        public void checkFail(int errorCode) {
                            Log.i(TAG,"checkFail:"+errorCode);
                        }
                    });

                }


            }

            @Override
            public void getDevicesFail(int errorCode) {
                 Log.i(TAG,"getDevicesFail:"+errorCode);
            }
        });
    }

    @Override
    public void onClick(View view) {
         switch (view.getId()){
             case R.id.device_name:
                 alertDialog = ECAlertDialog.buildAlert(DeviceSettingActivity.this, getResources().getString(R.string.update_name),getResources().getString(R.string.dialog_btn_cancel),getResources().getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         alertDialog.setDismissFalse(true);
                     }
                 }, new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                         final String newname = text.getText().toString().trim();
                         final DeviceBean deviceBean = deviceDao.findDeviceBySid(deviceid);
                         if(!TextUtils.isEmpty(newname)){

                             UserAction.getInstance(DeviceSettingActivity.this).renameDevice(deviceBean.getDevTid(), deviceBean.getCtrlKey(), newname, null, deviceBean.getDcInfo().getConnectHost(), new SiterUser.RenameDeviceListener() {
                                 @Override
                                 public void renameDeviceSuccess() {
                                     alertDialog.setDismissFalse(true);
                                     deviceDao.updateDeviceName(deviceid,newname);
                                     settingItem_name.setDetailText(newname);
                                     Toast.makeText(DeviceSettingActivity.this,getResources().getString(R.string.success_modify),Toast.LENGTH_SHORT).show();
                                 }

                                 @Override
                                 public void renameDeviceFail(int errorCode) {
                                     alertDialog.setDismissFalse(false);
                                     Toast.makeText(DeviceSettingActivity.this, Errcode.errorCode2Msg(DeviceSettingActivity.this,errorCode),Toast.LENGTH_SHORT).show();
                                 }

                                 @Override
                                 public void NameLongErr() {
                                     alertDialog.setDismissFalse(false);
                                     Toast.makeText(DeviceSettingActivity.this,getResources().getString(R.string.name_is_too_long), Toast.LENGTH_SHORT).show();
                                 }

                                 @Override
                                 public void NameContainEmojiErr() {
                                     alertDialog.setDismissFalse(false);
                                     Toast.makeText(DeviceSettingActivity.this,getResources().getString(R.string.name_contain_emoji),Toast.LENGTH_SHORT).show();
                                 }
                             });

                         }
                         else{
                             alertDialog.setDismissFalse(false);
                             Toast.makeText(DeviceSettingActivity.this,getResources().getString(R.string.name_is_null),Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
                 alertDialog.setContentView(R.layout.edit_alert);
                 alertDialog.setTitle(getResources().getString(R.string.update_name));
                 EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                 DeviceBean deviceBean2 = deviceDao.findDeviceBySid(deviceid);
                 if(!TextUtils.isEmpty(deviceBean2.getDeviceName())){

                     if(deviceBean2.getDeviceName().contains("Battery-")){
                         text.setText(deviceBean2.getDeviceName().replace("Battery-","Unijem Battery-"));
                     }else if(deviceBean2.getDeviceName().contains("智能电池-")){
                         text.setText(deviceBean2.getDeviceName().replace("智能电池-","Unijem Battery-"));
                     }else{
                 text.setText(deviceBean2.getDeviceName());
                     }

                 }else{
                     text.setText(DeviceActivitys.getDeviceType(deviceBean2));
                 }
                 alertDialog.show();
                 break;
             case R.id.firmware_ver:
                 if(atomicBoolean.get()){
                     //doUpgradeActionSend();
                     UpgradeCommand upgradeCommand = new UpgradeCommand(deviceBean,file,this);
                     upgradeCommand.sendUpgradeCommand(null);
                     atomicBoolean.set(false);
                 }
                 break;
             default:
                 break;
         }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SitewellSDK.getInstance(this).removeTimeoutListener(this);
        SitewellSDK.getInstance(this).removeUpgradeListener(this);
    }



    @Override
    public void timeout() {
        Log.i(TAG,"onReceiveTimeout()");
        if(progressBar.isShowing()){
            progressBar.cancel();
        }
        Toast.makeText(DeviceSettingActivity.this,getResources().getString(R.string.upgrade_fail),Toast.LENGTH_LONG).show();
    }

    @Override
    public void progressComplete(String devTid) {
     if(deviceid.equals(devTid)){
         if(progressBar!=null && progressBar.isShowing()){
             progressBar.cancel();
             Toast.makeText(this,getResources().getString(R.string.upgrade_success),Toast.LENGTH_LONG).show();
             settingItem_firmware.setDetailText(file.getLatestBinVer());
             settingItem_bintype.setDetailText(file.getLatestBinType());
             settingItem_firmware.setNewUpdateVisibility(false);
         }
      }
    }

    @Override
    public void progressIng(String devTid,int progress) {


        if(deviceid.equals(devTid)){
            if(!progressBar.isShowing()){
                progressBar.show();
            }
            progressBar.setProgress(progress);


        }


    }

    private void createScanCode(){
        UserAction.getInstance(this).oAuthCreateCode(deviceBean.getCtrlKey(), new SiterUser.CreateOAuthQRCodeListener() {
            @Override
            public void createSuccess(String url) {
                // 生成二维码
                Bitmap qrCodeBmp = UIFactory.createCode(
                        url, 600, 0xff202020);
                if ( null != qrCodeBmp ) {
                    if ( null != mQrCodeBmp ) {
                        mQrCodeBmp.recycle();
                    }
                    mQrCodeBmp = qrCodeBmp;
                    scancode_imageView.setImageBitmap(qrCodeBmp);
                }
            }

            @Override
            public void createFail(int errorCode) {
                Toast.makeText(DeviceSettingActivity.this, Errcode.errorCode2Msg(DeviceSettingActivity.this,errorCode),Toast.LENGTH_SHORT).show();
            }
        });
    }
}

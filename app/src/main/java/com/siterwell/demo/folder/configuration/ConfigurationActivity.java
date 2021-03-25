package com.siterwell.demo.folder.configuration;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.siterwell.demo.R;
import com.siterwell.demo.common.PermissionUtils;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.CodeEdit;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.folder.guide.GuideBattery2Activty;
import com.siterwell.demo.folder.guide.SocketGuide1Activity;
import com.siterwell.demo.folder.guide.WaterSensor1Activity;
import com.siterwell.demo.bean.DeviceType;
import com.siterwell.demo.protocol.GS140Command;

/**
 * Created by ST-020111 on 2017/4/14.
 */


public class ConfigurationActivity extends TopbarSuperActivity implements View.OnClickListener{
    private BroadcastReceiver connectionReceiver;
    private TextView wifi;
    private CodeEdit psw;
    private Button btn_con;
    private GS140Command BatteryType;
    private String Device_type;
    private EspWifiAdminSimple mWifiAdmin;
    private static final int REQUEST_PERMISSION_LOCATION=1001;
    private static final  int REQUEST_LOCATION_SERVICE = 1002;
    private String[] permission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
    @Override
    protected void onCreateInit() {

        initView();
        createReceiver();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            if(PermissionUtils.requestPermission(this,permission,REQUEST_PERMISSION_LOCATION)){
                if(UnitTools.isLocServiceEnable(this)){
                    refreshWifi();
                }else {

                    DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                        }
                    };
                    ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(ConfigurationActivity.this, getResources().getString(R.string.permission_reject_location_service_tip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                        }
                    });
                    ecAlertDialog.setTitle(getResources().getString(R.string.permission_register));
                    ecAlertDialog.setButton(ECAlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.goto_set),listener);
                    ecAlertDialog.setCancelable(false);
                    ecAlertDialog.setCanceledOnTouchOutside(false);
                    ecAlertDialog.show();


                }
            }
        }else {
            refreshWifi();
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_configuration;
    }

    private void initView(){
        BatteryType = (GS140Command)getIntent().getSerializableExtra("dev");
        Device_type  = getIntent().getStringExtra("devicetype");
        if(TextUtils.isEmpty(Device_type)){
            finish();
        }
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.wifi_contect), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        },null,R.color.bar_bg);
        wifi = (TextView)findViewById(R.id.tvApSssidConnected);
        psw = (CodeEdit)findViewById(R.id.edtApPassword);
        btn_con = (Button)findViewById(R.id.btnConfirm);
        btn_con.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();






    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            if(PermissionUtils.requestPermission(this,permission,REQUEST_PERMISSION_LOCATION)){
                if(UnitTools.isLocServiceEnable(this)){
                    refreshWifi();
                }else {

                    DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                        }
                    };
                    ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(ConfigurationActivity.this, getResources().getString(R.string.permission_reject_location_service_tip), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                        }
                    });
                    ecAlertDialog.setTitle(getResources().getString(R.string.permission_register));
                    ecAlertDialog.setButton(ECAlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.goto_set),listener);
                    ecAlertDialog.setCancelable(false);
                    ecAlertDialog.setCanceledOnTouchOutside(false);
                    ecAlertDialog.show();


                }
            }
        }else {
            refreshWifi();
        }

    }

    private void refreshWifi(){
        String apSsid = mWifiAdmin.getWifiConnectedSsid();
        if (apSsid != null) {
            //兼容某些8.0以上手机
            if(apSsid.contains("ssid")){
                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                if(networkInfo.getExtraInfo()==null){
                    apSsid = "";
                }else {
                    apSsid= networkInfo.getExtraInfo().replace("\"","");
                }

            }
            wifi.setText(apSsid);
        } else {
            wifi.setText("");
        }
        UnitTools unitTools = new UnitTools(this);
        String ds = unitTools.readSSidcode(apSsid);
        if(ds!=null){
            psw.getCodeEdit().setText(ds);
            psw.getCodeEdit().setSelection(ds.length());
        }
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConfirm:
                hideSoftKeyboard();
                String apSsid = wifi.getText().toString();
                String apPassword = psw.getCodeEdit().getText().toString().trim();
                if(TextUtils.isEmpty(wifi.getText().toString())){
                    Toast.makeText(this,getResources().getString(R.string.no_wifi),Toast.LENGTH_LONG).show();
                }
                else if(wifi.getText().toString().indexOf(" ")!=-1){
                    Toast.makeText(this,getResources().getString(R.string.ssid_is_illegal),Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(apPassword)){
                    Toast.makeText(this,getResources().getString(R.string.password_is_null),Toast.LENGTH_SHORT).show();
                }else{
                    UnitTools unitTools = new UnitTools(this);
                    unitTools.writeSSidcode(apSsid,apPassword);


                    if(DeviceType.BATTERY.toString().equals(Device_type)){
                        Intent intent = new Intent(ConfigurationActivity.this,GuideBattery2Activty.class);
                        intent.putExtra("wifi",apSsid);
                        intent.putExtra("pwd",apPassword);
                        intent.putExtra("dev",BatteryType);
                        startActivity(intent);
                        finish();
                    }else if(DeviceType.WATERSENEOR.toString().equals(Device_type)){
                        Intent intent = new Intent(ConfigurationActivity.this,WaterSensor1Activity.class);
                        intent.putExtra("wifi",apSsid);
                        intent.putExtra("pwd",apPassword);
                        intent.putExtra("devicetype",Device_type);
                        startActivity(intent);
                        finish();
                    }else if(DeviceType.WIFISOKECT.toString().equals(Device_type)){
                        Intent intent = new Intent(ConfigurationActivity.this,SocketGuide1Activity.class);
                        intent.putExtra("wifi",apSsid);
                        intent.putExtra("pwd",apPassword);
                        intent.putExtra("devicetype",Device_type);
                        startActivity(intent);
                        finish();
                    }else{
                        Intent intent = new Intent(ConfigurationActivity.this,EsptouchAnimationActivity.class);
                        intent.putExtra("wifi",apSsid);
                        intent.putExtra("pwd",apPassword);
                        intent.putExtra("devicetype",Device_type);
                        startActivity(intent);
                        finish();
                    }


                }

                break;
        }

    }


    /**
     * 监听网络变化
     */
    public void createReceiver() {
        mWifiAdmin = new EspWifiAdminSimple(this);
        // 创建网络监听广播
        if (connectionReceiver == null) {
            connectionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                        ConnectivityManager mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
                        if (netInfo != null && netInfo.isAvailable()) {

                            String apSsid = mWifiAdmin.getWifiConnectedSsid();
                            if (apSsid != null) {
                                //兼容某些8.0以上手机
                                if(apSsid.contains("ssid")){
                                    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                                    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
                                    if(networkInfo.getExtraInfo()==null){
                                        apSsid = "";
                                    }else {
                                        apSsid= networkInfo.getExtraInfo().replace("\"","");
                                    }
                                }
                                wifi.setText(apSsid);
                            } else {
                                wifi.setText("");
                            }

                        } else {
                            wifi.setText("");
                            psw.getCodeEdit().setText("");
                        }
                    }
                }
            };
            // 注册网络监听广播
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(connectionReceiver, intentFilter);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectionReceiver != null) {
            unregisterReceiver(connectionReceiver);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (permissions != null && grantResults != null &&
                    permissions.length == grantResults.length) {

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        if(UnitTools.isLocServiceEnable(this)){
                            refreshWifi();
                        }else {

                            DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                                }
                            };
                            ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(ConfigurationActivity.this, getResources().getString(R.string.permission_reject_location_service_tip), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent, REQUEST_LOCATION_SERVICE);
                                }
                            });
                            ecAlertDialog.setTitle(getResources().getString(R.string.permission_register));
                            ecAlertDialog.setButton(ECAlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.goto_set),listener);
                            ecAlertDialog.setCancelable(false);
                            ecAlertDialog.setCanceledOnTouchOutside(false);
                            ecAlertDialog.show();


                        }
                    }else {
                        DialogInterface.OnClickListener listener=new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PermissionUtils.startToSetting(ConfigurationActivity.this);
                            }
                        };
                        ECAlertDialog ecAlertDialog = ECAlertDialog.buildAlert(ConfigurationActivity.this, getResources().getString(R.string.permission_reject_location_tip), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                PermissionUtils.startToSetting(ConfigurationActivity.this);
                            }
                        });
                        ecAlertDialog.setTitle(getResources().getString(R.string.permission_register));
                        ecAlertDialog.setButton(ECAlertDialog.BUTTON_POSITIVE,getResources().getString(R.string.goto_set),listener);
                        ecAlertDialog.setCancelable(false);
                        ecAlertDialog.setCanceledOnTouchOutside(false);
                        ecAlertDialog.show();
                    }
                }




            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK) return;

        if(requestCode==REQUEST_LOCATION_SERVICE){
            if(PermissionUtils.requestPermission(this,permission,REQUEST_PERMISSION_LOCATION)){
                refreshWifi();
            }

        }
    }

}


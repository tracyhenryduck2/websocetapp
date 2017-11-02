package com.siterwell.siterapp;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.assist.Toastor;
import com.siterwell.sdk.action.HekrUser;
import com.siterwell.sdk.action.HekrUserAction;
import com.siterwell.sdk.bean.JWTBean;
import com.siterwell.sdk.bean.MOAuthBean;
import com.siterwell.sdk.bean.ProfileBean;
import com.siterwell.sdk.util.HekrCodeUtil;

import java.io.InvalidClassException;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    private EditText et_username;
    private EditText et_pwd;
    private HekrUserAction hekrUserAction;
    private Toastor toastor;
    private ProgressDialog progressDialog;
    private LinearLayout save_password_button;
    private ImageView savepsw;
    private boolean flagauto = false;
    private Button switch_lan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initGTService();

    }


    private void initData() {
        toastor = new Toastor(this);
        hekrUserAction = HekrUserAction.getInstance(this);
    }

    private void initView() {
        save_password_button = (LinearLayout)findViewById(R.id.save_password_button);
        savepsw = (ImageView)findViewById(R.id.save_password);
        save_password_button.setOnClickListener(this);
        et_username = (EditText) findViewById(R.id.et_phone);
        et_username.setText("");
        et_pwd = (EditText) findViewById(R.id.codeedit);
        et_pwd.setText("");
        switch_lan = (Button)findViewById(R.id.switch_lan);
        Button btn_login = (Button) findViewById(R.id.btn_login);

        if (btn_login != null) {
            btn_login.setOnClickListener(this);
        }
        findViewById(R.id.regist).setOnClickListener(this);
        findViewById(R.id.reset_code).setOnClickListener(this);
        switch_lan.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                final String username = et_username.getText().toString().trim();
                final String pwd = et_pwd.getText().toString().trim();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.show();
                    hekrUserAction.login(username, pwd, new HekrUser.LoginListener() {
                        @Override
                        public void loginSuccess(String str) {
                            Log.i(TAG,"loginSuccess:"+str);
                            hekrUserAction.getProfile(new HekrUser.GetProfileListener() {
                                @Override
                                public void getProfileSuccess(ProfileBean profileBean) {
                                    Log.i(TAG,"loginSuccess:"+profileBean.toString());
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void getProfileFail(int errorCode) {
                                    Log.i(TAG,"getProfileFail:"+errorCode);
                                }
                            });
                        }

                        @Override
                        public void loginFail(int errorCode) {
                            if(progressDialog!=null&progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            toastor.showSingleLongToast(HekrCodeUtil.errorCode2Msg(errorCode));
                        }
                    });
                } else {
                    toastor.showSingleLongToast(getResources().getResourceName(R.string.login_check));
                }
                break;
            case R.id.save_password_button:
                break;
            case R.id.reset_code:
                break;
            case R.id.regist:
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    //创建匿名帐号并且绑定
    private void createUserAndBind(final int type, final String bindToken) {
        hekrUserAction.createUserAndBind(type, bindToken, new HekrUser.CreateUserAndBindListener() {
            @Override
            public void createSuccess(String str) {
//                startActivity(new Intent(LoginActivity.this, DeviceListActivity.class));
//                finish();
            }

            @Override
            public void createFail(int errorCode) {
            }
        });
    }





    private void initGTService() {
        MyApplication.sActivity = this;
        PackageManager pkgManager = getPackageManager();

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        if ( !phoneSatePermission) {
            requestPermission();
        }else{
            hekrUserAction = HekrUserAction.getInstance(this);
            if (TextUtils.isEmpty(hekrUserAction.getJWT_TOKEN())) {
                initView();
                initData();
            } else {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }

        }


    }
    private static final int REQUEST_PERMISSION = 0;
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE},
                REQUEST_PERMISSION);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hekrUserAction = HekrUserAction.getInstance(this);
                if (TextUtils.isEmpty(hekrUserAction.getJWT_TOKEN())) {
                    initView();
                    initData();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            } else {

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }


}

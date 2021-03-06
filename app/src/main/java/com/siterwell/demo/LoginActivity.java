package com.siterwell.demo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.common.assist.Toastor;

import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.CodeEdit;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.ProgressDialog;
import com.siterwell.demo.user.ClientUser;
import me.siter.sdk.http.UserAction;

import java.io.InvalidClassException;

import me.siter.sdk.Siter;
import me.siter.sdk.inter.SiterCallback;
import me.siter.sdk.utils.CacheUtil;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    private EditText et_username;
    private CodeEdit et_pwd;
    private Toastor toastor;
    private ProgressDialog progressDialog;
    private Button chooseLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnitTools ut = new UnitTools(this);
        ut.shiftLanguage(this,ut.readLanguage());
        setContentView(R.layout.activity_login);
        initGTService();

    }


    private void initView() {
        toastor = new Toastor(this);
        chooseLanguage = (Button)findViewById(R.id.language_c);
        chooseLanguage.setOnClickListener(this);
        et_username = (EditText) findViewById(R.id.et_phone);
        et_username.setText(CacheUtil.getUserName());
        et_pwd = (CodeEdit) findViewById(R.id.codeedit);
        et_pwd.getCodeEdit().setText(CacheUtil.getUserPassword());
        Button btn_login = (Button) findViewById(R.id.btn_login);
        if (btn_login != null) {
            btn_login.setOnClickListener(this);
        }
        findViewById(R.id.regist).setOnClickListener(this);
        findViewById(R.id.reset_code).setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:

                final String username = et_username.getText().toString().trim();
                final String pwd = et_pwd.getCodeEdit().getText().toString().trim();
                progressDialog = new ProgressDialog(this);
                progressDialog.show();
                    Siter.getSiterUser().login(username, pwd, new SiterCallback() {
                        @Override
                        public void onSuccess() {
                            final String id =  Siter.getSiterUser().getUserId();
                            ClientUser user = new ClientUser();
                            user.setId(id);
                            CCPAppManager.setClientUser(user);
                            CacheUtil.setUserLoginInfo(username,pwd);
                            if(progressDialog!=null&progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            try {
                                JSONObject d = JSON.parseObject(message);
                                int code = d.getInteger("code");
                                toastor.showSingleLongToast(Errcode.errorCode2Msg(LoginActivity.this,code));
                            }catch (Exception e){
                                toastor.showSingleLongToast(Errcode.errorCode2Msg(LoginActivity.this,errorCode));
                            }finally {
                                if(progressDialog!=null&progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }

                        }
                    });
                break;
            case R.id.reset_code:
                startActivity(new Intent(LoginActivity.this, ResetCodeActivity.class));
                break;
            case R.id.regist:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
        	case R.id.language_c:
            	startActivity(new Intent(LoginActivity.this, ChooseLanguageActivity.class));
            	finish();
            	break;

        }
    }







    private void initGTService() {
        parseManifests();
        PackageManager pkgManager = getPackageManager();

        // read phone state用于获取 imei 设备信息
        boolean phoneSatePermission =
                pkgManager.checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()) == PackageManager.PERMISSION_GRANTED;

        // read phone state用于获取 imei 设备信息
        boolean writeSatePermission =
                pkgManager.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, getPackageName()) == PackageManager.PERMISSION_GRANTED;


        if ( !phoneSatePermission || !writeSatePermission) {
            requestPermission();
        }else{
            if (TextUtils.isEmpty(UserAction.getInstance(this).getJWT_TOKEN())) {
                initView();
            } else {
                startActivity(new Intent(this, InitActivity.class));
                finish();
            }

        }


    }
    private static final int REQUEST_PERMISSION = 0;
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_PERMISSION);
    }
    private void parseManifests() {
        String packageName = getApplicationContext().getPackageName();
        try {
            android.content.pm.ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                String appid = appInfo.metaData.getString("PUSH_APPID");
                String appsecret = appInfo.metaData.getString("PUSH_APPSECRET");
                String appkey = appInfo.metaData.getString("PUSH_APPKEY");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                if (TextUtils.isEmpty(UserAction.getInstance(this).getJWT_TOKEN())) {
                    initView();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            } else {
                initView();
                ECAlertDialog ecAlertDialog = ECAlertDialog.buildPositiveAlert(this, R.string.you_must_grant_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        finish();
                    }
                });
                ecAlertDialog.setCanceledOnTouchOutside(false);
                ecAlertDialog.setCancelable(false);
                ecAlertDialog.show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }



}

package com.siterwell.demo;

import android.Manifest;
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

import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.common.ECPreferenceSettings;
import com.siterwell.demo.common.ECPreferences;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.CodeEdit;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.ProgressDialog;
import com.siterwell.demo.user.ClientUser;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;
import com.siterwell.sdk.http.bean.UserBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidClassException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.utils.CacheUtil;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = "LoginActivity";
    private EditText et_username;
    private CodeEdit et_pwd;
    private Toastor toastor;
    private ProgressDialog progressDialog;
    private LinearLayout save_password_button;
    private ImageView savepsw;
    private boolean flagauto = false;
    private Button chooseLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UnitTools ut = new UnitTools(this);
        ut.shiftLanguage(this,ut.readLanguage());
        tcpGetDomain();
        setContentView(R.layout.activity_login);
        initGTService();

    }

    private String getUsername(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_USERNAME;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private boolean getAutoLogin(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_REMEMBER_PASSWORD;
        boolean autoflag = sharedPreferences.getBoolean(flag.getId(), (boolean) flag.getDefaultValue());
        return autoflag;
    }

    private String getPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PASSWORD;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private void initView() {
        toastor = new Toastor(this);
        flagauto = getAutoLogin();
        chooseLanguage = (Button)findViewById(R.id.language_c);
        chooseLanguage.setOnClickListener(this);
        save_password_button = (LinearLayout)findViewById(R.id.save_password_button);
        savepsw = (ImageView)findViewById(R.id.save_password);
        if(flagauto){
            savepsw.setImageResource(R.drawable.save_pass_1);
        }else{
            savepsw.setImageResource(R.drawable.save_pass_0);
        }
        save_password_button.setOnClickListener(this);
        et_username = (EditText) findViewById(R.id.et_phone);
        et_username.setText(getUsername());
        et_pwd = (CodeEdit) findViewById(R.id.codeedit);
        et_pwd.getCodeEdit().setText(getPassword());
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
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
                    progressDialog = new ProgressDialog(this);
                    progressDialog.show();



                    Hekr.getHekrUser().login(username, pwd, new HekrCallback() {
                        @Override
                        public void onSuccess() {
                            final String id =  Hekr.getHekrUser().getUserId();

                            UserBean userBean = new UserBean(username, pwd, CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                            HekrUserAction.getInstance(LoginActivity.this).setUserCache(userBean);
                            HekrUserAction.getInstance(LoginActivity.this).getProfile(new HekrUser.GetProfileListener() {
                                @Override
                                public void getProfileSuccess(Object object) {
                                    JSONObject d = JSON.parseObject(object.toString());
                                    Log.i(TAG,"object:"+object.toString());
                                    ClientUser user = new ClientUser();
                                    user.setId(id);
                                    if(!TextUtils.isEmpty(d.getString("birthday"))) user.setBirthday(d.getLong("birthday"));
                                    if(!TextUtils.isEmpty(d.getString("description"))) user.setDescription(d.getString("description"));
                                    user.setEmail(d.getString("email"));
                                    user.setFirstName(d.getString("firstName"));
                                    user.setLastName(d.getString("lastName"));
                                    user.setGender(d.getString("gender"));
                                    user.setPhoneNumber(d.getString("phoneNumber"));
                                    if(!TextUtils.isEmpty(d.getString("updateDate")))  user.setUpdateDate(d.getLong("updateDate"));
                                    CCPAppManager.setClientUser(user);
                                    try {
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_REMEMBER_PASSWORD, flagauto, true);
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_USERNAME,username,true);
                                        ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_PASSWORD,flagauto?pwd:"",true);
                                    } catch (InvalidClassException e) {
                                        e.printStackTrace();
                                    }
                                    if(progressDialog!=null&progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                                @Override
                                public void getProfileFail(int errorCode) {
                                    if(progressDialog!=null&progressDialog.isShowing()){
                                        progressDialog.dismiss();
                                    }
                                    toastor.showSingleLongToast(Errcode.errorCode2Msg(LoginActivity.this,errorCode));
                                }
                            });

                        }

                        @Override
                        public void onError(int errorCode, String message) {
                            try {
                                JSONObject d = JSON.parseObject(message);
                                int code = d.getInteger("code");
                                toastor.showSingleLongToast(Errcode.errorCode2Msg(LoginActivity.this,code));
                            }catch (Exception e){
                                e.printStackTrace();
                                toastor.showSingleLongToast(Errcode.errorCode2Msg(LoginActivity.this,errorCode));
                            }finally {
                                if(progressDialog!=null&progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }

                        }
                    });
                } else {
                    toastor.showSingleLongToast(getResources().getResourceName(R.string.login_check));
                }
                break;
            case R.id.save_password_button:
                if(flagauto){
                    savepsw.setImageResource(R.drawable.save_pass_0);
                }else{
                    savepsw.setImageResource(R.drawable.save_pass_1);
                }
                flagauto = !flagauto;
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
            if (TextUtils.isEmpty(HekrUserAction.getInstance(this).getJWT_TOKEN())) {
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
                if (TextUtils.isEmpty(HekrUserAction.getInstance(this).getJWT_TOKEN())) {
                    initView();
                } else {
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                }
            } else {

                ECAlertDialog ecAlertDialog = ECAlertDialog.buildPositiveAlert(this, R.string.you_must_grant_permission, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
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

    private String getdomain(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_DOMAIN;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private void tcpGetDomain(){

        String domain = getdomain();
        Log.i(TAG,"设置本地domain:"+domain);
        Constants.setOnlineSite(domain);

        new Thread(){
            @Override
            public void run() {

                try {
                    final String HOST="info.hekr.me";
                    //final String HOST="127.0.0.1";
                    Socket socket = null;//创建一个客户端连接

                    socket = new Socket();
                    socket.connect(new InetSocketAddress(HOST,91),5000);
                    OutputStream out = socket.getOutputStream();//获取服务端的输出流，为了向服务端输出数据
                    InputStream in=socket.getInputStream();//获取服务端的输入流，为了获取服务端输入的数据

                    PrintWriter bufw=new PrintWriter(out,true);
                    BufferedReader bufr=new BufferedReader(new InputStreamReader(in));
                    Log.i(TAG,"发送啦");//打印服务端传来的数据
                    bufw.println("{\"action\":\"getAppDomain\"}");//发送数据给服务端
                    bufw.flush();
                    while (true)
                    {
                        String line=null;
                        line=bufr.readLine();//读取服务端传来的数据
                        if(line==null)
                            break;
                        Log.i(TAG,"服务端说:"+line);//打印服务端传来的数据
                        JSONObject jsonObject = JSONObject.parseObject(line);
                        JSONObject jsonObject1 = jsonObject.getJSONObject("dcInfo");
                        String domain = jsonObject1.getString("domain");
                        try {
                            if(!TextUtils.isEmpty(domain)){
                                Log.i(TAG,"获取到的domain:"+domain);
                                ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_DOMAIN, domain, true);
                                Constants.setOnlineSite(domain);
                                break;
                            }
                        } catch (InvalidClassException e) {
                            e.printStackTrace();
                        }



                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

}

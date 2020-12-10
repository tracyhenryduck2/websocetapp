package com.siterwell.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.common.ECPreferenceSettings;
import com.siterwell.demo.common.ECPreferences;
import com.siterwell.demo.commonview.loadingView.ZLoadingView;
import com.siterwell.demo.commonview.loadingView.Z_TYPE;
import com.siterwell.sdk.http.HekrUserAction;
import com.siterwell.sdk.http.bean.UserBean;

import java.io.InvalidClassException;

import me.hekr.sdk.Constants;
import me.hekr.sdk.Hekr;
import me.hekr.sdk.inter.HekrCallback;
import me.hekr.sdk.utils.CacheUtil;


/**
 * Created by TracyHenry on 2018/5/9.
 */

public class InitActivity extends AppCompatActivity {
private final static String TAG = "InitActivity";
private ImageView imageView1;
private ZLoadingView zLoadingView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        zLoadingView = (ZLoadingView)findViewById(R.id.loadingView_1);
        login();
        handler.sendEmptyMessageDelayed(1,5000l);
        zLoadingView.setLoadingBuilder(Z_TYPE.SINGLE_CIRCLE);
    }

    private String getUsername(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_USERNAME;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private String getPassword(){

        SharedPreferences sharedPreferences = ECPreferences.getSharedPreferences();
        ECPreferenceSettings flag = ECPreferenceSettings.SETTINGS_PASSWORD;
        String autoflag = sharedPreferences.getString(flag.getId(), (String) flag.getDefaultValue());
        return autoflag;
    }

    private void login(){

       Log.i(TAG,"自动登录");
        Hekr.getHekrUser().login(getUsername(), getPassword(), new HekrCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"自动登录成功");
                UserBean userBean = new UserBean(getUsername(), getPassword(), CacheUtil.getUserToken(), CacheUtil.getString(Constants.REFRESH_TOKEN,""));
                HekrUserAction.getInstance(InitActivity.this).setUserCache(userBean);

            }

            @Override
            public void onError(int errorCode, String message) {

                try {
                    JSONObject d = JSON.parseObject(message);
                    int code = d.getInteger("code");
                    //密码错误
                    if(code == 3400010){
                        try {
                            ECPreferences.savePreference(ECPreferenceSettings.SETTINGS_HUAWEI_TOKEN, "", true);
                        } catch (InvalidClassException e) {
                            e.printStackTrace();
                        }
                        HekrUserAction.getInstance(InitActivity.this).userLogout();
                        CCPAppManager.setClientUser(null);
                        handler.sendEmptyMessage(2);
                    }else {

                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Log.i(TAG,"自动登录失败");
                    handler.sendEmptyMessage(1);
                }


            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    startActivity(new Intent(InitActivity.this,MainActivity.class));
                    finish();
                    break;
                case 2:
                    startActivity(new Intent(InitActivity.this,LoginActivity.class));
                    finish();
                    break;
            }
        }
    };


}

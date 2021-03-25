package com.siterwell.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.commonview.loadingView.ZLoadingView;
import com.siterwell.demo.commonview.loadingView.Z_TYPE;
import me.siter.sdk.http.UserAction;

import me.siter.sdk.Siter;
import me.siter.sdk.inter.SiterCallback;
import me.siter.sdk.utils.CacheUtil;


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


        return CacheUtil.getUserName();
    }

    private String getPassword(){

        return CacheUtil.getUserPassword();
    }

    private void login(){

       Log.i(TAG,"自动登录");
        Siter.getSiterUser().login(getUsername(), getPassword(), new SiterCallback() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"自动登录成功");

            }

            @Override
            public void onError(int errorCode, String message) {

                try {
                    JSONObject d = JSON.parseObject(message);
                    int code = d.getInteger("code");
                    //密码错误
                    if(code == 3400010){
                        UserAction.getInstance(InitActivity.this).userLogout();
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

package com.siterwell.demo.folder.guide;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.folder.configuration.EsptouchAnimationActivity;
import com.siterwell.sdk.bean.DeviceType;
import com.siterwell.sdk.protocol.GS140Command;

/**
 * Created by ST-020111 on 2017/4/14.
 */

public class GuideBattery2Activty extends TopbarSuperActivity implements View.OnClickListener{
    private ImageView imageView;
    private AnimationDrawable ad;
    private GS140Command BatteryType;
    private String ssid;
    private String pwd;
    private Button btn_connect;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_battery2;
    }

    private void initView(){
        BatteryType = (GS140Command) getIntent().getSerializableExtra("dev");
        ssid = getIntent().getStringExtra("wifi");
        pwd  = getIntent().getStringExtra("pwd");
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.add_battery_instruct), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(GuideBattery2Activty.this,GuideBattery1Activty.class));
                finish();
                //onBackPressed();
            }
        },null,R.color.bar_bg);
        imageView = (ImageView)findViewById(R.id.guide);
        imageView.setBackgroundResource(R.drawable.config_tishi);

        ad = (AnimationDrawable) imageView.getBackground();
        btn_connect = (Button)findViewById(R.id.conect);
        btn_connect.setOnClickListener(this);

        imageView.post(new Runnable()
        {

            @Override

            public void run()

            {

                ad.start();

            }

        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //startActivity(new Intent(GuideBattery2Activty.this,GuideBattery1Activty.class));
            finish();
            //onBackPressed();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent =new Intent(GuideBattery2Activty.this, EsptouchAnimationActivity.class);
        intent.putExtra("dev",BatteryType);
        intent.putExtra("devicetype", DeviceType.BATTERY.toString());
        intent.putExtra("wifi",ssid);
        intent.putExtra("pwd",pwd);
        startActivity(intent);
        finish();
    }
}

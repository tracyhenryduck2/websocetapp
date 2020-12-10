package com.siterwell.demo.folder.guide;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.siterwell.demo.R;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.folder.configuration.ConfigurationActivity;
import com.siterwell.demo.folder.configuration.EsptouchAnimationActivity;
import com.siterwell.sdk.bean.DeviceType;

/**
 * Created by ST-020111 on 2017/7/21.
 */

public class WaterSensor1Activity extends TopbarSuperActivity implements View.OnClickListener {
    private ImageView imageView;
    private AnimationDrawable ad;
    private String ssid;
    private String pwd;
    private Button btn_connect;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_guide_watersensor1;
    }

    private void initView(){
        ssid = getIntent().getStringExtra("wifi");
        pwd  = getIntent().getStringExtra("pwd");
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.add_battery_instruct), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WaterSensor1Activity.this,ConfigurationActivity.class);
                i.putExtra("devicetype", DeviceType.WATERSENEOR.toString());
                startActivity(i);
                finish();
            }
        },null,R.color.bar_bg);
        imageView = (ImageView)findViewById(R.id.guide_water);
        imageView.setBackgroundResource(R.drawable.config_water);

        ad = (AnimationDrawable) imageView.getBackground();
        btn_connect = (Button)findViewById(R.id.conect_water);
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
            Intent i = new Intent(this,ConfigurationActivity.class);
            i.putExtra("devicetype", DeviceType.WATERSENEOR.toString());
            startActivity(i);
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent =new Intent(this, EsptouchAnimationActivity.class);
        intent.putExtra("wifi",ssid);
        intent.putExtra("pwd",pwd);
        intent.putExtra("devicetype", DeviceType.WATERSENEOR.toString());
        startActivity(intent);
        finish();
    }
}

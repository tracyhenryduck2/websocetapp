package com.siterwell.demo;

import android.view.View;

import com.siterwell.demo.common.Config;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.commonview.SettingItem;
import com.siterwell.demo.updateapp.UpdateAppAuto;

/**
 * Created by gc-0001 on 2017/5/28.
 */

public class AboutActivity extends TopbarSuperActivity {
    private final String TAG  = "AboutActivity";
    private SettingItem app_txt;

    @Override
    protected void onCreateInit() {
        initGuider();
        initview();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    private void initGuider(){
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.about), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null, R.color.bar_bg);
    }

    private void initview(){

        app_txt     = (SettingItem)findViewById(R.id.app_version);
        String verName = Config.getVerName(this, getPackageName());
        app_txt.setDetailText(verName);



    }

}

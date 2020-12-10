package com.siterwell.demo.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.siterwell.demo.R;
import com.siterwell.demo.common.CCPAppManager;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.ECAlertDialog;
import com.siterwell.demo.commonview.SettingItem;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;


/**
 * Created by gc-0001 on 2017/4/25.
 */

public class PersonalActivity extends TopbarSuperActivity implements View.OnClickListener{
    private TextView user_txt;
    private SettingItem settingItem_modifypassword,settingItem_modifyage;
    private  ECAlertDialog alertDialog;
    private  String age;

    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_personal;
    }

    private void initView(){
        getTopBarView().setTopBarStatus(R.drawable.back, -1, getResources().getString(R.string.personal_setting), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                  finish();
            }
        },null,R.color.bar_bg);
        user_txt   =  (TextView)findViewById(R.id.user);
        settingItem_modifypassword = (SettingItem)findViewById(R.id.modifycode);
        settingItem_modifyage = (SettingItem)findViewById(R.id.age);
        if(TextUtils.isEmpty(CCPAppManager.getClientUser().getPhoneNumber())){
            user_txt.setText(CCPAppManager.getClientUser().getEmail());
        }else{
            user_txt.setText(CCPAppManager.getClientUser().getPhoneNumber());
        }
        settingItem_modifypassword.setOnClickListener(this);
        settingItem_modifyage.setOnClickListener(this);
        refreshage();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.modifycode:
                startActivity(new Intent(this,ChangePasswordActivity.class));
                break;
            case R.id.age:
              alertDialog = ECAlertDialog.buildAlert(PersonalActivity.this, getResources().getString(R.string.modify_age),getResources().getString(R.string.dialog_btn_cancel),getResources().getString(R.string.dialog_btn_confim), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.setDismissFalse(true);
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                        final String newname = text.getText().toString().trim();

                        if(!TextUtils.isEmpty(newname)){

                            if(UnitTools.isNumeric(newname)){
                                alertDialog.setDismissFalse(true);
                                com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
                                object.put("description",newname.toString().trim());
                                HekrUserAction.getInstance(PersonalActivity.this).setProfile(object, new HekrUser.SetProfileListener() {
                                    @Override
                                    public void setProfileSuccess() {

                                        CCPAppManager.getClientUser().setDescription(newname);
                                        hideSoftKeyboard();
                                        Toast.makeText(PersonalActivity.this,getResources().getString(R.string.success_modify),Toast.LENGTH_SHORT).show();
                                        refreshage();
                                        CCPAppManager.setClientUser(CCPAppManager.getClientUser());
                                    }

                                    @Override
                                    public void setProfileFail(int errorCode) {
                                        Toast.makeText(PersonalActivity.this, Errcode.errorCode2Msg(PersonalActivity.this,errorCode),Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                alertDialog.setDismissFalse(false);
                                Toast.makeText(PersonalActivity.this,getResources().getString(R.string.age_must_be_number),Toast.LENGTH_SHORT).show();
                            }



                        }
                        else{
                            alertDialog.setDismissFalse(false);
                            Toast.makeText(PersonalActivity.this,getResources().getString(R.string.content_is_null),Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                alertDialog.setContentView(R.layout.edit_alert);
                alertDialog.setTitle(getResources().getString(R.string.modify_age));
                EditText text = (EditText) alertDialog.getContent().findViewById(R.id.tet);
                if(!TextUtils.isEmpty(age)){
                    text.setText(age);
                    text.setSelection(age.length());
                }

                alertDialog.show();
                break;
        }
    }



    private void refreshage(){
        age = CCPAppManager.getClientUser().getDescription();
        if(!TextUtils.isEmpty(age)){
            settingItem_modifyage.setDetailText(age);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshage();
    }
}

package com.siterwell.demo.user;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.siterwell.demo.R;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import me.siter.sdk.http.SiterUser;
import me.siter.sdk.http.UserAction;


/**
 * Created by gc-0001 on 2017/4/21.
 */

public class ChangePasswordActivity extends TopbarSuperActivity implements View.OnClickListener{
    private EditText oldtext,newtext,confirmtext;
    private Button btn_ok;
    @Override
    protected void onCreateInit() {
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_change_password;
    }

    private void initView(){

        getTopBarView().setTopBarStatus(R.drawable.back,null, getResources().getString(R.string.modify_code), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, null, R.color.bar_bg);
        oldtext = (EditText)findViewById(R.id.old_code);
        newtext = (EditText)findViewById(R.id.new_code);
        confirmtext = (EditText)findViewById(R.id.new_code_confirm);
        btn_ok= (Button)findViewById(R.id.ok);
        btn_ok.setOnClickListener(this);
    }


    private void modify(){
        String oldpsw = oldtext.getText().toString().trim();
        String newpsw = newtext.getText().toString().trim();
        String confirmpsw = confirmtext.getText().toString().trim();

        if(TextUtils.isEmpty(oldpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(newpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_new_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(confirmpsw)){
            Toast.makeText(this,getResources().getString(R.string.setting_confirm_new_password_hint), Toast.LENGTH_SHORT).show();
            return;
        }else if(!confirmpsw.equals(newpsw)){
            Toast.makeText(this,getResources().getString(R.string.password_two_different), Toast.LENGTH_SHORT).show();
            return;
        }else if(confirmpsw.length()<6){
            Toast.makeText(this,getResources().getString(R.string.password_length), Toast.LENGTH_SHORT).show();
            return;
        }else{
             showProgressDialog(getResources().getString(R.string.modifying_newpassword));
            UserAction.getInstance(this).changePassword(newpsw, oldpsw, new SiterUser.ChangePwdListener() {
                @Override
                public void changeSuccess() {
                    Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.modify_newpassword_success), Toast.LENGTH_SHORT).show();
                    hideProgressDialog();
                    hideSoftKeyboard();
                    finish();
                }

                @Override
                public void changeFail(int errorCode) {
                    hideProgressDialog();
                    if(errorCode == 400014){
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.code_fault), Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this, Errcode.errorCode2Msg(ChangePasswordActivity.this,errorCode), Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }

    }

    @Override
    public void onClick(View view) {
          switch (view.getId()){
              case R.id.ok:
                  modify();
                  break;
          }
    }
}

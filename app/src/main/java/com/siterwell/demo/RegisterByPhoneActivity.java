package com.siterwell.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.litesuits.common.assist.Toastor;
import com.siterwell.demo.common.Errcode;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;

public class RegisterByPhoneActivity extends AppCompatActivity implements View.OnClickListener, VerfyDialog.VerifyCodeSuccess {
    private EditText et_phone, et_code, et_pwd;
    private Button btn_get_code, btn_register;
    private String phone, pwd;
    private Toastor toastor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initData();
    }

    private void initData() {
        toastor = new Toastor(this);
        btn_get_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_code = (EditText) findViewById(R.id.et_code);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_register = (Button) findViewById(R.id.btn_register);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_code:


                phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone) && VerfyDialog.isMobile(phone)) {
                    VerfyDialog dialog = new VerfyDialog();
                    dialog.showDialog(this, HekrUserAction.getInstance(this), et_phone.getText().toString().trim(), HekrUserAction.CODE_TYPE_REGISTER,1);
                    dialog.show();
                    dialog.setVerifyCodeSuccess(this);
                } else {
                    toastor.showSingletonToast(getResources().getString(R.string.enter_valid_number));
                }
                break;

            case R.id.btn_register:
                String code = et_code.getText().toString().trim();
                pwd = et_pwd.getText().toString().trim();
                if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd)) {
                    HekrUserAction.getInstance(this).checkVerifyCode(phone, code, new HekrUser.CheckVerifyCodeListener() {
                        @Override
                        public void checkVerifyCodeSuccess(String phoneNumber, String verifyCode, String token, String expireTime) {
                            register(phoneNumber, pwd, token);
                        }

                        @Override
                        public void checkVerifyCodeFail(int errorCode) {
                            showError(errorCode);
                        }
                    });
                } else {
                    toastor.showSingleLongToast(getResources().getString(R.string.enter_name));
                }
                break;
        }
    }


    private void register(String phoneNumber, String pwd, String token) {
        HekrUserAction.getInstance(this).registerByPhone(phoneNumber, pwd, token, new HekrUser.RegisterListener() {
            @Override
            public void registerSuccess(String uid) {
                toastor.showSingleLongToast(getResources().getString(R.string.register_success));
                finish();
            }

            @Override
            public void registerFail(int errorCode) {
                showError(errorCode);
            }
        });
    }

    private void showError(int errorCode) {
        toastor.showSingleLongToast( Errcode.errorCode2Msg(RegisterByPhoneActivity.this,errorCode));
    }

    @Override
    public void getToken(String tokenId) {
        toastor.showSingleLongToast(getResources().getString(R.string.verification_code_success));
    }
}

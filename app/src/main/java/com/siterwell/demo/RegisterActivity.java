package com.siterwell.demo;

import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.litesuits.common.assist.Toastor;
import com.siterwell.demo.common.Errcode;
import com.siterwell.demo.common.TopbarSuperActivity;
import com.siterwell.demo.common.UnitTools;
import com.siterwell.demo.commonview.CodeEdit;
import com.siterwell.demo.commonview.ECAlertDialog;
import me.siter.sdk.http.SiterUser;
import me.siter.sdk.http.UserAction;


public class RegisterActivity extends TopbarSuperActivity implements View.OnClickListener{
    private EditText et_phone, et_code,et_email;
    private CodeEdit codeEdit,codeEdit_firm;
    private Button btn_get_code, btn_register;
    private String phone, pwd,con_pwd,email;
    private Toastor toastor;
    private LinearLayout liner_phone,liner_email;
    private int type = 1; //1代表手机 2代表邮箱


    @Override
    protected void onCreateInit() {
        initData();
        initView();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    private void initData() {
        toastor = new Toastor(this);
        UnitTools tools = new UnitTools(this);
        String lan = tools.readLanguage();

        if(!"zh".equals(lan)){
            type =2;
        }

    }

    private void initView() {
        liner_phone = (LinearLayout)findViewById(R.id.liner_phone);
        liner_email = (LinearLayout)findViewById(R.id.liner_email);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText)findViewById(R.id.et_email);
        et_code = (EditText) findViewById(R.id.et_code);
        codeEdit = (CodeEdit) findViewById(R.id.code1);
        codeEdit_firm =(CodeEdit)findViewById(R.id.code2);
        btn_get_code = (Button) findViewById(R.id.btn_get_code);
        btn_register = (Button) findViewById(R.id.btn_register);
        btn_get_code.setOnClickListener(this);
        btn_register.setOnClickListener(this);

        getTopBarView().setTopBarStatus(R.drawable.back, getResources().getString(R.string.email_register), getResources().getString(R.string.register), 1, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchType();
            }
        }, R.color.bar_bg);

        UnitTools tools = new  UnitTools(this);
        String lan = tools.readLanguage();
        if("zh".equals(lan)){
            liner_email.setVisibility(View.GONE);
            liner_phone.setVisibility(View.VISIBLE);
        }else{
            liner_email.setVisibility(View.VISIBLE);
            liner_phone.setVisibility(View.GONE);
            getTopBarView().getSettingTxt().setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.btn_get_code:
            if(type == 1){
                phone = et_phone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {

                    VerfyDialog dialog = new VerfyDialog();
                    dialog.showDialog(this, UserAction.getInstance(this),phone, UserAction.REGISTER_TYPE_PHONE,1);
                    dialog.show();
                } else {
                    toastor.showSingleLongToast(getResources().getString(R.string.please_input_phone));
                }

            }else{
                email = et_email.getText().toString().trim();
                if (!TextUtils.isEmpty(email)) {

                    VerfyDialog dialog = new VerfyDialog();
                    dialog.showDialog(this, UserAction.getInstance(this),email, UserAction.REGISTER_TYPE_PHONE,2);
                    dialog.show();
                } else {
                    toastor.showSingleLongToast(getResources().getString(R.string.please_input_email));
                }

            }

            break;

        case R.id.btn_register:

            if(type==1){
                String code = et_code.getText().toString().trim();
                pwd = codeEdit.getCodeEdit().getText().toString().trim();
                con_pwd = codeEdit_firm.getCodeEdit().getText().toString().trim();
                if (!TextUtils.isEmpty(code) && !TextUtils.isEmpty(pwd)) {
                    if(pwd.length()<6){
                        toastor.showSingleLongToast(getResources().getString(R.string.password_length));
                    }else if(!con_pwd.equals(pwd)){
                        toastor.showSingleLongToast(getResources().getString(R.string.password_two_different));
                    }
                    else{
                        register(phone, pwd, code);
                    }


                } else {
                    toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                }
            }else{
                String code = et_code.getText().toString().trim();
                email = et_email.getText().toString().trim();
                pwd = codeEdit.getCodeEdit().getText().toString().trim();
                con_pwd = codeEdit.getCodeEdit().getText().toString().trim();
                if (!TextUtils.isEmpty(email) ) {
                    if(pwd.length()<6){
                        toastor.showSingleLongToast(getResources().getString(R.string.password_length));
                    }else if(!con_pwd.equals(pwd)){
                        toastor.showSingleLongToast(getResources().getString(R.string.password_two_different));
                    }
                    else{
                        registerByEmail(email,pwd,code);

                    }
                }
                else{
                    toastor.showSingleLongToast(getResources().getString(R.string.please_input_complete));
                }

            }



            break;
        }
    }


    private void register(String phoneNumber, String pwd, String code) {
        UserAction.getInstance(this).registerByPhone(phoneNumber, pwd, code, new SiterUser.RegisterListener() {
            @Override
            public void registerSuccess(String uid) {
                toastor.showSingleLongToast(getResources().getString(R.string.success_register));
                finish();
            }

            @Override
            public void registerFail(int errorCode) {
                showError(errorCode);
            }
        });
    }

    private void registerByEmail(String email, String pwd,String code) {
        UserAction.getInstance(this).registerByEmail(email, pwd,code,  new SiterUser.RegisterListener() {
            @Override
            public void registerSuccess(String uid) {
                ECAlertDialog D = ECAlertDialog.buildPositiveAlert(RegisterActivity.this, R.string.check_email, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                D.show();
            }

            @Override
            public void registerFail(int errorCode) {
                showError(errorCode);
            }
        });
    }

    private void showError(int errorCode) {
        toastor.showSingleLongToast( Errcode.errorCode2Msg(RegisterActivity.this,errorCode));
    }

    private void switchType(){
        switch (type){
        case 1:
            type=2;
            getTopBarView().setSettingTxt(getResources().getString(R.string.phone_register));
            et_phone.setText("");
            et_phone.clearFocus();
            et_code.setText("");
            et_code.clearFocus();
            liner_email.setVisibility(View.VISIBLE);
            liner_phone.setVisibility(View.GONE);
            break;
        case 2:
            type=1;
            getTopBarView().setSettingTxt(getResources().getString(R.string.email_register));
            et_email.setText("");
            et_email.clearFocus();
            liner_email.setVisibility(View.GONE);
            liner_phone.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }

    }


}

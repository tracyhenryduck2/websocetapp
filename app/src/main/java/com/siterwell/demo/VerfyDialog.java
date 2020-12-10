package com.siterwell.demo;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.litesuits.common.assist.Toastor;
import com.siterwell.demo.common.Errcode;
import com.siterwell.sdk.http.HekrUser;
import com.siterwell.sdk.http.HekrUserAction;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.regex.Pattern;


/**
 * Created by linlinlin on 2016/8/9.
 * 作者：TianNuo
 * 邮箱：1320917731@qq.com
 */
public class VerfyDialog {
    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(14[57])|(17[0])|(17[7])|(18[0,0-9]))\\d{8}$";
    public final static String USER_MESS = "user_mess";
    private AlertDialog vfDialog;
    private EditText et_code;
    private ImageView iv_code;
    private Toastor toastor;
    private Activity activity;
    //随机数
    private String str_rid;
    private HekrUserAction hekrUserAction;

    //手机号
    private String username;
    //验证的type
    private int yz_type;

    //是手机还是邮箱;
    private int stype_type;

    public VerfyDialog() {

    }

    /**
     * @param context context
     * @param action  HekrUserAction
     * @param phone   手机号
     * @param type    类型
     * @return AlertDialog
     */
    public AlertDialog showDialog(final Activity context, HekrUserAction action, String phone, int type,int style_type) {
        if (vfDialog == null) {
            this.hekrUserAction = action;
            this.activity = context;
            this.username = phone;
            this.yz_type = type;
            this.stype_type = style_type;
            toastor = new Toastor(context);
            View layout = context.getLayoutInflater().inflate(R.layout.dialog_verynew, null);
            et_code = (EditText) layout.findViewById(R.id.dialog_etcode);
            iv_code = (ImageView) layout.findViewById(R.id.dialog_ivcode);
            iv_code.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    disPlay();
                }
            });
            vfDialog = new AlertDialog.Builder(context).setView(layout)
                .setPositiveButton(activity.getString(R.string.dialog_sure), null)
                .setNegativeButton(activity.getString(R.string.dialog_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        et_code.setText("");
                    }
                })
                .create();

            vfDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    vfDialog.getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (TextUtils.isEmpty(et_code.getText().toString().trim())) {
                                toastor.showSingletonToast(context.getString(R.string.dialog_empetycode));
                            } else {
                                //进行网络请求
                                verifyImg();
                            }
                        }
                    });
                }
            });

        }
        return vfDialog;
    }


    public void hide() {
        vfDialog.hide();
    }

    public void show() {
        //进行图片的验证码请求
        disPlay();
        vfDialog.show();
    }


    public void setClickPosListner(ClickPosListener listener) {
        ClickPosListener listner1 = listener;
    }


    public interface ClickPosListener {
        void click(DialogInterface dialog);
    }

    /**
     * 关于dialog positive方法是否立即取消
     */
    public void posiEnable(boolean isShow, DialogInterface dialog) {
        try {
            Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //true 为关闭，false 为不关闭
            field.set(dialog, isShow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取随机数
     *
     * @return 随机数
     */
    private String getRandomString() {
        Random ran = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 18) {
            int rand = ran.nextInt() & 0x7FFFFFFF;
            sb.append(Integer.toString(rand, 36));
        }
        str_rid = sb.substring(0, 18);
        return str_rid;
    }

    /**
     * 拉取图片
     */
    private void disPlay() {
        hekrUserAction.getImgCaptcha(getRandomString(), new HekrUser.GetImgCaptchaListener() {
            @Override
            public void getImgCaptchaSuccess(Bitmap bitmap) {
                iv_code.setImageBitmap(bitmap);
            }

            @Override
            public void getImgCaptchaFail(int errorCode) {
            }
        });
    }


    /**
     * 验证图片验证码
     */

    private void verifyImg() {
        hekrUserAction.checkCaptcha(et_code.getText().toString(), str_rid, new HekrUser.CheckCaptcha() {
            @Override
            public void checkCaptchaSuccess(String captchaToken) {
                Log.i("RegisterFragment", captchaToken);
                sendMess(captchaToken);

            }

            @Override
            public void checkCaptchaFail(int errorCode) {
                toastor.showSingletonToast(activity.getString(R.string.dialog_errorcode));
                disPlay();
            }
        });
    }

    /**
     * 获取短信验证
     */
    private void sendMess(final String captchaToken) {

        if(stype_type == 1){
            hekrUserAction.getVerifyCode(username, yz_type, captchaToken, new HekrUser.GetVerifyCodeListener() {
                @Override
                public void getVerifyCodeSuccess() {
                    toastor.showSingletonToast(activity.getString(R.string.success_send));
                    if (listener != null) {
                        listener.getToken(captchaToken);
                    }
                    hide();
                }

                @Override
                public void getVerifyCodeFail(int errorCode) {
                    toastor.showSingleLongToast(Errcode.errorCode2Msg(activity,errorCode));
                }
            });
        }else{
            hekrUserAction. getEmailVerifyCode(username, yz_type, captchaToken, new HekrUser.GetVerifyCodeListener() {
                @Override
                public void getVerifyCodeSuccess() {
                    toastor.showSingletonToast(activity.getString(R.string.success_send_email));
                    if (listener != null) {
                        listener.getToken(captchaToken);
                    }
                    hide();
                }

                @Override
                public void getVerifyCodeFail(int errorCode) {
                    toastor.showSingleLongToast(Errcode.errorCode2Msg(activity,errorCode));
                }
            });
        }


    }


    //设置一个完全成功的接口回掉返回token
    private VerifyCodeSuccess listener;

    public void setVerifyCodeSuccess(VerifyCodeSuccess listener) {
        this.listener = listener;
    }


    public interface VerifyCodeSuccess {
        void getToken(String tokenId);
    }

    /**
     * 已经注册！
     */
    private void registed() {
        hide();
    }


    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }


}

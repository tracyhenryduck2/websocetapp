package me.siter.sdk.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.android.log.Log;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.siterwell.sdk.bean.BatteryBean;
import com.siterwell.sdk.bean.DeviceType;
import com.siterwell.sdk.bean.WaterSensorBean;
import com.siterwell.sdk.common.GetDeviceListListener;
import me.siter.sdk.http.bean.BindDeviceBean;
import me.siter.sdk.http.bean.DefaultDeviceBean;
import me.siter.sdk.http.bean.DeviceBean;
import me.siter.sdk.http.bean.DeviceStatusBean;
import me.siter.sdk.http.bean.FileBean;
import me.siter.sdk.http.bean.FirmwareBean;
import me.siter.sdk.http.bean.FolderBean;
import me.siter.sdk.http.bean.FolderListBean;
import me.siter.sdk.http.bean.GroupBean;
import me.siter.sdk.http.bean.JWTBean;
import me.siter.sdk.http.bean.NewDeviceBean;
import me.siter.sdk.http.bean.NewsBean;
import me.siter.sdk.http.bean.OAuthListBean;
import me.siter.sdk.http.bean.OAuthRequestBean;
import me.siter.sdk.http.bean.ProfileBean;
import me.siter.sdk.http.bean.RuleBean;
import me.siter.sdk.http.bean.UserFileBean;
import com.siterwell.sdk.protocol.SocketCommand;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.message.BasicHeader;
import me.siter.sdk.Constants;
import me.siter.sdk.SiterSDK;
import me.siter.sdk.utils.CacheUtil;
import me.siter.sdk.utils.SpCache;

/**
 * Created by Administrator on 2017/10/16.
 */

public class UserAction {
    /**
     * 用户注册类型
     * 1为手机号注册
     * 2为Email注册
     */
    public static final int REGISTER_TYPE_PHONE = 1;
    public static final int REGISTER_TYPE_EMAIL = 2;
    /**
     * 用户注册数据节点
     */
    public static final int REGISTER_NODE_ASIA = 3;
    public static final int REGISTER_NODE_AMERICA = 4;
    public static final int REGISTER_NODE_EUROPE = 5;
    private static final String TAG = "userAction";

    /**
     * 发送验证码类型
     */
    public static final int CODE_TYPE_REGISTER = 1;
    public static final int CODE_TYPE_RE_REGISTER = 2;
    public static final int CODE_TYPE_CHANGE_PHONE = 3;

    /**
     * 第三方登录类型
     */
    public static final int OAUTH_QQ = 1;
    public static final int OAUTH_WECHAT = 2;
    public static final int OAUTH_SINA = 3;
    public static final int OAUTH_TWITTER = 4;
    public static final int OAUTH_FACEBOOK = 5;
    public static final int OAUTH_GOOGLE_PLUS = 6;


    private WeakReference<Context> mContext;
    private static volatile UserAction instance = null;


    public static UserAction getInstance(Context context) {
        if (instance == null) {
            synchronized (UserAction.class) {
                if (instance == null) {
                    instance = new UserAction(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private UserAction(Context context) {
        SpCache.init(context.getApplicationContext());
        mContext = new WeakReference<>(context.getApplicationContext());
        //判断是线上还是测试环境
    }

    /**
     * 3.18 获取图形验证码
     *
     * @param rid                   长度大于16，不能含有空格 验证码key
     * @param getImgCaptchaListener 回调接口
     */
    public void getImgCaptcha(@NotNull String rid, final SiterUser.GetImgCaptchaListener getImgCaptchaListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "images/getImgCaptcha?rid=", rid).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    getImgCaptchaListener.getImgCaptchaSuccess(bitmap);
                } else {
                    getImgCaptchaListener.getImgCaptchaFail(CodeUtil.getErrorCode(i, bytes));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                getImgCaptchaListener.getImgCaptchaFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.19 校验图形验证码
     *
     * @param code 验证码的值
     * @param rid  验证码key
     */
    public void checkCaptcha(@NotNull String code, @NotNull String rid, final SiterUser.CheckCaptcha checkCaptcha) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "images/checkCaptcha").toString();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", code);
        jsonObject.put("rid", rid);
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toJSONString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject jsonObject = JSON.parseObject(new String(bytes));
                checkCaptcha.checkCaptchaSuccess(jsonObject.getString("captchaToken"));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                checkCaptcha.checkCaptchaFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.1 发送短信验证码
     *
     * @param phoneNumber           手机号码
     * @param type                  验证码用途
     * @param getVerifyCodeListener 获取验证码回调
     */
    public void getVerifyCode(String phoneNumber, int type,int style_type, final SiterUser.GetVerifyCodeListener getVerifyCodeListener) {
        getVerifyCode(phoneNumber, type, 1, getVerifyCodeListener);
    }


    /**
     * 3.1 发送短信验证码
     *
     * @param phoneNumber           手机号码
     * @param type                  验证码用途
     * @param token                 校验图形验证码返回的token(发送手机短信校验码 接口中设备白名单过滤，如果pid在白名单中，访问改接口时，不需要带token 信息。否则访问时必须带token参数)
     * @param getVerifyCodeListener 获取验证码回调
     */
    public void getVerifyCode(String phoneNumber, int type, String token, final SiterUser.GetVerifyCodeListener getVerifyCodeListener) {
        String registerType;
        switch (type) {
            case CODE_TYPE_REGISTER:
                registerType = "register";
                break;
            case CODE_TYPE_RE_REGISTER:
                registerType = "resetPassword";
                break;
            case CODE_TYPE_CHANGE_PHONE:
                registerType = "changePhone";
                break;
            default:
                registerType = "register";
                break;
        }
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_GET_CODE_URL).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("phoneNumber", phoneNumber);
        maps.put("token", token);
        maps.put("type", registerType);
        maps.put("pid", SiterSDK.getPid());
        url = CommonUtil.getUrl(url, maps);
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        //获取成功
                        getVerifyCodeListener.getVerifyCodeSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        getVerifyCodeListener.getVerifyCodeFail(CodeUtil.getErrorCode(i, bytes));
                    }

                }

        );
    }

    /**
     * 3.1 发送邮箱验证码
     *
     * @param type                  验证码用途
     * @param token                 校验图形验证码返回的token(发送手机短信校验码 接口中设备白名单过滤，如果pid在白名单中，访问改接口时，不需要带token 信息。否则访问时必须带token参数)
     * @param getVerifyCodeListener 获取验证码回调
     */
    public void getEmailVerifyCode(String email, int type, String token, final SiterUser.GetVerifyCodeListener getVerifyCodeListener) {
        String registerType;
        switch (type) {
            case CODE_TYPE_REGISTER:
                registerType = "register";
                break;
            case CODE_TYPE_RE_REGISTER:
                registerType = "resetPassword";
                break;
            case CODE_TYPE_CHANGE_PHONE:
                registerType = "changePhone";
                break;
            default:
                registerType = "register";
                break;
        }
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_GET_EMAIL_CODE_URL).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("email", email);
        maps.put("token", token);
        maps.put("type", registerType);
        maps.put("pid", SiterSDK.getPid());
        url = CommonUtil.getUrl(url, maps);
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        //获取成功
                        getVerifyCodeListener.getVerifyCodeSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        getVerifyCodeListener.getVerifyCodeFail(CodeUtil.getErrorCode(i, bytes));
                    }

                }

        );
    }


    /**
     * 3.2 校验短信验证码
     *
     * @param phoneNumber             用户手机号码
     * @param code                    用户收到的验证码，长度为6位
     * @param checkVerifyCodeListener 验证码校验回调
     */
    public void checkVerifyCode(String phoneNumber, String code, final SiterUser.CheckVerifyCodeListener checkVerifyCodeListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_CHECK_CODE_URL, phoneNumber, "&code=", code).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject checkObject = JSON.parseObject(new String(bytes));
                //获取成功
                checkVerifyCodeListener.checkVerifyCodeSuccess(checkObject.get("phoneNumber").toString(), checkObject.get("verifyCode").toString(), checkObject.get("token").toString(), checkObject.get("expireTime").toString());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //获取验证码失败
                checkVerifyCodeListener.checkVerifyCodeFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.3 使用手机号注册用户
     *
     * @param password         用户的密码
     * @param phoneNumber      用户注册手机号
     * @param code            校验验证码返回的注册TokenToken
     * @param registerListener 注册接口
     */
    public void registerByPhone(String phoneNumber, String password, String code, final SiterUser.RegisterListener registerListener) {
        /*String node = null;
        int dataCenterNode = 3;
        switch (dataCenterNode) {
            case REGISTER_NODE_ASIA:
                node = "ASIA";
                break;
            case REGISTER_NODE_AMERICA:
                node = "AMERICA";
                break;
            case REGISTER_NODE_EUROPE:
                node = "EUROPE";
                break;
            default:
                node = "ASIA";
                break;
        }*/
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("code", code);
        jsonObject.put("pid", SiterSDK.getPid());
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_REGISTER_URL, "phone").toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                registerListener.registerSuccess(JSON.parseObject(new String(bytes)).getString("uid"));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                registerListener.registerFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 3.4 使用邮箱注册用户
     *
     * @param password         用户的密码
     * @param email            用户邮箱
     * @param registerListener 注册回调
     */
    public void registerByEmail(String email, String password,String code, final SiterUser.RegisterListener registerListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        jsonObject.put("email", email);
        jsonObject.put("code", code);
        jsonObject.put("pid", SiterSDK.getPid());
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_REGISTER_URL, "email_verify_code").toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        registerListener.registerSuccess(JSON.parseObject(new String(bytes)).getString("uid"));
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        registerListener.registerFail(CodeUtil.getErrorCode(i, bytes));
                    }
                }

        );
    }



    /**
     * 3.6 使用手机号重置密码
     *
     * @param phoneNumber      手机号码
     * @param verifyCode       验证码
     * @param password         密码
     * @param resetPwdListener 回调
     */
    public void resetPwd(String phoneNumber, String verifyCode, String password, final SiterUser.ResetPwdListener resetPwdListener) {
        _resetPwd(phoneNumber, verifyCode, null, password, resetPwdListener);
    }

    /**
     * 通过密保问题重置密码
     *
     * @param token            验证密保时返回的token
     * @param password         用户新密码
     * @param resetPwdListener 回调
     */
    public void resetPwdBySecurity(String token, String password, final SiterUser.ResetPwdListener resetPwdListener) {
        _resetPwd(null, null, token, password, resetPwdListener);
    }


    /**
     * 重置密码
     *
     * @param phoneNumber      手机号码
     * @param verifyCode       验证码
     * @param token            验证密保时返回的token
     * @param password         用户新密码
     * @param resetPwdListener 回调
     */
    private void _resetPwd(String phoneNumber, String verifyCode, String token, String password, final SiterUser.ResetPwdListener resetPwdListener) {
        String type = TextUtils.isEmpty(phoneNumber) ? "security" : "phone";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
        if (!TextUtils.isEmpty(phoneNumber)) {
            jsonObject.put("phoneNumber", phoneNumber);
            jsonObject.put("verifyCode", verifyCode);
            jsonObject.put("pid", SiterSDK.getPid());
        } else if (!TextUtils.isEmpty(token)) {
            jsonObject.put("token", token);
        } else {
            Log.e(TAG, "_resetPwd: 重置密码，参数错误");
            return;
        }
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_RESET_PWD_URL, type).toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        resetPwdListener.resetSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        resetPwdListener.resetFail(CodeUtil.getErrorCode(i, bytes));
                    }
                }

        );
    }


    /**
     * 重置密码
     *
     * @param email      邮箱�
     * @param verifyCode       验证码
     * @param password         用户新密码
     * @param resetPwdListener 回调
     */
    public void resetPwdByEmail(String email, String verifyCode, String password, final SiterUser.ResetPwdListener resetPwdListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("password", password);
            jsonObject.put("email", email);
            jsonObject.put("verifyCode", verifyCode);
            jsonObject.put("pid", SiterSDK.getPid());
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_RESET_PWD_URL, "email_verify_code").toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        resetPwdListener.resetSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        resetPwdListener.resetFail(CodeUtil.getErrorCode(i, bytes));
                    }
                }

        );
    }


    /**
     * 3.7 修改密码
     *
     * @param newPassword       新密码
     * @param oldPassword       旧密码
     * @param changePwdListener 回调接口
     */
    public void changePassword(String newPassword, String oldPassword, final SiterUser.ChangePwdListener changePwdListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newPassword", newPassword);
        jsonObject.put("oldPassword", oldPassword);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_CHANGR_PWD_URL);
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                changePwdListener.changeSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                changePwdListener.changeFail(errorCode);
            }
        });
    }


    /**
     * 3.8 修改用户手机号
     * <p>
     * 用户的老手机号不用了，想换一个手机号，而且希望老数据都能保存下来，可以使用该接口。
     *
     * @param token                     token(需要调用发送短信验证码接口给老手机号发送验证码{@link #getVerifyCode},类型为{@link #CODE_TYPE_CHANGE_PHONE}，并调用校验短信验证码接口{@link #checkVerifyCode(String, String, SiterUser.CheckVerifyCodeListener)}成功时获取。)
     * @param verifyCode                验证码(需要调用发送短信验证码接口给新手机号phoneNumber发送验证码获取{@link #getVerifyCode},类型为{@link #CODE_TYPE_REGISTER})
     * @param phoneNumber               用户新手机号码
     * @param changePhoneNumberListener 回调接口
     */
    public void changePhoneNumber(String token, String verifyCode, String phoneNumber, final SiterUser.ChangePhoneNumberListener changePhoneNumberListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("token", token);
        jsonObject.put("verifyCode", verifyCode);
        jsonObject.put("phoneNumber", phoneNumber);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_CHANGE_PHONE_URL);
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                changePhoneNumberListener.changePhoneNumberSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                changePhoneNumberListener.changePhoneNumberFail(errorCode);
            }
        });
    }

    /**
     * 3.9 发送重置密码邮件
     *
     * @param email                          邮箱
     * @param sendResetPasswordEmailListener 回调接口
     */
    public void sendResetPwdEmail(String email, final SiterUser.SendResetPwdEmailListener sendResetPasswordEmailListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "sendResetPasswordEmail?email=", CommonUtil.getEmail(email),"&pid=", SiterSDK.getPid()).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                sendResetPasswordEmailListener.sendSuccess();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                sendResetPasswordEmailListener.sendFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.10 重新发送确认邮件
     *
     * @param email               邮箱
     * @param reSendVerifiedEmail 回调接口
     */
    public void reSendVerifiedEmail(@NotNull String email, final SiterUser.ReSendVerifiedEmailListener reSendVerifiedEmail) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "resendVerifiedEmail?email=", CommonUtil.getEmail(email)).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                reSendVerifiedEmail.reSendVerifiedEmailSuccess();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                reSendVerifiedEmail.reSendVerifiedEmailFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.11 发送修改邮箱邮件
     * <p>
     * 用户的老邮箱不用了，想换一个邮箱，而且希望老数据都能保存下来，可以使用该接口。</p>
     *
     * @param email                   邮箱
     * @param sendChangeEmailListener 回调接口
     */
    public void sendChangeEmailStep1Email(@NotNull String email, final SiterUser.SendChangeEmailListener sendChangeEmailListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.UAA_SEND_CHANGE_EMAIL, CommonUtil.getEmail(email));
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                sendChangeEmailListener.sendChangeEmailSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                sendChangeEmailListener.sendChangeEmailFail(errorCode);
            }
        });
    }



    /**
     * 3.14 将OAuth账号和主账号绑定
     *
     * @param token             绑定token
     * @param bindOAuthListener 绑定接口。使用此接口之前必须登录！
     */
    public void bindOAuth(@NotNull String token, final SiterUser.BindOAuthListener bindOAuthListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "account/bind?token=", token);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                bindOAuthListener.bindSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                bindOAuthListener.bindFail(errorCode);
            }
        });
    }

    /**
     * 3.15 解除OAuth账号和主账号的绑定关系
     *
     * @param type              类型 QQ 微博 微信
     * @param bindOAuthListener 回调接口
     */
    public void unbindOAuth(int type, final SiterUser.BindOAuthListener bindOAuthListener) {
        String auth_type = null;
        switch (type) {
            case OAUTH_QQ:
                auth_type = "QQ";
                break;
            case OAUTH_WECHAT:
                auth_type = "WECHAT";
                break;
            case OAUTH_SINA:
                auth_type = "SINA";
                break;
            case OAUTH_TWITTER:
                auth_type = "TWITTER";
                break;
            case OAUTH_FACEBOOK:
                auth_type = "FACEBOOK";
                break;
            case OAUTH_GOOGLE_PLUS:
                auth_type = "GOOGLE";
                break;
            default:
                break;
        }
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "account/unbind?type=", auth_type);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                bindOAuthListener.bindSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                bindOAuthListener.bindFail(errorCode);
            }
        });

    }




    /**
     * 通过手机将第三方账号升级为主账号
     *
     * @param phoneNumber 用户手机号
     * @param password    用户密码
     * @param verifyCode  手机验证码
     * @param token       3.2返回里的token{@link #checkVerifyCode(String, String, SiterUser.CheckVerifyCodeListener)}
     */
    public void accountUpgrade(@NotNull String phoneNumber, @NotNull String password, @NotNull String verifyCode, @NotNull String token, final SiterUser.AccountUpgradeListener upgradeListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("password", password);
        jsonObject.put("token", token);
        jsonObject.put("verifyCode", verifyCode);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.ACCOUNT_UPGRADE);
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                upgradeListener.UpgradeSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                upgradeListener.UpgradeFail(errorCode);
            }
        });
    }

    /**
     * 发送校验邮件
     *
     * @param email    用户邮箱
     * @param password 用户密码
     */
    public void accountUpgradeByEmail(@NotNull String email, @NotNull String password, final SiterUser.SendEmailListener sendEmailListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("from", "uaa");
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, Constants.UrlUtil.SEND_EMAIL);
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                sendEmailListener.sendSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                sendEmailListener.sendFail(errorCode);
            }
        });
    }

    /**
     * 用户登录成功后如果还未设置密保问题，提示用户设置密保问题
     *
     * @param firstSecurityQues  密保问题1
     * @param secondSecurityQues 密保问题2
     * @param thirdSecurityQues  密保问题3
     */
    public void setSecurityQuestion(@NotNull String firstSecurityQues, @NotNull String secondSecurityQues, @NotNull String thirdSecurityQues, final SiterUser.SetSecurityQuestionListener setListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstSecurityQues", firstSecurityQues);
        jsonObject.put("secondSecurityQues", secondSecurityQues);
        jsonObject.put("thirdSecurityQues", thirdSecurityQues);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "setSecurityQuestion");
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                setListener.setSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                setListener.setFail(errorCode);
            }
        });
    }


    /**
     * 验证密保问题
     *
     * @param firstSecurityQues  密保问题1
     * @param secondSecurityQues 密保问题2
     * @param thirdSecurityQues  密保问题3
     */
    public void checkSecurityQuestion(@NotNull String firstSecurityQues, @NotNull String secondSecurityQues, @NotNull String thirdSecurityQues, String phoneNumber, final SiterUser.CheckVerifyCodeListener checkVerifyCodeListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstSecurityQues", firstSecurityQues);
        jsonObject.put("secondSecurityQues", secondSecurityQues);
        jsonObject.put("thirdSecurityQues", thirdSecurityQues);
        jsonObject.put("phoneNumber", phoneNumber);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "sms/checkSecurityQuestion");
        BaseHttpUtil.postData(mContext.get(), url.toString(), jsonObject.toJSONString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject checkObject = JSON.parseObject(new String(bytes));
                //获取成功
                checkVerifyCodeListener.checkVerifyCodeSuccess(checkObject.get("phoneNumber").toString(), "", checkObject.get("token").toString(), checkObject.get("expireTime").toString());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                checkVerifyCodeListener.checkVerifyCodeFail(CodeUtil.getErrorCode(i, bytes));

            }
        });
    }

    /**
     * 获取用户是否设置了密保问题
     *
     * @param phoneNumber 用户手机号
     * @param is          回调
     */
    public void isSecurityAccount(@NotNull String phoneNumber, final SiterUser.IsSecurityAccountListener is) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_UAA_URL, "isSecurityAccount?phoneNumber=", phoneNumber);
        BaseHttpUtil.getData(mContext.get(), url.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                boolean isSecurityAccount = JSONObject.parseObject(new String(bytes)).getBoolean("result");
                is.checkSuccess(isSecurityAccount);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                is.checkFail(CodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 4.1.1 绑定设备
     *
     * @param bindDeviceBean     绑定设备Bean
     * @param bindDeviceListener 回调接口
     */
    public void bindDevice(BindDeviceBean bindDeviceBean, final SiterUser.BindDeviceListener bindDeviceListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", bindDeviceBean.getDevTid());
        jsonObject.put("bindKey", bindDeviceBean.getBindKey());
        jsonObject.put("deviceName", bindDeviceBean.getDeviceName());
        jsonObject.put("desc", bindDeviceBean.getDesc());

        postSiterData(url, jsonObject.toString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                bindDeviceListener.bindDeviceSuccess(JSON.parseObject(object.toString(), DeviceBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                bindDeviceListener.bindDeviceFail(errorCode);
            }
        });
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(SiterUser.GetDevicesListener getDevicesListener) {
        getDevices(0, 20, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(String devTid, SiterUser.GetDevicesListener getDevicesListener) {
        getDevices(0, 20, devTid, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(int page, int size, SiterUser.GetDevicesListener getDevicesListener) {

        getDevices(page, size, null, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(int page, int size, String devTid, final SiterUser.GetDevicesListener getDevicesListener) {
        //CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE, "?page=", String.valueOf(page), "&size=", String.valueOf(size));
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("devTid", devTid);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = CommonUtil.getUrl(url, maps);

        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                List<DeviceBean> lists = JSON.parseArray(object.toString(), DeviceBean.class);
//                getDevicesListener.getDevicesSuccess(lists);
            }

            @Override
            public void getFail(int errorCode) {
                getDevicesListener.getDevicesFail(errorCode);
            }

        });
    }


    /**
     * 4.1.3 删除设备
     *
     * @param devTid        设备ID
     * @param bindKey       绑定码
     * @param deleteDevices 回调接口
     */
    public void deleteDevice(@NotNull String devTid, @NotNull String bindKey, final SiterUser.DeleteDeviceListener deleteDevices) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE, "/", devTid, "?bindKey=", bindKey);
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                deleteDevices.deleteDeviceSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                deleteDevices.deleteDeviceFail(errorCode);
            }
        });
    }


    /**
     * 4.1.4 更改设备名称/描述【普通设备】
     *
     * @param devTid               设备ID
     * @param ctrlKey              设备控制码
     * @param deviceName           设备名称  长度[1, 128]
     * @param desc                 设备描述 长度[1, 128]
     * @param renameDeviceListener 回调接口
     */
    public void renameDevice(@NotNull String devTid, @NotNull String ctrlKey, @NotNull String deviceName, String desc, final SiterUser.RenameDeviceListener renameDeviceListener) {
        renameDevice(devTid, null, ctrlKey, deviceName, desc, renameDeviceListener);
    }

    /**
     * 4.1.4 更改设备名称/描述【网关下子设备】
     *
     * @param devTid               设备ID
     * @param subDevTid            子设备Tid
     * @param ctrlKey              设备控制码
     * @param deviceName           设备名称  长度[1, 128]
     * @param desc                 设备描述 长度[1, 128]
     * @param renameDeviceListener 回调接口
     */
    public void renameDevice(@NotNull String devTid, String subDevTid, @NotNull String ctrlKey, @NotNull String deviceName, String desc, final SiterUser.RenameDeviceListener renameDeviceListener) {

        try {
            if(deviceName.getBytes("GBK").length<=15){

                if(!EmojiFilter.containsEmoji(deviceName)){
                    CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE, "/", devTid);
                    if (!TextUtils.isEmpty(subDevTid)) {
                        //网关下子设备
                        url = TextUtils.concat(url, "/", subDevTid);
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("deviceName", deviceName);
                    jsonObject.put("ctrlKey", ctrlKey);
                    if (!TextUtils.isEmpty(desc)) {
                        jsonObject.put("desc", desc);
                    }
                    patchSiterData(url, jsonObject.toString(), new GetDataListener() {
                        @Override
                        public void getSuccess(Object object) {
                            renameDeviceListener.renameDeviceSuccess();
                        }

                        @Override
                        public void getFail(int errorCode) {
                            renameDeviceListener.renameDeviceFail(errorCode);
                        }
                    });


                    DeviceBean deviceBean = new DeviceBean();
                    deviceBean.setDevTid(devTid);
                    deviceBean.setCtrlKey(ctrlKey);
                    SocketCommand socketCommand =new SocketCommand(deviceBean,mContext.get());
                    socketCommand.setSocketName(deviceName,null);
                }else {
                    renameDeviceListener.NameContainEmojiErr();
                }


            }else {
                renameDeviceListener.NameLongErr();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }


    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态<br>
     * 只返回正确的devTid/bindKey对应的设备绑定状态，所以返回里的元素数量会少于提交里的元素数量。<br>
     * 后续操作按照4.1.1执行
     *
     * @param devTid                设备ID
     * @param bindKey               绑定码
     * @param getBindStatusListener 回调接口
     */
    public void deviceBindStatus(String devTid, String bindKey, final SiterUser.GetBindStatusListener getBindStatusListener) {
        final JSONObject obj = new JSONObject();
        obj.put("devTid", devTid);
        obj.put("bindKey", bindKey);
        JSONArray array = new JSONArray();
        array.add(obj);
        deviceBindStatus(array, getBindStatusListener);
    }


    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态<br>
     * 只返回正确的devTid/bindKey对应的设备绑定状态，所以返回里的元素数量会少于提交里的元素数量。<br>
     * 后续操作按照4.1.1执行
     *
     * @param array                 [ {"bindKey" : "xxxxx", "devTid" : "ESP_test"},... }]
     * @param getBindStatusListener 回调接口{@link SiterUser.GetBindStatusListener}
     */
    public void deviceBindStatus(JSONArray array, final SiterUser.GetBindStatusListener getBindStatusListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.DEVICE_BIND_STATUS);
        postSiterData(url, array.toString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getBindStatusListener.getStatusSuccess(JSONArray.parseArray(object.toString(), DeviceStatusBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getBindStatusListener.getStatusFail(errorCode);
            }
        });
    }


    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态，
     * 如果可以绑定直接就进行调用4.1.1接口进行绑定操作;<br>
     *
     * @param devTid                       设备ID
     * @param bindKey                      绑定码
     * @param getBindStatusAndBindListener 回调接口 {@link SiterUser.GetBindStatusAndBindListener}
     */
    public void deviceBindStatusAndBind(final String devTid, final String bindKey, final SiterUser.GetBindStatusAndBindListener getBindStatusAndBindListener) {
        try {
            final JSONObject obj = new JSONObject();
            obj.put("devTid", devTid);
            obj.put("bindKey", bindKey);
            JSONArray array = new JSONArray();
            array.add(obj);
            deviceBindStatus(array, new SiterUser.GetBindStatusListener() {
                @Override
                public void getStatusSuccess(List<DeviceStatusBean> deviceStatusBeanLists) {
                    //直接进行绑定操作
                    if (deviceStatusBeanLists != null && !deviceStatusBeanLists.isEmpty()) {
                        //成功后回调
                        getBindStatusAndBindListener.getStatusSuccess(deviceStatusBeanLists);
                        DeviceStatusBean deviceStatusBean = deviceStatusBeanLists.get(0);
                        if (deviceStatusBean.isForceBind() || !deviceStatusBean.isBindToUser()) {
                            String name = (deviceStatusBean.getCidName().substring(deviceStatusBean.getCidName().indexOf("/") + 1));
                            BindDeviceBean bindDeviceBean = new BindDeviceBean(devTid, bindKey, name, "");
                            bindDevice(bindDeviceBean, new SiterUser.BindDeviceListener() {
                                @Override
                                public void bindDeviceSuccess(DeviceBean deviceBean) {
                                    getBindStatusAndBindListener.bindDeviceSuccess(deviceBean);
                                }

                                @Override
                                public void bindDeviceFail(int errorCode) {
                                    getBindStatusAndBindListener.bindDeviceFail(errorCode);
                                }
                            });
                        }
                    }
                }

                @Override
                public void getStatusFail(int errorCode) {
                    getBindStatusAndBindListener.getStatusFail(errorCode);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 4.1.8 查询设备属主
     *
     * @param devTid                设备ID
     * @param bindKey               绑定码
     * @param getQueryOwnerListener 回调接口
     */
    public void queryOwner(String devTid, String bindKey, final SiterUser.GetQueryOwnerListener getQueryOwnerListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, "queryOwner");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("bindKey", bindKey);
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                JSONObject jsonObject = JSONObject.parseObject(object.toString());
                getQueryOwnerListener.queryOwnerSuccess(jsonObject.getString("message"));
            }

            @Override
            public void getFail(int errorCode) {
                getQueryOwnerListener.queryOwnerFail(errorCode);
            }
        });

    }

    /**
     * 4.1.10 获取当前局域网设备配网详情
     * 该接口用于配网时查看当前局域网内设备配网进度
     */
    public void getNewDevices(String pinCode, String ssid, final SiterUser.GetNewDevicesListener getNewDevicesListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.GET_NEW_DEVICE, pinCode, "&ssid=", ssid);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG, "object:" + object.toString());
                getNewDevicesListener.getSuccess(JSON.parseArray(object.toString(), NewDeviceBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getNewDevicesListener.getFail(errorCode);
            }
        });
    }


    /**
     * 4.2.1 添加目录
     *
     * @param folderName        目录名称
     * @param addFolderListener 回调接口{@link SiterUser.AddFolderListener}
     */
    public void addFolder(@NotNull String folderName, final SiterUser.AddFolderListener addFolderListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER);
        JSONObject obj = new JSONObject();
        obj.put("folderName", folderName);
        postSiterData(url, obj.toString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                addFolderListener.addFolderSuccess(JSON.parseObject(object.toString(), FolderBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                addFolderListener.addFolderFail(errorCode);
            }
        });

    }

    /**
     * 4.2.2 列举目录
     *
     * @param page 页数
     */
    public void getFolder(int page, final SiterUser.GetFolderListsListener getFolderListsListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER, "?page=", String.valueOf(page));
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getFolderListsListener.getSuccess(JSON.parseArray(object.toString(), FolderListBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getFolderListsListener.getFail(errorCode);
            }
        });

    }

    /**
     * 4.2.3 修改目录名称
     *
     * @param newFolderName        新目录名字
     * @param folderId             目录ID
     * @param renameFolderListener 回调接口
     */
    public void renameFolder(String newFolderName, String folderId, final SiterUser.RenameFolderListener renameFolderListener) {
        JSONObject object = new JSONObject();
        object.put("newFolderName", newFolderName);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER, "/", folderId);

        putSiterData(url, object.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                renameFolderListener.renameSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                renameFolderListener.renameFail(errorCode);
            }
        });
    }


    /**
     * 4.2.4 删除全部自定义目录
     *
     * @param deleteFolderListener 回调
     */
    public void deleteFolder(final SiterUser.DeleteFolderListener deleteFolderListener) {
        deleteFolder(null, deleteFolderListener);
    }

    /**
     * 4.2.4 删除目录
     * 即使目录下有设备也可以删除，后续动作是把这些设备挪到根目录下。
     * 注意，如果不指定folderId参数，会删除全部自定义目录。
     *
     * @param folderId             目录ID
     * @param deleteFolderListener 回调
     */
    public void deleteFolder(String folderId, final SiterUser.DeleteFolderListener deleteFolderListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER);
        if (!TextUtils.isEmpty(folderId)) {
            url = TextUtils.concat(url, "/", folderId);
        }
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                deleteFolderListener.deleteSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                deleteFolderListener.deleteFail(errorCode);
            }
        });
    }

    /**
     * 4.2.5 将设备挪到指定目录
     *
     * @param folderId                目录id
     * @param ctrlKey                 设备控制码
     * @param devTid                  设备ID
     * @param devicePutFolderListener 回调接口
     */
    public void devicesPutFolder(String folderId, String ctrlKey, String devTid, final SiterUser.DevicePutFolderListener devicePutFolderListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER, "/", folderId);
        JSONObject obj = new JSONObject();
        obj.put("devTid", devTid);
        obj.put("ctrlKey", ctrlKey);
        postSiterData(url, obj.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                devicePutFolderListener.putSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                devicePutFolderListener.putFail(errorCode);
            }
        });
    }

    /**
     * 4.2.6 将设备从目录挪到根目录下
     *
     * @param folderId                目录ID
     * @param ctrlKey                 设备ID
     * @param devTid                  设备控制码
     * @param devicePutFolderListener 回调方法
     */
    public void folderToRoot(String folderId, String ctrlKey, String devTid, final SiterUser.DevicePutFolderListener devicePutFolderListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.FOLDER, "/", folderId, "/", devTid, "?", Constants.UrlUtil.CTRL_KEY, ctrlKey);
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                devicePutFolderListener.putSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                devicePutFolderListener.putFail(errorCode);
            }
        });
    }

    /**
     * 4.3.2 反向授权创建
     * <p>
     * 1. 创建授权二维码URL
     * </p>
     *
     * @param createOAuthQRCodeListener 回调接口
     */
    public void oAuthCreateCode(String ctrlKey, final SiterUser.CreateOAuthQRCodeListener createOAuthQRCodeListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_AUTH_URL);
        JSONObject obj = new JSONObject();
        obj.put("ctrlKey", ctrlKey);
        postSiterData(url, JSON.toJSONString(obj), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                String reverseAuthorizationTemplateId = JSON.parseObject(object.toString()).getString("reverseAuthorizationTemplateId");
                createOAuthQRCodeListener.createSuccess(reverseAuthorizationTemplateId);
            }

            @Override
            public void getFail(int errorCode) {
                createOAuthQRCodeListener.createFail(errorCode);
            }
        });
    }

    /**
     * 4.3.2 反向授权创建
     * <p>
     * 2. 被授权用户扫描二维码
     * </p>
     *
     * @param reverseAuthorizationTemplateId 回调接口
     * @param registerOAuthQRCodeListener    授权id
     */
    public void registerAuth(String reverseAuthorizationTemplateId, final SiterUser.RegisterOAuthQRCodeListener registerOAuthQRCodeListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, Constants.UrlUtil.REVERSE_TEMPLATE_ID, reverseAuthorizationTemplateId);
        postSiterData(url, null, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                registerOAuthQRCodeListener.registerSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                registerOAuthQRCodeListener.registerFail(errorCode);
            }
        });

    }


    /**
     * 4.3.2 反向授权创建
     * <p>
     * 3. 授权用户收到被授权者的请求
     * </p>
     *
     * @param devTid               可选
     * @param page                 0
     * @param size                 1
     * @param reverseRegisterId    可选
     * @param getOauthInfoListener 回调接口
     */
    public void getOAuthInfoRequest(String devTid, int page, int size, String reverseRegisterId, final SiterUser.GetOauthInfoListener getOauthInfoListener) {
        //CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, "?page=", String.valueOf(page), "&size=", String.valueOf(size));
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_REGISTER).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("devTid", devTid);
        maps.put("reverseRegisterId", reverseRegisterId);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getOauthInfoListener.getOauthInfoSuccess(JSON.parseArray(object.toString(), OAuthRequestBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getOauthInfoListener.getOauthInfoFail(errorCode);
            }
        });
    }

    /**
     * 4.3.2 反向授权创建
     * <p>
     * 4. 授权用户同意
     * </p>
     *
     * @param devTid             必选
     * @param ctrlKey            必选
     * @param reverseRegisterId  必选 通过4.3.2-3{@link #getOAuthInfoRequest(String, int, int, String, SiterUser.GetOauthInfoListener)}接口拿到的数据
     * @param agreeOauthListener 回调接口
     */
    public void agreeOAuth(String devTid, String ctrlKey, String reverseRegisterId, final SiterUser.AgreeOauthListener agreeOauthListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_DEV_TID, devTid, "&", Constants.UrlUtil.CTRL_KEY, ctrlKey, Constants.UrlUtil.REVERSE_REGISTER_ID, reverseRegisterId);
        postSiterData(url, null, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                agreeOauthListener.AgreeOauthSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                agreeOauthListener.AgreeOauthFail(errorCode);
            }
        });
    }

    /**
     * 4.3.2 反向授权创建
     * <p>
     * 5. 授权用户拒绝
     * </p>
     *
     * @param devTid              必选
     * @param ctrlKey             必选
     * @param grantee             必选 通过4.3.2-3接口{@link #getOAuthInfoRequest(String, int, int, String, SiterUser.GetOauthInfoListener)}拿到的数据
     * @param reverseRegisterId   必选 通过4.3.2-3接口{@link #getOAuthInfoRequest(String, int, int, String, SiterUser.GetOauthInfoListener)}拿到的数据！
     * @param refuseOAuthListener 回调接口
     */
    public void refuseOAuth(@NotNull String devTid, @NotNull String ctrlKey, @NotNull String grantee, @NotNull String reverseRegisterId, final SiterUser.RefuseOAuthListener refuseOAuthListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, "/", reverseRegisterId, "?", Constants.UrlUtil.DEV_TID,
                devTid, "&uid=", grantee, "&", Constants.UrlUtil.CTRL_KEY, ctrlKey);
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                refuseOAuthListener.refuseOauthSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                refuseOAuthListener.refuseOauthFail(errorCode);
            }
        });
    }

    /**
     * 4.3.4 取消授权
     *
     * @param ctrlKey 被授权用户uid，多个使用逗号分隔；当不提交该参数时，表示授权者删除该设备上对所有被授权者的授权关系
     * @param grantee 控制码
     */
    public void cancelOAuth(String ctrlKey, String grantee,final SiterUser.CancelOAuthListener cancelOAuthListener) {
        //CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_GRANTOR, grantor, "&", Constants.UrlUtil.CTRL_KEY, ctrlKey, "&", Constants.UrlUtil.GRANTEE, grantee, "&", Constants.UrlUtil.DEV_TID, devTid);
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL,Constants.UrlUtil.AUTHORIZATION_REVERSE_CANCEL, ctrlKey).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("grantee", grantee);
        url = CommonUtil.getUrl(url, maps);

        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                cancelOAuthListener.CancelOAuthSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                cancelOAuthListener.CancelOauthFail(errorCode);
            }
        });
    }


    /**
     * 4.3.5 列举授权信息/被授权者调用
     *
     * @param grantor 授权用户uid
     * @param ctrlKey 控制码
     * @param devTid  设备ID
     * @param grantee 被授权用户uid，多个使用逗号分隔；当不提交该参数时，表示列举该设备上所有的授权关系；被授权者调用时不得为空，且其值为当前调用用户的uid
     */
    public void getOAuthList(String grantor, String ctrlKey, String devTid, String grantee, final SiterUser.GetOAuthListener getOAuthListener) {
        _getOAuthList(grantor, ctrlKey, devTid, grantee, getOAuthListener);
    }


    /**
     * 4.3.5 列举授权信息/授权者调用
     *
     * @param grantor          授权用户uid
     * @param ctrlKey          控制码
     * @param devTid           设备ID
     * @param getOAuthListener 回调接口
     */
    public void getOAuthList(String grantor, String ctrlKey, String devTid, final SiterUser.GetOAuthListener getOAuthListener) {
        _getOAuthList(grantor, ctrlKey, devTid, null, getOAuthListener);
    }

    /**
     * 4.3.5 列举授权信息
     *
     * @param grantor          grantor	必选	String		授权用户uid
     * @param ctrlKey          ctrlKey	必选	String		控制码
     * @param devTid           devTid	必选	String		设备ID
     * @param grantee          grantee	可选	String		被授权用户uid，多个使用逗号分隔；当不提交该参数时，表示列举该设备上所有的授权关系；被授权者调用时不得为空，且其值为当前调用用户的uid
     * @param getOAuthListener 回调接口
     */
    private void _getOAuthList(String grantor, String ctrlKey, String devTid, String grantee, final SiterUser.GetOAuthListener getOAuthListener) {
        //CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.AUTHORIZATION_GRANTOR, grantor, "&", Constants.UrlUtil.CTRL_KEY, ctrlKey, "&", Constants.UrlUtil.DEV_TID, devTid);
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, "authorization").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("grantor", grantor);
        maps.put("ctrlKey", ctrlKey);
        maps.put("grantee", grantee);
        maps.put("devTid", devTid);
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getOAuthListener.getOAuthListSuccess(JSON.parseArray(object.toString(), OAuthListBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getOAuthListener.getOAuthListFail(errorCode);
            }
        });
    }

    /**
     * 4.4.5 添加预约任务/4.4.5.1 添加一次性预约任务/4.4.5.2 添加循环预约任务
     * <p>注意区分RuleBean的不同
     *
     * @param ruleBean 预约任务
     */
    public void creatRule(RuleBean ruleBean, final SiterUser.CreateRuleListener createRuleListener) {
        postSiterData(TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.CREATE_RULE), JSON.toJSONString(ruleBean), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                createRuleListener.createSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                createRuleListener.createFail(errorCode);
            }
        });
    }

    /**
     * 4.4.6 列举预约任务
     *
     * @param devTid           设备ID，按其value筛选
     * @param ctrlKey          控制码
     * @param taskId           任务ID，按其value筛选
     * @param getRulesListener 回调方法
     */
    public void getRules(String devTid, String ctrlKey, String taskId, final SiterUser.GetRulesListener getRulesListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getRulesListener.getRulesSuccess(JSON.parseArray(object.toString(), RuleBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getRulesListener.getRulesFail(errorCode);
            }
        });
    }

    /**
     * 4.4.7 编辑预约任务
     *
     * @param devTid                设备ID
     * @param ctrlKey               控制码
     * @param taskId                任务ID
     * @param ruleBean              ruleBean（taskName, desc,  code,enable,cronExpr, feedback）
     * @param operationRuleListener 回调方法
     */
    public void editRule(String devTid, String ctrlKey, @NotNull String taskId, RuleBean ruleBean, final SiterUser.OperationRuleListener operationRuleListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = CommonUtil.getUrl(url, maps);
        putSiterData(url, ruleBean.toString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                operationRuleListener.operationRuleSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                operationRuleListener.operationRuleFail(errorCode);
            }
        });
    }

    /**
     * 4.4.8 删除预约任务
     *
     * @param devTid  设备ID
     * @param ctrlKey 控制码
     * @param taskId  任务ID，多个逗号分隔；若不指定该参数，则会删除全部预约任务
     */
    public void deleteRules(String devTid, String ctrlKey, String taskId, final SiterUser.OperationRuleListener operationRuleListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = CommonUtil.getUrl(url, maps);
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                operationRuleListener.operationRuleSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                operationRuleListener.operationRuleFail(errorCode);
            }
        });
    }

    /**
     * 4.5.1 获取用户档案
     *
     * @param getProfileListener 回调接口
     */
    public void getProfile(final SiterUser.GetProfileListener getProfileListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.PROFILE);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                setUserCache(object.toString());
                getProfileListener.getProfileSuccess(object);
            }

            @Override
            public void getFail(int errorCode) {
                getProfileListener.getProfileFail(errorCode);
            }
        });

    }



    /**
     * 4.5.2 更新用户档案
     *
     * @param jsonObject         用户json，参考文档docs4.5.2
     * @param setProfileListener 回调
     */
    public void setProfile(@NotNull final JSONObject jsonObject, final SiterUser.SetProfileListener setProfileListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.PROFILE);
        putSiterData(url, jsonObject.toString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                setProfileListener.setProfileSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                setProfileListener.setProfileFail(errorCode);
            }
        });
    }


    /**
     * 4.5.16 上传文件
     *
     * @param uri                绝对地址
     * @param uploadFileListener 回调
     */
    public void uploadFile(@NotNull final String uri, final SiterUser.UploadFileListener uploadFileListener) throws FileNotFoundException {
        File file = new File(uri);
        RequestParams params = new RequestParams();
        params.put("file", file, "image/png", System.currentTimeMillis() + ".png");
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.USER_FILE);
        postParamsSiterData(url.toString(), params, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                uploadFileListener.uploadFileSuccess(JSONObject.parseObject(object.toString(), FileBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                uploadFileListener.uploadFileFail(errorCode);
            }


            @Override
            public void getProgress(long bytesWritten, long totalSize) {
                super.getProgress(bytesWritten, totalSize);
                uploadFileListener.uploadProgress(CommonUtil.getProgress(bytesWritten, totalSize));
            }
        });
    }

    /**
     * 4.5.17 列举已上传文件
     *
     * @param fileName fileName	可选	String		文件名
     * @param page     page	可选	int	[0, ?]	分页参数
     * @param size     size	可选	int	[0, 20]	分页参数
     */
    public void getUserFiles(String fileName, int page, int size, final SiterUser.GetFileListener getFileListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, "user/file").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("fileName", fileName);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getFileListener.getSuccess(JSONObject.parseObject(object.toString(), UserFileBean.class));

            }

            @Override
            public void getFail(int errorCode) {
                getFileListener.getFail(errorCode);
            }
        });
    }

    /**
     * 4.5.18 删除已上传文件
     *
     * @param fileName           fileName	必选 	String		文件名
     * @param deleteFileListener 回调
     */
    public void deleteUserFile(@NotNull String fileName, final SiterUser.DeleteFileListener deleteFileListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, "user/file?fileName=", fileName).toString();
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                deleteFileListener.deleteSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                deleteFileListener.deleteFail(errorCode);
            }
        });
    }


    public void getFoldDeviceList(Context context,int page,int size,String folderid,final GetDeviceListListener getDeviceListListener){
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.BIND_DEVICE,"/",folderid);
        UserAction.getInstance(context).getSiterData(url.toString()+"?page="+page+"&size="+size, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    List<DeviceBean> listold = new ArrayList<DeviceBean>();

                    JSONArray jsonArray = JSON.parseArray(object.toString());
                    for(int i=0;i<jsonArray.size();i++){
                        DeviceBean deviceBean = new DeviceBean();
                        deviceBean.setFolderId(jsonArray.getJSONObject(i).getString("folderId"));
                        deviceBean.setDevTid(jsonArray.getJSONObject(i).getString("devTid"));
                        deviceBean.setModel(jsonArray.getJSONObject(i).getString("model"));

                        deviceBean.setDeviceName(jsonArray.getJSONObject(i).getString("deviceName"));
                        deviceBean.setOnline(jsonArray.getJSONObject(i).getBoolean("online")?true:false);
                        deviceBean.setCtrlKey(jsonArray.getJSONObject(i).getString("ctrlKey"));
                        deviceBean.setBindKey(jsonArray.getJSONObject(i).getString("bindKey"));
                        deviceBean.setProductPublicKey(jsonArray.getJSONObject(i).getString("productPublicKey"));
                        listold.add(deviceBean);
                    }
                    getDeviceListListener.succuss(listold);

                }catch (Exception e){
                    e.printStackTrace();
                    getDeviceListListener.error(2);
                }
            }

            @Override
            public void getFail(int errorCode) {
                getDeviceListListener.error(errorCode);
            }
        });


    }

    /**
     * 获取某个设备的报警信息，所有的
     * @param deviceBean
     * @param GetDataListener
     */
    public void getAlarmHistory(int page,int size, DeviceBean deviceBean,final UserAction.GetDataListener GetDataListener) {
        UserAction userAction = UserAction.getInstance(mContext.get());
        BasicHeader header = new BasicHeader("X-Siter-ProdPubKey",deviceBean.getProductPublicKey());
        CharSequence url2 = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.QUERY_WARNINGS);
        String url = url2.toString() +
                "devTid="+deviceBean.getDevTid()+"" +
                "&startTime="+dateGetOneDay()+
                "&size="+size+
                "&page="+page;

        userAction.getSiterData(url, new Header[]{header}, new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                GetDataListener.getSuccess(object);
            }

            @Override
            public void getFail(int errorCode) {
                GetDataListener.getFail(errorCode);
            }
        });
    }


    /**
     * 获取所有报警数据
     * @param GetDataListener
     */
    public void getAllAlarmHistory(int page,int size,final UserAction.GetDataListener GetDataListener) {
        UserAction userAction = UserAction.getInstance(mContext.get());
        CharSequence url2 = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.QUERY_WARNINGS);
        String url = url2.toString() +
                "&startTime="+dateGetOneDay()+
                "&size="+size+
                "&page="+page;

        userAction.getSiterData(url,new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                GetDataListener.getSuccess(object);
            }

            @Override
            public void getFail(int errorCode) {
                GetDataListener.getFail(errorCode);
            }
        });
    }

    /**
     * 批量获取GS156W水感以及GS140当前状态接口
     * @param deviceBeanList
     *
     */
    public void getGS140AndGS156WCurrentStatus(@NotNull final List<DeviceBean> deviceBeanList,final SiterUser.GetGS140AndGS156WListener getGS140AndGS156WListener) {
        UserAction userAction = UserAction.getInstance(mContext.get());
        CharSequence url2 = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.QUERY_DEVICE_STATUS);
        JSONArray jsonArray = new JSONArray();


        for(int i=0;i<deviceBeanList.size();i++){
            JSONObject J2 = new JSONObject();
            J2.put("devTid",deviceBeanList.get(i).getDevTid());
            J2.put("ctrlKey",deviceBeanList.get(i).getCtrlKey());
            jsonArray.add(i,J2);
        }
        userAction.postSiterData(url2,jsonArray.toString(), new UserAction.GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    JSONArray jsonArray = JSONArray.parseArray(object.toString());
                    List<BatteryBean> batteryBeanList = new ArrayList<BatteryBean>();
                    List<WaterSensorBean> waterSensorBeanList = new ArrayList<WaterSensorBean>();
                    for(int i=0;i<jsonArray.size();i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String deviceid = obj.getString("devTid");

                        if(i<=(deviceBeanList.size()-1)){
                            DeviceBean deviceBean =  deviceBeanList.get(i);
                            if(DeviceType.BATTERY.toString().equals(deviceBean.getModel()) ){
                                int status_current = 0;
                                int per_current = -1;
                                int signal = -1;

                                JSONObject status = obj.getJSONObject("status");

                                if(status.containsKey("status")){
                                    JSONObject s = status.getJSONObject("status");
                                    status_current = s.containsKey("currentValue")?s.getIntValue("currentValue"):0;
                                }

                                if(status.containsKey("battPercent")){
                                    JSONObject p = status.getJSONObject("battPercent");
                                    per_current = p.containsKey("currentValue")?p.getIntValue("currentValue"):-1;
                                }
                                if(status.containsKey("signal")) {
                                    JSONObject sig = status.getJSONObject("signal");
                                    signal = sig.containsKey("currentValue")?sig.getIntValue("currentValue"):-1;
                                }

                                BatteryBean batteryDescBean = new BatteryBean();
                                batteryDescBean.setDevTid(deviceid);
                                batteryDescBean.setSignal(signal);
                                batteryDescBean.setStatus(status_current);
                                batteryDescBean.setBattPercent(per_current);
                                batteryBeanList.add(batteryDescBean);
                            }else if(DeviceType.WATERSENEOR.toString().equals(deviceBean.getModel())){
                                int status_current = 0;
                                int per_current = -1;
                                int signal = -1;

                                JSONObject status = obj.getJSONObject("status");

                                if(status.containsKey("status")){
                                    JSONObject s = status.getJSONObject("status");
                                    status_current = s.containsKey("currentValue")?s.getIntValue("currentValue"):0;
                                }

                                if(status.containsKey("battPercent")){
                                    JSONObject p = status.getJSONObject("battPercent");
                                    per_current = p.containsKey("currentValue")?p.getIntValue("currentValue"):-1;
                                }
                                if(status.containsKey("signal")) {
                                    JSONObject sig = status.getJSONObject("signal");
                                    signal = sig.containsKey("currentValue")?sig.getIntValue("currentValue"):-1;
                                }

                                WaterSensorBean waterSensorBean = new WaterSensorBean();
                                waterSensorBean.setDevTid(deviceid);
                                waterSensorBean.setSignal(signal);
                                waterSensorBean.setStatus(status_current);
                                waterSensorBean.setBattPercent(per_current);
                                waterSensorBeanList.add(waterSensorBean);
                            }
                        }
                        }


                    getGS140AndGS156WListener.getSuccess(batteryBeanList,waterSensorBeanList);
                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

            @Override
            public void getFail(int errorCode) {
                android.util.Log.i(TAG,"errorCode:"+errorCode);
                getGS140AndGS156WListener.getFail(errorCode);
            }
        });
    }


    /**
     * 1970-01-01的时间转换
     * @return
     */
    public static String dateGetOneDay() {
        SimpleDateFormat s=new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0l);
        String curDate = s.format(c.getTime());  //��ǰ����
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        String hour1 = hour<10 ? "0"+hour : ""+hour;
        String min1 = (min<10 ? "0"+min : ""+min);
        SimpleDateFormat zz = new SimpleDateFormat("Z");
        String where2 = zz.format(c.getTime());
        String where3 = where2.substring(1);
        return curDate+"T"+hour1+":"+min1+":00.000-"+where3;
    }

    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程执行)
     *
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(@NotNull String clientId, final SiterUser.PushTagBindListener pushTagBindListener) {
        pushTagBind(clientId, 0, pushTagBindListener);
    }





    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程调用)
     *
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(@NotNull String clientId, int type, final SiterUser.PushTagBindListener pushTagBindListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.PUSH_TAG_BIND);
        String phoneType = "";
        String platform = "";
        switch (type) {
            case 0:
                phoneType = "个推";
                platform ="GETUI";
                break;
            case 1:
                phoneType = "小米";
                platform ="XIAOMI";
                break;
            case 2:
                phoneType = "华为";
                platform ="HUAWEI";
                break;
            case 3:
                phoneType = "FCM";
                platform ="FCM";
                break;
        }
        Log.i(TAG, phoneType + "调用绑定推送标签接口:" + clientId + "语言:" + Locale.getDefault());
        JSONObject object = new JSONObject();
        object.put("clientId", clientId);
        object.put("locale", Locale.getDefault());
        object.put("pushPlatform",platform);
        final String finalPhoneType = phoneType;
        postSiterData(url, object.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG, finalPhoneType + "绑定推送标签接口调用成功");
                pushTagBindListener.pushTagBindSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                Log.i(TAG, finalPhoneType + "绑定推送标签接口调用失败");
                pushTagBindListener.pushTagBindFail(errorCode);
            }
        });
    }

    /**
     * 4.5.20 解绑推送别名
     *
     * @param clientId              个推分配给app的客户端id
     * @param unPushTagBindListener 回调方法
     */
    public void unPushTagBind(@NotNull String clientId, int type, final SiterUser.UnPushTagBindListener unPushTagBindListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.UNPUSH_ALIAS_BIND);
        String phoneType = "";
        String platform = "";
        switch (type) {
            case 0:
                phoneType = "个推";
                platform ="GETUI";
                break;
            case 1:
                phoneType = "小米";
                platform ="XIAOMI";
                break;
            case 2:
                phoneType = "华为";
                platform ="HUAWEI";
                break;
            case 3:
                phoneType = "FCM";
                platform ="FCM";
                break;
        }
        Log.i(TAG, phoneType + "调用绑定推送标签接口:" + clientId);
        JSONObject object = new JSONObject();
        object.put("clientId", clientId);
        object.put("pushPlatform", platform);
        postSiterData(url, object.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG, "推送解绑标签接口调用成功");
                unPushTagBindListener.unPushTagBindSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                Log.i(TAG, "推送解绑标签接口调用失败");
                unPushTagBindListener.unPushTagBindFail(errorCode);
            }
        });
    }




    /**
     * 4.7.2 列举群组
     */
    public void getGroup(final SiterUser.GetGroupListener getGroupListener) {
        getGroup(null, getGroupListener);
    }


    /**
     * 4.7.2 列举群组
     *
     * @param groupId 群组id
     */
    private void getGroup(String groupId, final SiterUser.GetGroupListener getGroupListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.UAA_GROUP).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put(groupId, groupId);
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                List<GroupBean> groupBeen = JSONArray.parseArray(object.toString(), GroupBean.class);
                getGroupListener.getGroupSuccess(groupBeen);
            }

            @Override
            public void getFail(int errorCode) {
                getGroupListener.getGroupFail(errorCode);
            }
        });
    }

    /**
     * 4.7.3 群组改名
     *
     * @param groupId                群组ID(必选)
     * @param newGroupName           群组新的名称(必选)
     * @param operationGroupListener 回调方法
     */
    private void renameGroup(@NotNull String groupId, @NotNull String newGroupName, final SiterUser.OperationGroupListener operationGroupListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.UAA_GROUP, "/", groupId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newGroupName", newGroupName);
        putSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                operationGroupListener.OperationSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                operationGroupListener.OperationGroupFail(errorCode);
            }
        });
    }


    /**
     * 4.7.4 删除群组
     *
     * @param groupId                群组ID(必选)
     * @param operationGroupListener 回调方法
     */
    private void deleteGroup(@NotNull String groupId, final SiterUser.OperationGroupListener operationGroupListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.UAA_GROUP, "?groupId=", groupId);
        deleteSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                operationGroupListener.OperationSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                operationGroupListener.OperationGroupFail(errorCode);
            }
        });
    }


    /**
     * 5.1 判断设备模块固件是否需要升级
     *
     * @param devTid           设备id
     * @param productPublicKey 产品公开码
     * @param binType          固件类型
     * @param binVer           固件版本
     */
    public void checkFirmwareUpdate(@NotNull String devTid, @NotNull String productPublicKey, @NotNull String binType, @NotNull String binVer, final SiterUser.CheckFwUpdateListener checkFwUpdateListener) {
        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("productPublicKey", productPublicKey);
        jsonObject.put("binType", binType);
        jsonObject.put("binVer", binVer);
        jsonArray.add(jsonObject);
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_CONSOLE_URL, Constants.UrlUtil.CHECK_FW_UPDATE);
        postSiterData(url, jsonArray.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    JSONArray jsonArray1 = JSONArray.parseArray(object.toString());
                    if (jsonArray1.isEmpty()) {
                        checkFwUpdateListener.checkNotNeedUpdate();
                    } else {
                        JSONObject jsonObject1 = jsonArray1.getJSONObject(0);
                        if (jsonObject1.getBoolean("update")) {
                            FirmwareBean firmwareBean = JSON.parseObject(jsonObject1.getString("devFirmwareOTARawRuleVO"), FirmwareBean.class);
                            checkFwUpdateListener.checkNeedUpdate(firmwareBean);
                        } else {
                            checkFwUpdateListener.checkNotNeedUpdate();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    checkFwUpdateListener.checkNotNeedUpdate();
                }
            }

            @Override
            public void getFail(int errorCode) {
                checkFwUpdateListener.checkFail(errorCode);
            }
        });
    }


    /**
     * 5.2 根据pid获取企业资讯
     */
    public void getNewsByPid(SiterUser.GetInfoListener getInfoListener) {
        getNewsByPid(null, null, getInfoListener);
    }

    /**
     * 5.2 根据pid获取企业资讯
     */
    public void getNewsByPid(int page, int size, SiterUser.GetInfoListener getInfoListener) {
        getNewsByPid(String.valueOf(page), String.valueOf(size), getInfoListener);
    }


    /**
     * 5.2 根据pid获取企业资讯
     */
    private void getNewsByPid(String page, String size, final SiterUser.GetInfoListener getInfoListener) {
        String url = TextUtils.concat(Constants.UrlUtil.BASE_CONSOLE_URL, "external/vc/getByPid").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("page", page);
        maps.put("size", size);
        url = CommonUtil.getUrl(url, maps);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getInfoListener.getInfoSuccess(JSON.parseObject(object.toString(), NewsBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getInfoListener.getInfoFail(errorCode);
            }
        });

    }

    /**
     * 5.5 售后管理 - 针对设备反馈问题
     *
     * @param content          反馈内容
     * @param feedbackListener 回调接口
     */
    public void feedback(@NotNull String content, String images, final SiterUser.FeedbackListener feedbackListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_CONSOLE_URL, "external/feedback");
        JSONObject jsonObject = new JSONObject();
        if (TextUtils.isEmpty(getUserCache().getEmail())) {
            jsonObject.put("UserNumber", getUserCache().getPhoneNumber());
        } else {
            jsonObject.put("UserNumber", getUserCache().getEmail());
        }
        jsonObject.put("title", "蜂鸟Android反馈");
        jsonObject.put("content", content);
        if (!TextUtils.isEmpty(images)) {
            jsonObject.put("images", images);
        }
        postSiterData(url, jsonObject.toJSONString(), new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                android.util.Log.d(TAG, "getSuccess: " + object.toString());
                feedbackListener.feedbackSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                feedbackListener.feedFail(errorCode);
            }
        });

    }

    /**
     * 5.10 获取默认演示设备
     */
    public void getDefaultStatic(final SiterUser.GetDefaultDevicesListener getDefaultDevices) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_CONSOLE_URL, Constants.UrlUtil.DEFAULT_STATIC);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                getDefaultDevices.getSuccess(JSON.parseArray(object.toString(), DefaultDeviceBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getDefaultDevices.getFail(errorCode);
            }
        });
    }

    /**
     * 获取pinCode4.1.9
     *
     * @param getPinCodeListener 回调接口
     */
    public void getPinCode(String ssid, final SiterUser.GetPinCodeListener getPinCodeListener) {
        CharSequence url = TextUtils.concat(Constants.UrlUtil.BASE_USER_URL, Constants.UrlUtil.GET_PIN_CODE, ssid);
        getSiterData(url, new GetDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    if (object != null && !TextUtils.isEmpty(object.toString())) {
                        String pinCode = JSONObject.parseObject(object.toString()).getString("PINCode");
                        if (!TextUtils.isEmpty(pinCode) && pinCode.length() == 6) {
                            getPinCodeListener.getSuccess(pinCode);
                        } else {
                            getPinCodeListener.getFailInSuccess();
                        }
                    } else {
                        getPinCodeListener.getFailInSuccess();
                    }
                } catch (JSONException e) {
                    getPinCodeListener.getFailInSuccess();
                }
            }

            @Override
            public void getFail(int errorCode) {
                getPinCodeListener.getFail(errorCode);
            }
        });
    }




    /**
     * 退出登录
     */
    public void userLogout() {
        //退出APP之后关闭所有的请求！
        BaseHttpUtil.getClient().cancelAllRequests(true);
        SpCache.clear();
        //清理掉后
        SpCache.putBoolean("pushTag", false);
        SpCache.putString(Constants.PUSH_GETUI_ID, "");
        SpCache.putString(Constants.MI_PUSH_CLIENT_ID, "");
        SpCache.putString(Constants.HUA_WEI_PUSH_CLIENT_ID, "");
    }

    /**
     * @return 返回用户token
     */
    public String getJWT_TOKEN() {
            return CacheUtil.getUserToken();
    }

    public String getRefresh_Token(){
        return CacheUtil.getRefreshToken();
    }

    /**
     * 将获取到的token保存下来
     */
    private void setTokenWIthCache(JWTBean jwtBean) {
        //把此token保存下来
        if (!TextUtils.isEmpty(jwtBean.getAccessToken())) {
            SpCache.putString(Constants.JWT_TOKEN, jwtBean.getAccessToken());
        }
        if (!TextUtils.isEmpty(jwtBean.getRefreshToken())) {
            SpCache.putString(Constants.REFRESH_TOKEN, jwtBean.getRefreshToken());
        }
    }


    /**
     * 将获取到的用户信息保存下来
     */
    private void setUserCache(String userInfo) {
        SpCache.putString("SITER_USER_INFO", userInfo);
    }

    /**
     * 拿到缓存到本地的用户档案
     *
     * @return 用户档案
     */
    public ProfileBean getUserCache() {
        ProfileBean profileBean = new ProfileBean();
        String var = SpCache.getString("SITER_USER_INFO", "");
        if (!TextUtils.isEmpty(var)) {
            profileBean = JSON.parseObject(var, ProfileBean.class);
        }
        return profileBean;
    }





    /**
     * HttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param GetDataListener 回调方法
     */
    public void getSiterData(CharSequence url, final GetDataListener GetDataListener) {
        getSiterData(url.toString(), GetDataListener);
    }


    /**
     * HttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param GetDataListener 回调方法
     */
    public void getSiterData(String url, final GetDataListener GetDataListener) {
        HttpUtil.getDataReFreshToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, null, new GetSiterData(GetDataListener));
    }


    /**
     * HttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param GetDataListener 回调方法
     */
    public void getSiterData(String url, Header[] headers, final GetDataListener GetDataListener) {
        HttpUtil.getDataReFreshToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, headers, new GetSiterData(GetDataListener));
    }


    /**
     * HttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void postSiterData(CharSequence url, String entity, final GetDataListener GetDataListener) {
        postSiterData(url.toString(), null, entity, GetDataListener);
    }

    /**
     * HttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void postSiterData(String url, String entity, final GetDataListener GetDataListener) {
        postSiterData(url, null, entity, GetDataListener);
    }


    /**
     * HttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void postSiterData(String url, Header[] headers, String entity, final GetDataListener GetDataListener) {
        HttpUtil.postDataReFreshToken(mContext.get(), getJWT_TOKEN(), getRefresh_Token(), url, headers, entity, new GetSiterData(GetDataListener));
    }


    /**
     * HttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void putSiterData(String url, String entity, final GetDataListener GetDataListener) {
        putSiterData(url, null, entity, GetDataListener);
    }


    /**
     * HttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void putSiterData(CharSequence url, String entity, final GetDataListener GetDataListener) {
        putSiterData(url.toString(), null, entity, GetDataListener);
    }

    /**
     * HttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void putSiterData(String url, Header[] headers, String entity, final GetDataListener GetDataListener) {
        HttpUtil.putDataRefreshToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, headers, entity, new GetSiterData(GetDataListener));
    }


    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param GetDataListener 回调
     */
    public void deleteSiterData(String url, final GetDataListener GetDataListener) {
        deleteSiterData(url, null, GetDataListener);
    }


    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param GetDataListener 回调
     */
    public void deleteSiterData(CharSequence url, final GetDataListener GetDataListener) {
        deleteSiterData(url.toString(), null, GetDataListener);
    }

    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param GetDataListener 回调
     */
    public void deleteSiterData(String url, Header[] headers, final GetDataListener GetDataListener) {
        HttpUtil.deleteDataReFreshToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, headers, new GetSiterData(GetDataListener));
    }


    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void patchSiterData(CharSequence url, String entity, final GetDataListener GetDataListener) {
        patchSiterData(url.toString(), null, entity, GetDataListener);
    }

    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void patchSiterData(String url, String entity, final GetDataListener GetDataListener) {
        patchSiterData(url, null, entity, GetDataListener);
    }

    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param entity              entity
     * @param GetDataListener 回调
     */
    public void patchSiterData(String url, Header[] headers, String entity, final GetDataListener GetDataListener) {
        HttpUtil.patchDataToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, headers, entity, new GetSiterData(GetDataListener));
    }

    /**
     * HttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param params              表单
     * @param GetDataListener 回调
     */
    public void postParamsSiterData(String url, RequestParams params, final GetDataListener GetDataListener) {
        HttpUtil.postFileReFreshToken(mContext.get(), getJWT_TOKEN(),  getRefresh_Token(), url, params, new GetSiterData(GetDataListener));
    }

    /**
     * 获取云端数据抽象类<br>
     * 获取数据成功/获取数据失败/进度显示
     */
    public static abstract class GetDataListener {

        public abstract void getSuccess(Object object);

        public abstract void getFail(int errorCode);

        public void getProgress(long bytesWritten, long totalSize) {

        }

    }


    private class GetSiterData extends GetSiterDataWithTokenListener {

        private GetDataListener getDataListener;


        public GetSiterData(GetDataListener GetDataListener) {
            this.getDataListener = GetDataListener;
        }

        @Override
        public void getDataSuccess(Object object) {
            getDataListener.getSuccess(object);
        }

        @Override
        public void getToken(JWTBean jwtBean) {
            //如果能够获取到新token，将新token直接保存下来
            setTokenWIthCache(jwtBean);
            android.util.Log.d(TAG, "新token: " + jwtBean.toString());
        }

        @Override
        public void getDataFail(int errorCode) {
            getDataListener.getFail(errorCode);
        }

        @Override
        public void getDataProgress(long bytesWritten, long totalSize) {
            super.getDataProgress(bytesWritten, totalSize);
           /* int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
            if (count > 100) {
                count = 100;
            }*/
            getDataListener.getProgress(bytesWritten, totalSize);
        }
    }

    /**
     * Created by TracyHenry on 2018/1/8.
     */

    public static class EmojiFilter {
        public static boolean containsEmoji(String source) {
            int len = source.length();
            boolean isEmoji = false;
            for (int i = 0; i < len; i++) {
                char hs = source.charAt(i);
                if (0xd800 <= hs && hs <= 0xdbff) {
                    if (source.length() > 1) {
                        char ls = source.charAt(i + 1);
                        int uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                        if (0x1d000 <= uc && uc <= 0x1f77f) {
                            return true;
                        }
                    }
                } else {
                    // non surrogate
                    if (0x2100 <= hs && hs <= 0x27ff && hs != 0x263b) {
                        return true;
                    } else if (0x2B05 <= hs && hs <= 0x2b07) {
                        return true;
                    } else if (0x2934 <= hs && hs <= 0x2935) {
                        return true;
                    } else if (0x3297 <= hs && hs <= 0x3299) {
                        return true;
                    } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d
                            || hs == 0x3030 || hs == 0x2b55 || hs == 0x2b1c
                            || hs == 0x2b1b || hs == 0x2b50 || hs == 0x231a) {
                        return true;
                    }
                    if (!isEmoji && source.length() > 1 && i < source.length() - 1) {
                        char ls = source.charAt(i + 1);
                        if (ls == 0x20e3) {
                            return true;
                        }
                    }
                }
            }
            return isEmoji;
        }


        private static boolean isEmojiCharacter(char codePoint) {
            return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                    || (codePoint == 0xD)
                    || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                    || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                    || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
        }
    }
}

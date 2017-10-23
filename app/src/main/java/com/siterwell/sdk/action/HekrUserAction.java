package com.siterwell.sdk.action;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.android.log.Log;
import com.litesuits.common.assist.Base64;
import com.litesuits.common.utils.MD5Util;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.siterwell.sdk.bean.AirQualityBean;
import com.siterwell.sdk.bean.BindDeviceBean;
import com.siterwell.sdk.bean.DefaultDeviceBean;
import com.siterwell.sdk.bean.DeviceBean;
import com.siterwell.sdk.bean.DeviceStatusBean;
import com.siterwell.sdk.bean.FileBean;
import com.siterwell.sdk.bean.FirmwareBean;
import com.siterwell.sdk.bean.FolderBean;
import com.siterwell.sdk.bean.FolderListBean;
import com.siterwell.sdk.bean.Global;
import com.siterwell.sdk.bean.GroupBean;
import com.siterwell.sdk.bean.JWTBean;
import com.siterwell.sdk.bean.MOAuthBean;
import com.siterwell.sdk.bean.NewDeviceBean;
import com.siterwell.sdk.bean.NewsBean;
import com.siterwell.sdk.bean.OAuthBean;
import com.siterwell.sdk.bean.OAuthListBean;
import com.siterwell.sdk.bean.OAuthRequestBean;
import com.siterwell.sdk.bean.ProfileBean;
import com.siterwell.sdk.bean.RuleBean;
import com.siterwell.sdk.bean.UserBean;
import com.siterwell.sdk.bean.UserFileBean;
import com.siterwell.sdk.bean.WeatherAirBean;
import com.siterwell.sdk.bean.WeatherBeanResultsNow;
import com.siterwell.sdk.event.WsSwitchEvent;
import com.siterwell.sdk.service.HekrCoreService;
import com.siterwell.sdk.util.BaseHttpUtil;
import com.siterwell.sdk.util.ConstantsUtil;
import com.siterwell.sdk.util.DevicesCacheUtil;
import com.siterwell.sdk.util.HekrCodeUtil;
import com.siterwell.sdk.util.HekrCommonUtil;
import com.siterwell.sdk.util.HekrHttpUtil;
import com.siterwell.sdk.util.HekrSDK;
import com.siterwell.sdk.util.SpCache;
import com.siterwell.siterapp.R;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2017/10/16.
 */

public class HekrUserAction {
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
    private static final String TAG = "HekrUserAction";

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

    private static int startWebServicesFlag = 0;


    private WeakReference<Context> mContext;
    private String JWT_TOKEN = null;
    private String refresh_TOKEN = null;
    private String userId = null;
    private static volatile HekrUserAction instance = null;
    private static String pid;


    public static HekrUserAction getInstance(Context context) {
        if (instance == null) {
            synchronized (HekrUserAction.class) {
                if (instance == null) {
                    instance = new HekrUserAction(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    private HekrUserAction(Context context) {
        SpCache.init(context.getApplicationContext());
        mContext = new WeakReference<>(context.getApplicationContext());
        startWebServicesFlag = 1;
        //初始化的时候先读取token
        JWT_TOKEN = SpCache.getString(ConstantsUtil.JWT_TOKEN, "");
        refresh_TOKEN = SpCache.getString(ConstantsUtil.REFRESH_TOKEN, "");
        String sp_pid = SpCache.getString(ConstantsUtil.HEKR_PID, HekrSDK.pid);
        if (TextUtils.isEmpty(sp_pid)) {
            if (HekrSDK.isHekrInited) {
                throw new NullPointerException(ConstantsUtil.ERROR_PID);
            } else {
                throw new NullPointerException(ConstantsUtil.SDK_INIT_ERROR);
            }
        } else {
            pid = sp_pid;
        }
        userId = TokenToUid();
        Log.i(TAG,"userId+++++++++++++++++++++++++++++++++++"+userId);
        //读取到pid后再启动service
        context.startService(new Intent(mContext.get(), HekrCoreService.class));
        //判断是线上还是测试环境
        ConstantsUtil.setDebugSite(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("debug_site", false));
    }

    /**
     * 3.18 获取图形验证码
     *
     * @param rid                   长度大于16，不能含有空格 验证码key
     * @param getImgCaptchaListener 回调接口
     */
    public void getImgCaptcha(@NotNull String rid, final HekrUser.GetImgCaptchaListener getImgCaptchaListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "images/getImgCaptcha?rid=", rid).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (bytes != null && bytes.length > 0) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    getImgCaptchaListener.getImgCaptchaSuccess(bitmap);
                } else {
                    getImgCaptchaListener.getImgCaptchaFail(HekrCodeUtil.getErrorCode(i, bytes));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                getImgCaptchaListener.getImgCaptchaFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.19 校验图形验证码
     *
     * @param code 验证码的值
     * @param rid  验证码key
     */
    public void checkCaptcha(@NotNull String code, @NotNull String rid, final HekrUser.CheckCaptcha checkCaptcha) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "images/checkCaptcha").toString();
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
                checkCaptcha.checkCaptchaFail(HekrCodeUtil.getErrorCode(i, bytes));
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
    public void getVerifyCode(String phoneNumber, int type, final HekrUser.GetVerifyCodeListener getVerifyCodeListener) {
        getVerifyCode(phoneNumber, type, "", getVerifyCodeListener);
    }


    /**
     * 3.1 发送短信验证码
     *
     * @param phoneNumber           手机号码
     * @param type                  验证码用途
     * @param token                 校验图形验证码返回的token(发送手机短信校验码 接口中设备白名单过滤，如果pid在白名单中，访问改接口时，不需要带token 信息。否则访问时必须带token参数)
     * @param getVerifyCodeListener 获取验证码回调
     */
    public void getVerifyCode(String phoneNumber, int type, String token, final HekrUser.GetVerifyCodeListener getVerifyCodeListener) {
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
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_GET_CODE_URL).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("phoneNumber", phoneNumber);
        maps.put("pid", pid);
        maps.put("token", token);
        maps.put("type", registerType);
        url = HekrCommonUtil.getUrl(url, maps);
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        //获取成功
                        getVerifyCodeListener.getVerifyCodeSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        getVerifyCodeListener.getVerifyCodeFail(HekrCodeUtil.getErrorCode(i, bytes));
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
    public void checkVerifyCode(String phoneNumber, String code, final HekrUser.CheckVerifyCodeListener checkVerifyCodeListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_CHECK_CODE_URL, phoneNumber, "&code=", code).toString();
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
                checkVerifyCodeListener.checkVerifyCodeFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.3 使用手机号注册用户
     *
     * @param password         用户的密码
     * @param phoneNumber      用户注册手机号
     * @param token            校验验证码返回的注册TokenToken
     * @param registerListener 注册接口
     */
    public void registerByPhone(String phoneNumber, String password, String token, final HekrUser.RegisterListener registerListener) {
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
        jsonObject.put("pid", pid);
        jsonObject.put("password", password);
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("token", token);
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_REGISTER_URL, "phone").toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                registerListener.registerSuccess(JSON.parseObject(new String(bytes)).getString("uid"));
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                registerListener.registerFail(HekrCodeUtil.getErrorCode(i, bytes));
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
    public void registerByEmail(String email, String password, final HekrUser.RegisterListener registerListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", pid);
        jsonObject.put("password", password);
        jsonObject.put("email", email);
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_REGISTER_URL, "email").toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        registerListener.registerSuccess(JSON.parseObject(new String(bytes)).getString("uid"));
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        registerListener.registerFail(HekrCodeUtil.getErrorCode(i, bytes));
                    }
                }

        );
    }


    /**
     * 3.5 用户登录
     *
     * @param userName      用户名
     * @param passWord      密码
     * @param loginListener 回调接口
     */

    public void login(final String userName, final String passWord, final HekrUser.LoginListener loginListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", userName);
        jsonObject.put("password", passWord);
        jsonObject.put("pid", pid);
        jsonObject.put("clientType", "Android");
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_LOGIN_URL).toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JWTBean jwtBean = JSON.parseObject(new String(bytes), JWTBean.class);
                UserBean userBean = new UserBean(pid, userName, passWord, jwtBean.getAccessToken(), jwtBean.getRefreshToken());
                //把相关的用户信息保存下来
                setUserCache(userBean);
                //执行登录
                loginListener.loginSuccess(new String(bytes));
                //启动服务
                connectWsServices();
                android.util.Log.e("xxxx",jwtBean.getAccessToken());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                loginListener.loginFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 3.6 使用手机号重置密码
     *
     * @param phoneNumber      手机号码
     * @param verifyCode       验证码
     * @param password         密码
     * @param resetPwdListener 回调
     */
    public void resetPwd(String phoneNumber, String verifyCode, String password, final HekrUser.ResetPwdListener resetPwdListener) {
        _resetPwd(phoneNumber, verifyCode, null, password, resetPwdListener);
    }

    /**
     * 通过密保问题重置密码
     *
     * @param token            验证密保时返回的token
     * @param password         用户新密码
     * @param resetPwdListener 回调
     */
    public void resetPwdBySecurity(String token, String password, final HekrUser.ResetPwdListener resetPwdListener) {
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
    private void _resetPwd(String phoneNumber, String verifyCode, String token, String password, final HekrUser.ResetPwdListener resetPwdListener) {
        String type = TextUtils.isEmpty(phoneNumber) ? "security" : "phone";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", pid);
        jsonObject.put("password", password);
        if (!TextUtils.isEmpty(phoneNumber)) {
            jsonObject.put("phoneNumber", phoneNumber);
            jsonObject.put("verifyCode", verifyCode);
        } else if (!TextUtils.isEmpty(token)) {
            jsonObject.put("token", token);
        } else {
            Log.e(TAG, "_resetPwd: 重置密码，参数错误");
            return;
        }
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_RESET_PWD_URL, type).toString();
        BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        resetPwdListener.resetSuccess();
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        resetPwdListener.resetFail(HekrCodeUtil.getErrorCode(i, bytes));
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
    public void changePassword(String newPassword, String oldPassword, final HekrUser.ChangePwdListener changePwdListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", pid);
        jsonObject.put("newPassword", newPassword);
        jsonObject.put("oldPassword", oldPassword);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_CHANGR_PWD_URL);
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
     * @param token                     token(需要调用发送短信验证码接口给老手机号发送验证码{@link #getVerifyCode},类型为{@link #CODE_TYPE_CHANGE_PHONE}，并调用校验短信验证码接口{@link #checkVerifyCode(String, String, HekrUser.CheckVerifyCodeListener)}成功时获取。)
     * @param verifyCode                验证码(需要调用发送短信验证码接口给新手机号phoneNumber发送验证码获取{@link #getVerifyCode},类型为{@link #CODE_TYPE_REGISTER})
     * @param phoneNumber               用户新手机号码
     * @param changePhoneNumberListener 回调接口
     */
    public void changePhoneNumber(String token, String verifyCode, String phoneNumber, final HekrUser.ChangePhoneNumberListener changePhoneNumberListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pid", pid);
        jsonObject.put("token", token);
        jsonObject.put("verifyCode", verifyCode);
        jsonObject.put("phoneNumber", phoneNumber);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_CHANGE_PHONE_URL);
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void sendResetPwdEmail(String email, final HekrUser.SendResetPwdEmailListener sendResetPasswordEmailListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "sendResetPasswordEmail?email=", HekrCommonUtil.getEmail(email), "&pid=", pid).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                sendResetPasswordEmailListener.sendSuccess();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                sendResetPasswordEmailListener.sendFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }


    /**
     * 3.10 重新发送确认邮件
     *
     * @param email               邮箱
     * @param reSendVerifiedEmail 回调接口
     */
    public void reSendVerifiedEmail(@NotNull String email, final HekrUser.ReSendVerifiedEmailListener reSendVerifiedEmail) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "resendVerifiedEmail?email=", HekrCommonUtil.getEmail(email), "&pid=", pid).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                reSendVerifiedEmail.reSendVerifiedEmailSuccess();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                reSendVerifiedEmail.reSendVerifiedEmailFail(HekrCodeUtil.getErrorCode(i, bytes));
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
    public void sendChangeEmailStep1Email(@NotNull String email, final HekrUser.SendChangeEmailListener sendChangeEmailListener) {
        //http://uaa.openapi.hekr.me/sendChangeEmailStep1Email?email=test@hekr.me&pid=01698862200
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_SEND_CHANGE_EMAIL, HekrCommonUtil.getEmail(email), "&pid=", pid);
        getHekrData(url, new GetHekrDataListener() {
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
     * 3.12 刷新Access Token
     */
    public void refresh_token() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("refresh_token", getRefreshToken());
        if (!TextUtils.isEmpty(getRefreshToken())) {
            String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.UAA_REFRESH_TOKEN).toString();
            BaseHttpUtil.postData(mContext.get(), url, jsonObject.toString(), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    JWTBean jwtBean = JSON.parseObject(new String(bytes), JWTBean.class);
                    setTokenWIthCache(jwtBean);
                    Log.d(TAG, "Token刷新成功 ");
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e(ConstantsUtil.HEKR_SDK_ERROR, HekrCodeUtil.errorCode2Msg(HekrCodeUtil.getErrorCode(i, bytes)));
                }
            });
        } else {
            Log.e(ConstantsUtil.HEKR_SDK_ERROR, "Token is null");
        }
    }

    /**
     * 3.13 移动端OAuth/第三方登录
     *
     * @param type           OOGLE, WECHAT, QQ, SINA, FACEBOOK, TWITTER	OAuth账号类型
     * @param certificate    移动端OAuth之后返回的code，或者Twitter返回的access_token
     * @param moAuthListener 回调接口
     */
    public void OAuthLogin(int type, @NotNull String certificate, final HekrUser.MOAuthListener moAuthListener) {
        OAuthLogin(type, certificate, true, moAuthListener);
    }


    /**
     * 3.13 移动端OAuth/第三方绑定
     *
     * @param type           OOGLE, WECHAT, QQ, SINA, FACEBOOK, TWITTER	OAuth账号类型
     * @param certificate    移动端OAuth之后返回的code，或者Twitter返回的access_token
     * @param isOAuthLogin   如果使用第三方登录，此参数为true【第三方绑定，此参数为false】
     * @param moAuthListener 回调接口
     */
    public void OAuthLogin(int type, @NotNull String certificate, final boolean isOAuthLogin, final HekrUser.MOAuthListener moAuthListener) {
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
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "MOAuth?type=", auth_type, "&pid=", pid, "&clientType=ANDROID&certificate=", certificate).toString();
        BaseHttpUtil.getData(mContext.get(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject jsonObject = JSONObject.parseObject(new String(bytes));
                if (jsonObject.containsKey("uid")) {
                    MOAuthBean moAuthBean = JSONObject.parseObject(new String(bytes), MOAuthBean.class);
                    moAuthListener.mOAuthSuccess(moAuthBean);
                } else {
                    JWTBean jwtBean = JSONObject.parseObject(new String(bytes), JWTBean.class);
                    UserBean userBean = new UserBean(pid, "", "", jwtBean.getAccessToken(), jwtBean.getRefreshToken());
                    if (isOAuthLogin) {
                        //把相关的用户信息保存下来
                        setUserCache(userBean);
                        //启动服务
                        connectWsServices();
                    }
                    moAuthListener.mOAuthSuccess(jwtBean);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                moAuthListener.mOAuthFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });

    }


    /**
     * 3.14 将OAuth账号和主账号绑定
     *
     * @param token             绑定token
     * @param bindOAuthListener 绑定接口。使用此接口之前必须登录！
     */
    public void bindOAuth(@NotNull String token, final HekrUser.BindOAuthListener bindOAuthListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "account/bind?token=", token, "&pid=", pid);
        getHekrData(url, new GetHekrDataListener() {
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
    public void unbindOAuth(int type, final HekrUser.BindOAuthListener bindOAuthListener) {
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
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "account/unbind?type=", auth_type, "&pid=", pid);
        getHekrData(url, new GetHekrDataListener() {
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
     * 3.16 移动端使用微信第三方账号登录
     *
     * @param certificate 移动端OAuth之后返回的code
     */
    public void weChatMOAuth(String certificate, final HekrUser.LoginListener loginListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "weChatMOAuth?type=WECHAT&pid=", pid, "&clientType=Android&certificate=", certificate);
        BaseHttpUtil.getData(mContext.get(), url.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JWTBean jwtBean = JSON.parseObject(new String(bytes), JWTBean.class);
                UserBean userBean = new UserBean(pid, "", "", jwtBean.getAccessToken(), jwtBean.getRefreshToken());
                //把相关的用户信息保存下来
                setUserCache(userBean);
                //执行登录
                loginListener.loginSuccess(new String(bytes));
                //启动服务
                connectWsServices();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                loginListener.loginFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 3.17 创建匿名Hekr主账户并与当前登录三方账户绑定
     *
     * @param type                      第三方登录类型
     * @param token                     token参数是调用3.13 移动端OAuth接口，当OAuth账号和主账号没绑定时返回里的bindToken
     * @param createUserAndBindListener 回调
     */
    public void createUserAndBind(int type, @NotNull String token, final HekrUser.CreateUserAndBindListener createUserAndBindListener) {
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
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "account/createUserAndBind?token=", token, "&pid=", pid, "&type=", auth_type, "&clientType=ANDROID");
        BaseHttpUtil.getData(mContext.get(), url.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JWTBean jwtBean = JSON.parseObject(new String(bytes), JWTBean.class);
                UserBean userBean = new UserBean(pid, "", "", jwtBean.getAccessToken(), jwtBean.getRefreshToken());
                //把相关的用户信息保存下来
                setUserCache(userBean);
                //执行登录
                createUserAndBindListener.createSuccess(new String(bytes));
                //启动服务
                connectWsServices();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                createUserAndBindListener.createFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 通过手机将第三方账号升级为hekr主账号
     *
     * @param phoneNumber 用户手机号
     * @param password    用户密码
     * @param verifyCode  手机验证码
     * @param token       3.2返回里的token{@link #checkVerifyCode(String, String, HekrUser.CheckVerifyCodeListener)}
     */
    public void accountUpgrade(@NotNull String phoneNumber, @NotNull String password, @NotNull String verifyCode, @NotNull String token, final HekrUser.AccountUpgradeListener upgradeListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("password", password);
        jsonObject.put("token", token);
        jsonObject.put("verifyCode", verifyCode);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.ACCOUNT_UPGRADE);
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void accountUpgradeByEmail(@NotNull String email, @NotNull String password, final HekrUser.SendEmailListener sendEmailListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("email", email);
        jsonObject.put("password", password);
        jsonObject.put("from", "uaa");
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, ConstantsUtil.UrlUtil.SEND_EMAIL);
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void setSecurityQuestion(@NotNull String firstSecurityQues, @NotNull String secondSecurityQues, @NotNull String thirdSecurityQues, final HekrUser.SetSecurityQuestionListener setListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstSecurityQues", firstSecurityQues);
        jsonObject.put("secondSecurityQues", secondSecurityQues);
        jsonObject.put("thirdSecurityQues", thirdSecurityQues);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "setSecurityQuestion");
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void checkSecurityQuestion(@NotNull String firstSecurityQues, @NotNull String secondSecurityQues, @NotNull String thirdSecurityQues, String phoneNumber, final HekrUser.CheckVerifyCodeListener checkVerifyCodeListener) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("firstSecurityQues", firstSecurityQues);
        jsonObject.put("secondSecurityQues", secondSecurityQues);
        jsonObject.put("thirdSecurityQues", thirdSecurityQues);
        jsonObject.put("phoneNumber", phoneNumber);
        jsonObject.put("pid", pid);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "sms/checkSecurityQuestion");
        BaseHttpUtil.postData(mContext.get(), url.toString(), jsonObject.toJSONString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                JSONObject checkObject = JSON.parseObject(new String(bytes));
                //获取成功
                checkVerifyCodeListener.checkVerifyCodeSuccess(checkObject.get("phoneNumber").toString(), "", checkObject.get("token").toString(), checkObject.get("expireTime").toString());
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                checkVerifyCodeListener.checkVerifyCodeFail(HekrCodeUtil.getErrorCode(i, bytes));

            }
        });
    }

    /**
     * 获取用户是否设置了密保问题
     *
     * @param phoneNumber 用户手机号
     * @param is          回调
     */
    public void isSecurityAccount(@NotNull String phoneNumber, final HekrUser.IsSecurityAccountListener is) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_UAA_URL, "isSecurityAccount?phoneNumber=", phoneNumber, "&pid=", pid);
        BaseHttpUtil.getData(mContext.get(), url.toString(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                boolean isSecurityAccount = JSONObject.parseObject(new String(bytes)).getBoolean("result");
                is.checkSuccess(isSecurityAccount);
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                is.checkFail(HekrCodeUtil.getErrorCode(i, bytes));
            }
        });
    }

    /**
     * 4.1.1 绑定设备
     *
     * @param bindDeviceBean     绑定设备Bean
     * @param bindDeviceListener 回调接口
     */
    public void bindDevice(BindDeviceBean bindDeviceBean, final HekrUser.BindDeviceListener bindDeviceListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.BIND_DEVICE);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", bindDeviceBean.getDevTid());
        jsonObject.put("bindKey", bindDeviceBean.getBindKey());
        jsonObject.put("deviceName", bindDeviceBean.getDeviceName());
        jsonObject.put("desc", bindDeviceBean.getDesc());

        postHekrData(url, jsonObject.toString(), new GetHekrDataListener() {
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
    public void getDevices(HekrUser.GetDevicesListener getDevicesListener) {
        getDevices(0, 20, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(String devTid, HekrUser.GetDevicesListener getDevicesListener) {
        getDevices(0, 20, devTid, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(int page, int size, HekrUser.GetDevicesListener getDevicesListener) {

        getDevices(page, size, null, getDevicesListener);
    }


    /**
     * 4.1.2 列举设备列表
     *
     * @param getDevicesListener 回调接口
     */
    public void getDevices(int page, int size, String devTid, final HekrUser.GetDevicesListener getDevicesListener) {
        //CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.BIND_DEVICE, "?page=", String.valueOf(page), "&size=", String.valueOf(size));
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.BIND_DEVICE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("devTid", devTid);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = HekrCommonUtil.getUrl(url, maps);

        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                List<DeviceBean> lists = JSON.parseArray(object.toString(), DeviceBean.class);
                getDevicesListener.getDevicesSuccess(lists);
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
    public void deleteDevice(@NotNull String devTid, @NotNull String bindKey, final HekrUser.DeleteDeviceListener deleteDevices) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.BIND_DEVICE, "/", devTid, "?bindKey=", bindKey);
        //http://user.openapi.hekr.me/device/{devTid}?bindKey={bindKey};
        deleteHekrData(url, new GetHekrDataListener() {
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
    public void renameDevice(@NotNull String devTid, @NotNull String ctrlKey, @NotNull String deviceName, String desc, final HekrUser.RenameDeviceListener renameDeviceListener) {
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
    public void renameDevice(@NotNull String devTid, String subDevTid, @NotNull String ctrlKey, @NotNull String deviceName, String desc, final HekrUser.RenameDeviceListener renameDeviceListener) {
        // "http://user.openapi.hekr.me/device/{devTid}"
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.BIND_DEVICE, "/", devTid);
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
        patchHekrData(url, jsonObject.toString(), new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                renameDeviceListener.renameDeviceSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                renameDeviceListener.renameDeviceFail(errorCode);
            }
        });
    }


    /**
     * 4.1.5 获取当前局域网内所有设备绑定状态<br>
     * 只返回正确的devTid/bindKey对应的设备绑定状态，所以返回里的元素数量会少于提交里的元素数量。<br>
     * 后续操作按照4.1.1执行
     * {@link #bindDevice(BindDeviceBean, HekrUser.BindDeviceListener)}
     *
     * @param devTid                设备ID
     * @param bindKey               绑定码
     * @param getBindStatusListener 回调接口
     */
    public void deviceBindStatus(String devTid, String bindKey, final HekrUser.GetBindStatusListener getBindStatusListener) {
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
     * {@link #bindDevice(BindDeviceBean, HekrUser.BindDeviceListener)}
     *
     * @param array                 [ {"bindKey" : "xxxxx", "devTid" : "ESP_test"},... }]
     * @param getBindStatusListener 回调接口{@link HekrUser.GetBindStatusListener}
     */
    public void deviceBindStatus(JSONArray array, final HekrUser.GetBindStatusListener getBindStatusListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.DEVICE_BIND_STATUS);
        postHekrData(url, array.toString(), new GetHekrDataListener() {
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
     * 如果可以绑定直接就进行调用4.1.1接口{@link #bindDevice(BindDeviceBean, HekrUser.BindDeviceListener)}进行绑定操作;<br>
     *
     * @param devTid                       设备ID
     * @param bindKey                      绑定码
     * @param getBindStatusAndBindListener 回调接口 {@link HekrUser.GetBindStatusAndBindListener}
     */
    public void deviceBindStatusAndBind(final String devTid, final String bindKey, final HekrUser.GetBindStatusAndBindListener getBindStatusAndBindListener) {
        try {
            final JSONObject obj = new JSONObject();
            obj.put("devTid", devTid);
            obj.put("bindKey", bindKey);
            JSONArray array = new JSONArray();
            array.add(obj);
            deviceBindStatus(array, new HekrUser.GetBindStatusListener() {
                @Override
                public void getStatusSuccess(List<DeviceStatusBean> deviceStatusBeanLists) {
                    //直接进行绑定操作
                    if (deviceStatusBeanLists != null && !deviceStatusBeanLists.isEmpty()) {
                        //成功后回调
                        getBindStatusAndBindListener.getStatusSuccess(deviceStatusBeanLists);
                        DeviceStatusBean deviceStatusBean = deviceStatusBeanLists.get(0);
                        if (deviceStatusBean.isForceBind() || !deviceStatusBean.isBindToUser()) {
                            String name = (deviceStatusBean.getCidName().substring(deviceStatusBean.getCidName().indexOf("/") + 1));
                            BindDeviceBean bindDeviceBean = new BindDeviceBean(devTid, bindKey, name, mContext.get().getString(R.string.app_name));
                            bindDevice(bindDeviceBean, new HekrUser.BindDeviceListener() {
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
    public void queryOwner(String devTid, String bindKey, final HekrUser.GetQueryOwnerListener getQueryOwnerListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "queryOwner");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("bindKey", bindKey);
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void getNewDevices(String pinCode, String ssid, final HekrUser.GetNewDevicesListener getNewDevicesListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.GET_NEW_DEVICE, pinCode, "&ssid=", ssid);
        getHekrData(url, new GetHekrDataListener() {
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
     * @param addFolderListener 回调接口{@link HekrUser.AddFolderListener}
     */
    public void addFolder(@NotNull String folderName, final HekrUser.AddFolderListener addFolderListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER);
        JSONObject obj = new JSONObject();
        obj.put("folderName", folderName);
        postHekrData(url, obj.toString(), new GetHekrDataListener() {
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
    public void getFolder(int page, final HekrUser.GetFolderListsListener getFolderListsListener) {
        //http://user.openapi.hekr.me/folder?folderId=xxx,xxx1&page=1&size=1
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER, "?page=", String.valueOf(page));
        getHekrData(url, new GetHekrDataListener() {
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
    public void renameFolder(String newFolderName, String folderId, final HekrUser.RenameFolderListener renameFolderListener) {
        JSONObject object = new JSONObject();
        object.put("newFolderName", newFolderName);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER, "/", folderId);

        putHekrData(url, object.toJSONString(), new GetHekrDataListener() {
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
    public void deleteFolder(final HekrUser.DeleteFolderListener deleteFolderListener) {
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
    public void deleteFolder(String folderId, final HekrUser.DeleteFolderListener deleteFolderListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER);
        if (!TextUtils.isEmpty(folderId)) {
            url = TextUtils.concat(url, "/", folderId);
        }
        deleteHekrData(url, new GetHekrDataListener() {
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
    public void devicesPutFolder(String folderId, String ctrlKey, String devTid, final HekrUser.DevicePutFolderListener devicePutFolderListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER, "/", folderId);
        JSONObject obj = new JSONObject();
        obj.put("devTid", devTid);
        obj.put("ctrlKey", ctrlKey);
        postHekrData(url, obj.toJSONString(), new GetHekrDataListener() {
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
    public void folderToRoot(String folderId, String ctrlKey, String devTid, final HekrUser.DevicePutFolderListener devicePutFolderListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.FOLDER, "/", folderId, "/", devTid, "?", ConstantsUtil.UrlUtil.CTRL_KEY, ctrlKey);
        deleteHekrData(url, new GetHekrDataListener() {
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
    public void oAuthCreateCode(OAuthBean oAuthBean, final HekrUser.CreateOAuthQRCodeListener createOAuthQRCodeListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_AUTH_URL);
        postHekrData(url, JSON.toJSONString(oAuthBean), new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                String reverseAuthorizationTemplateId = JSON.parseObject(object.toString()).getString("reverseAuthorizationTemplateId");
                createOAuthQRCodeListener.createSuccess(TextUtils.concat(ConstantsUtil.UrlUtil.OAUTH_URL, reverseAuthorizationTemplateId).toString());
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
    public void registerAuth(String reverseAuthorizationTemplateId, final HekrUser.RegisterOAuthQRCodeListener registerOAuthQRCodeListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, ConstantsUtil.UrlUtil.REVERSE_TEMPLATE_ID, reverseAuthorizationTemplateId);
        postHekrData(url, null, new GetHekrDataListener() {
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
    public void getOAuthInfoRequest(String devTid, int page, int size, String reverseRegisterId, final HekrUser.GetOauthInfoListener getOauthInfoListener) {
        //CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, "?page=", String.valueOf(page), "&size=", String.valueOf(size));
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_REGISTER).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("devTid", devTid);
        maps.put("reverseRegisterId", reverseRegisterId);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
     * @param reverseRegisterId  必选 通过4.3.2-3{@link #getOAuthInfoRequest(String, int, int, String, HekrUser.GetOauthInfoListener)}接口拿到的数据
     * @param agreeOauthListener 回调接口
     */
    public void agreeOAuth(String devTid, String ctrlKey, String reverseRegisterId, final HekrUser.AgreeOauthListener agreeOauthListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_DEV_TID, devTid, "&", ConstantsUtil.UrlUtil.CTRL_KEY, ctrlKey, ConstantsUtil.UrlUtil.REVERSE_REGISTER_ID, reverseRegisterId);
        postHekrData(url, null, new GetHekrDataListener() {
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
     * @param grantee             必选 通过4.3.2-3接口{@link #getOAuthInfoRequest(String, int, int, String, HekrUser.GetOauthInfoListener)}拿到的数据
     * @param reverseRegisterId   必选 通过4.3.2-3接口{@link #getOAuthInfoRequest(String, int, int, String, HekrUser.GetOauthInfoListener)}拿到的数据！
     * @param refuseOAuthListener 回调接口
     */
    public void refuseOAuth(@NotNull String devTid, @NotNull String ctrlKey, @NotNull String grantee, @NotNull String reverseRegisterId, final HekrUser.RefuseOAuthListener refuseOAuthListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_REVERSE_REGISTER, "/", reverseRegisterId, "?", ConstantsUtil.UrlUtil.DEV_TID,
                devTid, "&uid=", grantee, "&", ConstantsUtil.UrlUtil.CTRL_KEY, ctrlKey);
        deleteHekrData(url, new GetHekrDataListener() {
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
     * @param grantor 授权用户uid
     * @param ctrlKey 被授权用户uid，多个使用逗号分隔；当不提交该参数时，表示授权者删除该设备上对所有被授权者的授权关系
     * @param grantee 控制码
     * @param devTid  设备ID
     */
    public void cancelOAuth(@NotNull String grantor, String ctrlKey, String grantee, String devTid, final HekrUser.CancelOAuthListener cancelOAuthListener) {
        //CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_GRANTOR, grantor, "&", ConstantsUtil.UrlUtil.CTRL_KEY, ctrlKey, "&", ConstantsUtil.UrlUtil.GRANTEE, grantee, "&", ConstantsUtil.UrlUtil.DEV_TID, devTid);
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "authorization").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("grantor", grantor);
        maps.put("ctrlKey", ctrlKey);
        maps.put("grantee", grantee);
        maps.put("devTid", devTid);
        url = HekrCommonUtil.getUrl(url, maps);

        deleteHekrData(url, new GetHekrDataListener() {
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
    public void getOAuthList(String grantor, String ctrlKey, String devTid, String grantee, final HekrUser.GetOAuthListener getOAuthListener) {
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
    public void getOAuthList(String grantor, String ctrlKey, String devTid, final HekrUser.GetOAuthListener getOAuthListener) {
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
    private void _getOAuthList(String grantor, String ctrlKey, String devTid, String grantee, final HekrUser.GetOAuthListener getOAuthListener) {
        //CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.AUTHORIZATION_GRANTOR, grantor, "&", ConstantsUtil.UrlUtil.CTRL_KEY, ctrlKey, "&", ConstantsUtil.UrlUtil.DEV_TID, devTid);
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "authorization").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("grantor", grantor);
        maps.put("ctrlKey", ctrlKey);
        maps.put("grantee", grantee);
        maps.put("devTid", devTid);
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
    public void creatRule(RuleBean ruleBean, final HekrUser.CreateRuleListener createRuleListener) {
        postHekrData(TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.CREATE_RULE), JSON.toJSONString(ruleBean), new GetHekrDataListener() {
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
    public void getRules(String devTid, String ctrlKey, String taskId, final HekrUser.GetRulesListener getRulesListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
    public void editRule(String devTid, String ctrlKey, @NotNull String taskId, RuleBean ruleBean, final HekrUser.OperationRuleListener operationRuleListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = HekrCommonUtil.getUrl(url, maps);
        putHekrData(url, ruleBean.toString(), new GetHekrDataListener() {
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
    public void deleteRules(String devTid, String ctrlKey, String taskId, final HekrUser.OperationRuleListener operationRuleListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.CREATE_RULE).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("ctrlKey", ctrlKey);
        maps.put("devTid", devTid);
        maps.put("taskId", taskId);
        url = HekrCommonUtil.getUrl(url, maps);
        deleteHekrData(url, new GetHekrDataListener() {
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
    public void getProfile(final HekrUser.GetProfileListener getProfileListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.PROFILE);
        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                ProfileBean profileBean = JSONObject.parseObject(object.toString(), ProfileBean.class);
                setUserCache(object.toString());
                getProfileListener.getProfileSuccess(profileBean);
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
     * @param jsonObject         用户json，参考文档docs4.5.2 http://docs.hekr.me/v4/developerGuide/openapi/#452
     * @param setProfileListener 回调
     */
    public void setProfile(@NotNull final JSONObject jsonObject, final HekrUser.SetProfileListener setProfileListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.PROFILE);
        putHekrData(url, jsonObject.toString(), new GetHekrDataListener() {
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
    public void uploadFile(@NotNull final String uri, final HekrUser.UploadFileListener uploadFileListener) throws FileNotFoundException {
        File file = new File(uri);
        RequestParams params = new RequestParams();
        params.put("file", file, "image/png", System.currentTimeMillis() + ".png");
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.USER_FILE);
        postParamsHekrData(url.toString(), params, new GetHekrDataListener() {
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
                uploadFileListener.uploadProgress(HekrCommonUtil.getProgress(bytesWritten, totalSize));
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
    public void getUserFiles(String fileName, int page, int size, final HekrUser.GetFileListener getFileListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "user/file").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("fileName", fileName);
        maps.put("page", String.valueOf(page));
        maps.put("size", String.valueOf(size));
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
    public void deleteUserFile(@NotNull String fileName, final HekrUser.DeleteFileListener deleteFileListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, "user/file?fileName=", fileName).toString();
        deleteHekrData(url, new GetHekrDataListener() {
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

    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程执行)
     *
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(@NotNull String clientId, final HekrUser.PushTagBindListener pushTagBindListener) {
        pushTagBind(clientId, 0, pushTagBindListener);
    }


    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程调用)
     *
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(String clientId, int type, final HekrUser.PushTagBindListener pushTagBindListener) {
        String appTid = HekrCommonUtil.getHEKRIMEI(mContext.get());
        pushTagBind(appTid, clientId, type, pushTagBindListener);
    }


    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程调用)
     *
     * @param appTid              APP ID
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(@NotNull String appTid, @NotNull String clientId, final HekrUser.PushTagBindListener pushTagBindListener) {
        pushTagBind(appTid, clientId, 0, pushTagBindListener);
    }

    /**
     * 4.5.19 绑定推送标签接口(此接口请在主线程调用)
     *
     * @param appTid              APP ID
     * @param clientId            个推分配给app的客户端id
     * @param pushTagBindListener 回调方法
     */
    public void pushTagBind(@NotNull String appTid, @NotNull String clientId, int type, final HekrUser.PushTagBindListener pushTagBindListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.PUSH_TAG_BIND);
        String phoneType = "";
        switch (type) {
            case 0:
                phoneType = "个推";
                break;
            case 1:
                phoneType = "小米";
                break;
            case 2:
                phoneType = "华为";
                break;
        }
        Log.i(TAG, phoneType + "调用绑定推送标签接口:" + clientId + "语言:" + Locale.getDefault() + "appTid:" + appTid);
        JSONObject object = new JSONObject();
        object.put("appTid", appTid);
        object.put("clientId", clientId);
        object.put("type", type);
        object.put("locale", Locale.getDefault());
        final String finalPhoneType = phoneType;
        postHekrData(url, object.toJSONString(), new GetHekrDataListener() {
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
     * 4.5.20 解绑华为推送别名
     *
     * @param clientId              个推分配给app的客户端id
     * @param unPushTagBindListener 回调方法
     */
    public void unPushTagBind(@NotNull String clientId, final HekrUser.UnPushTagBindListener unPushTagBindListener) {
        String appTid = HekrCommonUtil.getHEKRIMEI(mContext.get());
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UNPUSH_ALIAS_BIND);
        Log.i(TAG, "华为推送解绑标签:" + clientId + "语言:" + Locale.getDefault() + "appTid:" + appTid);
        JSONObject object = new JSONObject();
        object.put("appTid", appTid);
        object.put("clientId", clientId);
        object.put("locale", Locale.getDefault());
        postHekrData(url, object.toJSONString(), new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                Log.i(TAG, "华为推送解绑标签接口调用成功");
                unPushTagBindListener.unPushTagBindSuccess();
            }

            @Override
            public void getFail(int errorCode) {
                Log.i(TAG, "华为推送解绑标签接口调用失败");
                unPushTagBindListener.unPushTagBindFail(errorCode);
            }
        });
    }

    /**
     * 4.6.1 天气实况
     *
     * @param getWeatherListener 回调
     */
    public void getWeather(String location, String Code, final HekrUser.GetWeatherListener getWeatherListener) {
        String time = String.valueOf(System.currentTimeMillis());
        byte[] bytes = (MD5Util.md5(TextUtils.concat(time, Code, time).toString()));
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            ret.append(Character.forDigit((aByte >> 4) & 0xf, 16));
            ret.append(Character.forDigit(aByte & 0xf, 16));
        }
        String language;
        switch (HekrCodeUtil.getLanguage(mContext.get())) {
            case HekrCodeUtil.LANGUAGE_zh_Hans:
                language = "zh-Hans";
                break;
            case HekrCodeUtil.LANGUAGE_zh_Hant:
                language = "zh-Hant";
                break;
            case HekrCodeUtil.LANGUAGE_en:
                language = "en";
                break;
            default:
                language = "en";
                break;
        }


        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_WEATHER, location, "&sign=", ret.toString(), "&timestamp=", time, "&language=", language);
        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                String results = JSONArray.parseArray(JSONObject.parseObject(object.toString()).getString("results")).get(0).toString();
                JSONObject obj = JSONObject.parseObject(results);
                WeatherBeanResultsNow now = JSONObject.parseObject(obj.getString("now"), WeatherBeanResultsNow.class);
                getWeatherListener.getWeatherSuccess(now, JSONObject.parseObject(obj.getString("location")).getString("name"));
            }

            @Override
            public void getFail(int errorCode) {
                getWeatherListener.getWeatherFail(errorCode);
            }
        });

    }


    /**
     * 4.6.3 空气质量实况
     *
     * @param getAirQualityListener 回调
     */
    public void getAirQuality(String location, String Code, final HekrUser.GetAirQualityListener getAirQualityListener) {
        String time = String.valueOf(System.currentTimeMillis());
        byte[] bytes = (MD5Util.md5(TextUtils.concat(time, Code, time).toString()));
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            ret.append(Character.forDigit((aByte >> 4) & 0xf, 16));
            ret.append(Character.forDigit(aByte & 0xf, 16));
        }
        String language;
        switch (HekrCodeUtil.getLanguage(mContext.get())) {
            case HekrCodeUtil.LANGUAGE_zh_Hans:
                language = "zh-Hans";
                break;
            case HekrCodeUtil.LANGUAGE_zh_Hant:
                language = "zh-Hant";
                break;
            case HekrCodeUtil.LANGUAGE_en:
                language = "en";
                break;
            default:
                language = "en";
                break;
        }


        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_AIR_QUALITY, location, "&sign=", ret.toString(), "&timestamp=", time, "&language=", language);
        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                AirQualityBean airQualityBean = JSON.parseObject(object.toString(),AirQualityBean.class);
                JSONObject results = JSONArray.parseArray(JSONObject.parseObject(object.toString()).getString("results")).getJSONObject(0);

                getAirQualityListener.getAirQualitySuccess(airQualityBean,results.getJSONObject("air").getJSONObject("city").getString("pm25"));
            }

            @Override
            public void getFail(int errorCode) {
                getAirQualityListener.getAirQualityFail(errorCode);
            }
        });

    }


    /**
     * 4.6.1 天气+PM25实况
     *
     * @param getWeatherListener 回调
     */
    public void getNewWeather(String location, String Code, final HekrUser.GetNewWeatherListener getWeatherListener) {
        String time = String.valueOf(System.currentTimeMillis());
        byte[] bytes = (MD5Util.md5(TextUtils.concat(time, Code, time).toString()));
        StringBuilder ret = new StringBuilder(bytes.length << 1);
        for (byte aByte : bytes) {
            ret.append(Character.forDigit((aByte >> 4) & 0xf, 16));
            ret.append(Character.forDigit(aByte & 0xf, 16));
        }
        String language;
        switch (HekrCodeUtil.getLanguage(mContext.get())) {
            case HekrCodeUtil.LANGUAGE_zh_Hans:
                language = "zh-Hans";
                break;
            case HekrCodeUtil.LANGUAGE_zh_Hant:
                language = "zh-Hant";
                break;
            case HekrCodeUtil.LANGUAGE_en:
                language = "en";
                break;
            default:
                language = "en";
                break;
        }


        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_WEATHER_ADD_QUALITY, location, "&sign=", ret.toString(), "&timestamp=", time, "&language=", language);
        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                getWeatherListener.getSuccess(JSONObject.parseObject(object.toString(), WeatherAirBean.class));
            }

            @Override
            public void getFail(int errorCode) {
                getWeatherListener.getFail(errorCode);
            }
        });

    }

    /**
     * 4.7.2 列举群组
     */
    public void getGroup(final HekrUser.GetGroupListener getGroupListener) {
        getGroup(null, getGroupListener);
    }


    /**
     * 4.7.2 列举群组
     *
     * @param groupId 群组id
     */
    private void getGroup(String groupId, final HekrUser.GetGroupListener getGroupListener) {
        //"http://user.openapi.hekr.me/group?groupId=xxx"
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_GROUP).toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put(groupId, groupId);
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
    private void renameGroup(@NotNull String groupId, @NotNull String newGroupName, final HekrUser.OperationGroupListener operationGroupListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_GROUP, "/", groupId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("newGroupName", newGroupName);
        putHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    private void deleteGroup(@NotNull String groupId, final HekrUser.OperationGroupListener operationGroupListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.UAA_GROUP, "?groupId=", groupId);
        deleteHekrData(url, new GetHekrDataListener() {
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
    public void checkFirmwareUpdate(@NotNull String devTid, @NotNull String productPublicKey, @NotNull String binType, @NotNull String binVer, final HekrUser.CheckFwUpdateListener checkFwUpdateListener) {
        final JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("productPublicKey", productPublicKey);
        jsonObject.put("binType", binType);
        jsonObject.put("binVer", binVer);
        jsonArray.add(jsonObject);
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_CONSOLE_URL, ConstantsUtil.UrlUtil.CHECK_FW_UPDATE);
        postHekrData(url, jsonArray.toJSONString(), new GetHekrDataListener() {
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
    public void getNewsByPid(HekrUser.GetInfoListener getInfoListener) {
        getNewsByPid(null, null, getInfoListener);
    }

    /**
     * 5.2 根据pid获取企业资讯
     */
    public void getNewsByPid(int page, int size, HekrUser.GetInfoListener getInfoListener) {
        getNewsByPid(String.valueOf(page), String.valueOf(size), getInfoListener);
    }


    /**
     * 5.2 根据pid获取企业资讯
     */
    private void getNewsByPid(String page, String size, final HekrUser.GetInfoListener getInfoListener) {
        String url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_CONSOLE_URL, "external/vc/getByPid").toString();
        HashMap<String, String> maps = new HashMap<>();
        maps.put("pid", pid);
        maps.put("page", page);
        maps.put("size", size);
        url = HekrCommonUtil.getUrl(url, maps);
        getHekrData(url, new GetHekrDataListener() {
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
    public void feedback(@NotNull String content, String images, final HekrUser.FeedbackListener feedbackListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_CONSOLE_URL, "external/feedback");
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
        postHekrData(url, jsonObject.toJSONString(), new GetHekrDataListener() {
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
    public void getDefaultStatic(final HekrUser.GetDefaultDevicesListener getDefaultDevices) {
        //http://console.openapi.hekr.me/external/device/default/static
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_CONSOLE_URL, ConstantsUtil.UrlUtil.DEFAULT_STATIC);
        getHekrData(url, new GetHekrDataListener() {
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
    public void getPinCode(String ssid, final HekrUser.GetPinCodeListener getPinCodeListener) {
        CharSequence url = TextUtils.concat(ConstantsUtil.UrlUtil.BASE_USER_URL, ConstantsUtil.UrlUtil.GET_PIN_CODE, ssid);
        getHekrData(url, new GetHekrDataListener() {
            @Override
            public void getSuccess(Object object) {
                try {
                    if (object != null && !TextUtils.isEmpty(object.toString())) {
                        String pinCode = new org.json.JSONObject(object.toString()).getString("PINCode");
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
     * 将用户的bean的数据保存下来
     *
     * @param userBean 用户实体类
     */
    private void setUserCache(UserBean userBean) {
        this.JWT_TOKEN = userBean.getJWT_TOKEN();
        this.refresh_TOKEN = userBean.getRefresh_token();
        this.userId = TokenToUid();
        try {
            //把此token保存下来
            SpCache.putString(ConstantsUtil.JWT_TOKEN, userBean.getJWT_TOKEN());
            SpCache.putString(ConstantsUtil.HEKR_USER_NAME, userBean.getUsername());
            SpCache.putString(ConstantsUtil.HEKR_PID, userBean.getPid());
            SpCache.putString(ConstantsUtil.REFRESH_TOKEN, userBean.getRefresh_token());
        } catch (Exception e) {
            e.printStackTrace();
            android.util.Log.e(TAG, "setUserCacheError");
        }

    }


    /**
     * 退出登录
     */
    public void userLogout() {
        startWebServicesFlag = 1;
        //退出APP之后关闭所有的请求！
        BaseHttpUtil.getClient().cancelAllRequests(true);
        JWT_TOKEN = null;
        refresh_TOKEN = null;
        //停止webSocket
        EventBus.getDefault().post(new WsSwitchEvent(ConstantsUtil.EventCode.WS_SWITCH_EVENT_STATUS_DISCONNECT));
        //清除掉缓存
        //DataCleanManager.clearAllCache(mContext.get());
        DevicesCacheUtil.deleteDeviceLists(mContext.get());
        String un_name = SpCache.getString("uname", "");
        SpCache.clear();
        SpCache.putString("uname", un_name);
        //清理掉后
        SpCache.putBoolean("pushTag", false);
        SpCache.putString(ConstantsUtil.HEKR_PID, pid);
        SpCache.putString(ConstantsUtil.HEKR_PUSH_CLIENT_ID, Global.clientId);
        SpCache.putString(ConstantsUtil.HEKR_MI_PUSH_CLIENT_ID, Global.mRegId);
        SpCache.putString(ConstantsUtil.HEKR_HUA_WEI_PUSH_CLIENT_ID, Global.huaWeiToken);
    }

    /**
     * @return 返回用户token
     */
    public String getJWT_TOKEN() {
        if (TextUtils.isEmpty(JWT_TOKEN)) {
            /*if (!TextUtils.isEmpty(jwt)) {
                connectWsServices();
            } else {
                startWebServicesFlag = 1;
            }*/
            return SpCache.getString(ConstantsUtil.JWT_TOKEN, "");
        } else {
            //connectWsServices();
            return JWT_TOKEN;
        }
    }

    /**
     * 连接webSocket
     */
    private synchronized void connectWsServices() {
        if (startWebServicesFlag == 1) {
            startWebServicesFlag = 0;
            Log.d(TAG, "ws未初始化");
            //mContext.get().startService(new Intent(mContext.get(), WebSocketService.class));
            EventBus.getDefault().postSticky(new WsSwitchEvent(ConstantsUtil.EventCode.WS_SWITCH_EVENT_STATUS_CONNECT));
        }
    }

    private String getRefreshToken() {
        if (TextUtils.isEmpty(refresh_TOKEN)) {
            return SpCache.getString(ConstantsUtil.REFRESH_TOKEN, "");
        } else {
            return refresh_TOKEN;
        }
    }


    /**
     * 将获取到的token保存下来
     */
    private void setTokenWIthCache(JWTBean jwtBean) {
        this.JWT_TOKEN = jwtBean.getAccessToken();
        this.refresh_TOKEN = jwtBean.getRefreshToken();
        //把此token保存下来
        this.userId = TokenToUid();
        if (!TextUtils.isEmpty(JWT_TOKEN)) {
            SpCache.putString(ConstantsUtil.JWT_TOKEN, JWT_TOKEN);
        }
        if (!TextUtils.isEmpty(refresh_TOKEN)) {
            SpCache.putString(ConstantsUtil.REFRESH_TOKEN, refresh_TOKEN);
        }
    }


    /**
     * 将获取到的用户信息保存下来
     */
    private void setUserCache(String userInfo) {
        SpCache.putString("HEKR_USER_INFO", userInfo);
    }

    /**
     * 拿到缓存到本地的用户档案
     *
     * @return 用户档案
     */
    public ProfileBean getUserCache() {
        ProfileBean profileBean = new ProfileBean();
        String var = SpCache.getString("HEKR_USER_INFO", "");
        if (!TextUtils.isEmpty(var)) {
            profileBean = JSON.parseObject(var, ProfileBean.class);
            if (TextUtils.isEmpty(profileBean.avatarUrl())) {
                ProfileBean.AvatarUrl avatarUrl = new ProfileBean.AvatarUrl("");
                profileBean.setAvatarUrl(avatarUrl);
            }
        }
        return profileBean;
    }


    /**
     * 获取UID
     *
     * @return uid
     */
    public String getUserId() {
        if (TextUtils.isEmpty(userId)) {
            return TokenToUid();
        } else {
            return userId;
        }
    }


    /**
     * 提取出来UID
     *
     * @return uid
     */
    private String TokenToUid() {
        if (getJWT_TOKEN().contains(".")) {
            String[] strs = getJWT_TOKEN().split("\\.");
            if (strs.length == 3 && !TextUtils.isEmpty(strs[1])) {
                JSONObject uidObj = JSONObject.parseObject(new String(Base64.decode(strs[1], Base64.DEFAULT)));
                if (uidObj.containsKey("uid")) {
                    return uidObj.getString("uid");
                }
            }
        }
        return null;
    }

    /**
     * hekrHttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param getHekrDataListener 回调方法
     */
    public void getHekrData(CharSequence url, final GetHekrDataListener getHekrDataListener) {
        getHekrData(url.toString(), getHekrDataListener);
    }


    /**
     * hekrHttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param getHekrDataListener 回调方法
     */
    public void getHekrData(String url, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.getDataReFreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, null, new GetHekrData(getHekrDataListener));
    }


    /**
     * hekrHttpGet  <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param getHekrDataListener 回调方法
     */
    public void getHekrData(String url, Header[] headers, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.getDataReFreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, headers, new GetHekrData(getHekrDataListener));
    }


    /**
     * hekrHttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void postHekrData(CharSequence url, String entity, final GetHekrDataListener getHekrDataListener) {
        postHekrData(url.toString(), null, entity, getHekrDataListener);
    }

    /**
     * hekrHttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void postHekrData(String url, String entity, final GetHekrDataListener getHekrDataListener) {
        postHekrData(url, null, entity, getHekrDataListener);
    }


    /**
     * hekrHttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void postHekrData(String url, Header[] headers, String entity, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.postDataReFreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, headers, entity, new GetHekrData(getHekrDataListener));
    }


    /**
     * hekrHttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void putHekrData(String url, String entity, final GetHekrDataListener getHekrDataListener) {
        putHekrData(url, null, entity, getHekrDataListener);
    }


    /**
     * hekrHttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void putHekrData(CharSequence url, String entity, final GetHekrDataListener getHekrDataListener) {
        putHekrData(url.toString(), null, entity, getHekrDataListener);
    }

    /**
     * hekrHttpPut <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void putHekrData(String url, Header[] headers, String entity, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.putDataRefreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, headers, entity, new GetHekrData(getHekrDataListener));
    }


    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param getHekrDataListener 回调
     */
    public void deleteHekrData(String url, final GetHekrDataListener getHekrDataListener) {
        deleteHekrData(url, null, getHekrDataListener);
    }


    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param getHekrDataListener 回调
     */
    public void deleteHekrData(CharSequence url, final GetHekrDataListener getHekrDataListener) {
        deleteHekrData(url.toString(), null, getHekrDataListener);
    }

    /**
     * deleteHttpDelete <br>此接口可自动管理token
     *
     * @param url                 url
     * @param getHekrDataListener 回调
     */
    public void deleteHekrData(String url, Header[] headers, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.deleteDataReFreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, headers, new GetHekrData(getHekrDataListener));
    }


    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void patchHekrData(CharSequence url, String entity, final GetHekrDataListener getHekrDataListener) {
        patchHekrData(url.toString(), null, entity, getHekrDataListener);
    }

    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void patchHekrData(String url, String entity, final GetHekrDataListener getHekrDataListener) {
        patchHekrData(url, null, entity, getHekrDataListener);
    }

    /**
     * deleteHttpPatch
     * <br>此接口可自动管理token
     *
     * @param url                 url
     * @param headers             headers
     * @param entity              entity
     * @param getHekrDataListener 回调
     */
    public void patchHekrData(String url, Header[] headers, String entity, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.patchDataToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, headers, entity, new GetHekrData(getHekrDataListener));
    }

    /**
     * hekrHttpPost <br>此接口可自动管理token
     *
     * @param url                 url
     * @param params              表单
     * @param getHekrDataListener 回调
     */
    public void postParamsHekrData(String url, RequestParams params, final GetHekrDataListener getHekrDataListener) {
        HekrHttpUtil.postFileReFreshToken(mContext.get(), JWT_TOKEN, refresh_TOKEN, url, params, new GetHekrData(getHekrDataListener));
    }

    /**
     * 获取云端数据抽象类<br>
     * 获取数据成功/获取数据失败/进度显示
     */
    public static abstract class GetHekrDataListener {

        public abstract void getSuccess(Object object);

        public abstract void getFail(int errorCode);

        public void getProgress(long bytesWritten, long totalSize) {

        }

    }


    private class GetHekrData extends GetHekrDataWithTokenListener {

        private GetHekrDataListener getHekrDataListener;


        public GetHekrData(GetHekrDataListener getHekrDataListener) {
            this.getHekrDataListener = getHekrDataListener;
        }

        @Override
        public void getDataSuccess(Object object) {
            getHekrDataListener.getSuccess(object);
        }

        @Override
        public void getToken(JWTBean jwtBean) {
            //如果能够获取到新token，将新token直接保存下来
            setTokenWIthCache(jwtBean);
            android.util.Log.d(TAG, "新token: " + jwtBean.toString());
        }

        @Override
        public void getDataFail(int errorCode) {
            getHekrDataListener.getFail(errorCode);
        }

        @Override
        public void getDataProgress(long bytesWritten, long totalSize) {
            super.getDataProgress(bytesWritten, totalSize);
           /* int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
            if (count > 100) {
                count = 100;
            }*/
            getHekrDataListener.getProgress(bytesWritten, totalSize);
        }
    }

}

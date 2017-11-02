package com.siterwell.sdk.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.siterwell.sdk.bean.ConfigBean;

import org.json.JSONException;

/*
@class HekrSDK
@autor Administrator
@time 2017/10/16 14:07
@email xuejunju_4595@qq.com
*/
public class HekrSDK {


    private static ConfigBean weiBoBean;
    private static ConfigBean qqBean;
    private static ConfigBean weiXinBean;
    private static ConfigBean facebookBean;
    private static ConfigBean googleBean;
    private static ConfigBean twitterBean;
    private static ConfigBean pushBean;
    public static boolean isHekrInited = false;
    private static boolean logIsPrint = false;
    public static final String VERSION = "1.2.4";
    private static int timeout = 5 * 1000;

    /**
     * 初始化SDK
     *
     * @param context 请使用getApplicationContext()
     * @param id      R.raw.config.json
     */
    public static void init(Context context, int id) {
        if (!isHekrInited) {
            Log.d(ConstantsUtil.HEKR_SDK, "HEKR_SDK初始化,HEKR_SDK Version" + VERSION);
            Log.isPrint = logIsPrint;
            //初始化sp
            SpCache.init(context.getApplicationContext());
            //SpCache.putBoolean(ConstantsUtil.HEKR_SDK_DEBUG_VIEW, false);

            String jsonStr = HekrCommonUtil.convertStreamToString(context, id);
            try {
                if (!TextUtils.isEmpty(jsonStr)) {
                    org.json.JSONObject jsonConfig = new org.json.JSONObject(jsonStr);
                    if (jsonConfig.has("Social")) {
                        JSONObject Social = JSON.parseObject(jsonConfig.getJSONObject("Social").toString());
                        weiBoBean = JSON.parseObject(Social.getString("Weibo"), ConfigBean.class);
                        qqBean = JSON.parseObject(Social.getJSONObject("QQ").toString(), ConfigBean.class);
                        weiXinBean = JSON.parseObject(Social.getJSONObject("Weixin").toString(), ConfigBean.class);
                        facebookBean = JSON.parseObject(Social.getJSONObject("Facebook").toString(), ConfigBean.class);
                        googleBean = JSON.parseObject(Social.getJSONObject("Google").toString(), ConfigBean.class);
                        twitterBean = JSON.parseObject(Social.getJSONObject("Twitter").toString(), ConfigBean.class);
                    }
                    if (jsonConfig.has("push")) {
                        pushBean = JSON.parseObject(jsonConfig.getJSONObject("push").toString(), ConfigBean.class);
                    }
                } else {
                    throw new NullPointerException("config.json is error");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            isHekrInited = true;
        }
    }

    public static ConfigBean getWeiBoBean() {
        return weiBoBean;
    }

    public static ConfigBean getQqBean() {
        return qqBean;
    }

    public static ConfigBean getWeiXinBean() {
        return weiXinBean;
    }

    public static ConfigBean getFacebookBean() {
        return facebookBean;
    }

    public static ConfigBean getGoogleBean() {
        return googleBean;
    }

    public static ConfigBean getTwitterBean() {
        return twitterBean;
    }

    public static ConfigBean getPushBean() {
        return pushBean;
    }

    /**
     * SDK log开关，默认为关
     *
     * @param openLog log开关
     */
    public static void openLog(boolean openLog) {
        logIsPrint = openLog;
        Log.isPrint = logIsPrint;
    }

    public static void setTimeout(int time) {
        timeout = time;
    }

    static int getTimeout() {
        return timeout;
    }



}

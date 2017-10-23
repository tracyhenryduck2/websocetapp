package com.siterwell.sdk.util;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import com.litesuits.common.utils.TelephoneUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/*
@class HekrCommonUtil
@autor Administrator
@time 2017/10/16 14:05
@email xuejunju_4595@qq.com
*/
public class HekrCommonUtil {

    /**
     * 读取R.raw.hekr.json
     */
    public static String convertStreamToString(Context mContext, int id) {
        InputStream is = mContext.getResources().openRawResource(id);
        String s = "";
        try {
            Scanner scanner = new Scanner(is, "UTF-8").useDelimiter("\\A");
            if (scanner.hasNext()) s = scanner.next();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }


    /**
     * 获取手机唯一标识并拼接厂家pid
     *
     * @param context
     * @return
     */
    public static String getHEKRIMEI(Context context) {
        String str = TelephoneUtil.getIMEI(context);
        String IMEI = TextUtils.concat(str, HekrSDK.pid, context.getPackageName()).toString();
        if (str == null || TextUtils.isEmpty(TelephoneUtil.getIMEI(context))) {
            IMEI = TextUtils.concat(Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID), HekrSDK.pid, context.getPackageName()).toString();
        }
        if (str == null &&
                Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID) == null) {
            IMEI = "";
        }
        return IMEI;
    }

    /**
     * 将数据转换为进度
     *
     * @return 进度
     */
    public static int getProgress(long bytesWritten, long totalSize) {
        int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);
        if (count > 100) {
            count = 100;
        }
        return count;
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：net.loonggg.testbackstage.TestService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public static boolean isServiceRunning(Context mContext, String serviceName) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(serviceName)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }


    /**
     * 获取访问的url
     *
     * @param baseUrl   baseUrl
     * @param urlParams 参数
     * @return url
     */
    public static String getUrl(String baseUrl, HashMap<String, String> urlParams) {
        // 添加url参数
        if (urlParams != null && urlParams.size() > 0) {
            StringBuilder sb = null;
            for (Map.Entry<String, String> it : urlParams.entrySet()) {
                String key = it.getKey();
                String value = it.getValue();
                //key与value均不能为空
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(value)) {
                    if (sb == null) {
                        sb = new StringBuilder();
                        sb.append("?");
                    } else {
                        sb.append("&");
                    }
                    sb.append(key).append("=").append(value);
                }
            }
            if (sb != null) {
                baseUrl = TextUtils.concat(baseUrl, sb).toString();
            }
        }
        return baseUrl;
    }


    /**
     * 邮箱转义
     *
     * @param email email
     * @return email
     */
    public static String getEmail(String email) {
        try {
            email = URLEncoder.encode(email, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return email;
    }

}

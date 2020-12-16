package me.hekr.sdk;

import android.text.TextUtils;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 常量类
 */

public class Constants {

    public static final String HEKR_PID = "HEKR_PID";
    public static final String HEKR_SDK = "HEKR_SDK";

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String CLOUD_CHANNELS = "CLOUD_CHANNELS";

    private static boolean IS_DEBUG_SITE = false;

    public static class UrlUtil {
        public static String BASE_UAA_URL = "http://192.168.12.163:1418/";
        public static String BASE_USER_URL = "https://user-openapi.hekr.me/";
        public static String BASE_CONSOLE_URL = "https://console-openapi.hekr.me/";
        public static String APP_WEBSOCKET_REPLACE_CLOUD_URL = "ws://%s:8899";
        public static String APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL = "ws://%s:8899";

        /**
         * 认证授权API
         */
        public static final String UAA_LOGIN_URL = "login";
        public static final String UAA_REFRESH_TOKEN = "token/refresh";

        /**
         * 设备管理
         */
        public static final String BIND_DEVICE = "device";
        public static final String DEVICE_BIND_STATUS = "deviceBindStatus";

        /**
         * 授权
         */
        public static final String GET_NEW_DEVICE = "getNewDeviceList?pinCode=";
    }

    /**
     * 切换到测试环境
     *
     * @param domain 域名
     */
    public static void setTestSite(String domain) {
        IS_DEBUG_SITE = true;
        if (TextUtils.isEmpty(domain)) {
            UrlUtil.BASE_UAA_URL = "https://test-uaa-openapi.hekr.me/";
            UrlUtil.BASE_USER_URL = "https://test-user-openapi.hekr.me/";
            UrlUtil.BASE_CONSOLE_URL = "https://test-console-openapi.hekr.me/";
        } else {
            UrlUtil.BASE_UAA_URL = "https://test-uaa-openapi." + domain + "/";
            UrlUtil.BASE_USER_URL = "https://test-user-openapi." + domain + "/";
            UrlUtil.BASE_CONSOLE_URL = "https://test-console-openapi." + domain + "/";
        }
    }

    /**
     * 切换到正式环境
     *
     * @param domain 域名
     */
    public static void setOnlineSite(String domain) {
        IS_DEBUG_SITE = false;
        if (TextUtils.isEmpty(domain)) {
            UrlUtil.BASE_UAA_URL = "https://uaa-openapi.hekr.me/";
            UrlUtil.BASE_USER_URL = "https://user-openapi.hekr.me/";
            UrlUtil.BASE_CONSOLE_URL = "https://console-openapi.hekr.me/";
        } else {
            UrlUtil.BASE_UAA_URL = "https://uaa-openapi." + domain + "/";
            UrlUtil.BASE_USER_URL = "https://user-openapi." + domain + "/";
            UrlUtil.BASE_CONSOLE_URL = "https://console-openapi." + domain + "/";
        }
    }

    public static boolean isTestSite() {
        return IS_DEBUG_SITE;
    }
}

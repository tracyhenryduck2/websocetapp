package me.siter.sdk;

import android.text.TextUtils;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 常量类
 */

public class Constants {

    public static final String SITER_PID = "SITER_PID";
    public static final String SITER_SDK = "SITER_SDK";

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String REFRESH_TOKEN = "REFRESH_TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String CLOUD_CHANNELS = "CLOUD_CHANNELS";
    public static final String SDK_ERROR = "SDK_ERROR";
    private static boolean IS_DEBUG_SITE = false;

    public static final String WS_PAYLOAD = "siter_ws_payload";
    public static final String PUSH_GETUI_ID = "clientid";
    public static final String MI_PUSH_CLIENT_ID = "mRegId";
    public static final String HUA_WEI_PUSH_CLIENT_ID = "huaWeiToken";

    public static class UrlUtil {
        public static String BASE_UAA_URL = "http://192.168.12.163:1418/";
        public static String BASE_USER_URL = "http://192.168.12.163:1418/";
//        public static String BASE_UAA_URL = "https://uaa-openapi.hekr.me/";
//        public static String BASE_USER_URL = "https://user-openapi.hekr.me/";
        public static String BASE_CONSOLE_URL = "https://console-openapi.siter.me/";
        public static String APP_WEBSOCKET_REPLACE_CLOUD_URL = "ws://%s:8899";
        public static String APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL = "ws://%s:8899";
//        public static String APP_WEBSOCKET_REPLACE_CLOUD_URL = "wss://%s:186";
//        public static String APP_WEBSOCKET_REPLACE_TEST_CLOUD_URL = "wss://%s:186";


        /**
         * 认证授权API
         */
        public static final String UAA_LOGIN_URL = "login";
        public static final String UAA_REFRESH_TOKEN = "token/refresh";
        public static final String UAA_GET_CODE_URL = "sms/getVerifyCode";
        public static final String UAA_GET_EMAIL_CODE_URL = "email/getVerifyCode";
        public static final String UAA_CHECK_CODE_URL = "sms/checkVerifyCode?phoneNumber=";
        public static final String UAA_REGISTER_URL = "register?type=";
        public static final String UAA_RESET_PWD_URL = "resetPassword?type=";
        public static final String UAA_CHANGR_PWD_URL = "changePassword";
        public static final String UAA_CHANGE_PHONE_URL = "changePhoneNumber";
        public static final String UAA_SEND_CHANGE_EMAIL = "sendChangeEmailStep1Email?email=";
        public static final String UAA_GROUP = "group";

        /**
         * 设备管理
         */
        public static final String BIND_DEVICE = "user/deviceList";
        public static final String FOLDER = "folder";
        public static final String PROFILE = "user/profile";
        public static final String USER_FILE = "user/file";

        public static final String UAA_WEATHER_ADD_QUALITY = "external/now?location=";

        public static final String DEVICE_BIND_STATUS = "deviceBindStatus";
        public static final String DEFAULT_STATIC = "external/device/default/static";
        public static final String CHECK_FW_UPDATE = "external/device/fw/ota/check";
        public static final String PUSH_TAG_BIND = "user/pushTagBind";
        public static final String UNPUSH_ALIAS_BIND = "user/unbindPushAlias";
        public static final String QUERY_DEVICE_STATUS="deviceStatusQuery";
        public static final String QUERY_WARNINGS="api/v1/notification?type=WARNING&";

        /**
         * 授权
         */
        public static final String AUTHORIZATION_REVERSE_REGISTER = "authorization/reverse/register";
        public static final String AUTHORIZATION_REVERSE_AUTH_URL = "authorization/reverse/authUrl";
        public static final String REVERSE_TEMPLATE_ID = "?reverseTemplateId=";
        public static final String AUTHORIZATION_GRANTOR = "authorization?grantor=";
        public static final String AUTHORIZATION_REVERSE_DEV_TID = "authorization/reverse?devTid=";
        public static final String AUTHORIZATION_REVERSE_CANCEL = "v1/device-auth/";
        public static final String CTRL_KEY = "ctrlKey=";
        public static final String GRANTEE = "grantee=";
        public static final String DEV_TID = "devTid=";
        public static final String TASK_ID = "taskId=";
        public static final String REVERSE_REGISTER_ID = "&reverseRegisterId=";

        public static final String GET_PIN_CODE = "getPINCode?ssid=";
        public static final String GET_NEW_DEVICE = "getNewDeviceList?pinCode=";
        public static final String CREATE_RULE = "rule/schedulerTask";

        public static final String ACCOUNT_UPGRADE = "accountUpgrade";
        public static final String SEND_EMAIL = "accountUpgradeByEmail";

    }




    public static class ActionStrUtil {
        public static final String ACTION_WS_DATA_RECEIVE = "me.action.ws.data";
        public static final String ACTION_PUSH_DATA_RECEIVE = "me.push.action";
    }

    public static class ErrorCode {
        public static final int NETWORK_TIME_OUT = 0;
        public static final int TOKEN_TIME_OUT = 1;
        public static final int UNKNOWN_ERROR = 2;
        public static final int SERVER_ERROR = 500;
        public static final int FILE_NOT_FOUND = 3;
    }

    /**
     * 切换到测试环境
     *
     * @param domain 域名
     */
    public static void setTestSite(String domain) {
        IS_DEBUG_SITE = true;
        if (TextUtils.isEmpty(domain)) {
            UrlUtil.BASE_UAA_URL = "https://test-uaa-openapi.siter.me/";
            UrlUtil.BASE_USER_URL = "https://test-user-openapi.siter.me/";
            UrlUtil.BASE_CONSOLE_URL = "https://test-console-openapi.siter.me/";
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
            UrlUtil.BASE_UAA_URL = "https://uaa-openapi.siter.me/";
            UrlUtil.BASE_USER_URL = "https://user-openapi.siter.me/";
            UrlUtil.BASE_CONSOLE_URL = "https://console-openapi.siter.me/";
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

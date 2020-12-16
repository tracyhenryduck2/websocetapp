package com.siterwell.sdk.http;


/*
@class SiterConstantsUtil
@autor Administrator
@time 2017/10/16 13:32
@email xuejunju_4595@qq.com
*/
public class SiterConstantsUtil {

    public static final String NETWORK_ERROR = "Network is not available";
    public static final String TOKEN_OUT_ERROR = "Token expired, please re login";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";

    public static final String HEKR_SDK_ERROR = "HEKR_SDK_ERROR";
    public static final String HEKR_WS_PAYLOAD = "hekr_ws_payload";
    public static final String HEKR_SDK = "HEKR_SDK";
    public static final String HEKR_PUSH_CLIENT_ID = "clientid";
    public static final String HEKR_MI_PUSH_CLIENT_ID = "mRegId";
    public static final String HEKR_HUA_WEI_PUSH_CLIENT_ID = "huaWeiToken";

    /**
     * 网址
     */
    public static class UrlUtil {

        /**
         * 认证授权API
         */
        public static final String UAA_GET_CODE_URL = "sms/getVerifyCode";
        public static final String UAA_GET_EMAIL_CODE_URL = "email/getVerifyCode";
        public static final String UAA_CHECK_CODE_URL = "sms/checkVerifyCode?phoneNumber=";
        public static final String UAA_LOGIN_URL = "login";
        public static final String UAA_REGISTER_URL = "register?type=";
        public static final String UAA_RESET_PWD_URL = "resetPassword?type=";
        public static final String UAA_CHANGR_PWD_URL = "changePassword";
        public static final String UAA_CHANGE_PHONE_URL = "changePhoneNumber";
        public static final String UAA_REFRESH_TOKEN = "token/refresh";
        public static final String UAA_SEND_CHANGE_EMAIL = "sendChangeEmailStep1Email?email=";
        public static final String UAA_GROUP = "group";

        /**
         * 设备管理
         */
        public static final String BIND_DEVICE = "device";
        public static final String FOLDER = "folder";
        public static final String PROFILE = "user/profile";
        public static final String USER_FILE = "user/file";

        public static final String UAA_WEATHER = "weather/now?location=";
        public static final String UAA_AIR_QUALITY = "air/now?location=";
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
        public static final String OAUTH_URL = "http://www.hekr.me?action=rauth&token=";
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
        public static final String ACTION_WS_DATA_RECEIVE = "me.hekr.action.ws.data";
        public static final String ACTION_PUSH_DATA_RECEIVE = "me.hekr.push.action";
    }

    public static class ErrorCode {
        public static final int NETWORK_TIME_OUT = 0;
        public static final int TOKEN_TIME_OUT = 1;
        public static final int UNKNOWN_ERROR = 2;
        public static final int SERVER_ERROR = 500;
        public static final int FILE_NOT_FOUND = 3;
    }



}

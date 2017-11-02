package com.siterwell.sdk.util;


/*
@class ConstantsUtil
@autor Administrator
@time 2017/10/16 13:32
@email xuejunju_4595@qq.com
*/
public class ConstantsUtil {

    public static final String NETWORK_ERROR = "Network is not available";
    public static final String TOKEN_OUT_ERROR = "Token expired, please re login";
    public static final String UNKNOWN_ERROR = "UNKNOWN_ERROR";
    public static final String SERVER_ERROR = "The server encountered an internal error and was unable to complete your request";
    public static final String SP_TEMP_ACCOUNT = "temp_account";
    public static final String SP_TEMP_TOKEN = "temp_bind_token";
    public static final int LOGOUT_FLAG_INT = 1;
    public static final int CONFIG_REFRESH_INT = 2;
    public static final String LOGOUT_FLAG_STRING = "logout";
    public static final String DEVICES_JSON_NAME = "HekrDevices.Json";

    public static final String JWT_TOKEN = "JWT_TOKEN";
    public static final String HEKR_USER_NAME = "HEKR_USER_NAME";
    public static final String REFRESH_TOKEN = "refresh_TOKEN";
    public static final String SDK_INIT_ERROR = "you show invoke HekrSDK.init() before you use it ";
    public static final String ERROR_PID = "Hekr AppId is error in Config.json";
    public static final String HEKR_SDK_ERROR = "HEKR_SDK_ERROR";
    public static final String HEKR_WS_PAYLOAD = "hekr_ws_payload";
    public static final String HEKR_SDK = "HEKR_SDK";
    public static final String HEKR_SDK_DEBUG_VIEW = "HEKR_SDK_DEBUG_VIEW";
    public static final String HEKR_SDK_DEBUG_NUMBER = "HEKR_SDK_DEBUG_NUMBER";
    public static final String HEKR_PUSH_CLIENT_ID = "clientid";
    public static final String HEKR_MI_PUSH_CLIENT_ID = "mRegId";
    public static final String HEKR_HUA_WEI_PUSH_CLIENT_ID = "huaWeiToken";

    /**
     * 网址
     */
    public static class UrlUtil {
        public static final String BASE_QR_CODE_URL = "http://www.hekr.me?";
        public static final String BASE_CONFIG_HElP_URL = "https://hekr.daikeapp.com/kb/articles/1672";
        public static final String BASE_CONFIG_HElP_URL_EN = "https://hekr.daikeapp.com/kb/articles/1688";
        public static String BASE_UAA_URL = "http://192.168.3.140:8086/point/";
        public static String BASE_USER_URL = "http://192.168.3.140:8086/point/";
        public static String BASE_CONSOLE_URL = "https://console-openapi.hekr.me/";
        //public static String APP_WEBSOCKET_CONNECT_CLOUD_URL = "ws://asia.app.hekr.me:86";
        public static String APP_WEBSOCKET_CONNECT_CLOUD_URL = "ws://192.168.3.140:8086/point/phone";
        //public static final String BASE_UAA_URL = "http://123.59.81.102:8080/";
        //public static final String BASE_USER_URL = "http://123.59.81.102:8082/";
        //public static final String BASE_CONSOLE_URL = "http://106.75.9.91/";
        //public static final String APP_WEBSOCKET_CONNECT_CLOUD_URL = "ws://106.75.27.22:86";
        //public static final String APP_WEBSOCKET_CONNECT_CLOUD_URL = "wss://asia-app.hekr.me:186";


        /**
         * 认证授权API
         */
        public static final String UAA_GET_CODE_URL = "sms/getVerifyCode";
        public static final String UAA_CHECK_CODE_URL = "sms/checkVerifyCode?phoneNumber=";
        public static final String UAA_LOGIN_URL = "app/SiterAction!login.action";
        public static final String UAA_REGISTER_URL = "register?type=";
        public static final String UAA_RESET_PWD_URL = "resetPassword?type=";
        public static final String UAA_CHANGR_PWD_URL = "changePassword";
        public static final String UAA_CHANGE_PHONE_URL = "changePhoneNumber";
        public static final String UAA_REFRESH_TOKEN = "app/SiterAction!refreshToken.action";
        public static final String UAA_SEND_CHANGE_EMAIL = "sendChangeEmailStep1Email?email=";
        public static final String UAA_GROUP = "group";

        /**
         * 设备管理
         */
        public static final String BIND_DEVICE = "device";
        public static final String FOLDER = "folder";
        public static final String PROFILE = "app/SiterAction!profile.action";
        public static final String USER_FILE = "user/file";

        public static final String UAA_WEATHER = "weather/now?location=";
        public static final String UAA_AIR_QUALITY = "air/now?location=";
        public static final String UAA_WEATHER_ADD_QUALITY = "external/now?location=";

        public static final String DEVICE_BIND_STATUS = "deviceBindStatus";
        public static final String DEFAULT_STATIC = "external/device/default/static";
        public static final String CHECK_FW_UPDATE = "external/device/fw/ota/check";
        public static final String PUSH_TAG_BIND = "user/pushTagBind";
        public static final String UNPUSH_ALIAS_BIND = "user/unbindPushAlias";


        /**
         * 授权
         */
        public static final String OAUTH_URL = "http://www.hekr.me?action=rauth&token=";
        public static final String AUTHORIZATION_REVERSE_REGISTER = "authorization/reverse/register";
        public static final String AUTHORIZATION_REVERSE_AUTH_URL = "authorization/reverse/authUrl";
        public static final String REVERSE_TEMPLATE_ID = "?reverseTemplateId=";
        public static final String AUTHORIZATION_GRANTOR = "authorization?grantor=";
        public static final String AUTHORIZATION_REVERSE_DEV_TID = "authorization/reverse?devTid=";
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

    public static class PushCodeUtil {
        /**
         * 推送渠道
         */
        public static final int GEITUI_PUSH_CHANNEL = 0;
        public static final int MI_PUSH_CHANNEL = 1;
        public static final int HUAWEI_PUSH_CHANNEL = 2;

        public static final int NOT_HUAWEI_XIAOMI_PHONE = 0;
        public static final int IS_XIAOMI_PHONE = 1;
        public static final int IS_HUAWEI_PHONE=2;
        public static final int IS_OTHERS_PHONE=3;
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

    public static class ServiceCode {
        public static final int NETWORK_AVAILABLE = 3;
        public static final int NETWORK_NO = 4;
        public static final int WS_DATA_SEND_WHAT = 5;
        public static final int WS_DATA_RECEIVE_WHAT = 6;
        public static final int LAN_DATA_SEND_WHAT = 7;
        public static final int LAN_DATA_RECEIVE_WHAT = 8;

        public static final int APP_REPEAT_LOGIN = 1400009;
    }

    public static class EventCode {
        public static final int WS_SWITCH_EVENT_STATUS_DISCONNECT = 1;
        public static final int WS_SWITCH_EVENT_STATUS_CONNECT = 2;
    }



}

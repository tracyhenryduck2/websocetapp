package me.siter.sdk.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: Android 端的错误码
 */

public class AndroidErrorMap {

    public static final int OK = 0;
    public static final int MODEL_CORE = 10000;
    public static final int MODEL_CONFIG = 20000;
    public static final int MODEL_WEB = 30000;
    public static final int MODEL_MOAUTH = 40000;

    public static final int ERROR_COMMON = 0;
    public static final int MESSAGE = 1000;
    public static final int ERROR_HTTP = 2000;
    public static final int ERROR_LAN = 3000;

    /**
     * 局域网连接产生的错误
     */
    public static final int ERROR_CLIENT_LAN_CONNECT_FAIL = MODEL_CORE + ERROR_LAN + 1;
    public static final int ERROR_CLIENT_LAN_AUTH_TIMEOUT = MODEL_CORE + ERROR_LAN + 2;

    /**
     * 发送消息的错误码
     */
    public static final int ERROR_MESSAGE_FORMAT_ERROR = MODEL_CORE + MESSAGE + 1;
    public static final int ERROR_MESSAGE_MESSAGE_NULL = MODEL_CORE + MESSAGE + 2;
    public static final int ERROR_MESSAGE_APP_ID_NULL = MODEL_CORE + MESSAGE + 3;
    public static final int ERROR_MESSAGE_NO_PARAM = MODEL_CORE + MESSAGE + 4;
    public static final int ERROR_MESSAGE_NO_CONNECTION = MODEL_CORE + MESSAGE + 5;

    /**
     * 配网产生的错误
     */
    public static final int ERROR_CONFIG_NO_CONFIG_DEPENDENT_DEVICE = MODEL_CONFIG + ERROR_COMMON + 1;
    public static final int ERROR_CONFIG_NO_CONFIG_SUB_DEVICE = MODEL_CONFIG + ERROR_COMMON + 2;

    /**
     * Crosswalk中的错误
     */
    public static final int ERROR_WEB_LOAD_ERROR = MODEL_WEB + ERROR_COMMON + 1;

    /**
     * 网络请求的错误
     */
    public static final int ERROR_HTTP_CONNECTION_ERROR = MODEL_CORE + ERROR_HTTP + 1;

    public static final Map<Integer, String> errMap = new TreeMap<>();

    static {
        errMap.put(ERROR_CLIENT_LAN_CONNECT_FAIL, "Fail to connect device");
        errMap.put(ERROR_CLIENT_LAN_AUTH_TIMEOUT, "Device auth timeout");

        errMap.put(ERROR_CONFIG_NO_CONFIG_DEPENDENT_DEVICE, "Quest config info fail for dependent device");
        errMap.put(ERROR_CONFIG_NO_CONFIG_SUB_DEVICE, "Receive config message timeout for sub device");

        errMap.put(ERROR_WEB_LOAD_ERROR, "Load page error");

        errMap.put(ERROR_MESSAGE_FORMAT_ERROR, "Json format is incorrect");
        errMap.put(ERROR_MESSAGE_MESSAGE_NULL, "Message is null");
        errMap.put(ERROR_MESSAGE_APP_ID_NULL, "App ID is null");
        errMap.put(ERROR_MESSAGE_NO_PARAM, "No param found in the message");
        errMap.put(ERROR_MESSAGE_NO_CONNECTION, "No connection found when send message");

        errMap.put(ERROR_HTTP_CONNECTION_ERROR, "Connection exception");

    }

    public static String code2Desc(int code) {
        return errMap.get(code);
    }
}

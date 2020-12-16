package me.siter.sdk.utils;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * <p>
 * 云端的错误码Map
 */
class CloudErrorMap {
    /**
     * xx000000 is a representative of model number
     */
    public static final int MODEL_ALL = 0;
    public static final int MODEL_EMBEDDED = 1000000;
    public static final int MODEL_PUSH = 2_000_000;
    public static final int MODEL_UAA = 3000000;
    public static final int MODEL_CONNECT_MANAGER = 4000000;
    public static final int MODEL_INTERNAL_API = 5000000;
    public static final int MODEL_WEB_API = 6000000;
    public static final int MODEL_IFTTT = 7000000;
    public static final int MODEL_CONSOLE = 8000000;
    public static final int MODEL_GDI = 9000000;
    public static final int MODEL_WECHAT = 10000000;
    /**
     * 00xxx000 is a representative of exception level
     */
    public static final int ERROR_OK = 200000;
    public static final int ERROR_ERROR = 400000;
    public static final int ERROR_INTERNAL_ERROR = 500000;

    public final static int ERROR_ALL_CLIENT_ERROR = MODEL_EMBEDDED + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_ALL_CLIENT_NO_SUCH_REQUEST_HANDLING_METHOD_ERROR = MODEL_ALL + ERROR_ERROR + 1;
    public final static int ERROR_ALL_CLIENT_HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR = MODEL_ALL + ERROR_ERROR + 2;
    public final static int ERROR_ALL_CLIENT_HTTP_MEDIA_TYPE_NOT_SUPPORTED_ERROR = MODEL_ALL + ERROR_ERROR + 3;
    public final static int ERROR_ALL_CLIENT_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_ERROR = MODEL_ALL + ERROR_ERROR + 4;
    public final static int ERROR_ALL_CLIENT_MISSING_PATH_VARIABLE_ERROR = MODEL_ALL + ERROR_ERROR + 5;
    public final static int ERROR_ALL_CLIENT_MISSING_SERVLET_REQUEST_PARAMETER_ERROR = MODEL_ALL + ERROR_ERROR + 6;
    public final static int ERROR_ALL_CLIENT_SERVLET_REQUEST_BINDING_ERROR = MODEL_ALL + ERROR_ERROR + 7;
    public final static int ERROR_ALL_CLIENT_CONVERSION_NOT_SUPPORTED_ERROR = MODEL_ALL + ERROR_ERROR + 8;
    public final static int ERROR_ALL_CLIENT_HTTP_MESSAGE_NOT_READABLE_ERROR = MODEL_ALL + ERROR_ERROR + 9;
    public final static int ERROR_ALL_CLIENT_HTTP_MESSAGE_NOT_WRITABLE_ERROR = MODEL_ALL + ERROR_ERROR + 10;
    public final static int ERROR_ALL_CLIENT_MISSING_SERVLET_REQUEST_PART_ERROR = MODEL_ALL + ERROR_ERROR + 11;
    public final static int ERROR_ALL_CLIENT_NO_HANDLER_FOUND_ERROR = MODEL_ALL + ERROR_ERROR + 12;
    public final static int ERROR_ALL_CLIENT_BIND_ERROR = MODEL_ALL + ERROR_ERROR + 13;
    public final static int ERROR_ALL_CLIENT_METHOD_ARGUMENT_NOT_VALID_ERROR = MODEL_ALL + ERROR_ERROR + 14;
    public final static int ERROR_ALL_CLIENT_TYPE_MISMATCH_ERROR = MODEL_ALL + ERROR_ERROR + 15;
    public final static int ERROR_ALL_CLIENT_RATE_LIMIT_EXCEEDED_ERROR = MODEL_ALL + ERROR_ERROR + 16;
    public final static int ERROR_ALL_CLIENT_TIMES_LIMIT_EXCEEDED_ERROR = MODEL_ALL + ERROR_ERROR + 17;
    public final static int ERROR_ALL_CLIENT_INVALID_ID_ERROR = MODEL_ALL + ERROR_ERROR + 18;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 1xxxxxx is a specific exception code of UAA service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_EMBEDDED_CLIENT_OK = MODEL_EMBEDDED + ERROR_OK /*+ 0*/;
    /**
     * exception level exception
     */
    public final static int ERROR_EMBEDDED_CLIENT_ERROR = MODEL_EMBEDDED + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_EMBEDDED_CLIENT_JSON_PARSE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 1;
    public final static int ERROR_EMBEDDED_CLIENT_JWT_PARSE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 2;
    public final static int ERROR_EMBEDDED_CLIENT_FIELD_HIGH_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 3;
    public final static int ERROR_EMBEDDED_CLIENT_FIELD_LOW_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 4;
    public final static int ERROR_EMBEDDED_CLIENT_FIELD_NOT_ENUMERATION_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 5;
    public final static int ERROR_EMBEDDED_CLIENT_FIELD_NOT_EXIST_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 6;
    public final static int ERROR_EMBEDDED_CLIENT_PARAM_IS_INVALID_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 7;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_TID_NOT_MATCH_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 8;
    public final static int ERROR_EMBEDDED_CLIENT_APP_REPEAT_LOGIN_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 9;
    public final static int ERROR_EMBEDDED_CLIENT_USER_NOT_FOUND_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 10;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_NO_HAVE_INSTRUCTION_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 11;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_NOT_BELONG_TO_USER_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 12;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_REPEAT_LOGIN_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 13;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_FRAME_TYPE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 14;
    public final static int ERROR_EMBEDDED_CLIENT_LAST_TOKEN_CANNOT_USE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 15;
    public final static int ERROR_EMBEDDED_CLIENT_BAD_ACTION_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 16;
    public final static int ERROR_EMBEDDED_CLIENT_TOKEN_VERIFICATION_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 17;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_NOT_ONLINE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 18;
    public final static int ERROR_EMBEDDED_CLIENT_APP_NOT_LOGIN_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 19;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_NOT_LOGIN_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 20;
    public final static int ERROR_EMBEDDED_CLIENT_CANNOT_FIND_APP_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 21;
    public final static int ERROR_EMBEDDED_CLIENT_CANNOT_FIND_DEVICE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 22;
    public final static int ERROR_EMBEDDED_CLIENT_APP_TID_NOT_MATCH_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 23;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_REPORT_INFO_NOT_MATCH_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 24;
    public final static int ERROR_EMBEDDED_CLIENT_RAW_NOT_VALID_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 25;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_AUTHKEY_CANNOT_AUTH_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 26;
    public final static int ERROR_EMBEDDED_CLIENT_PRODUCT_KEY_NOT_AVAILABLE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 27;
    public final static int ERROR_EMBEDDED_CLIENT_PINCODE_NOT_AVAILABLE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 28;
    public final static int ERROR_EMBEDDED_CLIENT_BIND_TIMEOUT_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 29;
    public final static int ERROR_EMBEDDED_CLIENT_BIND_OTHER_MANUFACTURE_DEVICE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 30;
    public final static int ERROR_EMBEDDED_CLIENT_DEVICE_CANNOT_FORCE_BIND_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 31;
    public final static int ERROR_EMBEDDED_CLIENT_FIELD_INVALID_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 32;
    public final static int ERROR_EMBEDDED_CLIENT_EXIST_SUB_DEVICE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 33;
    public final static int ERROR_EMBEDDED_CLIENT_CANNOT_FIND_SUB_DEVICE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 34;
    public final static int ERROR_EMBEDDED_CLIENT_SUB_DEVICE_NOT_ONLINE_ERROR = MODEL_EMBEDDED + ERROR_ERROR + 35;
    /**
     * exception level internal error
     */
    public final static int ERROR_EMBEDDED_SERVER_ERROR = MODEL_EMBEDDED + ERROR_INTERNAL_ERROR /*+ 0*/;
    public final static int ERROR_EMBEDDED_SERVER_LINK_ERROR = MODEL_EMBEDDED + ERROR_INTERNAL_ERROR + 1;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 2xxxxxx is a specific exception code of push service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_PUSH_CLIENT_OK = MODEL_PUSH + ERROR_OK /*+ 0*/;
    /**
     * exception level error
     */
    public final static int ERROR_PUSH_CLIENT_ERROR = MODEL_PUSH + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_PUSH_CLIENT_TEMPLATE_ID_NOT_EXIST_ERROR = MODEL_PUSH + ERROR_ERROR + 1;
    public final static int ERROR_PUSH_CLIENT_EMAIL_SEND_ERROR = MODEL_PUSH + ERROR_ERROR + 2;
    /**
     * exception level internal error
     */
    public final static int ERROR_PUSH_SERVER_ERROR = MODEL_PUSH + ERROR_INTERNAL_ERROR/*+ 0*/;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 3xxxxxx is a specific exception code of UAA service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_UAA_CLIENT_OK = MODEL_UAA + ERROR_OK /*+ 0*/;
    /**
     * exception level exception
     */
    public final static int ERROR_UAA_CLIENT_ERROR = MODEL_UAA + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_UAA_CLIENT_INVALID_PHONE_ERROR = MODEL_UAA + ERROR_ERROR + 1;
    public final static int ERROR_UAA_CLIENT_VERIFY_CODE_ERROR = MODEL_UAA + ERROR_ERROR + 2;
    public final static int ERROR_UAA_CLIENT_VERIFY_CODE_EXPIRE_ERROR = MODEL_UAA + ERROR_ERROR + 3;
    public final static int ERROR_UAA_CLIENT_VERIFY_CODE_SEND_TOO_FAST_ERROR = MODEL_UAA + ERROR_ERROR + 4;
    public final static int ERROR_UAA_CLIENT_VERIFY_CODE_SEND_TOO_MANY_ERROR = MODEL_UAA + ERROR_ERROR + 5;
    public final static int ERROR_UAA_CLIENT_INVALID_GET_TYPE_ERROR = MODEL_UAA + ERROR_ERROR + 6;
    public final static int ERROR_UAA_CLIENT_INVALID_OLD_PASSWORD_ERROR = MODEL_UAA + ERROR_ERROR + 7;
    public final static int ERROR_UAA_CLIENT_USER_HAS_BEEN_REGISTER_ERROR = MODEL_UAA + ERROR_ERROR + 8;
    public final static int ERROR_UAA_CLIENT_ACCOUNT_DISABLE_ERROR = MODEL_UAA + ERROR_ERROR + 9;
    public final static int ERROR_UAA_CLIENT_ACCOUNT_CANNOT_AUTHENTICATION_ERROR = MODEL_UAA + ERROR_ERROR + 10;
    public final static int ERROR_UAA_CLIENT_ACCOUNT_CANNOT_FIND_ERROR = MODEL_UAA + ERROR_ERROR + 11;
    public final static int ERROR_UAA_CLIENT_INVALID_EMAIL_TOKEN_ERROR = MODEL_UAA + ERROR_ERROR + 12;
    public final static int ERROR_UAA_CLIENT_ACCOUNT_ALREADY_ENABLE_ERROR = MODEL_UAA + ERROR_ERROR + 13;
    public final static int ERROR_UAA_CLIENT_OAUTH_CANNOT_REPEAT_BIND_ERROR = MODEL_UAA + ERROR_ERROR + 14;
    public final static int ERROR_UAA_CLIENT_INVALID_TOKEN = MODEL_UAA + ERROR_ERROR + 15;
    public final static int ERROR_UAA_CLIENT_ACCOUNT_CANNOT_UPGRADE_ERROR = MODEL_UAA + ERROR_ERROR + 16;
    public final static int ERROR_UAA_CLIENT_INVALID_CAPTCHA_TOKEN_ERROR = MODEL_UAA + ERROR_ERROR + 17;
    public final static int ERROR_UAA_CLIENT_SECURITY_VALIDATION_FAILS_ERROR = MODEL_UAA + ERROR_ERROR + 18;
    public final static int ERROR_UAA_CLIENT_INVALID_TOKEN_ERROR = MODEL_UAA + ERROR_ERROR + 19;
    public final static int ERROR_UAA_CLIENT_INVALID_CAPTCHA_PARAMS_ERROR = MODEL_UAA + ERROR_ERROR + 20;
    public final static int ERROR_UAA_CLIENT_CAPTCHA_CODE_NOT_MATCH_ERROR = MODEL_UAA + ERROR_ERROR + 21;

    /**
     * ys exception
     */
    public final static int ERROR_UAA_CLIENT_GET_YS_ACCESS_TOKEN_REQUEST_FAIL_ERROR = MODEL_UAA + ERROR_ERROR + 22;
    public final static int ERROR_UAA_CLIENT_NOT_OPEN_YS_SERVICE_ERROR = MODEL_UAA + ERROR_ERROR + 23;
    public final static int ERROR_UAA_CLIENT_INVALID_PARAMS_ERROR = MODEL_UAA + ERROR_ERROR + 24;
    public final static int ERROR_UAA_CLIENT_OAUTH_NOT_CONFIG_ERROR = MODEL_UAA + ERROR_ERROR + 25;

    /**
     * exception level internal error
     */
    public final static int ERROR_UAA_SERVER_ERROR = MODEL_UAA + ERROR_INTERNAL_ERROR/*+ 0*/;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 4xxxxxx is a specific exception code of connect manager service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_CONNECT_MANAGER_CLIENT_OK = MODEL_CONNECT_MANAGER + ERROR_OK /*+ 0*/;
    /**
     * exception level exception
     */
    public final static int ERROR_CONNECT_MANAGER_CLIENT_ERROR = MODEL_CONNECT_MANAGER + ERROR_ERROR  /*+ 0*/;
    /**
     * exception level internal error
     */
    public final static int ERROR_CONNECT_MANAGER_SERVER_ERROR = MODEL_CONNECT_MANAGER + ERROR_INTERNAL_ERROR/*+ 0*/;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 5xxxxxx is a specific exception code of internal api service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_INTERNAL_API_CLIENT_OK = MODEL_INTERNAL_API + ERROR_OK /*+ 0*/;

    /**
     * exception level exception
     */
    public final static int ERROR_INTERNAL_API_CLIENT_ERROR = MODEL_INTERNAL_API + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_INTERNAL_API_CLIENT_APP_REPEAT_LOGIN_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 2;
    public final static int ERROR_INTERNAL_API_CLIENT_APP_TID_CAN_NOT_EMPTY_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 3;
    public final static int ERROR_INTERNAL_API_CLIENT_AUTH_EXIST_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 4;
    public final static int ERROR_INTERNAL_API_CLIENT_AUTH_NOT_EXIST_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 5;
    public final static int ERROR_INTERNAL_API_CLIENT_BIND_NET_WORK_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 6;
    public final static int ERROR_INTERNAL_API_CLIENT_BIND_TIMEOUT_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 7;
    public final static int ERROR_INTERNAL_API_CLIENT_CAN_NOT_BIND_OTHER_PID_DEVICE = MODEL_INTERNAL_API + ERROR_ERROR + 8;
    public final static int ERROR_INTERNAL_API_CLIENT_CHANGE_USER_PROFILE_FAILED_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 9;
    public final static int ERROR_INTERNAL_API_CLIENT_CHECK_VERIFY_CODE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 10;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_AUTH_COUNT_REACH_LIMIT_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 11;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_BIND_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 12;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_CAN_NOT_REPEAT_BIND_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 13;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_NOT_BELONG_TO_USER_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 14;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_NO_HAVE_THIS_INSTRUCTION_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 15;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_DEVICE_REPEAT_LOGIN_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 16;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_TID_CAN_NOT_EMPTY_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 17;
    public final static int ERROR_INTERNAL_API_CLIENT_TIMER_TASK_COUNT_REACH_LIMIT_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 18;
    public final static int ERROR_INTERNAL_API_CLIENT_INSTRUCTION_AUTH_EXPIRE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 19;
    public final static int ERROR_INTERNAL_API_CLIENT_INSTRUCTION_NOT_SUPPORT_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 20;
    public final static int ERROR_INTERNAL_API_CLIENT_INVALID_EMAIL_TOKEN_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 21;
    public final static int ERROR_INTERNAL_API_CLIENT_INVALID_OLD_PASSWORD_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 22;
    public final static int ERROR_INTERNAL_API_CLIENT_INVALID_VERIFY_CODE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 23;
    public final static int ERROR_INTERNAL_API_CLIENT_NO_HAVE_DEVICE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 24;
    public final static int ERROR_INTERNAL_API_CLIENT_NO_HAVE_PID_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 25;
    public final static int ERROR_INTERNAL_API_CLIENT_NO_PERMISSION_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 26;
    public final static int ERROR_INTERNAL_API_CLIENT_NO_TEMPLATE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 27;
    public final static int ERROR_INTERNAL_API_CLIENT_NOT_MASTER_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 28;
    public final static int ERROR_INTERNAL_API_CLIENT_TASK_ID_NOT_EXIST_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 35;
    public final static int ERROR_INTERNAL_API_CLIENT_TEMPLATE_EXIST_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 36;
    public final static int ERROR_INTERNAL_API_CLIENT_TID_NOT_MATCH_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 37;
    public final static int ERROR_INTERNAL_API_CLIENT_USER_NOT_FOUND_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 39;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_CAN_NOT_FORCE_BIND_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 43;
    public final static int ERROR_INTERNAL_API_CLIENT_DEVICE_NOT_ONLINE_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 44;

    /**
     * exception level internal error
     */
    public final static int ERROR_INTERNAL_API_SERVER_ERROR = MODEL_INTERNAL_API + ERROR_INTERNAL_ERROR  /*+ 0*/;
    public final static int ERROR_INTERNAL_API_SERVICE_DB_ERROR = MODEL_INTERNAL_API + ERROR_ERROR + 1;
    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 6xxxxxx is a specific exception code of web api service
     */
    /**
     * exception level ok
     */
    public final static int ERROR_WEB_API_CLIENT_OK = MODEL_WEB_API + ERROR_OK /*+ 0*/;

    /**
     * exception level exception
     */
    public final static int ERROR_WEB_API_CLIENT_ERROR = MODEL_WEB_API + ERROR_ERROR  /*+ 0*/;
    public final static int ERROR_WEB_API_CLIENT_DEFINE_REVERSE_AUTH_TEMPLATE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 1;
    public final static int ERROR_WEB_API_CLIENT_INVALID_REVERSE_AUTH_ERROR = MODEL_WEB_API + ERROR_ERROR + 2;
    public final static int ERROR_WEB_API_CLIENT_ONLY_OWNER_CAN_AUTH_ERROR = MODEL_WEB_API + ERROR_ERROR + 3;
    public final static int ERROR_WEB_API_CLIENT_DEVICE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 4;
    public final static int ERROR_WEB_API_CLIENT_FOLDER_CONTAIN_DEVICE_COUNT_REACH_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 5;
    public final static int ERROR_WEB_API_CLIENT_FOLDER_DUPLICATE_ERROR = MODEL_WEB_API + ERROR_ERROR + 6;
    public final static int ERROR_WEB_API_CLIENT_FOLDER_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 7;
    public final static int ERROR_WEB_API_CLIENT_FOLDER_COUNT_REACH_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 8;
    public final static int ERROR_WEB_API_CLIENT_ROOT_FOLDER_CANNOT_DEL_ERROR = MODEL_WEB_API + ERROR_ERROR + 9;
    public final static int ERROR_WEB_API_CLIENT_ROOT_FOLDER_CANNOT_RENAME_ERROR = MODEL_WEB_API + ERROR_ERROR + 10;
    public final static int ERROR_WEB_API_CLIENT_DEFINE_EVENT_RULE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 11;
    public final static int ERROR_WEB_API_CLIENT_DEFINE_SCHEDULER_TASK_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 12;
    public final static int ERROR_WEB_API_CLIENT_EVENT_RULE_DUPLICATED_ERROR = MODEL_WEB_API + ERROR_ERROR + 13;
    public final static int ERROR_WEB_API_CLIENT_SCHEDULER_TASK_DUPLICATED_ERROR = MODEL_WEB_API + ERROR_ERROR + 14;
    public final static int ERROR_WEB_API_CLIENT_INVALID_PPK_ERROR = MODEL_WEB_API + ERROR_ERROR + 15;
    public final static int ERROR_WEB_API_CLIENT_INVOKER_HASH_NO_PRIVILEGE_ERROR = MODEL_WEB_API + ERROR_ERROR + 16;
    public final static int ERROR_WEB_API_CLIENT_WEB_INVALID_PARAM_ERROR = MODEL_WEB_API + ERROR_ERROR + 17;
    public final static int ERROR_WEB_API_CLIENT_CLOUD_STORAGE_FILE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 18;
    public final static int ERROR_WEB_API_CLIENT_INVALID_PRODPUBKEY_ERROR = MODEL_WEB_API + ERROR_ERROR + 19;

    public final static int ERROR_WEB_API_CLIENT_INFRARED_CANNOT_FIND_ERROR = MODEL_WEB_API + ERROR_ERROR + 20;
    public final static int ERROR_WEB_API_CLIENT_INFRARED_THIRD_PARTY_REQUEST_ERROR = MODEL_WEB_API + ERROR_ERROR + 21;
    public final static int ERROR_WEB_API_CLIENT_INFRARED_CANNOT_FIND_INST_SET_ERROR = MODEL_WEB_API + ERROR_ERROR + 22;
    public final static int ERROR_WEB_API_CLIENT_INFRARED_PARAM_ERROR = MODEL_WEB_API + ERROR_ERROR + 23;
    public final static int ERROR_WEB_API_CLIENT_INFRARED_TRANSLATING_ERROR = MODEL_WEB_API + ERROR_ERROR + 24;

    public final static int ERROR_WEB_API_CLIENT_NOT_SUPPORT_SCHEDULER_ERROR = MODEL_WEB_API + ERROR_ERROR + 25;
    public final static int ERROR_WEB_API_CLIENT_GROUP_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 26;
    public final static int ERROR_WEB_API_CLIENT_GROUP_CONTAIN_DEVICE_REACH_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 27;
    public final static int ERROR_WEB_API_CLIENT_REACH_CREATE_GROUP_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 28;
    public final static int ERROR_WEB_API_REPEAT_REVERSE_AUTH_REQUEST_ERROR = MODEL_WEB_API + ERROR_ERROR + 29;
    /**
     * subDevice exception
     */
    public final static int ERROR_WEB_API_CLIENT_SUBDEVICE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 30;
    public final static int ERROR_WEB_API_CLIENT_SUBDEVICE_SUPPORT_SCHEDULER_ERROR = MODEL_WEB_API + ERROR_ERROR + 31;
    public final static int ERROR_WEB_API_CLIENT_DEVICE_NOT_HAVE_SUBDEVICE_ERROR = MODEL_WEB_API + ERROR_ERROR + 32;
    /**
     * scene exception
     */
    public final static int ERROR_WEB_API_SCENE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 33;
    public final static int ERROR_WEB_API_SCENE_NAME_ALREADY_EXISTS_ERROR = MODEL_WEB_API + ERROR_ERROR + 34;
    public final static int ERROR_WEB_API_SCENETEMPLATE_MAXIMUM_NUMBER_ERROR = MODEL_WEB_API + ERROR_ERROR + 35;
    public final static int ERROR_WEB_API_SCENE_NUMBER_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 39;
    /**
     * ifffrule exception
     */
    public final static int ERROR_WEB_API_IFTTTRULE_PARAMS_INVALID_ERROR = MODEL_WEB_API + ERROR_ERROR + 36;
    public final static int ERROR_WEB_API_IFTTTRULE_NOT_FOUND_ERROR = MODEL_WEB_API + ERROR_ERROR + 37;
    public final static int ERROR_WEB_API_IFTTTRULE_NAME_REPEAT_ERROR = MODEL_WEB_API + ERROR_ERROR + 38;
    public final static int ERROR_WEB_API_JSON_FORMAT_TO48_ERROR = MODEL_WEB_API + ERROR_ERROR + 40;
    public final static int ERROR_WEB_API_MAIN_DEVICE_NUM_BEYONE_LIMIT_ERROR = MODEL_WEB_API + ERROR_ERROR + 41;
    public final static int ERROR_WEB_API_THIRD_INTERFACE_CALL_FAIL_ERROR = MODEL_WEB_API + ERROR_ERROR + 42;
    /**
     * many device auth
     */
    public final static int ERROR_WEB_API_AUTH_BETWEEN_DIFF_APP_ERROR = MODEL_WEB_API + ERROR_ERROR + 43;
    /**
     * exception level internal error
     */
    public final static int ERROR_WEB_API_SERVER_ERROR = MODEL_WEB_API + ERROR_INTERNAL_ERROR/*+ 0*/;
    public final static int ERROR_WEB_API_SERVER_CLOUD_STORAGE_FILE_DELETE_FAILED_ERROR = MODEL_WEB_API + ERROR_INTERNAL_ERROR + 1;
    public final static int ERROR_WEB_API_SERVER_CLOUD_STORAGE_FILE_UPLOAD_FAILED_ERROR = MODEL_WEB_API + ERROR_INTERNAL_ERROR + 2;
    public final static int ERROR_WEB_API_SERVER_HTTP_CLIENT_ERROR = MODEL_WEB_API + ERROR_INTERNAL_ERROR + 3;


    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 8xxxxxx is a specific exception code of console service
     */
    /**
     * console service exception level : ok
     */
    public final static int ERROR_CONSOLE_CLIENT_OK = MODEL_CONSOLE + ERROR_OK /*+ 0*/;

    /**
     * console service exception level : exception
     */
    public final static int ERROR_CONSOLE_CLIENT_PRODUCT_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR;  /*+ 0*/
    public final static int ERROR_CONSOLE_CLIENT_PROTOCOL_TEMPLATE_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 1;
    public final static int ERROR_CONSOLE_CLIENT_ILLEGAL_ARGUMENT = MODEL_CONSOLE + ERROR_ERROR + 2;
    public final static int ERROR_CONSOLE_CLIENT_ILLEGAL_PLATFORM = MODEL_CONSOLE + ERROR_ERROR + 3;
    public final static int ERROR_CONSOLE_CLIENT_ILLEGAL_PID = MODEL_CONSOLE + ERROR_ERROR + 4;
    public final static int ERROR_CONSOLE_CLIENT_H5_TEMPLATE_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 5;
    public final static int ERROR_CONSOLE_CLIENT_PERMISSION_DENY = MODEL_CONSOLE + ERROR_ERROR + 6;
    public final static int ERROR_CONSOLE_CLIENT_NOT_ALLOW = MODEL_CONSOLE + ERROR_ERROR + 7;
    public final static int ERROR_CONSOLE_CLIENT_OPERATION_ILLEGAL = MODEL_CONSOLE + ERROR_ERROR + 8;
    public final static int ERROR_CONSOLE_CLIENT_PARAMETER__NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 9;
    public final static int ERROR_CONSOLE_CLIENT_NAME__REPETITION = MODEL_CONSOLE + ERROR_ERROR + 10;
    public final static int ERROR_CONSOLE_CLIENT_MODULE_TYPE_DUPLICATE = MODEL_CONSOLE + ERROR_ERROR + 11;
    public final static int ERROR_CONSOLE_CLIENT_FIRMWARE_ALREADY_EXISTS = MODEL_CONSOLE + ERROR_ERROR + 12;
    public final static int ERROR_CONSOLE_CLIENT_FIRMWARE_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 13;
    public final static int ERROR_CONSOLE_CLIENT_PUSH_ACCOUNT_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 14;
    public final static int ERROR_CONSOLE_CLIENT_NO_CERTIFICATION = MODEL_CONSOLE + ERROR_ERROR + 14;
    public final static int ERROR_CONSOLE_CLIENT_STATUS_CHANGED = MODEL_CONSOLE + ERROR_ERROR + 15;

    public final static int ERROR_CONSOLE_CLIENT_PRODUCT_NOT_EDITABLE = MODEL_CONSOLE + ERROR_ERROR + 16;
    public final static int ERROR_CONSOLE_CLIENT_PRODUCT_FIELD_INVALID = MODEL_CONSOLE + ERROR_ERROR + 17;
    public final static int ERROR_CONSOLE_CLIENT_CUSTOM_APP_USE_CONFLICT = MODEL_CONSOLE + ERROR_ERROR + 18;

    public final static int ERROR_CONSOLE_CLIENT_IS_NOT_GATEWAY_PRODUCT = MODEL_CONSOLE + ERROR_ERROR + 19;

    public final static int ERROR_CONSOLE_CLIENT_SMS_ACCOUNT_ALREADY_EXISTS = MODEL_CONSOLE + ERROR_ERROR + 20;

    public final static int ERROR_CONSOLE_CLIENT_USER_NOT_EXIST = MODEL_CONSOLE + ERROR_ERROR + 21;
    public final static int ERROR_CONSOLE_CLIENT_PROTOCOL_QUICK_OPERATION_CONFIG_DUPLICATED = MODEL_CONSOLE + ERROR_ERROR + 22;
    public final static int ERROR_CONSOLE_CLIENT_PROTOCOL_QUICK_OPERATION_CONFIG_TOO_MANY = MODEL_CONSOLE + ERROR_ERROR + 23;
    public final static int ERROR_CONSOLE_CLIENT_PROTOCOL_QUICK_OPERATION_CONFIG_NOT_EXISTS = MODEL_CONSOLE + ERROR_ERROR + 24;

    /**
     * console service exception level : internal error
     */
    public final static int ERROR_CONSOLE_SERVER_ERROR = MODEL_CONSOLE + ERROR_INTERNAL_ERROR/*+ 0*/;

    /*------------------------------------------------------------------------------------------------------------------*/
    /**
     * 9xxxxxx is a specific exception code of gdi service
     */
    /**
     * gdi service exception level : ok
     */
    public final static int ERROR_GDI_CLIENT_OK = MODEL_GDI + ERROR_OK;

    /**
     * gdi service exception level : exception
     */
    public final static int ERROR_GDI_CLIENT_ERROR = MODEL_GDI + ERROR_ERROR;
    public final static int ERROR_GDI_CLIENT_BAD_PARAM = MODEL_GDI + ERROR_ERROR + 1;
    public final static int ERROR_GDI_CLIENT_ACTION_NOT_EXIST = MODEL_GDI + ERROR_ERROR + 2;
    public final static int ERROR_GDI_CLIENT_PRODUCT_NOT_EXIST = MODEL_GDI + ERROR_ERROR + 3;
    public final static int ERROR_GDI_CLIENT_TIME_OUT = MODEL_GDI + ERROR_ERROR + 4;
    public final static int ERROR_GDI_CLIENT_METHOD_NOT_SUPPORT = MODEL_GDI + ERROR_ERROR + 5;

    /**
     * gdi service exception level : internal error
     */
    public final static int ERROR_GDI_SERVER_ERROR = MODEL_GDI + ERROR_INTERNAL_ERROR;
    public final static int ERROR_GDI_SERVER_RESPONSE_ERROR = MODEL_GDI + ERROR_INTERNAL_ERROR + 1;

    /*------------------------------------------------------------------------------------------------------------------*/

    /**
     * 10xxxxxx is a specific exception code of gdi service
     */
    /**
     * wechat service exception level : ok
     */
    public final static int ERROR_WECHAT_CLIENT_OK = MODEL_WECHAT + ERROR_OK;

    /**
     * gdi service exception level : exception
     */
    public final static int ERROR_WECHAT_CLIENT_ERROR = MODEL_WECHAT + ERROR_ERROR;
    public final static int ERROR_WECHAT_CLIENT_USER_NOT_EXIST_ERROR = MODEL_WECHAT + ERROR_ERROR + 1;
    public final static int ERROR_WECHAT_CLIENT_USER_NOT_CERTIFIED_ERROR = MODEL_WECHAT + ERROR_ERROR + 2;
    public final static int ERROR_WECHAT_CLIENT_MENU_NOT_EXIST_ERROR = MODEL_WECHAT + ERROR_ERROR + 3;
    public final static int ERROR_WECHAT_CLIENT_ORIGINAL_ID_IS_EXIST = MODEL_WECHAT + ERROR_ERROR + 4;

    /**
     * gdi service exception level : internal error
     */
    public final static int ERROR_WECHAT_SERVER_ERROR = MODEL_WECHAT + ERROR_INTERNAL_ERROR;

    /*------------------------------------------------------------------------------------------------------------------*/
    private static final Map<Integer, String> errMap = new TreeMap<>();

    static {
        errMap.put(ERROR_ALL_CLIENT_NO_SUCH_REQUEST_HANDLING_METHOD_ERROR, "Not found");
        errMap.put(ERROR_ALL_CLIENT_HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR, "Method not allowed");
        errMap.put(ERROR_ALL_CLIENT_HTTP_MEDIA_TYPE_NOT_SUPPORTED_ERROR, "Unsupported media type");
        errMap.put(ERROR_ALL_CLIENT_HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_ERROR, "Not acceptable");
        errMap.put(ERROR_ALL_CLIENT_MISSING_PATH_VARIABLE_ERROR, "URI path error");
        errMap.put(ERROR_ALL_CLIENT_MISSING_SERVLET_REQUEST_PARAMETER_ERROR, "Required parameter is not present");
        errMap.put(ERROR_ALL_CLIENT_SERVLET_REQUEST_BINDING_ERROR, "Request bind error");
        errMap.put(ERROR_ALL_CLIENT_CONVERSION_NOT_SUPPORTED_ERROR, "No suitable editor or converter can be found for property");
        errMap.put(ERROR_ALL_CLIENT_HTTP_MESSAGE_NOT_READABLE_ERROR, "Read method failed");
        errMap.put(ERROR_ALL_CLIENT_HTTP_MESSAGE_NOT_WRITABLE_ERROR, "Write method failed");
        errMap.put(ERROR_ALL_CLIENT_MISSING_SERVLET_REQUEST_PART_ERROR,
                "Part of a \"multipart/form-data\" request identified by its name cannot be found");
        errMap.put(ERROR_ALL_CLIENT_NO_HANDLER_FOUND_ERROR, "DispatcherServlet can't find a handler for a request");
        errMap.put(ERROR_ALL_CLIENT_BIND_ERROR, "Argument bind error");
        errMap.put(ERROR_ALL_CLIENT_METHOD_ARGUMENT_NOT_VALID_ERROR, "Invalid method");
        errMap.put(ERROR_ALL_CLIENT_TYPE_MISMATCH_ERROR, "Argument type error");
        errMap.put(ERROR_ALL_CLIENT_RATE_LIMIT_EXCEEDED_ERROR, "Rate limit exceeded");
        errMap.put(ERROR_ALL_CLIENT_TIMES_LIMIT_EXCEEDED_ERROR, "Times limit exceeded");
        errMap.put(ERROR_ALL_CLIENT_INVALID_ID_ERROR, "id is invalid");

        errMap.put(ERROR_EMBEDDED_CLIENT_OK, "Success");
        errMap.put(ERROR_EMBEDDED_CLIENT_JSON_PARSE_ERROR, "Json parse error");
        errMap.put(ERROR_EMBEDDED_CLIENT_JWT_PARSE_ERROR, "JWT parse error");
        errMap.put(ERROR_EMBEDDED_CLIENT_FIELD_HIGH_ERROR, "The field {0} contains a value that is too high");
        errMap.put(ERROR_EMBEDDED_CLIENT_FIELD_LOW_ERROR, "The field {0} contains a value that is too low");
        errMap.put(ERROR_EMBEDDED_CLIENT_FIELD_NOT_ENUMERATION_ERROR, "The value of the field {0} must be an enumerated value");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_TID_NOT_MATCH_ERROR, "DevTid not match");
        errMap.put(ERROR_EMBEDDED_CLIENT_APP_REPEAT_LOGIN_ERROR, "App repeat login");
        errMap.put(ERROR_EMBEDDED_CLIENT_USER_NOT_FOUND_ERROR, "User does not exist");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_NO_HAVE_INSTRUCTION_ERROR, "The device does not have this instruction");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_NOT_BELONG_TO_USER_ERROR, "Device does not belong to user");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_REPEAT_LOGIN_ERROR, "Device repeat login");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_FRAME_TYPE_ERROR, "Frame parse error");
        errMap.put(ERROR_EMBEDDED_CLIENT_LAST_TOKEN_CANNOT_USE_ERROR, "Device last token can not use");
        errMap.put(ERROR_EMBEDDED_CLIENT_BAD_ACTION_ERROR, "Action not support");
        errMap.put(ERROR_EMBEDDED_CLIENT_TOKEN_VERIFICATION_ERROR, "Device token can not verification");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_NOT_ONLINE_ERROR, "Device not online");
        errMap.put(ERROR_EMBEDDED_CLIENT_CANNOT_FIND_DEVICE_ERROR, "The device is not found");
        errMap.put(ERROR_EMBEDDED_SERVER_ERROR, "Internal error ({0})");
        errMap.put(ERROR_EMBEDDED_CLIENT_FIELD_NOT_EXIST_ERROR, "Field {0} not exist");
        errMap.put(ERROR_EMBEDDED_CLIENT_PARAM_IS_INVALID_ERROR, "{0}");
        errMap.put(ERROR_EMBEDDED_SERVER_LINK_ERROR, "Link error");
        errMap.put(ERROR_EMBEDDED_CLIENT_RAW_NOT_VALID_ERROR, "RAW not valid, Please check your protocol template");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_REPORT_INFO_NOT_MATCH_ERROR, "You report info does not match your connect server");
        errMap.put(ERROR_EMBEDDED_CLIENT_APP_TID_NOT_MATCH_ERROR, "AppTid does not match");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_AUTHKEY_CANNOT_AUTH_ERROR, "AuthKey can't auth");
        errMap.put(ERROR_EMBEDDED_CLIENT_PRODUCT_KEY_NOT_AVAILABLE_ERROR, "Product key not available");
        errMap.put(ERROR_EMBEDDED_CLIENT_PINCODE_NOT_AVAILABLE_ERROR, "PinCode or SSID not available");
        errMap.put(ERROR_EMBEDDED_CLIENT_BIND_TIMEOUT_ERROR, "Bind failed due to timeout error");
        errMap.put(ERROR_EMBEDDED_CLIENT_BIND_OTHER_MANUFACTURE_DEVICE_ERROR, "Can not bind other manufacture's device");
        errMap.put(ERROR_EMBEDDED_CLIENT_FIELD_INVALID_ERROR, "Field invalid");
        errMap.put(ERROR_EMBEDDED_CLIENT_DEVICE_CANNOT_FORCE_BIND_ERROR, "Can not force bind device");
        errMap.put(ERROR_EMBEDDED_CLIENT_APP_NOT_LOGIN_ERROR, "App not login");
        errMap.put(ERROR_EMBEDDED_CLIENT_EXIST_SUB_DEVICE_ERROR, "The sub device already exists");
        errMap.put(ERROR_EMBEDDED_CLIENT_CANNOT_FIND_SUB_DEVICE_ERROR, "The sub device is not found");
        errMap.put(ERROR_EMBEDDED_CLIENT_SUB_DEVICE_NOT_ONLINE_ERROR, "The sub device not online");

        errMap.put(ERROR_PUSH_CLIENT_OK, "Success");
        errMap.put(ERROR_PUSH_CLIENT_ERROR, "Error");
        errMap.put(ERROR_PUSH_CLIENT_TEMPLATE_ID_NOT_EXIST_ERROR, "Template id does not exist");
        errMap.put(ERROR_PUSH_CLIENT_EMAIL_SEND_ERROR, "Email send error");
        errMap.put(ERROR_PUSH_SERVER_ERROR, "Internal error");

        errMap.put(ERROR_UAA_CLIENT_INVALID_PHONE_ERROR, "Phone number invalid");
        errMap.put(ERROR_UAA_CLIENT_VERIFY_CODE_ERROR, "Verify code error");
        errMap.put(ERROR_UAA_CLIENT_VERIFY_CODE_EXPIRE_ERROR, "Verify code expire");
        errMap.put(ERROR_UAA_CLIENT_VERIFY_CODE_SEND_TOO_FAST_ERROR, "Verify code send too fast");
        errMap.put(ERROR_UAA_CLIENT_VERIFY_CODE_SEND_TOO_MANY_ERROR, "Verify code send too many");
        errMap.put(ERROR_UAA_CLIENT_INVALID_GET_TYPE_ERROR, "Invalid request type");
        errMap.put(ERROR_UAA_CLIENT_INVALID_OLD_PASSWORD_ERROR, "Invalid old password");
        errMap.put(ERROR_UAA_CLIENT_USER_HAS_BEEN_REGISTER_ERROR, "User has been registered");
        errMap.put(ERROR_UAA_CLIENT_ACCOUNT_DISABLE_ERROR, "Your account has not been verified");
        errMap.put(ERROR_UAA_CLIENT_ACCOUNT_CANNOT_AUTHENTICATION_ERROR,
                "Could not authenticate user. Verify the user name and password, and try again");
        errMap.put(ERROR_UAA_CLIENT_ACCOUNT_CANNOT_FIND_ERROR, "User does not exist");
        errMap.put(ERROR_UAA_CLIENT_INVALID_EMAIL_TOKEN_ERROR, "The token is expired or broken");
        errMap.put(ERROR_UAA_CLIENT_ACCOUNT_ALREADY_ENABLE_ERROR, "Your account has already been verified");
        errMap.put(ERROR_UAA_CLIENT_OAUTH_CANNOT_REPEAT_BIND_ERROR, "Your account has already been linked to a {0} account");
        errMap.put(ERROR_UAA_CLIENT_INVALID_TOKEN, "Invalid token");
        errMap.put(ERROR_UAA_CLIENT_ACCOUNT_CANNOT_UPGRADE_ERROR, "Your account has been upgraded");
        errMap.put(ERROR_UAA_CLIENT_INVALID_CAPTCHA_TOKEN_ERROR, "The token is expired or broken");
        errMap.put(ERROR_UAA_CLIENT_SECURITY_VALIDATION_FAILS_ERROR, "{0}");
        errMap.put(ERROR_UAA_CLIENT_INVALID_TOKEN_ERROR, "{0}");
        errMap.put(ERROR_UAA_CLIENT_INVALID_CAPTCHA_PARAMS_ERROR, "{0}");
        errMap.put(ERROR_UAA_CLIENT_CAPTCHA_CODE_NOT_MATCH_ERROR, "captch code can not match");
        errMap.put(ERROR_UAA_CLIENT_GET_YS_ACCESS_TOKEN_REQUEST_FAIL_ERROR, "{0}");
        errMap.put(ERROR_UAA_CLIENT_NOT_OPEN_YS_SERVICE_ERROR, "not open ys service");
        errMap.put(ERROR_UAA_CLIENT_INVALID_PARAMS_ERROR, "{0}");

        errMap.put(ERROR_INTERNAL_API_CLIENT_OK, "Success");
        errMap.put(ERROR_INTERNAL_API_CLIENT_APP_REPEAT_LOGIN_ERROR, "App repeat login error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_APP_TID_CAN_NOT_EMPTY_ERROR, "AppTid can not be empty");
        errMap.put(ERROR_INTERNAL_API_CLIENT_AUTH_EXIST_ERROR, "Authorization already exists");
        errMap.put(ERROR_INTERNAL_API_CLIENT_AUTH_NOT_EXIST_ERROR, "Authorization does not exist");
        errMap.put(ERROR_INTERNAL_API_CLIENT_BIND_NET_WORK_ERROR, "Bind failed due to network error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_BIND_TIMEOUT_ERROR, "Bind failed due to timeout error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_CAN_NOT_BIND_OTHER_PID_DEVICE, "Can not bind other manufacture's device");
        errMap.put(ERROR_INTERNAL_API_CLIENT_CHANGE_USER_PROFILE_FAILED_ERROR, "Modified user profile failed");
        errMap.put(ERROR_INTERNAL_API_CLIENT_CHECK_VERIFY_CODE_ERROR, "Check verify code error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_AUTH_COUNT_REACH_LIMIT_ERROR, "You have reached your device's authorization limits");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_BIND_ERROR, "Bind failed due to internal error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_CAN_NOT_REPEAT_BIND_ERROR, "Bind failed due to repeat bind");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_NOT_BELONG_TO_USER_ERROR, "Device does not belong to user");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_NO_HAVE_THIS_INSTRUCTION_ERROR, "No such instruction error");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_DEVICE_REPEAT_LOGIN_ERROR, "Device can not repeat login");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_TID_CAN_NOT_EMPTY_ERROR, "devTid can not be empty");
        errMap.put(ERROR_INTERNAL_API_CLIENT_TIMER_TASK_COUNT_REACH_LIMIT_ERROR, "Create timer task failed due to counts reach limit");
        errMap.put(ERROR_INTERNAL_API_CLIENT_INSTRUCTION_AUTH_EXPIRE_ERROR, "Instruction expired");
        errMap.put(ERROR_INTERNAL_API_CLIENT_INSTRUCTION_NOT_SUPPORT_ERROR, "Instruction not support");
        errMap.put(ERROR_INTERNAL_API_CLIENT_INVALID_EMAIL_TOKEN_ERROR, "Invalid email token");
        errMap.put(ERROR_INTERNAL_API_CLIENT_INVALID_OLD_PASSWORD_ERROR, "Invalid old password");
        errMap.put(ERROR_INTERNAL_API_CLIENT_INVALID_VERIFY_CODE_ERROR, "Invalid verify code");
        errMap.put(ERROR_INTERNAL_API_CLIENT_NO_HAVE_DEVICE_ERROR, "Device does not found due to internal error, please reconnect");
        errMap.put(ERROR_INTERNAL_API_CLIENT_NO_HAVE_PID_ERROR, "No such manufacture id");
        errMap.put(ERROR_INTERNAL_API_CLIENT_NO_PERMISSION_ERROR, "No permission to access the instruction");
        errMap.put(ERROR_INTERNAL_API_CLIENT_NO_TEMPLATE_ERROR, "Template with given id does not exist");
        errMap.put(ERROR_INTERNAL_API_CLIENT_NOT_MASTER_ERROR, "Device does not found due to incorrect status");
        errMap.put(ERROR_INTERNAL_API_CLIENT_TASK_ID_NOT_EXIST_ERROR, "TaskId does not exist");
        errMap.put(ERROR_INTERNAL_API_CLIENT_TEMPLATE_EXIST_ERROR, "Can not create duplicate template");
        errMap.put(ERROR_INTERNAL_API_CLIENT_TID_NOT_MATCH_ERROR, "DevTid not match");
        errMap.put(ERROR_INTERNAL_API_CLIENT_USER_NOT_FOUND_ERROR, "User does not exist");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_CAN_NOT_FORCE_BIND_ERROR, "Device can not force bind");
        errMap.put(ERROR_INTERNAL_API_CLIENT_DEVICE_NOT_ONLINE_ERROR, "device is not online");
        errMap.put(ERROR_INTERNAL_API_SERVICE_DB_ERROR, "Internal error");

        errMap.put(ERROR_WEB_API_CLIENT_OK, "Success");
        errMap.put(ERROR_WEB_API_CLIENT_WEB_INVALID_PARAM_ERROR, "{0}");
        errMap.put(ERROR_WEB_API_CLIENT_DEFINE_REVERSE_AUTH_TEMPLATE_NOT_FOUND_ERROR, "Reverse auth template with given id does not exist");
        errMap.put(ERROR_WEB_API_CLIENT_INVALID_REVERSE_AUTH_ERROR, "Invalid reverse authorization request");
        errMap.put(ERROR_WEB_API_CLIENT_ONLY_OWNER_CAN_AUTH_ERROR, "Only owner can authorize");
        errMap.put(ERROR_WEB_API_CLIENT_DEVICE_NOT_FOUND_ERROR, "{0}");
        errMap.put(ERROR_WEB_API_CLIENT_FOLDER_CONTAIN_DEVICE_COUNT_REACH_LIMIT_ERROR, "Reached the maximum number of device the folder can hold");
        errMap.put(ERROR_WEB_API_CLIENT_FOLDER_DUPLICATE_ERROR, "Can not create duplicate template folder");
        errMap.put(ERROR_WEB_API_CLIENT_FOLDER_NOT_FOUND_ERROR, "Folder with given id does not exist");
        errMap.put(ERROR_WEB_API_CLIENT_FOLDER_COUNT_REACH_LIMIT_ERROR, "Reached the maximum number of folder the user can create");
        errMap.put(ERROR_WEB_API_CLIENT_ROOT_FOLDER_CANNOT_DEL_ERROR, "Can not remove root folder");
        errMap.put(ERROR_WEB_API_CLIENT_ROOT_FOLDER_CANNOT_RENAME_ERROR, "Can not rename root folder");
        errMap.put(ERROR_WEB_API_CLIENT_DEFINE_EVENT_RULE_NOT_FOUND_ERROR, "Rule with given id does not exist");
        errMap.put(ERROR_WEB_API_CLIENT_DEFINE_SCHEDULER_TASK_NOT_FOUND_ERROR, "Scheduler task with given id does not exist");
        errMap.put(ERROR_WEB_API_CLIENT_EVENT_RULE_DUPLICATED_ERROR, "Can not create duplicate rule");
        errMap.put(ERROR_WEB_API_CLIENT_SCHEDULER_TASK_DUPLICATED_ERROR, "Can not create duplicate scheduler task");
        errMap.put(ERROR_WEB_API_CLIENT_INVALID_PPK_ERROR, "Invalid prodPubKey");
        errMap.put(ERROR_WEB_API_CLIENT_INVOKER_HASH_NO_PRIVILEGE_ERROR, "Has no privilege do that");
        errMap.put(ERROR_WEB_API_CLIENT_CLOUD_STORAGE_FILE_NOT_FOUND_ERROR, "Cloud storage file with given name does not exist");
        errMap.put(ERROR_WEB_API_SERVER_CLOUD_STORAGE_FILE_DELETE_FAILED_ERROR, "Delete cloud storage file failed");
        errMap.put(ERROR_WEB_API_SERVER_CLOUD_STORAGE_FILE_UPLOAD_FAILED_ERROR, "Upload cloud storage file failed");
        errMap.put(ERROR_WEB_API_SERVER_HTTP_CLIENT_ERROR, "Server use httpClient invoke failed");
        errMap.put(ERROR_WEB_API_CLIENT_INVALID_PRODPUBKEY_ERROR, "Invalid prodPubKey");

        errMap.put(ERROR_WEB_API_CLIENT_INFRARED_CANNOT_FIND_ERROR, "Can not find this infrared code");
        errMap.put(ERROR_WEB_API_CLIENT_INFRARED_THIRD_PARTY_REQUEST_ERROR, "Infrared code request without response");
        errMap.put(ERROR_WEB_API_CLIENT_INFRARED_CANNOT_FIND_INST_SET_ERROR, "Can not find instruction set");
        errMap.put(ERROR_WEB_API_CLIENT_INFRARED_PARAM_ERROR, "Request params not supported");
        errMap.put(ERROR_WEB_API_CLIENT_INFRARED_TRANSLATING_ERROR, "Translating Json to String failure");
        errMap.put(ERROR_WEB_API_CLIENT_NOT_SUPPORT_SCHEDULER_ERROR, "Mobile not support Scheduler");
        errMap.put(ERROR_WEB_API_CLIENT_GROUP_NOT_FOUND_ERROR, "Group not found");
        errMap.put(ERROR_WEB_API_CLIENT_GROUP_CONTAIN_DEVICE_REACH_LIMIT_ERROR, "Reached the maximum number of device the group can hold");
        errMap.put(ERROR_WEB_API_CLIENT_REACH_CREATE_GROUP_LIMIT_ERROR, "Reached the limit of create group");
        errMap.put(ERROR_WEB_API_REPEAT_REVERSE_AUTH_REQUEST_ERROR, "had already request reverse auth, can't repeat request");
        errMap.put(ERROR_WEB_API_CLIENT_SUBDEVICE_SUPPORT_SCHEDULER_ERROR, "subDevice of different mid not support Scheduler");
        errMap.put(ERROR_WEB_API_CLIENT_SUBDEVICE_NOT_FOUND_ERROR, "{0}");
        errMap.put(ERROR_WEB_API_CLIENT_DEVICE_NOT_HAVE_SUBDEVICE_ERROR, "Device not have subDevice");
        errMap.put(ERROR_WEB_API_SCENE_NOT_FOUND_ERROR, "Scene not found");
        errMap.put(ERROR_WEB_API_SCENE_NAME_ALREADY_EXISTS_ERROR, "Scene name had already exists, not repeat");
        errMap.put(ERROR_WEB_API_SCENETEMPLATE_MAXIMUM_NUMBER_ERROR, "Scene template number greater than ten");
        errMap.put(ERROR_WEB_API_SCENE_NUMBER_LIMIT_ERROR, "Scene Number is beyond the limit");
        errMap.put(ERROR_WEB_API_IFTTTRULE_PARAMS_INVALID_ERROR, "{0}");
        errMap.put(ERROR_WEB_API_IFTTTRULE_NOT_FOUND_ERROR, "iftttRule not found");
        errMap.put(ERROR_WEB_API_IFTTTRULE_NAME_REPEAT_ERROR, "iftttRule name had already exists");
        errMap.put(ERROR_WEB_API_JSON_FORMAT_TO48_ERROR, "{0}");
        errMap.put(ERROR_WEB_API_MAIN_DEVICE_NUM_BEYONE_LIMIT_ERROR, "main device num is beyond limit");
        errMap.put(ERROR_WEB_API_AUTH_BETWEEN_DIFF_APP_ERROR, "No authorization between different app");
        errMap.put(ERROR_WEB_API_THIRD_INTERFACE_CALL_FAIL_ERROR, "{0}");

        errMap.put(ERROR_CONSOLE_CLIENT_OK, "Success");
        errMap.put(ERROR_CONSOLE_CLIENT_PRODUCT_NOT_EXIST, "Product does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_PROTOCOL_TEMPLATE_NOT_EXIST, "Protocol template does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_ILLEGAL_ARGUMENT, "Illegal argument");
        errMap.put(ERROR_CONSOLE_CLIENT_ILLEGAL_PLATFORM, "The param of platform is not illegal. The client should be Android or IOS");
        errMap.put(ERROR_CONSOLE_CLIENT_ILLEGAL_PID, "The pid does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_H5_TEMPLATE_NOT_EXIST, "The h5 template does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_OPERATION_ILLEGAL, "Operation illegal");
        errMap.put(ERROR_CONSOLE_CLIENT_PARAMETER__NOT_EXIST, "Parameter not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_NAME__REPETITION, "Name repetition");
        errMap.put(ERROR_CONSOLE_CLIENT_MODULE_TYPE_DUPLICATE, "Module type duplicate");
        errMap.put(ERROR_CONSOLE_CLIENT_FIRMWARE_ALREADY_EXISTS, "The firmware already exists");
        errMap.put(ERROR_CONSOLE_CLIENT_FIRMWARE_NOT_EXIST, "The firmware does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_PUSH_ACCOUNT_NOT_EXIST, "The push account does not exist");
        errMap.put(ERROR_CONSOLE_CLIENT_IS_NOT_GATEWAY_PRODUCT, "The product is not a gateway products");
        errMap.put(ERROR_CONSOLE_CLIENT_SMS_ACCOUNT_ALREADY_EXISTS, "SMS account already exists");
        errMap.put(ERROR_CONSOLE_CLIENT_PROTOCOL_QUICK_OPERATION_CONFIG_DUPLICATED, "protocol quick operation config already exists");
        errMap.put(ERROR_CONSOLE_CLIENT_PROTOCOL_QUICK_OPERATION_CONFIG_TOO_MANY, "protocol quick operation configs already defined 5");

        errMap.put(ERROR_GDI_CLIENT_OK, "Success");
        errMap.put(ERROR_GDI_CLIENT_ERROR, "Error");
        errMap.put(ERROR_GDI_CLIENT_BAD_PARAM, "Bad param");
        errMap.put(ERROR_GDI_CLIENT_ACTION_NOT_EXIST, "Action does not exist");
        errMap.put(ERROR_GDI_CLIENT_PRODUCT_NOT_EXIST, "Product does not exist");
        errMap.put(ERROR_GDI_CLIENT_TIME_OUT, "Time out");
        errMap.put(ERROR_GDI_CLIENT_METHOD_NOT_SUPPORT, "Method not support");
        errMap.put(ERROR_GDI_SERVER_ERROR, "Server error");
        errMap.put(ERROR_GDI_SERVER_RESPONSE_ERROR, "Server response error");

        errMap.put(ERROR_WECHAT_CLIENT_OK, "Success");
        errMap.put(ERROR_WECHAT_CLIENT_ERROR, "Error");
        errMap.put(ERROR_WECHAT_CLIENT_USER_NOT_EXIST_ERROR, "User does not exist");
        errMap.put(ERROR_WECHAT_CLIENT_USER_NOT_CERTIFIED_ERROR, "User does not certify");
        errMap.put(ERROR_WECHAT_CLIENT_MENU_NOT_EXIST_ERROR, "Menu does not exist");
        errMap.put(ERROR_WECHAT_CLIENT_ORIGINAL_ID_IS_EXIST, "OriginalId is exist");
        errMap.put(ERROR_WECHAT_SERVER_ERROR, "Server error");
    }

    public static String code2Desc(int code) {
        return errMap.get(code);
    }
}

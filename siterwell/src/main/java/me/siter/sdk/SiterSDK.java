package me.siter.sdk;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;

import me.siter.sdk.utils.CacheUtil;
import me.siter.sdk.utils.LogUtil;
import me.siter.sdk.utils.OsUtil;
import me.siter.sdk.utils.ResourceUtil;

/**
 * SDK的入口，初始化请调用init方法。
 * 请在App启动时初始化。
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 */
public class SiterSDK {

    private static final String TAG = SiterSDK.class.getSimpleName();
    private static String pid;
    private static boolean isSDKInited = false;
    private static Context application;

    public static final String SDK_CORE_VERSION = "2.0.0";

    /**
     * 初始化SDK, id 必须传入json格式文件的资源id。文件格式详见SDK文档。
     *
     * @param context 请使用getApplicationContext()
     * @param id      R.raw.config.json
     */
    public static void init(Context context, int id) {
        application = context.getApplicationContext();
        if (nowProcess(context) && !isSDKInited) {
            CacheUtil.init(application);
            Siter.init();
            initAppInfo(context, id);
            isSDKInited = true;
        } else {
            LogUtil.e(TAG, "SDK is not on the application's process");
        }
    }

    /**
     * 初始化SDK, id 必须传入json格式文件的资源id。文件格式详见SDK文档。
     *
     * @param context 请使用getApplicationContext()
     * @param json    json格式的配置信息
     */
    public static void init(Context context, String json) {
        application = context.getApplicationContext();
        if (nowProcess(context) && !isSDKInited) {
            CacheUtil.init(application);
            Siter.init();
            initAppInfo(json);
            isSDKInited = true;
        } else {
            LogUtil.e(TAG, "SDK is not on the application's process");
        }
    }


    /**
     * 设置是否输出SDK的debug日志。
     *
     * @param enable 是否打印debug日志
     */
    public static void enableDebug(boolean enable) {
        LogUtil.setDebugEnable(enable);
    }

    /**
     * 获取debug日志的启动信息。
     *
     * @return 是否启动了debug日志
     */
    public static boolean isDebugEnabled() {
        return LogUtil.getDebugEnable();
    }

    /**
     * 获取SDK当前的pid。
     *
     * @return 当前的pid信息
     */
    public static String getPid() {
        return pid;
    }

    /**
     * 重置SDK的pid信息,一般不需要使用此功能。可以切换SDK所使用的pid参数。
     *
     * @param currentPid 需要重置的pid
     */
    public static void resetPid(String currentPid) {
        pid = currentPid;
        CacheUtil.putString(Constants.HEKR_PID, pid);
    }

    /**
     * SDK当前保存的Context信息。
     *
     * @return Context上下文
     */
    public static Context getContext() {
        return application;
    }

    /**
     * SDK初始化信息。
     *
     * @return sdk是否已经初始化
     */
    public static boolean isSDKInited() {
        return isSDKInited;
    }

    /**
     * 设置SDK连接的服务器地址。true为debug服务器地址,false为正式的地址。默认为false。
     *
     * @param debug 是否是debug的服务器地址
     */
    public static void setTestSite(boolean debug) {
        if (debug) {
            Constants.setTestSite(null);
        } else {
            Constants.setOnlineSite(null);
        }
    }

    /**
     * 设置Debug域名
     *
     * @param domain 是否是debug的服务器地址
     */
    public static void setTestSite(String domain) {
        Constants.setTestSite(domain);
    }

    /**
     * 是否是debug域名
     *
     * @return 是否正在使用debug域名
     */
    public static boolean isTestSite() {
        return Constants.isTestSite();
    }

    /**
     * 设置线上的域名
     *
     * @param domain 设置线上的域名
     */
    public static void setOnlineSite(String domain) {
        Constants.setOnlineSite(domain);
    }

    /**
     * 初始化sdk的信息
     */
    private static void initAppInfo(Context context, int id) {
        String json = ResourceUtil.convertStreamToString(context, id);
        initAppInfo(json);
    }

    /**
     * 初始化sdk的信息
     */
    private static void initAppInfo(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                org.json.JSONObject jsonConfig = new org.json.JSONObject(json);
                if (jsonConfig.has("Hekr")) {
                    pid = jsonConfig.getJSONObject("Hekr").getString("AppId");
                    CacheUtil.putString(Constants.HEKR_PID, pid);
                    LogUtil.d(Constants.HEKR_SDK, "init: " + pid);
                    if (TextUtils.isEmpty(pid)) {
                        throw new IllegalArgumentException("Hekr AppId is error in json");
                    }
                } else {
                    throw new IllegalArgumentException("Can not find the Hekr config in json");
                }
            } else {
                throw new IllegalArgumentException("Json is empty");
            }
        } catch (JSONException e) {
            throw new IllegalArgumentException("Json format is incorrect");
        }
    }

    /**
     * 判断sdk是否在当前进程
     */
    private static boolean nowProcess(Context context) {
        String processName = OsUtil.getProcessName(context, android.os.Process.myPid());
        return !TextUtils.isEmpty(processName) && TextUtils.equals(context.getPackageName(), processName);
    }
}

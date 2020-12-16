package me.siter.sdk.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import me.siter.sdk.HekrSDK;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/
public class AppIdUtil {

    private static final String TAG = AppIdUtil.class.getSimpleName();

    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static final String PREFS_DEVICE_ID = "device_id";
    private static final String PREFS_INSTALL_ID = "install_id";
    private static final String PREFS_IMEI = "imei";
    private static final String PREFS_PSUEDO_ID = "psuedo_id";
    private static final String PREFS_APP_ID = "app_id";

    private volatile static String installId;
    private volatile static String androidId;
    private volatile static String imei;
    private volatile static String psuedoId;
    private volatile static String appId;

    /**
     * 获取App Id, 因为获取imei需要没有权限，改用psuedo id 方式，如果psuedo id 为空，使用android id，
     * 如果android id为空，随机生成一个uuid
     *
     * @param context Context
     * @return App Id
     */
    public synchronized static String getAppId(Context context) {
        if (appId == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            appId = prefs.getString(PREFS_APP_ID, null);
            if (appId == null) {
                int permission = context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
                boolean hasPermission = permission == PackageManager.PERMISSION_GRANTED;
                String id = "";
                if (hasPermission) {
                    id = getIMEI(context);
                }
                if (TextUtils.isEmpty(id)) {
                    id = getPsuedoId(context);
                }
                if (TextUtils.isEmpty(id)) {
                    id = getAndroidId(context);
                }
                if (TextUtils.isEmpty(id)) {
                    id = getInstallId(context);
                }
                String result = TextUtils.concat(id, HekrSDK.getPid(), context.getPackageName()).toString();
                result = result.replace("-", "");
                int len = result.length();
                if (len > 64) {
                    result = result.substring(len - 64, len);
                }
                appId = result;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREFS_APP_ID, appId);
                editor.apply();
            } else {
                int len = appId.length();
                if (len > 64) {
                    appId = appId.substring(len - 64, len);
                }
            }
            LogUtil.d(TAG, "App ID is " + appId);
        }
        return appId;
    }

    /**
     * 获取pseudo unique ID
     *
     * @return ID
     */
    public static String getPsuedoId(Context context) {
        if (psuedoId == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            psuedoId = prefs.getString(PREFS_PSUEDO_ID, null);
            if (psuedoId == null) {
                String m_szDevIDShort = "35" + (Build.BOARD.length() % 10)
                        + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10)
                        + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10)
                        + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);
                String serial;
                try {
                    serial = Build.class.getField("SERIAL").get(null).toString();
                    psuedoId = new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    psuedoId = "";
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREFS_PSUEDO_ID, imei).apply();
            }
        }
        LogUtil.d(TAG, "Psuedo ID is " + psuedoId);
        return psuedoId;
    }

    /**
     * 获取手机唯一标识并拼接厂家pid,但是在android 6.0 以上需要获取动态权限。
     *
     * @param context Context
     * @return imei
     */
    @SuppressLint("HardwareIds")
    public synchronized static String getIMEI(Context context) {
        try {
            if (imei == null) {
                SharedPreferences prefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
                imei = prefs.getString(PREFS_IMEI, null);
                if (imei == null) {
                    TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    String id = telephonyManager.getDeviceId();
                    if (id != null) {
                        try {
                            imei = UUID.nameUUIDFromBytes(id.getBytes("utf8")).toString();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        imei = "";
                    }
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(PREFS_IMEI, imei).apply();
                }
            }
            LogUtil.d(TAG, "IMEI is " + imei);
        }catch (SecurityException e){
            return "";
        }

        return imei;
    }

    /**
     * 获取Android ID，但是如<a href="https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id/5626208#562620">Android ID</a>
     * 回答中的描述，会有如下情况:
     * Android ID - Hardware (can be null, can change upon factory reset, can be altered on a rooted device)
     *
     * @param context Context
     * @return Android ID
     */
    @SuppressLint("HardwareIds")
    public synchronized static String getAndroidId(Context context) {
        if (androidId == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            String id = prefs.getString(PREFS_DEVICE_ID, null);
            if (id != null) {
                androidId = UUID.fromString(id).toString();
            } else {
                id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (id != null) {
                    try {
                        androidId = UUID.nameUUIDFromBytes(id.getBytes("utf8")).toString();
                    } catch (UnsupportedEncodingException e) {
                        androidId = "";
                        e.printStackTrace();
                    }
                } else {
                    androidId = "";
                }
                prefs.edit().putString(PREFS_DEVICE_ID, androidId).apply();
            }
        }
        LogUtil.d(TAG, "Android ID is " + androidId);
        return androidId;
    }

    /**
     * install_id 随机生成一个uuid，在每次安装和卸载，或者清除数据的时候都会被清除掉。
     *
     * @param context Context
     * @return install id
     */
    public synchronized static String getInstallId(Context context) {
        if (installId == null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            installId = prefs.getString(PREFS_INSTALL_ID, null);
            if (installId == null) {
                installId = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREFS_INSTALL_ID, installId).apply();
            }
        }
        LogUtil.d(TAG, "Install ID is " + installId);
        return installId;
    }
}

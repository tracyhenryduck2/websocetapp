package me.siter.sdk.utils;

import android.util.Log;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 * Description: 日志打印类
 */

public class LogUtil {

    private static boolean isDebug = true;

    public static void setDebugEnable(boolean debug) {
        if (isDebug)
            isDebug = debug;
    }

    public static boolean getDebugEnable() {
        return isDebug;
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (isDebug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }
}

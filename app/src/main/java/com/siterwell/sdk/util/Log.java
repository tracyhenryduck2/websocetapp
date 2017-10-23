package com.siterwell.sdk.util;

import com.siterwell.sdk.bean.LogBean;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.CopyOnWriteArrayList;


/*
@class Log
@autor Administrator
@time 2017/10/16 14:13
@email xuejunju_4595@qq.com
*/

public final class Log {

    public static boolean isPrint = true;
    private static final String LOG_FORMAT = "%1$s\n%2$s";
    public static CopyOnWriteArrayList<LogBean> logList = new CopyOnWriteArrayList<>();
    public static boolean logEvent = false;

    private Log() {
    }

    public static void v(String TAG, String message, Object... args) {
        log(android.util.Log.VERBOSE, null, TAG, message, args);
    }

    public static void d(String TAG, String message, Object... args) {
        log(android.util.Log.DEBUG, null, TAG, message, args);
    }

    public static void i(String TAG, String message, Object... args) {
        log(android.util.Log.INFO, null, TAG, message, args);
    }

    public static void w(String TAG, String message, Object... args) {
        log(android.util.Log.WARN, null, TAG, message, args);
    }

    public static void e(String TAG, Throwable ex) {
        log(android.util.Log.ERROR, ex, TAG, null);
    }

    public static void e(String TAG, String message, Object... args) {
        log(android.util.Log.ERROR, null, TAG, message, args);
    }

    public static void e(String TAG, Throwable ex, String message, Object... args) {
        log(android.util.Log.ERROR, ex, TAG, message, args);
    }

    public static void a(String TAG, String message, Object... args) {
        log(android.util.Log.ASSERT, null, TAG, message, args);
    }

    private static void log(int priority, Throwable ex, String TAG, String message, Object... args) {
        if (!isPrint) return;
        if (args.length > 0) {
            message = String.format(message, args);
        }

        String log;
        if (ex == null) {
            log = message;
        } else {
            String logMessage = message == null ? ex.getMessage() : message;
            String logBody = android.util.Log.getStackTraceString(ex);
            log = String.format(LOG_FORMAT, logMessage, logBody);
        }
        //ViewWindow.showView(TextUtils.concat(TAG, ":", message));
        LogBean logBean = new LogBean(System.currentTimeMillis(), priority, TAG, log);
        //FileUtils.writeLines();
        android.util.Log.println(priority, TAG, log);
        //将log添加到lists中
        add(logBean);
    }


    public static class LogEvent {
        private LogBean logBean;

        public LogBean getLogBean() {
            return logBean;
        }

        public void setLogBean(LogBean logBean) {
            this.logBean = logBean;
        }

        public LogEvent(LogBean logBean) {
            this.logBean = logBean;
        }
    }

    public static void clear() {
        logList.clear();
    }


    private static synchronized void add(LogBean logBean) {
        logList.add(logBean);
        if (!logEvent) return;
        EventBus.getDefault().post(new LogEvent(logBean));
    }


    /**
     * 将list中的数据按行输入
     */
    public static String toString(Object[] array) {
        if (array == null) {
            return "null";
        }
        if (array.length == 0) {
            return "\n";
        }
        StringBuilder sb = new StringBuilder(array.length * 7);
        sb.append("**************************************\n");
        sb.append(array[0]);
        for (int i = 1; i < array.length; i++) {
            sb.append("\n");
            sb.append(array[i]);
        }
        sb.append("\n");
        return sb.toString();
    }

}

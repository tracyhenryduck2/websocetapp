package me.siter.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by TracyHenry on 2020/12/16.
 * Author: TracyHenry
 **/
public class SpCache {
    private static final String TAG = SpCache.class.getSimpleName();
    private static SpCache INSTANCE;
    private ConcurrentMap<String, SoftReference<Object>> mCache;
    private String mPrefFileName = "HEKR_SDK";
    private Context mContext;


    private SpCache(Context context, String prefFileName) {
        mContext = context.getApplicationContext();
        mCache = new ConcurrentHashMap<>();
        initDatas(prefFileName);
    }

    private void initDatas(String prefFileName) {
        if (null != prefFileName && prefFileName.trim().length() > 0) {
            mPrefFileName = prefFileName;
        } else {
            LogUtil.d(TAG, "PrefFileName is invalid , we will use default value ");
        }
    }

    private static SpCache init(Context context, String prefFileName) {
        if (INSTANCE == null) {
            synchronized (SpCache.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpCache(context, prefFileName);
                }
            }
        }
        return INSTANCE;
    }

    public static SpCache init(Context context) {
        return init(context, null);
    }

    private static SpCache getInstance() {
        if (INSTANCE == null)
            throw new NullPointerException("You should invoke HekrSDK.init() before using it.");
        return INSTANCE;
    }

    //put
    public static SpCache putInt(String key, int val) {
        return getInstance().put(key, val);
    }

    public static SpCache putLong(String key, long val) {
        return getInstance().put(key, val);
    }

    public static SpCache putString(String key, String val) {
        return getInstance().put(key, val);
    }

    public static SpCache putBoolean(String key, boolean val) {
        return getInstance().put(key, val);
    }

    public static SpCache putFloat(String key, float val) {
        return getInstance().put(key, val);
    }

    //get
    public static int getInt(String key, int defaultVal) {
        return (int) (getInstance().get(key, defaultVal));
    }

    public static long getLong(String key, long defaultVal) {
        return (long) (getInstance().get(key, defaultVal));
    }

    public static String getString(String key, String defaultVal) {
        return (String) (getInstance().get(key, defaultVal));
    }

    public static boolean getBoolean(String key, boolean defaultVal) {
        return (boolean) (getInstance().get(key, defaultVal));
    }

    public static float getFloat(String key, float defaultVal) {
        return (float) (getInstance().get(key, defaultVal));
    }

    //contains
    public boolean contains(String key) {
        return mCache.get(key).get() != null || getSharedPreferences().contains(key);
    }

    //remove
    public static SpCache remove(String key) {
        return getInstance()._remove(key);
    }

    private SpCache _remove(String key) {
        mCache.remove(key);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
        return getInstance();
    }

    //clear
    public static SpCache clear() {
        return getInstance()._clear();
    }

    private SpCache _clear() {
        mCache.clear();
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
        return getInstance();
    }

    private <T> SpCache put(String key, T t) {
        mCache.put(key, new SoftReference<Object>(t));
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        if (t instanceof String) {
            editor.putString(key, (String) t);
        } else if (t instanceof Integer) {
            editor.putInt(key, (Integer) t);
        } else if (t instanceof Boolean) {
            editor.putBoolean(key, (Boolean) t);
        } else if (t instanceof Float) {
            editor.putFloat(key, (Float) t);
        } else if (t instanceof Long) {
            editor.putLong(key, (Long) t);
        } else {
            LogUtil.d(TAG, "you may be put a invalid object :" + t);
            editor.putString(key, t.toString());
        }

        SharedPreferencesCompat.apply(editor);
        return getInstance();
    }

    private Object readDisk(String key, Object defaultObject) {
        LogUtil.e("TAG", "readDisk");
        SharedPreferences sp = getSharedPreferences();

        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }
        LogUtil.e(TAG, "you can not read object , which class is " + defaultObject.getClass().getSimpleName());
        return null;
    }

    private Object get(String key, Object defaultVal) {
        SoftReference reference = mCache.get(key);
        Object val;
        if (null == reference || null == reference.get()) {
            val = readDisk(key, defaultVal);
            mCache.put(key, new SoftReference<>(val));
        }
        val = mCache.get(key).get();
        return val;
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return Method
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor editor
         */
        static void apply(final SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {
                    editor.commit();
                    return null;
                }
            };
        }
    }

    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(mPrefFileName, Context.MODE_PRIVATE);
    }

    /**
     * 获取所有数据
     *
     * @return maps
     */
    public static Map<String, ?> getAll() {
        Map<String, ?> maps = new HashMap<>();
        try {
            SharedPreferences sp = getInstance().getSharedPreferences();
            maps = sp.getAll();
            return maps;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maps;
    }
}
